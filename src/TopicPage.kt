import pl.droidsonroids.jspoon.annotation.Selector

class TopicPage {
  @Selector("h1.subpage > a.subtitle", defValue = "Unknown")
  lateinit var topicTitle: String
  @Selector("td.bottommenu > font", defValue = "")
  lateinit var isClosed: String
  @Selector("input[name~=auth_key]", attr = "outerHtml", format = "value=\"([a-z0-9]+)\"", defValue = "")
  lateinit var authKey: String
  @Selector("div.rating-value", defValue = "")
  lateinit var topicRating: String
  @Selector("div[rel=rating] img[src$=rating-cell-minus.gif]", attr = "src", defValue = "")
  lateinit var topicRatingPlusAvailable: String
  @Selector("div[rel=rating] img[src$=rating-cell-plus.gif]", attr = "src", defValue = "")
  lateinit var topicRatingMinusAvailable: String
  @Selector("div[rel=rating] img[src$=rating-cell-plus-clicked.gif]", attr = "src", defValue = "")
  lateinit var topicRatingPlusClicked: String
  @Selector("div[rel=rating] img[src$=rating-cell-minus-clicked.gif]", attr = "src", defValue = "")
  lateinit var topicRatingMinusClicked: String
  @Selector(
      value = "div[rel=rating] a[onclick~=doRatePost]",
      format = "(?<=\\d{2}, )(\\d+)((?=, ))",
      attr = "outerHtml",
      defValue = "")
  lateinit var topicRatingTargetId: String
  @Selector("table.row3")
  lateinit var navigation: TopicNavigationPanel
  @Selector("table[id~=p_row_\\d+]:has(.normalname)")
  lateinit var posts: List<TopicPost>
}

class TopicNavigationPanel {
  @Selector("td[nowrap=nowrap]:has(a[onclick~=multi_page_jump])", format = "\\[(\\d+)\\]", defValue = "1")
  lateinit var currentPage: String
  @Selector("td[nowrap=nowrap]:has(a[onclick~=multi_page_jump])", format = "(\\d+)", defValue = "1")
  lateinit var totalPages: String
}

class TopicPost {
  @Selector(".normalname", defValue = "Unknown")
  lateinit var authorNickname: String
  @Selector("a[title=Профиль]", attr = "href", defValue = "")
  lateinit var authorProfile: String
  @Selector("a[title=Профиль] img", attr = "src", defValue = "//www.yaplakal.com/html/static/noavatar.gif")
  lateinit var authorAvatar: String
  @Selector("div[align=left][style=padding-left:5px]", format = "Сообщений: (\\d+)", defValue = "0")
  lateinit var authorMessagesCount: String
  @Selector("a.anchor", defValue = "")
  lateinit var postDate: String
  @Selector("span[class~=rank-\\w+]", defValue = "")
  lateinit var postRank: String
  @Selector("a.post-plus", attr = "innerHtml", defValue = "")
  lateinit var postRankPlusAvailable: String
  @Selector("a.post-minus", attr = "innerHtml", defValue = "")
  lateinit var postRankMinusAvailable: String
  @Selector("span.post-plus-clicked", attr = "innerHtml", defValue = "")
  lateinit var postRankPlusClicked: String
  @Selector("span.post-minus-clicked", attr = "innerHtml", defValue = "")
  lateinit var postRankMinusClicked: String
  @Selector("td[width*=100%][valign*=top]", attr = "innerHtml", defValue = "")
  lateinit var postContent: String
  @Selector("a[name~=entry]", attr = "outerHtml", format = "entry(\\d+)", defValue = "0")
  lateinit var postId: String
  @Selector("a.title:containsOwn(#)")
  var tags: List<TopicTag> = emptyList()
}

class TopicTag {
  @Selector("a.title:containsOwn(#)")
  lateinit var tag: String
  @Selector("a.title:containsOwn(#)", attr = "href")
  lateinit var tagLink: String
}

fun main(args: Array<String>) {

  createRetrofit()
      .create(YapLoader::class.java)
      .loadTopicPage(forumId = 2, topicId = 1788232, startPage = 0)
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
              println("Tag: ${it.tag}")
              println("Tag link: ${it.tagLink}")
            }
          }

          ParsedPost(it.postContent).printContent()

          println(" ---------- ")
        }

      }, { throwable ->
        println("Error: ${throwable.message}")
      })

}