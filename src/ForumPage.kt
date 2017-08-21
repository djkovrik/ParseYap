import pl.droidsonroids.jspoon.annotation.Selector

class ForumPage {
  @Selector("a[href~=.*/forum\\d+/].title")
  lateinit var forumTitle: String
  @Selector("a[href~=.*/forum\\d+/].title", attr = "href", defValue = "0")
  lateinit var forumId: String
  @Selector("a[title~=Страница: \\d+]", attr = "title", format = "(\\d+)", defValue = "0")
  lateinit var totalPages: String
  @Selector("table tr:has(td.row4)")
  lateinit var topics: List<Topic>
}

class Topic {
  @Selector("a.subtitle") lateinit var title: String
  @Selector("a.subtitle", attr = "href") lateinit var link: String
  @Selector("td[class~=row(2|4)] > a") lateinit var author: String
  @Selector("td[class~=row(2|4)] > a", attr = "href") lateinit var authorLink: String
  @Selector("div.rating-short-value") lateinit var rating: String
  @Selector("td.row4:matchesOwn(\\d+)") lateinit var answers: String
  @Selector("span.desc", format = "([0-9\\.]+ - [0-9:]+)") lateinit var lastPostDate: String
  @Selector("span.desc a ~ a") lateinit var lastPostAuthor: String
}

fun main(args: Array<String>) {

  createRetrofit()
      .create(YapLoader::class.java)
      .loadForumPage(forumId = 2, startTopicNumber = 0, sortingMode = "last_post")

      .subscribe({ forumPage ->

        println("Title: ${forumPage.forumTitle}")
        println("Link: ${forumPage.forumId}")
        println("Total pages: ${forumPage.totalPages}")

        println(">>> TOPICS: ${forumPage.topics.size}")

        forumPage.topics.forEach {
          println("Title: ${it.title}")
          println("Link: ${it.link}")
          println("Author: ${it.author}")
          println("Author link: ${it.authorLink}")
          println("Rating: ${it.rating}")
          println("Answers: ${it.answers}")
          println("Last post date: ${it.lastPostDate}")
          println("Last post author: ${it.lastPostAuthor}")
          println(" --------- ")
        }

      }, { throwable ->
        println("Error: ${throwable.message}")
      })

}