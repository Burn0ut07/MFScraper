import mfscraper._

object Main extends App {
	
	override def main(args: Array[String]) = {
		if(args.length != 3) {
			throw new IllegalArgumentException()
		}
		
		var start = 0 
		var end = 0
		
		try {
			start = args(0).toInt
			end = args(1).toInt
		} catch {
			case nfe: NumberFormatException =>
				println("start and end must be valid integers!")
			case e: Exception =>
				println("unknown error occured!")
		}
		
		if(start != 0 && end != 0) {
			try {
				MFScraper.scrapeInRange(start, end, args(2))
			} catch {
				case e: Exception =>
					println("Error occured! Perhaps you gave bad paramters?")
			}
		}
	}
}