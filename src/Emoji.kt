import pl.droidsonroids.jspoon.annotation.Selector

class EmojiList {
  @Selector("tr:has(td.row1)")
  lateinit var list: List<Emoji>
}

class Emoji {
  @Selector("td.row1 > a", defValue = "")
  lateinit var code: String
  @Selector("td.row2 img", attr = "src", defValue = "")
  lateinit var link: String
}

fun main(args: Array<String>) {

  createRetrofit()
      .create(YapLoader::class.java)
      .loadEmoticons(
          act = "legends",
          code = "emoticons")
      .subscribe({ emojisList ->
        emojisList.list.forEach { emoji ->
          println("Code: ${emoji.code}, link: ${emoji.link}")
        }
      }, { throwable ->
        println("Error: ${throwable.message}")
      })
}
