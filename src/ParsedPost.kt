import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import java.util.*

class ParsedPost(html: String,
                 val content: MutableList<Content> = ArrayList(),
                 val images: MutableList<String> = ArrayList(),
                 val videos: MutableList<String> = ArrayList()) {

  companion object {
    val tagsToSkip = setOf("#root", "html", "head", "body", "table", "tbody", "tr", "br", "b", "i")
    val attrsToSkip = setOf("rating", "clear")
    val tagsWhiteList: Whitelist = Whitelist()
        .addTags("i", "u", "b", "br", "img")
        .addAttributes("img", "src")
  }

  init {
    val singlePost = Jsoup.parse(html)

    singlePost
        .allElements
        .filter { element -> !tagsToSkip.contains(element.tagName()) }
        .filter { element ->
          var notSkip = true
          attrsToSkip.forEach attrs@ { attribute ->
            if (element.attributes().toString().contains(attribute)) {
              notSkip = false
              return@attrs
            }
          }
          notSkip
        }
        .forEach { element ->

          // Texts +
          if (element.hasClass("postcolor") && element.text().isNotEmpty()) {
            element.select("div[rel=rating]").remove()
            element.select("span.edit").remove()
            element.select("span[style~=grey]").remove()
            element.select("img").not("[src*=emoticons]").remove()
            element.select("a").remove()
            content.add(PostText(text = element.html().cleanExtraTags().trimLinebreakTags()))
          }

          // Quotes +
          if (element.attributes().toString().contains("QUOTE") && !element.text().startsWith("Цитата")) {
            content.add(PostQuote(text = element.html().cleanExtraTags().trimLinebreakTags()))
          }

          // Quote authors +
          if (element.text().contains("@") && element.text().endsWith(")")) {
            content.add(PostQuoteAuthor(text = element.html()))
          }

          // Spoilers
          if (element.tagName() == "td" && element.attributes().toString().contains("SPOILER")) {
            content.add(PostHiddenText(text = element.html().cleanExtraTags().trimLinebreakTags()))
          }


          // Images +
          if (element.tagName() == "img"
              && element.hasAttr("src")
              && !element.attr("src").contains("emoticons")) {
            images.add(element.attr("src"))
          }

          // Videos +
          if (element.tagName() == "iframe" && element.hasAttr("src")) {
            videos.add(element.attr("src"))
          }

          // P.S. +
          if (element.attributes().toString().contains(Regex("edit|grey"))) {
            content.add(PostScript(
                text = element.html()))
          }

          // Links +
          if (element.tagName() == "a" && element.text().isNotEmpty()) {
            content.add(PostLink(
                url = element.attr("href"),
                title = element.text()))
          }
        }
  }

  fun printContent() {
    content.forEach {
      when (it) {
        is PostText -> println("Post text: ${it.text}")
        is PostQuote -> println("Quote block: ${it.text}")
        is PostQuoteAuthor -> println("Quote author block: ${it.text}")
        is PostHiddenText -> println("Spoiler text: ${it.text}")
        is PostScript -> println("P.S.: ${it.text}")
        is PostLink -> println("Link: ${it.url}, Link title: ${it.title}")
      }
    }

    images.forEach {
      println("Image: $it")
    }

    videos.forEach {
      println("Video: $it")
    }
  }

  private fun String.cleanExtraTags(): String {

    return Jsoup
        .clean(this, tagsWhiteList)
        // Replace html spaces
        .replace("&nbsp;", " ")
        // Replace extra <br>
        .replace(Regex("(<br>(\\s+)?\\R)+", RegexOption.MULTILINE), "")
        // Replace smile links with filename only
        .replace(Regex("<img src=.*/(\\w+).*>"), { matchResult ->
          val replacement = matchResult.groups[1]?.value ?: ""
          String.format("<img src='%s'>", replacement)
        })
  }

  private fun String.trimLinebreakTags(): String {
    return this.removePrefix("<br>").removeSuffix("<br>").trim()
  }
}

interface Content

class PostText(val text: String) : Content

class PostQuote(val text: String) : Content

class PostQuoteAuthor(val text: String) : Content

class PostHiddenText(val text: String) : Content

class PostScript(val text: String) : Content

class PostLink(val url: String, val title: String) : Content