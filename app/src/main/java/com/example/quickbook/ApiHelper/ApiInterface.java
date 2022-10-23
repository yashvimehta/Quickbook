package com.example.quickbook.ApiHelper;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
    //    String BASE_URL_PREDICTOR = "http://104.45.197.70:5000/";
    String BASE_URL_RECOMMENDATION = "https://rutvik-food-recommendation.herokuapp.com/";
    String BASE_URL_PREDICTOR= "http://20.219.149.149:5000/";
//    @Multipart
//    @POST("food-predictor")
//    Call<FoodPredictorResult> sendImage(@Part MultipartBody.Part image);
//
//    @GET("similar-recommendation")
//    Call<FoodRecommendationResult> getSimilarFoodItems(@Query("item-name") String itemName);
//
//    @GET("user-recommendation")
//    Call<FoodRecommendationResult> getUserRecommendation(@Query("user-uid") String userUid);

//    @Multipart
//    @POST("identify")
//    Call<IdentifyResult> sendImage(@Part MultipartBody.Part image);

    @Multipart
    @POST("bookandface")
    Call<BFResult> sendImage(@Part MultipartBody.Part image);

    @Multipart
    @POST("register")
    Call<RegisterResult> sendImageandName(@Part MultipartBody.Part image, @Part String name);
}

