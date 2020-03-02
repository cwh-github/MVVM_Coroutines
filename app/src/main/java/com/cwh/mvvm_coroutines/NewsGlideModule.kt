package com.qs.scenic

import android.app.ActivityManager
import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * Description:
 * Date：2020/1/16 0016-14:14
 * Author: cwh
 */

@GlideModule
class NewsGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE)
                as ActivityManager
        val defaultRequestOptions = RequestOptions()
            .format(
                if (activityManager.isLowRamDevice) DecodeFormat.PREFER_RGB_565
                else DecodeFormat.PREFER_ARGB_8888
            )
            .disallowHardwareConfig()
        builder.setDefaultRequestOptions(defaultRequestOptions)
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}