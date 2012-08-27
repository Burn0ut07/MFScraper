package com.github.burn0ut07.mfscraper

import org.jsoup.nodes

import scala.collection.JavaConversions._
import scala.collection.{mutable=>cm}

trait MFQuery {
  protected[this] val content: nodes.Document
  protected[this] val queryBase = "html body#body %s"

  protected def query(
      q:String, 
      withBase: Boolean = true): cm.Buffer[nodes.Element] = 
    if(withBase) content.select(queryBase.format(q)) else content.select(q)
}
