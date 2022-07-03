package com.example.androidwebviewcookiesync

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.browser.customtabs.CustomTabsIntent
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Cookie
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

object URL {
    const val localWebCookieSetter = "http://10.0.2.2/webview-cookie-set.php"
    const val localWebServer = "http://10.0.2.2/webview-cookie-read.php"
    const val localApiServer = "http://10.0.2.2/api-cookie-set.php"
}

class MainActivity : AppCompatActivity() {

    private val webviewCookierHandler = WebviewCookieHandler()
    private val okHttpClient = OkHttpClient().newBuilder().cookieJar(webviewCookierHandler).build()

    private var disposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_cookie_set_api).setOnClickListener {
            disposable = fetchNetwork.subscribe({ readFromCookieStore(URL.localApiServer,
                "api-cookie-set-key") },{
                    e -> Log.d("Tracking", "Ops! Error fetch API ${e.message}")
            })
        }

        findViewById<Button>(R.id.btn_cookie_set_app).setOnClickListener {
            val cookieBuilder = Cookie.Builder()
            val native_key = "native-cookie-set-key"
            cookieBuilder.name(native_key)
            cookieBuilder.value("this-is-set-by-native")
            cookieBuilder.domain("10.0.2.2")
            val cookie = cookieBuilder.build()
            val httpUrlBuilder = HttpUrl.Builder()
            httpUrlBuilder.host("10.0.2.2")
            httpUrlBuilder.scheme("http")
            val httpUrl = httpUrlBuilder.build()
            webviewCookierHandler.saveCookies(httpUrl, listOf(cookie))

            readFromCookieStore(httpUrl.toUrl().toString(), native_key)
        }

        findViewById<Button>(R.id.btn_cookie_set_webview).setOnClickListener {
            val intent = Intent(this, WebviewActivity::class.java)
            intent.putExtra(WebviewActivity.URL, URL.localWebCookieSetter);
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_webview_open).setOnClickListener {
            val intent = Intent(this, WebviewActivity::class.java)
            intent.putExtra(WebviewActivity.URL, URL.localWebServer);
            startActivity(intent)
        }

        findViewById<Button>(R.id.btn_chrome_tab_open).setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(this, Uri.parse(URL.localWebServer))
        }

        findViewById<Button>(R.id.btn_cookies_clear).setOnClickListener {
            webviewCookierHandler.clearCookie()
        }
    }

    private fun readFromCookieStore(url: String, cookieName: String) {
        val cookie = webviewCookierHandler.getCookie(url, cookieName)
        Log.d("Tracking", "Cookie set ${cookie.toString()}")
    }

    override fun onResume() {
        super.onResume()
        // This is required to persist the cookie that was set by the webview
        webviewCookierHandler.persistCookie()
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        disposable = null
    }

    private val fetchNetwork = Completable.fromCallable {
        val request = Request.Builder().url(URL.localApiServer).build()
        val call = okHttpClient.newCall(request)
        val response = call.execute()

        if (!response.isSuccessful) {
            throw IllegalStateException("Network fetch failure")
        }
    }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
}