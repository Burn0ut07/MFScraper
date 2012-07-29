package com.github.burn0ut07.mfscraper

import dispatch._

import java.io.File

import javax.imageio.ImageIO

import org.jsoup.Jsoup

import scala.collection.JavaConversions._

object MFScraper {
  def base = :/("mangafox.me") / "manga"
  val dispatch = new Http

  def shutdown() {
    dispatch.shutdown()
  }
}

case class MFScraper(comic:String) {
  private[this] def comicBase = MFScraper.base / comic
  val info : Promise[Either[String, ComicInfo]] = {
    val res = MFScraper.dispatch(comicBase OK as.String).either.right map { info =>
      ComicInfo(Jsoup.parse(info))
    }
    val msg = "Could not retrieve info: %s"
    for (exc <- res.left)
      yield exc match {
        case StatusCode(302) => msg.format("comic does not exist")
        case _ => msg.format(exc.getMessage)
      }
  }

  def scrapeComic() : Promise[Either[String, (Comic, Iterable[String])]] = {
    for {
      comicInfo <- info.right
      vols <- Promise.all(comicInfo.volumes map { case (v, cs) => scrapeVolume(v, cs) })
    } yield {
      val valid = for (Right(vol) <- vols) yield vol
      val errors = for (Left(err) <- vols) yield err
      Right(Comic(comic, valid), errors)
    }
  }

  def scrapeVolume(volume:Int, chapters:Iterable[String]) : Promise[Either[String, Volume]] = {
    Promise(Left("Error retriving volume " + volume))
  }

  def scrapeChapter(chapter:Int) : Promise[Either[String, Chapter]] = {
    Promise(Left("Error retriving chapter " + chapter))
  }

  def scrapeChpaterPage(chapter:Int, page:Int) : Promise[Either[String, Page]] = {
    val pageUrl = comicBase / "c%03d".format(chapter) / "%d.html".format(page)
    Promise(Left("Error retriving page " + page))
  }
}

trait MFQuery {
  protected[this] val content : org.jsoup.nodes.Document
  protected[this] val queryBase = "html body#body %s"

  protected[this] def query(q:String) : org.jsoup.select.Elements = 
    content.select(queryBase.format(q))
}

case class Page(content:org.jsoup.nodes.Document, val page:Int) extends MFQuery {
  private[this] val imageUrlQuery = "div#viewer a img#image[src$=.jpg]"
  private[this] val nextPageQuery = 
    "div.widepage.page div#top_center_bar form#top_bar div.r.m a.btn.next_page[href$=.html]"

  val imageUrl = query(imageUrlQuery).first.attr("src")
  val next:Option[String] = {
    val nextImgLink = Option(query(nextPageQuery).first)
    nextImgLink map { _ attr "href" }
  }
}

case class Chapter(val chapter:Int, val pages:Iterable[Page])
case class Volume(val volume:Int, val chapters:Iterable[Chapter])
case class Comic(val comic:String, val volumes:Iterable[Volume])

case class ComicInfo(content:org.jsoup.nodes.Document) extends MFQuery {
  private[this] val base = "div#page.widepage div.left div#chapters %s"
  private[this] val volumesQuery = base.format("div.slide h3.volume")
  private[this] val chapterBase = "ul.chlist li div %s a.tips"
  private[this] val chaptersQuery = 
    "%s, %s".format(chapterBase.format("h3"), chapterBase.format("h4"))

  // Map of volume number to iterable of chapter urls
  val volumes : Map[Int, Iterable[String]] = {
    val chapElems:Seq[org.jsoup.nodes.Element] = query(chaptersQuery)
    chapElems map { _ attr "href" } groupBy { link =>
      val pathComps = (new java.net.URL(link)).getPath.split("/")
      pathComps(3) substring 1 toInt
    }
  }
}
