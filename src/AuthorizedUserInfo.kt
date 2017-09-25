import pl.droidsonroids.jspoon.annotation.Selector

class AuthorizedUserInfo {
  @Selector("div.user-name > a", defValue = "Guest")
  lateinit var nickname: String
  @Selector("div.user-title", defValue = "")
  lateinit var title: String
  @Selector("span.user-rank", defValue = "")
  lateinit var uq: String
  @Selector("div[style~=float: left; padding: 10px] > a > img", attr = "src", defValue = "//www.yaplakal.com/html/avatars/noavatar.gif")
  lateinit var avatar: String
}

fun main(args: Array<String>) {

}