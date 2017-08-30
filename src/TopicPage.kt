import pl.droidsonroids.jspoon.annotation.Selector

class TopicPage {
  @Selector("h1.subpage > a.subtitle", defValue = "Unknown")
  lateinit var topicTitle: String
  @Selector("h1.subpage > a.subtitle", attr = "href", defValue = "Unknown")
  lateinit var topicLink: String
  @Selector(".rating-value", defValue = "0")
  lateinit var topicRank: String
  @Selector("td[nowrap=nowrap]:has(a[onclick])", format = "(\\d+)", defValue = "0")
  lateinit var totalPages: String
  @Selector("table[id~=p_row_\\d+]:has(.normalname)")
  lateinit var posts: List<TopicPost>
}

class TopicPost {
  @Selector(".normalname", defValue = "Unknown")
  lateinit var authorNickname: String
  @Selector("a[title=Профиль]", attr = "href", defValue = "")
  lateinit var authorProfile: String
  @Selector("a[title=Профиль] img", attr = "src", defValue = "//www.yaplakal.com/html/static/noavatar.gif")
  lateinit var authorAvatar: String
  @Selector("div[align=left][style=padding-left:5px]", format = "(Сообщений: \\d+)", defValue = "0")
  lateinit var authorMessagesCount: String
  @Selector("a.anchor", defValue = "")
  lateinit var postDate: String
  @Selector("span[class~=rank-\\w+]", defValue = "")
  lateinit var postRank: String
  @Selector("td[width*=100%][valign*=top]", attr = "innerHtml", defValue = "")
  lateinit var postContent: String
}

fun main(args: Array<String>) {

  createRetrofit()
      .create(YapLoader::class.java)
      .loadTopicPage(forumId = 1, topicId = 1644075, startPage = 125)
      .subscribe({ topicPage ->

        println("Title: ${topicPage.topicTitle}")
        println("Link: ${topicPage.topicLink}")
        println("Rank: ${topicPage.topicRank}")
        println("Total pages: ${topicPage.totalPages}")

        println(">>> POSTS: ${topicPage.posts.size}")
        println(" -------------------- ")

        topicPage.posts.forEach {
          println("Author nickname: ${it.authorNickname}")
          println("Author profile: ${it.authorProfile}")
          println("Author avatar: ${it.authorAvatar}")
          println("Author messages count: ${it.authorMessagesCount}")
          println("Post date: ${it.postDate}")
          println("Post rank: ${it.postRank}")

          ParsedPost(it.postContent).printContent()

          println(" ---------- ")
        }

      }, { throwable ->
        println("Error: ${throwable.message}")
      })

}