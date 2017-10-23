import pl.droidsonroids.jspoon.annotation.Selector

class ForumPage {
  @Selector("a[href~=.*/forum\\d+/].title", defValue = "Unknown")
  lateinit var forumTitle: String
  @Selector("a[href~=.*/forum\\d+/].title", attr = "href", defValue = "0")
  lateinit var forumId: String
  @Selector("table[width=100%]")
  lateinit var navigation: ForumNavigationPanel
  @Selector("table tr:has(td.row4)")
  lateinit var topics: List<Topic>
}

class ForumNavigationPanel {
  @Selector("td[nowrap=nowrap]", format = "\\[(\\d+)\\]", defValue = "0")
  lateinit var currentPage: String
  @Selector("td[nowrap=nowrap]", format = "(\\d+)", defValue = "0")
  lateinit var totalPages: String
}

class Topic {
  @Selector("a.subtitle", defValue = "Unknown") lateinit var title: String
  @Selector("a.subtitle", attr = "href") lateinit var link: String
  @Selector("img[src*=pinned]", attr="src", defValue = "") lateinit var isPinned: String
  @Selector("td[class~=row(2|4)] > a", defValue = "Unknown") lateinit var author: String
  @Selector("td[class~=row(2|4)] > a", attr = "href") lateinit var authorLink: String
  @Selector("div.rating-short-value", defValue = "0") lateinit var rating: String
  @Selector("td.row4:matchesOwn(\\d+)", defValue = "0") lateinit var answers: String
  @Selector("span.desc", format = "([0-9\\.]+ - [0-9:]+)", defValue = "Unknown") lateinit var lastPostDate: String
  @Selector("span.desc a ~ a", defValue = "Unknown") lateinit var lastPostAuthor: String
}

fun main(args: Array<String>) {

  createRetrofit()
      .create(YapLoader::class.java)
      .loadForumPage(forumId = 2, startTopicNumber = 300, sortingMode = "last_post")
      .subscribe({ forumPage ->

        println("Title: ${forumPage.forumTitle}")
        println("Link: ${forumPage.forumId}")

        println(">>> NAVIGATION:")
        println("Current page: ${forumPage.navigation.currentPage}")
        println("Total pages: ${forumPage.navigation.totalPages}")

        println(">>> TOPICS: ${forumPage.topics.size}")

        forumPage.topics.forEach {
          println("Title: ${it.title}")
          println("Link: ${it.link}")
          println("Is Pinned: ${it.isPinned}")
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