package com.ruoxu.learndemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ruoxu.update.Constants;
import com.ruoxu.update.UpdateAgent;

import org.json.JSONException;
import org.json.JSONObject;

public class AutoUpdateActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                UpdateAgent.init(Constants.SERVER_URL);
                UpdateAgent.update(this); //默认
//              UpdateAgent.forceUpdate(this);//强制
//              UpdateAgent.silentUpdate(this);//静默

                break;
            case R.id.btn2:

                /**
                 {
                 "versionName":"v1.0",
                 "versionCode":"2",
                 "downUrl":"https://git.oschina.net/need88.com/TempFile/raw/master/test.apk",
                 "updateInfo":"1.更新xxx\n2.update bugs\n3.update fixs"
                 }

                 */
                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("versionName", "v2.0");
                    jsonObject.put("versionCode", "2");
                    jsonObject.put("downUrl", "https://git.oschina.net/need88.com/TempFile/raw/master/test.apk");
                    jsonObject.put("updateInfo", "1.Content更新xxx\n2.update bugs\n3.update fixs");

                    UpdateAgent.updateByStr(this,jsonObject.toString(), UpdateAgent.Type.defaultType);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }

    }
}
