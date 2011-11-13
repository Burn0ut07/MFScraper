import java.io.File
import javax.imageio.ImageIO
import java.awt.image._

import scala.actors.Futures._
import scala.xml.NodeSeq

import dispatch._

package mfscraper {

	object MFScraper {
		
		val BASE_URL = :/("www.mangafox.com") / "manga"
		val NO_NEXT = 0
	
		def scrapeInRange(start:Int, end:Int, comic:String) = {
			val chapterScraper = scrapeAtChapter(comic) _

			val comicDir = new File("./" + comic)
			comicDir.mkdir()
		
			val range = (start to end).toList
			// Do each chapter in parallel
			range map { x => future { chapterScraper(x) } } foreach { _() }
		}
	
		def scrapeAtChapter(comic:String)(chapter:Int) = {
			val pagesScraper = getPageAndNext(comic)(chapter) _
		
			val chapterDir = new File("./" + comic + "/" + chapter)
			chapterDir.mkdirs()
		
			var i = 1; //First page is always 1
			while(i != NO_NEXT) {
				i = pagesScraper(i)
			}
		}
	
		def getPageAndNext(comic:String)(chapter:Int)(currPage:Int):Int = { 
			val pageURL = 
			BASE_URL / comic / "c%03d".format(chapter) / "%d.html".format(currPage)
			
			val imgLink = Http(pageURL </> { nodes =>
				(nodes \\ "div" \\ "a" \\ "img" \\ "@src") filter { link =>
					link.text.endsWith(".jpg")
				}
			}).head.text
		
			val imgFile = new File("./" + comic + "/" + chapter + "/" + currPage 
				+ ".jpg")
			
			val img = Http(url(imgLink) >> { in =>
				ImageIO.read(in)
			})
		
			ImageIO.write(img, "jpg", imgFile)
		
			// Try joining handlers?
			val nextPage = (Http(pageURL </> { nodes =>
				(nodes \\ "div" \\ "div" \\ "form" \\ "div" \\ "a") filter { link =>
					(link \\ "@class").text.endsWith("next_page")
				}
			}) \\ "@href").head.text.endsWith(".html")
		
			if(nextPage) currPage + 1
			else {
				println(currPage + " No more pages!")
				0
			}
		}
	}
}