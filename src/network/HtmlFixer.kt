package network

private const val HTML_WITH_LOADER = """
  <div align="center" id="vMDIwMjcwNjYt04f4" rel="yapfiles"><img src="//www.yaplakal.com/html/static/video-loader.gif" width="640" height="360"/>
"""

private const val SEARCH_REGEX = "<div align=.* id=[\"|'](.*)[\"|'] rel=[\"|']yapfiles[\"|'].*/>"
private const val REPLACING_TEXT = """
  <div align="center" id="$1" rel="yapfiles"><iframe src="//www.yapfiles.ru/get_player/?v=$1" width="640" height="360" frameborder="0" webkitallowfullscreen="" mozallowfullscreen="" allowfullscreen=""></iframe></div>
"""

fun String.fixYapHtml(): String = this.replace(Regex(SEARCH_REGEX), REPLACING_TEXT)

fun main(args: Array<String>) {
  println("Html: $HTML_WITH_LOADER")

  println("Replaced:")
  println(HTML_WITH_LOADER.replace(Regex(SEARCH_REGEX), REPLACING_TEXT))
}