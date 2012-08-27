package com.github.burn0ut07.mfscraper

import org.jsoup.nodes

case class ComicInfo(content: nodes.Document) extends MFQuery {
  private[this] val volumeQuery = "item > title"
  private[this] val Volume = """[\p{Graph} ]+? (?:Vol )?(\d*) ?Ch \d+(?:\.\d)?""".r
  private[this] val Chapter = """[\p{Graph} ]+? (?:Vol )?\d* ?Ch (\d+)(?:\.)?(\d)?""".r

  val vols: Map[Int, Iterable[Either[Int, Float]]] = {
    val vols = query(volumeQuery, false) map { _.ownText }
    
    vols groupBy { title =>
      val Volume(vol) = title
      vol
    } map { case (vol, titles) =>
      val chps = titles map { title =>
        val Chapter(chap, part) = title
        Option(part) match {
          case None => Left(chap.toInt)
          case Some(p) => Right("%s.%s".format(chap, part).toFloat)
        }
      }
      
      if(vol.isEmpty) (-1, chps) else (vol.toInt, chps)
    }
  }
}
