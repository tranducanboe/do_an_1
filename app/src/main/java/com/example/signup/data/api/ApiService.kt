package com.example.signup.data.api
import com.example.signup.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
interface ApiService {
    //Kiểm tra xem email có tồn tại hay không
    @GET("users")
    suspend fun checkEmailExists(@Query("email") email: String): Response<List<User>>

    @POST("users")
    suspend fun registerUser(@Body user: User): Response<User>

    @GET("/users")
    suspend fun getUsers(): List<User>

    // Cập nhật phương thức login để trả về thông tin người dùng
    @POST("login")
    suspend fun login(@Body credentials: Map<String, String>): Response<User>
}