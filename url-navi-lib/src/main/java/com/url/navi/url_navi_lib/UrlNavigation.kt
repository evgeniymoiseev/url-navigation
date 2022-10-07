package com.url.navi.url_navi_lib

import android.content.Context
import androidx.core.net.toUri
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Destination(private val context: Context) {
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun takeDestination(): String {
        val gd = takeAdbAsync().await()
        val dl = takeDL()
        return if (dl != null) {
            takeResult(dl, null, gd)
        } else {
            val af = takeAF()
            takeResult(null, af, gd)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun takeAdbAsync() = coroutineScope.async {
        AdvertisingIdClient.getAdvertisingIdInfo(context).id.toString()
    }

    private suspend fun takeDL() = suspendCoroutine { c ->
        AppLinkData.fetchDeferredAppLinkData(context) { d ->
            c.resume(d?.targetUri?.toString())
        }
    }

    private suspend fun takeAF() = suspendCoroutine { c ->
        AppsFlyerLib.getInstance().init(
            "5YqmhTxtnmsdZFWxqDhrP7",
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    c.resume(p0)
                }

                override fun onConversionDataFail(p0: String?) {
                    c.resume(null)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                }

                override fun onAttributionFailure(p0: String?) {
                }

            },
            context
        )
        AppsFlyerLib.getInstance().start(context)
    }

    private fun takeResult(dl: String?, af: MutableMap<String, Any>?, gd: String): String {
        OneSignal.initWithContext(context)
        OneSignal.setAppId("70579916-b43f-4743-aafe-a76173f5158d")
        OneSignal.setExternalUserId(gd)
        when {
            af?.get("campaign").toString() == "null" && dl == null -> {
                OneSignal.sendTag("key2", "organic")
            }
            dl != null -> {
                OneSignal.sendTag(
                    "key2",
                    dl.replace("myapp://", "").substringBefore("/")
                )
            }
            af?.get("campaign").toString() != "null" -> {
                OneSignal.sendTag(
                    "key2",
                    af?.get("campaign").toString().substringBefore("_")
                )
            }
        }
        return "https://richgame.site/abc.php".toUri().buildUpon()
            .apply {
                appendQueryParameter("fHhSwyFKwD", "aIEmXIfhTB")
                appendQueryParameter("YUr3oy9PTl", TimeZone.getDefault().id)
                appendQueryParameter("5MfZ1STrID", gd)
                appendQueryParameter("nf1Atfp2AW", dl.toString())
                appendQueryParameter(
                    "pyykgJQXVJ",
                    if (dl != null) "deeplink" else af?.get("media_source").toString()
                )
                appendQueryParameter(
                    "ulkCU2D86k",
                    if (af != null)
                        AppsFlyerLib.getInstance().getAppsFlyerUID(context)
                    else "null"
                )
                appendQueryParameter("tIngjlq48v", af?.get("adset_id").toString())
                appendQueryParameter("bGRsvnFDd3", af?.get("campaign_id").toString())
                appendQueryParameter("auWy8bmVQQ", af?.get("campaign").toString())
                appendQueryParameter("0zsuI64RpY", af?.get("adset").toString())
                appendQueryParameter("jIPCRgZB6n", af?.get("adgroup").toString())
                appendQueryParameter("Ep8bd9U3x1", af?.get("orig_cost").toString())
                appendQueryParameter("DkzMzSOnwc", af?.get("af_siteid").toString())
            }.toString()
    }
}