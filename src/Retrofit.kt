import io.reactivex.Single
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import retrofit2.http.Path

fun createRetrofit(): Retrofit =
    Retrofit.Builder()
        .baseUrl("http://www.yaplakal.com/")
        .addConverterFactory(JspoonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()


interface YapLoader {
  @GET("/st/{startPage}/")
  fun loadNews(@Path("startPage") startPage: Int): Single<News>

  @GET("/forum")
  fun loadForumsList(): Single<Forums>
}