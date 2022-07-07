package com.iodaniel.abbblinddating.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object ImageCompressor {
    fun compressImage(resId: Int, context: Context): Bitmap? {
        val contentResolver = context.contentResolver
        val bmm = BitmapFactory.decodeResource(context.resources, resId)
        val inh = (bmm.height * (512.0 / bmm.width)).toInt()
        return Bitmap.createScaledBitmap(bmm, 512, inh, true)
    }
    fun compressImage(dataUri: Uri, context: Context, bm: Bitmap? = null): Pair<ByteArrayInputStream, ByteArray> {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(dataUri)
        val bmm = bm ?: BitmapFactory.decodeStream(inputStream)
        val inh = (bmm.height * (512.0 / bmm.width)).toInt()
        val bitmap = Bitmap.createScaledBitmap(bmm, 512, inh, true)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return ByteArrayInputStream(stream.toByteArray()) to stream.toByteArray()
    }
    fun compressImage(context: Context, bmm: Bitmap): Pair<ByteArrayInputStream, ByteArray> {
        val inh = (bmm.height * (512.0 / bmm.width)).toInt()
        val bitmap = Bitmap.createScaledBitmap(bmm, 512, inh, true)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return ByteArrayInputStream(stream.toByteArray()) to stream.toByteArray()
    }
    fun compressImage(dataUri: Uri, context: Context): ByteArrayInputStream {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(dataUri)
        val bmm = BitmapFactory.decodeStream(inputStream)
        val inh = (bmm.height * (512.0 / bmm.width)).toInt()
        val bitmap = Bitmap.createScaledBitmap(bmm, 512, inh, true)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return ByteArrayInputStream(stream.toByteArray())
    }
}