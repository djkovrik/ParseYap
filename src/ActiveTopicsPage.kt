import pl.droidsonroids.jspoon.annotation.Selector

class ActiveTopicsPage {
  @Selector("form[name=dateline]")
  lateinit var navigation: ActiveTopicsNavigationPanel
  @Selector("table tr:has(td.row4)")
  lateinit var topics: List<ActiveTopic>
}

class ActiveTopicsNavigationPanel {
  @Selector(".pagelinks", format = "\\[(\\d+)\\]", defValue = "0")
  lateinit var currentPage: String
  @Selector(".pagelinks", format = "(\\d+)", defValue = "0")
  lateinit var totalPages: String
}

class ActiveTopic {
  @Selector("a.subtitle", defValue = "Unknown") lateinit var title: String
  @Selector("a.subtitle", attr = "href") lateinit var link: String
  @Selector("img[src*=pinned]", attr = "src", defValue = "") lateinit var isPinned: String
  @Selector("img[src*=closed]", attr = "src", defValue = "") lateinit var isClosed: String
  @Selector("td[class~=row(2|4)] > a", defValue = "Unknown") lateinit var forumTitle: String
  @Selector("td[class~=row(2|4)] > a", attr = "href") lateinit var forumLink: String
  @Selector("div.rating-short-value", defValue = "0") lateinit var rating: String
  @Selector("td.row4:matchesOwn(\\d+)", defValue = "0") lateinit var answers: String
  @Selector("span.desc", format = "([0-9\\.]+ - [0-9:]+)", defValue = "Unknown") lateinit var lastPostDate: String
}

fun main(args: Array<String>) {

  // TODO() While using in app generate searchid for each session
  createRetrofit()
      .create(YapLoader::class.java)
      .loadActiveTopics(
          act = "Search",
          code = "getactive",
          searchid= "d2a280145c686e07575b47d9b5208a6e",
          startTopicNumber = 25)
      .subscribe({ loadedPage ->

        println(">>> NAVIGATION:")
        println("Current page: ${loadedPage.navigation.currentPage}")
        println("Total pages: ${loadedPage.navigation.totalPages}")

        println(">>> TOPICS: ${loadedPage.topics.size}\n")

        loadedPage.topics.forEach {
          println("Topic title: ${it.title}")
          println("Topic link: ${it.link}")
//          println("Is Pinned: ${it.isPinned}")
//          println("Is Cosed: ${it.isClosed}")
          println("Forum: ${it.forumTitle}")
          println("Forum link: ${it.forumLink}")
          println("Rating: ${it.rating}")
          println("Answers: ${it.answers}")
          println("Last post date: ${it.lastPostDate}")
          println(" --------- ")
        }

      }, { throwable ->
        println("Error: ${throwable.message}")
      })
}