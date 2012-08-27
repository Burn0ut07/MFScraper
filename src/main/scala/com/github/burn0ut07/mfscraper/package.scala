package com.github.burn0ut07

package object mfscraper {
    class RichURL(repr: java.net.URL) {
        val pathComponents = repr.getPath.split("/") drop 1
    }

    implicit def url2RichUrl(url: java.net.URL) : RichURL = new RichURL(url)

    def validInt(f: Float) = f.toInt.toFloat == f

    def convertIfValid(f: Float) = 
        if (validInt(f)) Left(f.toInt) else Right(f)
}
