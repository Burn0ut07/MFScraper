package dispatch.as.mfscraper

import com.ning.http.client.Response

import com.github.burn0ut07.mfscraper

import org.jsoup.Jsoup
import org.jsoup.nodes

object Document extends (Response => nodes.Document) {
  def apply(r: Response) =
    (dispatch.as.String andThen Jsoup.parse)(r)
}

object ComicInfo extends (Response => mfscraper.ComicInfo) {
  def apply(r: Response) = 
    (dispatch.as.String andThen Jsoup.parse andThen mfscraper.ComicInfo)(r)
}

object Page extends {
  def apply(page: Int)(r: Response) = {
    def makePage(content: nodes.Document): mfscraper.Page = 
      mfscraper.Page(content, page)

    (dispatch.as.String andThen Jsoup.parse andThen makePage)(r)
  } 
}
