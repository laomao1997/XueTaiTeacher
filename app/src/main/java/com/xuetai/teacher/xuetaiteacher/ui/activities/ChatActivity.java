package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.adapters.ChatsAdapter;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.api.UploadImageApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.models.Chat;
import com.xuetai.teacher.xuetaiteacher.utils.ScreenUtil;
import com.xuetai.teacher.xuetaiteacher.utils.SharedPreferencesHelper;
import com.xuetai.teacher.xuetaiteacher.utils.StatusBarUtil;
import com.xuetai.teacher.xuetaiteacher.view.ChatBottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class ChatActivity extends AppCompatActivity {

    private static final int COUNT = 20;
    private final int REQUEST_CAMERA = 10;
    private final int REQUEST_ALBUM_OK = 11;
    private static final int CROP_REQUEST_CODE = 15;

    // 轮询订阅
    Subscription mSubscription;

    int studentId;
    String studentAvatar;
    String teacherAvatar;

    List<Chat> chatList = new ArrayList<>();

    ChatsAdapter chatsAdapter;
    Calendar calendar;
    int maxTime;

    String diploma;
    String almaMaterName;
    String beginTime;
    String endTime;
    String newDiplomaUrl = "";

    private String mCropFilePath;//裁剪图片的输出路径，intent无法传递大图（bitmap）,只能通过uri传递
    private Uri carmerauri;
    File storageDir;
    private Bitmap bm = null;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.edit_text)
    EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 设置沉浸式状态栏
        // 当 FitsSystemWindows 设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        // 设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        // 一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 会导致状态栏文字看不清
        // 所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            // 如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            // 这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }

        ButterKnife.bind(this);

        initPhotoError();

        File file = new File("/mnt/sdcard/XueTaiTeacher/DingNiuFuDao");
        // 判断文件夹是否存在如果不存在就创建, 否则不创建
        if (!file.exists()) {
            // 通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }
        storageDir = file;
        mCropFilePath = storageDir + "tmp.jpg";

        calendar = Calendar.getInstance();

        Intent intent = getIntent();
        studentId = intent.getIntExtra("STUDENT_ID", 0);
        studentAvatar = intent.getStringExtra("STUDENT_AVATAR");
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this, "setting");
        String userInfo = (String) sharedPreferencesHelper.getSharedPreference("info", "no_info");
        try {
            JSONObject infoJson = new JSONObject(userInfo);
            teacherAvatar = infoJson.getString("head_photo");
        } catch (JSONException e) {
            e.printStackTrace();
            teacherAvatar = "";
        }
        maxTime = (int) (new Date().getTime() / 1000);
        initView();
        requestChatsPerSecond();
    }

    private void showCamera() {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            //mTmpFile = ScreenUtils.createFile(getApplicationContext());
            //cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            //startActivityForResult(cameraIntent, REQUEST_CAMERA);
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                carmerauri = Uri.fromFile(photoFile);
            }
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(getApplicationContext(), "没有摄像头", Toast.LENGTH_SHORT).show();
        }

    }

    private File createImageFile() {
        File image = null;
        try {
            image = File.createTempFile(
                    generateFileName(),  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return imageFileName;
    }

    /**
     * 调用图库
     */
    public void initGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_ALBUM_OK);
    }

    private void startImageZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        File file = new File(mCropFilePath);
        if (file.exists()) {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("outputX", 200);
//        intent.putExtra("outputY", 200);
//        intent.putExtra("aspectX", 200);
//        intent.putExtra("aspectY", 200);
        intent.putExtra("crop", true);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    /**
     * android 7.0系统解决拍照的问题
     */
    private void initPhotoError() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    //图片压缩
    private void compressfile(String imagePath) {
        String thumImagePath =
                ScreenUtil.compressImage(imagePath,
                        "/mnt/sdcard/XueTaiTeacher/DingNiuFuDao" + File.separator + new Date().getTime() + ".jpg", 30);

        bm = BitmapFactory.decodeFile(thumImagePath);

        String a = ScreenUtil.ImageCompressL(1000.0, bm);

        Message message = new Message();
        message.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("bitmap", a);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    upLoadImage(msg.getData().getString("bitmap"), bm);

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void upLoadImage(String base64, Bitmap bitmap) {
        UploadImageApi.getInstance().uploadImageResult(base64, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String jsonargs = responseBody.string();
                    KLog.json(jsonargs);
                    JSONObject jsonObject = new JSONObject(jsonargs);
                    String resultArgs = jsonObject.getString("result");
                    JSONObject resultOject = new JSONObject(resultArgs);
                    newDiplomaUrl = resultOject.getString("url");
                    sendMessage(newDiplomaUrl, "img");
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    // 向服务器请求聊天数据
    private void requestChatsPerSecond() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
//            jsonObject.put("maxTime", maxTime);
//            jsonObject.put("count", COUNT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSubscription = Observable.interval(0, 1, TimeUnit.SECONDS).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Long aLong) {
                requestChats(jsonObject);
            }
        });
    }

    private void requestChats(JSONObject jsonObject) {
        NormalApi.getInstance().getResult(MethodCode.GetChartDetaile, jsonObject, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    fromJsonToChatList(new JSONObject(responseBody.string()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 停止轮询
    public void unSubscribe() {
        //判断subscribe是否已经取消订阅
        if (!mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    private void sendMessage(String message, String type) {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("studentId", studentId);
            jsonParams.put("type", type);
            jsonParams.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NormalApi.getInstance().getResult(MethodCode.SendDialoguesMessage, jsonParams, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("studentId", studentId);
                    requestChats(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // 解析获得的JSON文件到对象
    private void fromJsonToChatList(JSONObject jsonObject) {
        KLog.json(jsonObject.toString());
        try {
            JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("list");
            if (jsonArray.length() != chatList.size()) {
                chatList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String timeStamp = jsonArray.getJSONObject(i).getString("timestamp");
                    String message = jsonArray.getJSONObject(i).getString("message");
                    String from = jsonArray.getJSONObject(i).getString("from");
                    String type = jsonArray.getJSONObject(i).getString("type");
                    chatList.add(new Chat(timeStamp, message, from, type));
                }
                updateView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (chatList.size() > 0) {
            // 更新时间戳
            maxTime = Integer.parseInt(chatList.get(0).getTimeStamp()) - 1;
        }

    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatsAdapter = new ChatsAdapter(this, chatList, studentAvatar, teacherAvatar);
        recyclerView.setAdapter(chatsAdapter);
        chatsAdapter.setOnImageClickListener(new ChatsAdapter.OnImageClickListener() {
            @Override
            public void OnImageClick(int position) {
                Intent intent = new Intent(ChatActivity.this, PhotoReviewActivity.class);
                intent.putExtra("IMAGE_URL", chatList.get(position).getMessage());
                startActivity(intent);
            }
        });
    }

    private void updateView() {
        chatsAdapter.notifyDataSetChanged();
        if (!chatList.isEmpty()) recyclerView.smoothScrollToPosition(chatList.size() - 1);
    }

    /**
     * 弹出底部window上传照片
     */
    void doSetEduPhoto() {
        ChatBottomSheetDialog chatBottomSheetDialog = new ChatBottomSheetDialog();
        chatBottomSheetDialog.show(getSupportFragmentManager(), "edu_photo_dialog");
        chatBottomSheetDialog.setClicklistener(new ChatBottomSheetDialog.ClickListenerInterface() {
            @Override
            public void doCamera() {
                showCamera();
            }

            @Override
            public void doGallery() {
                initGallery();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CAMERA:
                if (carmerauri != null) {
                    KLog.d("相机裁剪", carmerauri);
                    startImageZoom(carmerauri);
                }
            case REQUEST_ALBUM_OK:
                try {
                    if (data != null) {
                        KLog.d("相册裁剪", data.toString());
                        startImageZoom(data.getData());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case CROP_REQUEST_CODE:
                if (mCropFilePath != null) {
                    compressfile(mCropFilePath);
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unSubscribe();
    }

    @OnClick(R.id.iv_send_image)
    void doUploadImage() {
        doSetEduPhoto();
    }

    @OnClick(R.id.btn_send_message)
    void doSendChat() {
        sendMessage(editText.getText().toString(), "text");
        editText.setText("");
    }
}
