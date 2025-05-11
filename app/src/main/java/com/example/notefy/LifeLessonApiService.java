package com.example.notefy;
import retrofit2.Call;
import retrofit2.http.GET;

public interface LifeLessonApiService {
    @GET("advice")
    Call<AdviceResponse> getRandomAdvice();
}
