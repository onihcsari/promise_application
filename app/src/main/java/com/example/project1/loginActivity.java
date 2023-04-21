package com.example.project1;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class loginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, loginActivity2.class);
                startActivity(intent);
            }
        });

        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        Button kakaoLoginButton = findViewById(R.id.kakao_login_button);
        kakaoLoginButton.setOnClickListener(v -> {
            // 카카오 로그인 실행
            loginWithKakao();
        });
    }
    private void loginWithKakao() {
        KakaoSdk.init(this, "d895188df2fb9c61af19815c1aa61ab0");
        UserApiClient.getInstance().loginWithKakaoTalk(this, (oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "카카오 로그인 실패", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "카카오 로그인 성공");

                // 사용자 정보 요청
                UserApiClient.getInstance().me((user, userError) -> {
                    if (userError != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", userError);
                    } else {
                        Log.i(TAG, "사용자 정보 요청 성공: " + user.toString());
                        // 로그인 성공 후 처리를 구현하세요.
                    }
                    return null;
                });
            }
            return null;
        });
    }
}