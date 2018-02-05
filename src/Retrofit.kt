import io.reactivex.Single
import pl.droidsonroids.retrofit2.JspoonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

fun createRetrofit(): Retrofit =
        Retrofit.Builder()
                .baseUrl("http://www.yaplakal.com/")
                .addConverterFactory(JspoonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

fun createRetrofitForIncubator(): Retrofit =
        Retrofit.Builder()
                .baseUrl("http://inkubator.yaplakal.com/")
                .addConverterFactory(JspoonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

interface YapLoader {
    @GET("/st/{startPage}/")
    fun loadNews(@Path("startPage") startPage: Int): Single<News>

    @GET("/st/{startPage}/")
    fun loadIncubator(@Path("startPage") startPage: Int): Single<IncubatorPage>

    @GET("/forum")
    fun loadForumsList(): Single<Forums>

    @GET("/forum{forumId}/st/{startFrom}/100/Z-A/{sortingMode}")
    fun loadForumPage(
            @Path("forumId") forumId: Int,
            @Path("startFrom") startTopicNumber: Int,
            @Path("sortingMode") sortingMode: String): Single<ForumPage>

    @GET("/forum{forumId}/st/{startPage}/topic{topicId}.html")
    fun loadTopicPage(
            @Path("forumId") forumId: Int,
            @Path("topicId") topicId: Int,
            @Path("startPage") startPage: Int): Single<TopicPage>

    @GET("/")
    fun loadActiveTopics(
            @Query("act") act: String,
            @Query("CODE") code: String,
            @Query("searchid") searchid: String,
            @Query("st") startTopicNumber: Int): Single<ActiveTopicsPage>

    @GET("/")
    fun loadEmoticons(
            @Query("act") act: String,
            @Query("CODE") code: String
    ): Single<EmojiList>

    @POST("/")
    @Multipart
    fun loadSearchedTopics(
            @Query("act") act: String,
            @Query("CODE") code: String,
            @Part("forums[]") forums: String,
            @Part("keywords") keywords: String,
            @Part("prune") prune: Int,
            @Part("search_how") search_how: String,
            @Part("search_in") search_in: String,
            @Part("searchsubs") searchsubs: Int,
            @Part("sort_by") sort_by: String
    ): Single<SearchTopicsPage>
}
