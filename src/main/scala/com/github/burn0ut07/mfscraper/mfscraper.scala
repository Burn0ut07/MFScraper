package com.github.burn0ut07.mfscraper

import dispatch._

import java.io.File
import javax.imageio.ImageIO

object MFScraper {
  val base = :/("www.mangafox.com") / "manga"
  val dispatcher = new Http

  def shutdown() {
    dispatcher.shutdown()
  }
}

class MFScraper(comic:String) {
  val comicBase = base / comic

  def scrapeInRange(start:Int, end:Int) : Promise[Either[String, Comic]] = {
    Promise(Left("Error retriving comic"))
  }

  def scrapeChapter(chapter:Int) : Promise[Either[String, Chapter]] = {
    Promise(Left("Error retriving chapter"))
  }

  def scrapeChpaterPage(chapter:Int, page:Int) : Promise[Either[String, Page]] = {
    val pageUrl = comicBase / "c%03d".format(chapter) / "%d.html".format(page)
    Promise(Left("Error retriving page"))
  }
}

case class Page(val content:xml.Elem, val nextPage:Option[Int]) {
  val imageUrl = {
    val imageElem = content \\ "div" \\ "a" \ "img" \\ "@src" filter { link =>
      link.text endsWith ".jpg"
    }
    imageElem.head.text
  }
}

case class Chapter(val chapter:Int, val pages:Seq[Page])
case class Comic(val comic:String, val chapters:Iterable[Chapter])
