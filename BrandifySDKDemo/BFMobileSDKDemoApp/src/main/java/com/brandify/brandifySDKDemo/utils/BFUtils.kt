package com.brandify.brandifySDKDemo.utils

import android.content.Context
import android.graphics.*
import android.location.Location
import android.util.Log
import com.brandify.BrandifyMobileSDK.models.BFLocation
import com.brandify.BrandifyMobileSDK.models.BFRunnable
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException

class BFUtils {
    private fun isInFavoritesList(ctx: Context, searchString: String?): Boolean {
        val favorites = getFavoritesList(ctx)
        var isFavorite = false
        val strArray = favorites.split(",").toTypedArray()
        for (s in strArray) {
            if (s.equals(searchString, ignoreCase = true)) {
                isFavorite = true
                break
            }
            break
        }
        return isFavorite
    }

    fun addToFavoritesList(ctx: Context, location: BFLocation?, completionBlock: BFRunnable<BFLocation?>) {
        if (location != null) {
            var favorite = getFavoritesList(ctx)
            //get value
            val clientKey = location.clientKey
            val didSave: Boolean
            if (!isInFavoritesList(ctx, clientKey)) {
                if (favorite.isNotEmpty()) {
                    favorite += ",$clientKey"
                } else {
                    favorite = clientKey
                }
                Log.d("BFUtils.addToFavs", " new: $favorite")

                //write to disk
                didSave = writeToDisk(ctx, favorite)
            } else {
                Log.d("BFUtils.addToFavs", "Already in disk")
                didSave = true
            }
            if (didSave) {
                Log.d("BFUtils.addToFavs", "Add Successful")
                completionBlock.model = location
            } else {
                Log.d("BFUtils.addToFavs", "Add Failed")
                completionBlock.model = null
            }
            completionBlock.run()
        }
    }

    fun removeFromFavoritesList(ctx: Context, location: BFLocation?, completionBlock: BFRunnable<BFLocation?>) {
        if (location != null) {
            val oldFavorites = getFavoritesList(ctx)
            val searchString = location.clientKey
            val newFavorites = StringBuilder()
            Log.d("BFUtils.removeFavoties", "removing: $searchString")
            val strArray = oldFavorites.split(",").toTypedArray()
            for (s in strArray) {
                if (!s.equals(searchString, ignoreCase = true)) {
                    if (newFavorites.isEmpty()) {
                        newFavorites.append(s)
                    } else {
                        newFavorites.append(",").append(s)
                    }
                }
            }
            Log.d("BFUtils.removeFavoties", "old: $oldFavorites new: $newFavorites")
            val didRemove = writeToDisk(ctx, newFavorites.toString())
            if (didRemove) {
                Log.d("BFUtils.remove", "remove successfull")
                completionBlock.model = location
            } else {
                Log.d("BFUtils.remove", "remove failed")
                completionBlock.model = null
            }
            completionBlock.run()
        }
    }

    private fun writeToDisk(ctx: Context, value: String): Boolean {
        var didSave = false
        try {
            val fos = ctx.openFileOutput(FAVORITES_FILE_NAME, Context.MODE_PRIVATE)
            fos.write(value.toByteArray())
            fos.close()
            fos.flush()
            Log.d("BFUtils.writeToDisk", "writing: $value")
            didSave = true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return didSave
    }

    fun getFavoritesList(ctx: Context?): String {
        val favorites = StringBuilder()
        if (ctx != null) {
            var fis: FileInputStream? = null
            try {
                fis = ctx.openFileInput(FAVORITES_FILE_NAME)
                var content: Int
                while (fis.read().also { content = it } != -1) {
                    favorites.append(content.toChar())
                }
                Log.d("BFUtils", "getFavorites: $favorites")
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fis?.close()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                }
            }
        }
        return favorites.toString()
    }

    fun searchFavorites(ctx: Context, searchString: String?): Boolean {
        val favorites = getFavoritesList(ctx)
        var isFavorite = false
        val strArray = favorites.split(",").toTypedArray()
        for (s in strArray) {
            if (s.equals(searchString, ignoreCase = true)) {
                isFavorite = true
                break
            }
        }
        return isFavorite
    }

    companion object {
        private const val FAVORITES_FILE_NAME = "favorites_file"
        fun getCurrentLocation(context: Context?): Location {
            val gpsTracker = GPSTracker(context)
            var location = gpsTracker.getLocation()
            if (location == null) {
                location = Location("")
                location.latitude = 0.0
                location.longitude = 0.0
            }
            return location
        }

        fun getCustomMarker(context: Context, imageId: Int, pos: LatLng, number: String, title: String?, snip: String?): MarkerOptions {
            return MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromBitmap(writeTextOnDrawable(context, imageId, number))).snippet(snip).title(title)
        }

        fun writeTextOnDrawable(context: Context, drawableId: Int, text: String): Bitmap {
            val bm = BitmapFactory.decodeResource(context.resources, drawableId)
                    .copy(Bitmap.Config.ARGB_8888, true)
            val paint = Paint()
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.color = Color.WHITE
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = convertToPixels(context, 25).toFloat()
            val textRect = Rect()
            paint.getTextBounds(text, 0, text.length, textRect)
            val canvas = Canvas(bm)

            //If the text is bigger than the canvas , reduce the font size
            if (textRect.width() >= canvas.width - 4) //the padding on either sides is considered as 4, so as to appropriately fit in the text
                paint.textSize = convertToPixels(context, 7).toFloat() //Scaling needs to be used for different dpi's

            //Calculate the positions
            val xPos = canvas.width / 2 //-2 is for regulating the x position offset

            //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
            val yPos = (canvas.height / 2 + (paint.descent() + paint.ascent()) / 2).toInt()
            canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), paint)
            return bm
        }

        private fun convertToPixels(context: Context, nDP: Int): Int {
            val conversionScale = context.resources.displayMetrics.density
            return (nDP * conversionScale + 0.5f).toInt()
        }
    }
}