import pl.droidsonroids.jspoon.annotation.Selector

class Forums {
  @Selector("td.row4 > b > a.title") lateinit var titles: List<String>
  @Selector("td.row4 > b > a.title", attr = "href") lateinit var ids: List<String>
  @Selector("td.row2[nowrap=nowrap]") lateinit var topics: List<LastTopic>
}

class LastTopic {
  @Selector(".desc", attr = "innerHtml") lateinit var htmlDesc: String
  @Selector("a.subtitle") lateinit var title: String
  @Selector("a ~ a ~ a") lateinit var author: String
}

fun main(args: Array<String>) {

  createRetrofit()
      .create(YapLoader::class.java)
      .loadForumsList()
      .subscribe({ forums ->

        assert(forums.titles.size == forums.ids.size)
        assert(forums.titles.size == forums.topics.size)

        for (index in 0 until forums.titles.size) {
          println("Forum: ${forums.titles[index]}")
          println("Forum id: ${forums.ids[index]}")
          println("Last topic: ${forums.topics[index].title}")
          println("Author: ${forums.topics[index].author}")
        }

      }, { throwable ->
        println("Error: ${throwable.message}")
      })
}