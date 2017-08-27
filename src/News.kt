
import pl.droidsonroids.jspoon.annotation.Selector

class News {
  @Selector(".newshead") lateinit var headers: List<NewsHead>
  @Selector(".news-content") lateinit var contents: List<NewsContent>
  @Selector(".newsbottom") lateinit var bottoms: List<NewsBottom>
}

class NewsHead {
  @Selector(".subtitle", defValue = "Unknown") lateinit var title: String
  @Selector(".subtitle", attr = "href") lateinit var link: String
  @Selector(".rating-short-value > a", defValue = "") lateinit var rating: String
}

class NewsContent {
  @Selector("[id~=news_.*]", attr = "innerHtml", defValue = "Unknown") lateinit var description: String
  @Selector("img[src]", attr = "src") lateinit var images: List<String>
  @Selector("iframe[src]", attr = "src") lateinit var videos: List<String>
}

class NewsBottom {
  @Selector(".icon-user > a", defValue = "Unknown") lateinit var author: String
  @Selector(".icon-user > a", attr = "href") lateinit var authorLink: String
  @Selector(".icon-date", defValue = "Unknown") lateinit var date: String
  @Selector(".icon-forum > a", defValue = "Unknown") lateinit var forumName: String
  @Selector(".icon-forum > a", attr="href", defValue = "Unknown") lateinit var forumLink: String
  @Selector("span", defValue = "0") lateinit var comments: String
}


fun main(args: Array<String>) {

  createRetrofit()
      .create(YapLoader::class.java)
      .loadNews(0)
      .subscribe({ news ->
        println("Headers: ${news.headers.size}")
        println("Contents: ${news.contents.size}")
        println("Bottoms: ${news.bottoms.size}")
        println("------")

        assert(news.headers.size == news.contents.size)
        assert(news.bottoms.size == news.contents.size)

        for(index in 0 until news.headers.size) {
          println("Title: ${news.headers[index].title}")
          println("Link: ${news.headers[index].link}")
          println("Rating: ${news.headers[index].rating}")
          println("Description: ${news.contents[index].description}")
          println("Images: ${news.contents[index].images}")
          println("Videos: ${news.contents[index].videos}")
          println("Author: ${news.bottoms[index].author}")
          println("Author link: ${news.bottoms[index].authorLink}")
          println("Date: ${news.bottoms[index].date}")
          println("Forum title: ${news.bottoms[index].forumName}")
          println("Forum link: ${news.bottoms[index].forumLink}")
          println("Comments: ${news.bottoms[index].comments}")
          println("------------------------")
        }

      }, { throwable ->
        println("Error: ${throwable.message}")
      })
}