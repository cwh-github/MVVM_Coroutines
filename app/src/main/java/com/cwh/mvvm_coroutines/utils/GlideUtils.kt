package com.cwh.mvvm_coroutines.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.graphics.createBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.request.target.Target
import com.cwh.mvvm_coroutines.R
import com.cwh.mvvm_coroutines_base.utils.DisplayUtils
import com.qs.scenic.GlideApp
import java.io.File


/**
 * Description:
 * Date：2020/1/16 0016-14:21
 * Author: cwh
 */

class GlideUtils {

    companion object {

        /**
         * 默认加载图片方式
         *
         * @param context
         * @param url 图片对应的链接
         * @param holder 加载图片时的占位图
         * @param errHolder 加载出错时的占位图
         * @param mImg
         */
        fun loadImage(
            context: Context, url: String, @DrawableRes holder: Int = R.drawable.default_holder,
            @DrawableRes errHolder: Int = R.drawable.default_holder,
            mImg: ImageView
        ) {
            GlideApp.with(context)
                .load(url)
                .placeholder(holder)
                .error(errHolder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(false)
                .into(mImg)
        }


        /**
         * 加载圆形图片
         */
        fun loadCircleImage(
            context: Context, url: String, @DrawableRes holder: Int = R.drawable.circle_white,
            @DrawableRes errHolder: Int = R.drawable.circle_white,
            mImg: ImageView
        ) {

            GlideApp.with(context)
                .load(url)
                .placeholder(holder)
                .error(errHolder)
                .centerCrop()
                .apply(bitmapTransform(CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(false)
                .into(mImg)
        }

        /**
         * 加载圆形图片
         */
        fun loadCircleImage(
            context: Context, uri: Uri, @DrawableRes holder: Int = R.drawable.circle_white,
            @DrawableRes errHolder: Int = R.drawable.circle_white,
            mImg: ImageView
        ) {

            GlideApp.with(context)
                .load(uri)
                .placeholder(holder)
                .error(errHolder)
                .centerCrop()
                .apply(bitmapTransform(CircleCrop()))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(false)
                .into(mImg)
        }

        /**
         * 加载圆角图片
         * @param radius 圆角半径
         * 圆角会与centerCrop有冲突  https://blog.csdn.net/tinggu/article/details/83620513
         */
        fun loadRoundImage(
            context: Context, url: String, @DrawableRes holder: Int = R.drawable.default_holder,
            @DrawableRes errHolder: Int = R.drawable.default_holder,
            radius: Int= DisplayUtils.dip2px(context,4f),
            mImg: ImageView
        ) {

            GlideApp.with(context)
                .load(url)
                .placeholder(holder)
                .error(errHolder)
                .apply(RequestOptions().transform(CenterCrop(),RoundedCorners(radius)))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(false)
                .into(mImg)

        }

        /**
         * 加载圆角图片
         * @param radius 圆角半径
         * 圆角会与centerCrop有冲突  https://blog.csdn.net/tinggu/article/details/83620513
         */
        fun loadRoundImage(
            context: Context, url: Uri, @DrawableRes holder: Int = R.drawable.default_holder,
            @DrawableRes errHolder: Int = R.drawable.default_holder,
            radius: Int=DisplayUtils.dip2px(context,4f),
            mImg: ImageView
        ) {

            GlideApp.with(context)
                .load(url)
                .placeholder(holder)
                .error(errHolder)
                .apply(RequestOptions().transform(CenterCrop(),RoundedCorners(radius)))
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(false)
                .into(mImg)

        }

        /**
         * 加载图片，并添加回调
         */
        fun loadImageWithListener(
            context: Context, url: String, @DrawableRes holder: Int = R.drawable.default_holder,
            @DrawableRes errHolder: Int = R.drawable.default_holder,
            mImg: ImageView, onLoadFail: (GlideException?) -> Boolean = {
                true
            },
            onLoadSuccess: (Drawable?) -> Boolean
        ) {
            GlideApp.with(context)
                .load(url)
                .placeholder(holder)
                .error(errHolder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(false)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return onLoadFail(e)
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return onLoadSuccess(resource)
                    }

                })
                .into(mImg)
        }

        /**
         * 现在图片(需要在子线程中调用)
         */
        fun downFile(context:Context,url:String): File {
            return Glide.with(context)
                .asFile()
                .load(url)
                .submit(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL)
                .get()
        }

    }


}