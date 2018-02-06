import pl.droidsonroids.jspoon.annotation.Selector

class SearchTopicsPage {
    @Selector(value = "a:matchesOwn(\\d+)", defValue = "")
    lateinit var hasNextPage: String
    @Selector(value = "a[href~=searchid]", attr = "href", format = "searchid=([\\d\\w]+)", defValue = "")
    lateinit var searchId: String
    @Selector("table tr:has(td.row4)")
    lateinit var topics: List<SearchTopicItem>
}

fun main(args: Array<String>) {

    createRetrofit()
            .create(YapLoader::class.java)
            .loadSearchedTopics(
                    act = "Search",
                    code = "01",
                    forums = "all",
                    keywords = "тест",
                    prune = 0,
                    search_how = "any",
                    search_in = "titles",
                    searchsubs = 1,
                    sort_by = "rel"
            )
            .subscribe({ loadedPage ->

                println("Has next page: ${loadedPage.hasNextPage}")
                println("SearchId: ${loadedPage.searchId}")

                println(">>> TOPICS: ${loadedPage.topics.size}\n")

                loadedPage.topics.forEach {
                    println("Topic title: ${it.title}")
                    println("Topic link: ${it.link}")
                    // println("Is Pinned: ${it.isPinned}")
                    // println("Is Cosed: ${it.isClosed}")
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
