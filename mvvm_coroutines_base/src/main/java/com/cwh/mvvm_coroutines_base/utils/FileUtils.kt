package com.cwh.mvvm_coroutines_base.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Description:
 * Date：2019/7/15-14:49
 * Author: cwh
 */
object FileUtils {

    /**
     * 图片转换为JPG
     */
    private fun convertImg2JPG(sourceFile: File, targetFile: File) {
        val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath)
        try {
            val bos = BufferedOutputStream(FileOutputStream(targetFile))
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                bos.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * 获取文件夹大小 (在线程中执行)
     *
     * @param file File实例(文件夹)
     * @return long
     */
    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (i in fileList.indices) {
                if (fileList[i].isDirectory) {
                    size += getFolderSize(fileList[i])

                } else {
                    size += fileList[i].length()

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //return size/1048576;
        return size
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    fun deleteFile(file: File) {
        if (file.isFile) {
            file.delete()
            return
        }
        if (file.isDirectory) {
            val childFile = file.listFiles()
            if (childFile == null || childFile.isEmpty()) {
                file.delete()
                return
            }
            for (f in childFile) {
                deleteFile(f)
            }
            file.delete()
        }
    }


    /**
     * uri to file
     */
    fun uri2File(uri: Uri, context: Context): File? {
        var path: String? = null
        if ("file" == uri.scheme) {
            path = uri.encodedPath
            if (path != null) {
                path = Uri.decode(path)
                val cr = context.contentResolver
                val buff = StringBuffer()
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'$path'").append(")")
                val cur = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA),
                    buff.toString(),
                    null,
                    null
                )
                var index = 0
                var dataIdx = 0
                cur!!.moveToFirst()
                while (!cur.isAfterLast) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID)
                    index = cur.getInt(index)
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    path = cur.getString(dataIdx)
                    cur.moveToNext()
                }
                cur.close()
                if (index == 0) {
                } else {
                    val u = Uri.parse("content://media/external/images/media/$index")
                    println("temp uri is :$u")
                }
            }
            if (path != null) {
                return File(path)
            }
        } else if ("content" == uri.scheme) {
            // 4.2.2以后
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(
                uri, proj,
                null, null, null
            )
            if (cursor!!.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = cursor.getString(columnIndex)
            }
            cursor.close()

            return File(path)
        } else {
            //Log.i(TAG, "Uri Scheme:" + uri.getScheme());
        }
        return null
    }


    //解决Android 7.0之后的Uri安全问题
    /**
     * get uri for file
     * 注意：在命名Provider时，
     * 要为authorities=${applicationId}.FileProvider 该方法才会起效
     *
     * 解决Android 7.0之后的Uri安全问题
     */
    fun getUriForFile(context: Context?, file: File?): Uri {
        if (context == null || file == null) {
            throw NullPointerException()
        }
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider
                .getUriForFile(
                    context.applicationContext,
                    "${context.applicationInfo.packageName}.FileProvider", file
                )
        } else {
            Uri.fromFile(file)
        }
        return uri
    }

    /**
     * File to uri.
     *  注意：在命名Provider时，
     * 要为authorities=${applicationId}.FileProvider 该方法才会起效
     * @param file The file.
     * @return uri
     */
    fun file2Uri(@NonNull file: File, context: Context): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = context.applicationInfo.packageName + ".FileProvider"
            FileProvider.getUriForFile(context, authority, file)
        } else {
            Uri.fromFile(file)
        }
    }
}