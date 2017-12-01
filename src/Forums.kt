import pl.droidsonroids.jspoon.annotation.Selector

class Forums {
  @Selector("td.row4 > b > a.title") lateinit var titles: List<String>
  @Selector("td.row4 > b > a.title", attr = "href", format = "/forum(\\d+)/") lateinit var ids: List<String>
  @Selector("td.row2[nowrap=nowrap]") lateinit var topics: List<LastTopic>
}

class LastTopic {
  @Selector("a.subtitle", defValue = "Unknown") lateinit var title: String
  @Selector("a[href~=members]", defValue = "Unknown") lateinit var author: String
  @Selector(".desc", format = "([0-9\\.]+ - [0-9:]+)", defValue = "Unknown") lateinit var date: String
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
          println("Date: ${forums.topics[index].date}")
        }

      }, { throwable ->
        println("Error: ${throwable.message}")
      })
}