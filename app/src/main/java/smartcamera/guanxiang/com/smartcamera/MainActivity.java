package smartcamera.guanxiang.com.smartcamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText mEtName;
    private EditText mEtUid;
    private EditText mEtPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        mEtName = findViewById(R.id.et_name);
        mEtUid = findViewById(R.id.et_uid);
        mEtPwd = findViewById(R.id.et_pwd);
    }


    public void startConnct(View view) {
        String name = mEtName.getText().toString().trim();
        String uid = mEtUid.getText().toString().trim();
        String pwd = mEtPwd.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(getApplicationContext(),"名称不能为空",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(uid)){
            Toast.makeText(getApplicationContext(),"UID不能为空",Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(pwd)){
            Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
        }else{
            Intent intent = new Intent(this,CameraActivity.class);
            intent.putExtra("name",name);
            intent.putExtra("uid",uid);
            intent.putExtra("name",pwd);
            startActivity(intent);
            finish();
        }
    }
}
