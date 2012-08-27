package com.github.burn0ut07.mfscraper

import org.jsoup.nodes

case class Page(
    content: nodes.Document, 
    val page: Int) 
  extends MFQuery {
  
  private[this] val imageUrlQuery = "div#viewer a img#image[src$=.jpg]"
  private[this] val nextPageQuery = 
    "div.widepage.page div#top_center_bar form#top_bar div.r.m a.btn.next_page[href$=.html]"

  val imageUrl = query(imageUrlQuery).head.attr("src")
  val next: Option[String] = {
    val nextImgLink = Option(query(nextPageQuery).head)
    nextImgLink map { _ attr "href" }
  }
}

case class Chapter(
    val chapter:AnyVal, 
    val pages:Iterable[Page], 
    val errors:Iterable[String])

case class Volume(
    val volume:Int, 
    val chapters:Iterable[Chapter], 
    val errors:Iterable[String])

case class Comic(
    val comic:String, 
    val volumes:Iterable[Volume], 
    val errors:Iterable[String])
