package com.github.burn0ut07.mfscraper

import dispatch._

import java.io.File

import javax.imageio.ImageIO

object MFScraper {
  def base = :/("mangafox.me") / "rss"
  val dispatch = new Http

  def shutdown() {
    dispatch.shutdown()
  }

  val ERR_MSG = "Could not retrieve page: %s"
  val PAGE_ERROR = "page %s in %s in %s %d does not exist"
}

case class MFScraper(comic:String) {
  private[this] def comicBase = MFScraper.base / comic
  lazy val info : Promise[Either[String, ComicInfo]] = {
    val res = MFScraper.dispatch(comicBase OK as.mfscraper.ComicInfo).either
    val msg = "Could not retrieve info: %s"
    for (exc <- res.left)
      yield exc match {
        case StatusCode(302) => MFScraper.ERR_MSG.format("comic does not exist")
        case _ => msg.format(exc.getMessage)
      }
  }

  def scrapeMetaComic(): Promise[Either[String, Comic]] = {
    for {
      comicInfo <- info.right
      vols <- Promise.all(comicInfo.vols map { case (v, cs) => scrapeVolume(v, cs) })
    } yield {
      val valid = for (Right(vol) <- vols) yield vol
      val errors = for (Left(err) <- vols) yield err
      Right(Comic(comic, valid, errors))
    }
  }

  def scrapeVolume(volume:Int, chapters:Iterable[Either[Int, Float]]): Promise[Either[String, Volume]] = {
    val chaps = for (chapter <- chapters) yield scrapeChapter(volume, chapter)
    for (chps <- Promise.all(chaps)) 
      yield {
        val valid = for (Right(chap) <- chps) yield chap
        val errors = for (Left(err) <- chps) yield err
        Right(Volume(volume, valid, errors))
      }
  }

  def scrapeChapter(volume:Int, chapter:Either[Int, Float]): Promise[Either[String, Chapter]] = {
    Promise(Left("Error retriving chapter " + chapter))
  }

  def scrapeChpaterPage(volume:Int, chapter:Either[Int, Float], page:Int): Promise[Either[String, Page]] = {
    val chap = chapter match {
      case Left(l) => "c%03d".format(l)
      case Right(r) => "c%05.1f".format(r)
    }
    
    val vol = volume match {
      case -1 => ""
      case v => "v%02d".format(volume)
    }
    
    val pageUrl = comicBase / vol / chap / "%d.html".format(page)
    
    val chpPage = Http(pageUrl OK as.mfscraper.Page(page)).either

    for (cPage <- chpPage.left)
      yield cPage match {
        case StatusCode(302) => 
          MFScraper.ERR_MSG.format(MFScraper.PAGE_ERROR.format(page, chap, vol))
        case _ => MFScraper.ERR_MSG.format(cPage.getMessage)
      }
  }
}
