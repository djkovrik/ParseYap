import pl.droidsonroids.jspoon.annotation.Selector

class IncubatorPage {
  @Selector("td.newshead") lateinit var headers: List<IncubatorHead>
  @Selector("td.postcolor") lateinit var contents: List<IncubatorContent>
  @Selector("td.holder") lateinit var bottoms: List<IncubatorBottom>
}

class IncubatorHead {
  @Selector(".subtitle", defValue = "Unknown") lateinit var title: String
  @Selector(".subtitle", attr = "href", defValue = "") lateinit var link: String
  @Selector(".rating-short-value > a", defValue = "0") lateinit var rating: String
}

class IncubatorContent {
  @Selector("[id~=news_.*]", attr = "innerHtml", defValue = "") lateinit var description: String
  @Selector("img[src]", attr = "src") var images: List<String> = emptyList()
  @Selector("iframe[src]", attr = "src") var videos: List<String> = emptyList()
  @Selector("iframe[src]", attr = "outerHtml") var videosRaw: List<String> = emptyList()
}


class IncubatorBottom {
  @Selector(".icon-user > a", defValue = "Unknown") lateinit var author: String
  @Selector(".icon-user > a", attr = "href", defValue = "") lateinit var authorLink: String
  @Selector(".icon-date", defValue = "Unknown") lateinit var date: String
  @Selector(".icon-forum > a", defValue = "Unknown") lateinit var forumName: String
  @Selector(".icon-forum > a", attr = "href", defValue = "") lateinit var forumLink: String
  @Selector("span", format = "(\\d+)", defValue = "0") lateinit var comments: String
}

fun main(args: Array<String>) {

  createRetrofitForIncubator()
      .create(YapLoader::class.java)
      .loadIncubator(0)
      .subscribe({ topics ->
        println("Headers: ${topics.headers.size}")
        println("Contents: ${topics.contents.size}")
        println("Bottoms: ${topics.bottoms.size}")
        println("------")

        assert(topics.headers.size == topics.contents.size)
        assert(topics.bottoms.size == topics.contents.size)

        for (index in 0 until topics.headers.size) {
          println("Title: ${topics.headers[index].title}")
          println("Link: ${topics.headers[index].link}")
          println("Rating: ${topics.headers[index].rating}")
          println("Description: ${topics.contents[index].description}")
          println("Images: ${topics.contents[index].images}")
          println("Videos: ${topics.contents[index].videos}")
          println("Videos raw: ${topics.contents[index].videosRaw}")
          println("Author: ${topics.bottoms[index].author}")
          println("Author link: ${topics.bottoms[index].authorLink}")
          println("Date: ${topics.bottoms[index].date}")
          println("Forum title: ${topics.bottoms[index].forumName}")
          println("Forum link: ${topics.bottoms[index].forumLink}")
          println("Comments: ${topics.bottoms[index].comments}")
          println("------------------------")
        }

      }, { throwable ->
        println("Error: ${throwable.message}")
      })
}
