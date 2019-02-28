package com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo;

import android.content.Intent;
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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.UploadImageApi;
import com.xuetai.teacher.xuetaiteacher.utils.ScreenUtil;
import com.xuetai.teacher.xuetaiteacher.view.AvatarBottomSheetDialog;
import com.xuetai.teacher.xuetaiteacher.view.DiplomaBottomSheetDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class MyWriteHonorActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA = 10;
    private final int REQUEST_ALBUM_OK = 11;
    private static final int CROP_REQUEST_CODE = 15;

    private String mCropFilePath;//裁剪图片的输出路径，intent无法传递大图（bitmap）,只能通过uri传递
    private Uri carmerauri;
    File storageDir;
    private Bitmap bm = null;

    String honorArgs;
    String newHonorUrl = "";

    @BindView(R.id.tv_counter)
    TextView tvCounter;
    @BindView(R.id.et_honor)
    EditText etHonor;
    @BindView(R.id.iv_diploma)
    ImageView ivDiploma;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_write_honor);

        ButterKnife.bind(this);

        initData();

        File file = new File("/mnt/sdcard/XueTaiTeacher/DingNiuFuDao");
        // 判断文件夹是否存在如果不存在就创建, 否则不创建
        if (!file.exists()) {
            // 通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }
        storageDir = file;
        mCropFilePath = storageDir + "tmp.jpg";

        initPhotoError();
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

    // 实时统计文本框字数
    @OnTextChanged(R.id.et_honor)
    void doCount() {
        honorArgs = etHonor.getText().toString();
        tvCounter.setText(honorArgs.length()+"");
    }

    @OnClick({R.id.iv_back_arrow, R.id.tv_submit, R.id.layout_upload_honor})
    void doDealWithThem(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.tv_submit:
                // 使用 Intent 返回数据
                Intent intent = new Intent();
                intent.putExtra("honor_result", honorArgs);
                intent.putExtra("honor_url", newHonorUrl);
                this.setResult(RESULT_OK, intent);
                this.finish();
                break;
            case R.id.layout_upload_honor:
                doSetHonorPhoto();
                break;
        }
    }

    /**
     * 初始化页面
     * 接受MyHonorActivity的传值
     */
    void initData() {
        Intent intent = getIntent();
        honorArgs = intent.getStringExtra("honor_result");
        newHonorUrl = intent.getStringExtra("honor_url");
        if (!honorArgs.isEmpty()) etHonor.setText(honorArgs);
        if (!newHonorUrl.isEmpty()) loadPhoto();
        System.out.println("数据初始化");
        System.out.println(honorArgs);
        System.out.println(newHonorUrl);
    }

    /**
     * 弹出底部window上传照片
     */
    void doSetHonorPhoto() {
        DiplomaBottomSheetDialog diplomaBottomSheetDialog = new DiplomaBottomSheetDialog();
        diplomaBottomSheetDialog.show(getSupportFragmentManager(), "honor_photo_dialog");
        diplomaBottomSheetDialog.setClicklistener(new AvatarBottomSheetDialog.ClickListenerInterface() {
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

    /**
     * android 7.0系统解决拍照的问题
     */
    private void initPhotoError() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
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

                    upLoadImage(msg.getData().getString("bitmap"), ivDiploma, bm);

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void upLoadImage(String base64, ImageView imageView, Bitmap bitmap) {
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
                    newHonorUrl = new JSONObject(jsonargs).getJSONObject("result").getString("url");
                    loadPhoto();
                    toastMessage("荣誉证明上传成功");
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    void loadPhoto() {
        Glide.with(this).load(newHonorUrl).into(ivDiploma);
    }

    /**
     * 显示一个短时间的提示信息
     *
     * @param message 将要提示的信息 String类型
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
