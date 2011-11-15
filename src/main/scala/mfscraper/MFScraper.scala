import java.io.File
import javax.imageio.ImageIO

import scala.actors.Futures._

import dispatch._

package mfscraper {

	object MFScraper {
		
		val BASE_URL = :/("www.mangafox.com") / "manga"
		var imgList:List[scala.actors.Future[Boolean]] = List()
	
		def scrapeInRange(start:Int, end:Int, comic:String) = {
			val chapterScraper = scrapeAtChapter(comic) _

			val comicDir = new File("./" + comic)
			comicDir.mkdir()
		
			val range = (start to end).toList
			// Do each chapter in parallel
			range.par map { x => chapterScraper(x) }
			// Block until all images are saved
			imgList foreach { _() }
		}
	
		def scrapeAtChapter(comic:String)(chapter:Int) = {
			val pagesScraper = getPageAndNext(comic)(chapter) _
		
			val chapterDir = new File("./" + comic + "/" + chapter)
			chapterDir.mkdirs()
		
			var page:Option[Int] = Some(1); //First page is always 1
			while(page.getOrElse(None) != None) {
				page = pagesScraper(page.get)
			}
		}
	
		def getPageAndNext(comic:String)(chapter:Int)(currPage:Int) = { 
			val pageURL = 
			BASE_URL / comic / "c%03d".format(chapter) / "%d.html".format(currPage)
			
			val imgLink = Http(pageURL </> { nodes =>
				(nodes \\ "div" \\ "a" \\ "img" \\ "@src").filter { link =>
					link.text.endsWith(".jpg")
				}.head.text
			})
			
			val imgFile = "./" + comic + "/" + chapter + "/" + currPage + ".jpg"
			
			imgList = future { 
				Http(url(imgLink) >> { in =>
					ImageIO.write(ImageIO.read(in), "jpg", new File(imgFile))
				})
			} +: imgList

			val nextPage = Http(pageURL </> { nodes =>
				((nodes \\ "div" \\ "div" \\ "form" \\ "div" \\ "a").filter { link =>
					(link \\ "@class").text.endsWith("next_page")
				} \\ "@href").head.text.endsWith(".html")
			})
		
			if(nextPage) Some(currPage + 1)
			else {
				println(currPage + " No more pages!")
				None
			}
		}
	}
}
