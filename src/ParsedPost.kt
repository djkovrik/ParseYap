import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist
import java.util.*

class ParsedPost(html: String,
                 val content: MutableList<Content> = ArrayList(),
                 val images: MutableList<String> = ArrayList(),
                 val videos: MutableList<String> = ArrayList(),
                 val videosRaw: MutableList<String> = ArrayList()) {

  companion object {
    private const val POST_TAG = "div"
    private const val POST_TEXT_CLASS = "postcolor"
    private const val RATING_SELECTOR = "div[rel=rating]"
    private const val EDITED_TIME_SELECTOR = "span.edit"
    private const val CLIENT_SELECTOR = "span[style~=grey]"
    private const val EMOTICON_SRC_SELECTOR = "[src*=emoticons]"
    private const val EMOTICON_SELECTOR = "emoticons"
    private const val WARNING_IMG_SELECTOR = "html/bot"
    private const val WARNING_HEADER_SELECTOR = "td[align=center][vAlign=top]"
    private const val WARNING_TEXT_SELECTOR = "td[vAlign=top]"
    private const val QUOTE_SELECTOR = "QUOTE"
    private const val SPOILER_SELECTOR = "SPOILER"
    private const val QUOTE_START_TEXT = "Цитата"
    private const val IFRAME_TAG = "iframe"
    private const val IMG_TAG = "img"
    private const val IMAGE_LOADER_SELECTOR = "yaplakal.com/html/static/video-loader.gif"
    private const val TD_TAG = "td"
    private const val SRC_ATTR = "src"
    private const val QUOTE_AUTHOR_MARKER_REGEX = " @ \\d{1,2}\\.\\d{2}.\\d{4}"
    private const val QUOTE_MARKER = "Цитата"
    private const val POST_EDIT_MARKER = "edit"
    private const val POST_TAGS_BLOCK_SELECTOR = "div.topic-tags"

    private val tagsToSkip =
        setOf("#root", "html", "head", "body", "table", "tbody", "tr", "br", "b", "i", "u")
    private val attrsToSkip = setOf("rating", "clear")
    private val contentWhitelist: Whitelist = Whitelist()
        .addTags("i", "u", "b", "br", "img", "a", "li", "ul")
        .addAttributes("img", "src")
        .addAttributes("a", "href")
  }

  init {
    val singlePost = Jsoup.parse(html)

    singlePost
        .allElements
        .filter { element -> !tagsToSkip.contains(element.tagName()) }
        .filter { element ->
          var notSkip = true
          attrsToSkip.forEach attrs@{ attribute ->
            if (element.attributes().toString().contains(attribute)) {
              notSkip = false
              return@attrs
            }
          }
          notSkip
        }
        .forEach { element ->
          // Texts
          if (element.tagName() == POST_TAG && element.className() == POST_TEXT_CLASS) {
            element.select(RATING_SELECTOR).remove()
            element.select(EDITED_TIME_SELECTOR).remove()
            element.select(CLIENT_SELECTOR).remove()
            element.select(WARNING_HEADER_SELECTOR).remove()
            element.select(WARNING_TEXT_SELECTOR).remove()
            element.select(POST_TAGS_BLOCK_SELECTOR).remove()
            element.select(IMG_TAG).not(EMOTICON_SRC_SELECTOR).remove()

            element.html().formatPostHtmlCode().trimLinebreakTags().apply {
              if (this.isNotEmpty())
                content.add(PostText(text = this))
            }
          }

          // Quotes
          if (element.attributes().toString().contains(QUOTE_SELECTOR) && !element.text().contains(
                  QUOTE_START_TEXT
              )
          ) {
            element.html().formatPostHtmlCode().trimLinebreakTags().apply {
              if (this.isNotEmpty())
                content.add(PostQuote(text = this))
            }
          }

          // Quote authors
          if (element.text().contains(Regex(QUOTE_AUTHOR_MARKER_REGEX)) && !element.html().contains(Regex("[\\r\\n]+"))) {
            content.add(PostQuoteAuthor(text = element.html()))
          } else if (element.text() == QUOTE_MARKER) {
            content.add(PostQuoteAuthor(text = element.html()))
          }

          // Spoilers
          if (element.tagName() == TD_TAG && element.attributes().toString().contains(SPOILER_SELECTOR)) {
            element.html().formatPostHtmlCode().trimLinebreakTags().apply {
              if (this.isNotEmpty())
                content.add(PostHiddenText(text = this))
            }
          }

          // Images
          @Suppress("ComplexCondition")
          if (element.tagName() == IMG_TAG &&
              element.hasAttr(SRC_ATTR) &&
              !element.attr(SRC_ATTR).contains(EMOTICON_SELECTOR) &&
              !element.attr(SRC_ATTR).contains(IMAGE_LOADER_SELECTOR) &&
              !element.attr(SRC_ATTR).contains(WARNING_IMG_SELECTOR)
          ) {
            images.add(element.attr(SRC_ATTR))
          }

          // Videos
          if (element.tagName() == IFRAME_TAG && element.hasAttr(SRC_ATTR)) {
            videos.add(element.attr(SRC_ATTR))
            videosRaw.add(element.toString().replace("&amp;", "&"))
          }

          // P.S.
          if (element.attributes().toString().contains(POST_EDIT_MARKER)) {
            content.add(PostScript(text = element.html()))
          }

          // Warnings
          if (element.tagName() == TD_TAG && element.className() == POST_TEXT_CLASS) {
            content.add(PostWarning(element.html()))
          }
        }
  }

  fun printContent() {
    content.forEach {
      when (it) {
        is PostText -> println(" > POST TEXT: ${it.text}")
        is PostQuote -> println(" > QUOTE BLOCK: ${it.text}")
        is PostQuoteAuthor -> println(" > QUOTE AUTHOR BLOCK: ${it.text}")
        is PostHiddenText -> println(" > SPOILER: ${it.text}")
        is PostScript -> println(" > P.S.: ${it.text}")
        is PostWarning -> println(" > WARNING: ${it.text}")
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
        .clean(this, contentWhitelist)
        // Replace html spaces
        .replace("&nbsp;", " ")
        // Replace extra <br>
        .replace(Regex("(<br>(\\s+)?\\R)+", RegexOption.MULTILINE), "<br>")
  }

  private fun String.trimLinebreakTags(): String {
    return this.removePrefix("<br>").removeSuffix("<br>").trim()
  }

  private fun String.formatPostHtmlCode(): String =
      Jsoup
          .clean(this, contentWhitelist)
          .replace("&nbsp;", " ")
          .replace("/go/?http", "http")
          .replace("%21", "!")
          .replace("%23", "#")
          .replace("%24", "$")
          .replace("%25", "%")
          .replace("%26", "&")
          .replace("%2B", "+")
          .replace("%2C", ",")
          .replace("%2D", "-")
          .replace("%2E", ".")
          .replace("%2F", "/")
          .replace("%3A", ":")
          .replace("%3B", ";")
          .replace("%3D", "=")
          .replace("%3F", "?")
}

interface Content

class PostText(val text: String) : Content

class PostQuote(val text: String) : Content

class PostQuoteAuthor(val text: String) : Content

class PostHiddenText(val text: String) : Content

class PostScript(val text: String) : Content

class PostWarning(val text: String) : Content
