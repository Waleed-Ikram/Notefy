package com.example.notefy;
import com.google.gson.annotations.SerializedName;

public class AdviceResponse {
    @SerializedName("slip")
    private Advice advice;

    public Advice getAdvice() {
        return advice;
    }

    public class Advice {
        @SerializedName("advice")
        private String adviceMessage;

        public String getAdviceMessage() {
            return adviceMessage;
        }
    }
}
