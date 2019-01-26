package smartcamera.guanxiang.com.smartcamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Monitor;

public class CameraActivity extends AppCompatActivity {

    private Monitor monitor;
    private TextView tvStatus;
    private View vCenter;
    private Camera mCamera;
    private MyIotclistener mMyIotcListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initValue();
        initView();
    }

    private void initView() {
        monitor = (Monitor) findViewById(R.id.monitor);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        vCenter = (View) findViewById(R.id.v_center);
    }

    private void initValue() {
        Intent intent = new Intent();
        String name = intent.getStringExtra("name");
        String uid = intent.getStringExtra("uip");
        String pwd = intent.getStringExtra("pwd");

        conn(name, uid, pwd);
    }

    //连接摄像头
    private void conn(String name, String uid, String pwd) {
        //初始化摄像头,初始化AV通道
        Camera.init();
        //创建Camera实例对象
        mCamera = new Camera();

        //注册回调接口
        mMyIotcListener = new MyIotclistener();
        mCamera.registerIOTCListener(mMyIotcListener);

        //连接摄像头,参数为uid
        mCamera.connect(uid);
        //开启摄像头
        //参数1:通道号,后面会用到,要保证一致性
        mCamera.start(Camera.DEFAULT_AV_CHANNEL, name, pwd);
        /**
         * 发送测试指令，测试是否连接成功
         * 参数2:通道号
         * 参数2:指令类型
         * 参数3:指令参数
         */
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());


    }

    //点击按钮发送不同的指令
    public void sendCtrl(View view) {
        switch (view.getId()) {
            case R.id.ib_left:
                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
                        AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent((byte)AVIOCTRLDEFs.AVIOCTRL_PTZ_LEFT,
                                (byte)0,(byte)0,(byte)0,(byte)0,(byte)Camera.DEFAULT_AV_CHANNEL));

                break;
            case R.id.ib_bottom:
                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
                        AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent((byte)AVIOCTRLDEFs.AVIOCTRL_PTZ_DOWN,
                                (byte)0,(byte)0,(byte)0,(byte)0,(byte)Camera.DEFAULT_AV_CHANNEL));

                break;
            case R.id.ib_right:
                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
                        AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent((byte)AVIOCTRLDEFs.AVIOCTRL_PTZ_RIGHT,
                                (byte)0,(byte)0,(byte)0,(byte)0,(byte)Camera.DEFAULT_AV_CHANNEL));
                break;
            case R.id.ib_top:
                mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                        AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
                        AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent((byte)AVIOCTRLDEFs.AVIOCTRL_PTZ_FLIP,
                                (byte)0,(byte)0,(byte)0,(byte)0,(byte)Camera.DEFAULT_AV_CHANNEL));
                break;
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Camera.CONNECTION_STATE_CONNECTING:
                    //连接中
                    tvStatus.setText("连接中");
                    break;
                case Camera.CONNECTION_STATE_CONNECTED:
                    //连接成功
                    tvStatus.setText("连接成功");
                    //显示画面
                    showCamera();
                    break;

                case Camera.CONNECTION_STATE_CONNECT_FAILED:
                    //连接失败
                    tvStatus.setText("连接失败");
                    break;
                case Camera.CONNECTION_STATE_TIMEOUT:
                    //连接超时
                    tvStatus.setText("连接超时");
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        //释放支援，断开连接
        super.onDestroy();
        mCamera.stopShow(Camera.DEFAULT_AV_CHANNEL);
        monitor.deattachCamera();
        mCamera.stop(Camera.DEFAULT_AV_CHANNEL);
        mCamera.disconnect();
        mCamera.unregisterIOTCListener(mMyIotcListener);
        Camera.uninit();

    }

    private void showCamera() {
        //判断连接是否成功
        if(mCamera != null && mCamera.isChannelConnected(Camera.DEFAULT_AV_CHANNEL)){
            //设置最大焦距
            monitor.setMaxZoom(1.0f);
            //挂载摄像头
            monitor.attachCamera(mCamera,Camera.DEFAULT_AV_CHANNEL);
            //开始显示
            mCamera.startShow(Camera.DEFAULT_AV_CHANNEL,true);
        }
    }

    class MyIotclistener implements IRegisterIOTCListener {

        //返回通道信息
        @Override
        public void receiveChannelInfo(Camera camera, int channel, int responseCode) {
            System.out.println("conn status:" + responseCode);
            //这个回调方法在子线程中的
//            tvStatus.setText(responseCode+"");
            handler.sendEmptyMessage(responseCode);
        }
        //返回摄像头捕捉到的信息
        @Override
        public void receiveFrameData(Camera camera, int i, Bitmap bitmap) {

        }
        //返回摄像头其他信息
        @Override
        public void receiveFrameInfo(Camera camera, int i, long l, int i1, int i2, int i3, int i4) {

        }
        //返回指令结果信息
        @Override
        public void receiveIOCtrlData(Camera camera, int i, int i1, byte[] bytes) {

        }
        //返回当前会话信息
        @Override
        public void receiveSessionInfo(Camera camera, int i) {

        }
    }
}
