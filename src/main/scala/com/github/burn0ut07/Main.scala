package com.github.burn0ut07

import mfscraper._

import org.clapper.argot._
import ArgotConverters._

object Main extends App {
  val parser = new ArgotParser("mfscraper", preUsage=Some("version 0.1.0"))

  val start = parser.parameter[Int]("start", "Chapter to start download from.", false)
  val end = parser.parameter[Int]("end", "Chapter to end download at.", false)
  val comic = parser.parameter[String]("comic", 
    "Name of comic to download as displayed in MangaFox url",
    false)
    
  try {
    parser.parse(args)
    println("Hello, mfscraper!")
    MFScraper.shutdown()
  } catch {
    case e: ArgotUsageException => println(e.message)
  }
}
