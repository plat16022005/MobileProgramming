package com.group02.mobile.data.remote

import android.content.Context
import android.net.Uri
import com.group02.mobile.utils.CloudinaryConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.security.MessageDigest

class CloudinaryService {
    private val client = OkHttpClient()

    private fun sha1(input: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        val result = md.digest(input.toByteArray())
        return result.joinToString("") { "%02x".format(it) }
    }

    suspend fun uploadImage(imageUri: Uri, context: Context): Result<String> = withContext(Dispatchers.IO) {
        try {
            val timestamp = (System.currentTimeMillis() / 1000).toString()
            val signatureString = "folder=nihonlish/avatars&timestamp=$timestamp${CloudinaryConfig.API_SECRET}"
            val signature = sha1(signatureString)

            val inputStream = context.contentResolver.openInputStream(imageUri)
                ?: return@withContext Result.failure(Exception("Cannot open image"))
            val byteArray = inputStream.readBytes()
            inputStream.close()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "avatar.jpg", byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull()))
                .addFormDataPart("api_key", CloudinaryConfig.API_KEY)
                .addFormDataPart("timestamp", timestamp)
                .addFormDataPart("signature", signature)
                .addFormDataPart("folder", "nihonlish/avatars")
                .build()

            val request = Request.Builder()
                .url(CloudinaryConfig.UPLOAD_URL)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Upload failed: ${response.message}"))
            }

            val responseBody = response.body?.string()
                ?: return@withContext Result.failure(Exception("Empty response body"))

            val jsonObject = JSONObject(responseBody)
            val secureUrl = jsonObject.getString("secure_url")

            Result.success(secureUrl)
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
