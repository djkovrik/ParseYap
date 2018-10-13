import pl.droidsonroids.jspoon.annotation.Selector

class TopicPage {
  @Selector(value = "h1.subpage > a.subtitle", defValue = "Unknown")
  lateinit var topicTitle: String
  @Selector(value = "td.bottommenu > font", defValue = "")
  lateinit var isClosed: String
  @Selector(value = "input[name~=auth_key]", attr = "outerHtml", regex = "value=\"([a-z0-9]+)\"", defValue = "")
  lateinit var authKey: String
  @Selector(value = "div.rating-value", regex = "([-\\d]+)", defValue = "0")
  lateinit var topicRating: String
  @Selector(value = "div[rel=rating] img[src$=rating-cell-minus.gif]", attr = "src", defValue = "")
  lateinit var topicRatingPlusAvailable: String
  @Selector(value = "div[rel=rating] img[src$=rating-cell-plus.gif]", attr = "src", defValue = "")
  lateinit var topicRatingMinusAvailable: String
  @Selector(value = "div[rel=rating] img[src$=rating-cell-plus-clicked.gif]", attr = "src", defValue = "")
  lateinit var topicRatingPlusClicked: String
  @Selector(value = "div[rel=rating] img[src$=rating-cell-minus-clicked.gif]", attr = "src", defValue = "")
  lateinit var topicRatingMinusClicked: String
  @Selector(
      value = "div[rel=rating] a[onclick~=doRatePost]",
      regex = "(?<=\\d{2}, )(\\d+)((?=, ))",
      attr = "outerHtml",
      defValue = "0"
  )
  lateinit var topicRatingTargetId: String
  @Selector(value = "table.row3")
  lateinit var navigation: TopicNavigationPanel
  @Selector(value = "table[id~=p_row_\\d+]:has(.normalname)")
  lateinit var posts: List<TopicPost>
}

class TopicNavigationPanel {
  @Selector(value = "td[nowrap=nowrap]:has(a[onclick~=multi_page_jump])", regex = "\\[(\\d+)\\]", defValue = "1")
  lateinit var currentPage: String
  @Selector(value = "td[nowrap=nowrap]:has(a[onclick~=multi_page_jump])", regex = "(\\d+)", defValue = "1")
  lateinit var totalPages: String
}

class TopicPost {
  @Selector(value = ".normalname", defValue = "Unknown")
  lateinit var authorNickname: String
  @Selector(value = "a[title=Профиль]", attr = "href", defValue = "")
  lateinit var authorProfile: String
  @Selector(value = "a[title=Профиль] img", attr = "src", defValue = "//www.yaplakal.com/html/static/noavatar.gif")
  lateinit var authorAvatar: String
  @Selector(value = "div[align=left][style=padding-left:5px]", regex = "Сообщений: ([-\\d]+)", defValue = "0")
  lateinit var authorMessagesCount: String
  @Selector(value = "a.anchor", defValue = "")
  lateinit var postDate: String
  @Selector(value = "span[class~=rank-\\w+]", regex = "([-\\d]+)", defValue = "0")
  lateinit var postRank: String
  @Selector(value = "a.post-plus", attr = "innerHtml", defValue = "")
  lateinit var postRankPlusAvailable: String
  @Selector(value = "a.post-minus", attr = "innerHtml", defValue = "")
  lateinit var postRankMinusAvailable: String
  @Selector(value = "span.post-plus-clicked", attr = "innerHtml", defValue = "")
  lateinit var postRankPlusClicked: String
  @Selector(value = "span.post-minus-clicked", attr = "innerHtml", defValue = "")
  lateinit var postRankMinusClicked: String
  @Selector(value = "td[width*=100%][valign*=top]", attr = "innerHtml", defValue = "")
  lateinit var postContent: String
  @Selector(value = "a[name~=entry]", attr = "outerHtml", regex = "entry(\\d+)", defValue = "0")
  lateinit var postId: String
  @Selector(value = "a:containsOwn(цитировать)", attr = "href", defValue = "")
  lateinit var hasQuoteButton: String
  @Selector(value = "a:containsOwn(правка)", attr = "href", defValue = "")
  lateinit var hasEditButton: String
  @Selector("a.title:containsOwn(#)")
  var tags: List<TopicTag> = emptyList()
}

class TopicTag {
  @Selector("a.title:containsOwn(#)", defValue = "")
  lateinit var name: String
  @Selector("a.title:containsOwn(#)", attr = "href", defValue = "")
  lateinit var link: String
}

fun main(args: Array<String>) {

  createRetrofit()
      .create(YapLoader::class.java)
      .loadTopicPage(forumId = 28, topicId = 1851991, startPage = 0)
      .subscribe({ topicPage ->

        println("Title: ${topicPage.topicTitle}")
        println("Rank: ${topicPage.topicRating}")
        println("Is closed: ${topicPage.isClosed}")
        println("Auth key: ${topicPage.authKey}")

        println(">>> NAVIGATION:")
        println("Current page: ${topicPage.navigation.currentPage}")
        println("Total pages: ${topicPage.navigation.totalPages}")

        println(">>> POSTS: ${topicPage.posts.size}")
        println(" -------------------- ")

        topicPage.posts.forEach {
          println("Author nickname: ${it.authorNickname}")
          println("Author profile: ${it.authorProfile}")
          println("Author avatar: ${it.authorAvatar}")
          println("Author messages count: ${it.authorMessagesCount}")
          println("- Post date: ${it.postDate}")
          println("- Post rank: ${it.postRank}")
          println("- Post id: ${it.postId}")

          if (it.tags.isNotEmpty()) {
            it.tags.forEach {
              println("Tag: ${it.name}")
              println("Tag link: ${it.link}")
            }
          }

          ParsedPost(it.postContent).printContent()

          println(" ---------- ")
        }

      }, { throwable ->
        println("Error: ${throwable.message}")
      })

}