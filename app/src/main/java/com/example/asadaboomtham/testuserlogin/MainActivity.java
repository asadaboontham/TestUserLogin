package com.example.asadaboomtham.testuserlogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private LoginButton btnLogin;
    private CallbackManager callbackManager;
    String user_id;
    String user_email;
    String accToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        printKeyHash(this);
        login_facebook();
        initInstance();

    }

    private void initInstance() {

    }

    private void login_facebook() {
        callbackManager = CallbackManager.Factory.create();
        btnLogin = (LoginButton) findViewById(R.id.login_fb);

        btnLogin.setReadPermissions(Arrays.asList("user_photos", "email", "public_profile"));


        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "Success " + loginResult.getAccessToken().getUserId(), Toast.LENGTH_SHORT).show();
                user_id = loginResult.getAccessToken().getUserId();//เก็บค่าใส่ ตัวแปลไว้ เพื่อส่งไปอีกหน้านึง
                String gg = loginResult.getAccessToken().getToken();
                Toast.makeText(MainActivity.this, gg, Toast.LENGTH_SHORT).show();
                accToken = gg; //เก็บ Token

                Log.d("5555", gg);
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,last_name,link,email,picture");

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        try {
                            String str_email = jsonObject.getString("email");
                            Toast.makeText(MainActivity.this, str_email, Toast.LENGTH_LONG).show();
                            user_email = str_email;//เก็บ Email
                            Log.d("test", user_email);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("user", jsonObject.toString());
                    }
                });
                request.setParameters(parameters);
                request.executeAsync();

//                Call<String> call = HttpManager.getInstance().getService().getUserId(gg);
//                call.enqueue(new Callback<String>() {
//                    @Override
//                    public void onResponse(Call<String> call, Response<String> response) {
//                        if (response.isSuccessful()) {
//                            SharedPreferences sp = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sp.edit();
//                            editor.putString("UserId", response.body().toString());
//                            editor.putString("Token", accToken);
//
//                            editor.commit();
//
//
//                            Log.d("5555isSuccessful", response.body().toString());
//                        } else {
//                            Log.d("5555errorBody", "" + response.message());
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<String> call, Throwable t) {
//                        Log.d("5555onFailure", "" + t);
//                    }
//                });

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, "Error " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //หลังจากล็อคอินเสร็จ ก็จะเปิด หน้า Main
//        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user_email", "test");
        intent.putExtra("isSmart", true);
        intent.putExtra("star", 5);
        startActivity(intent);

    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
