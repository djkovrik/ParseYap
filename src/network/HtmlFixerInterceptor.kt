package network

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody

class HtmlFixerInterceptor : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val originalResponse = chain.proceed(chain.request())
    val originalBodyString = originalResponse.body()?.string()
    val originalContentType = originalResponse.body()?.contentType()
    val newBodyString = originalBodyString?.fixYapHtml()
    return originalResponse.newBuilder().body(ResponseBody.create(originalContentType, newBodyString)).build()
  }
}
