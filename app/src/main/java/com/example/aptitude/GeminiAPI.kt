import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

object GeminiAPI {
    private const val API_KEY = "AIzaSyC_1z6kwYKKaPdP2wuEXeO94Mi4JzfVnAg"  // Replace with your actual API key
    private const val API_URL =
        "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=$API_KEY"

    private val client = OkHttpClient()

    fun analyzeResume(resumeText: String): String {
        val json = JSONObject().apply {
            put("contents", listOf(JSONObject().apply {
                put("parts", listOf(JSONObject().apply {
                    put("text", "Analyze this resume and generate 5 interview questions:\n$resumeText")
                }))
            }))
        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(API_URL)
            .post(requestBody)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: "No response"

                if (!response.isSuccessful) {
                    println("❌ API Error: ${response.code} - ${response.message}")
                    return "Error: API request failed\nCode: ${response.code}\nMessage: ${response.message}\nResponse: $responseBody"
                }

                println("✅ API Response: $responseBody")
                parseQuestionsFromResponse(responseBody)
            }
        } catch (e: IOException) {
            println("❌ Network Error: ${e.message}")
            "Error: Network issue - ${e.message}"
        }
    }

    private fun parseQuestionsFromResponse(response: String): String {
        return try {
            val jsonResponse = JSONObject(response)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")

            parts?.optJSONObject(0)?.optString("text", "No text response") ?: "No valid response"
        } catch (e: Exception) {
            println("❌ JSON Parsing Error: ${e.message}")
            "Error processing AI response"
        }
    }
}
