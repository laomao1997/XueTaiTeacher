package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.UploadImageApi;
import com.xuetai.teacher.xuetaiteacher.api.UploadInfoApi;
import com.xuetai.teacher.xuetaiteacher.utils.ScreenUtil;
import com.xuetai.teacher.xuetaiteacher.view.AvatarBottomSheetDialog;
import com.xuetai.teacher.xuetaiteacher.view.DiplomaBottomSheetDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class DiplomaIndentifyActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA = 10;
    private final int REQUEST_ALBUM_OK = 11;
    private static final int CROP_REQUEST_CODE = 15;

    String diplomaLevel;
    String almaMaterName;
    String schoolTime;
    String newDiplomaUrl = "";

    private String mCropFilePath;//裁剪图片的输出路径，intent无法传递大图（bitmap）,只能通过uri传递
    private Uri carmerauri;
    File storageDir;
    private Bitmap bm = null;

    // 我的学历选择控件
    OptionsPickerView diplomaPicker;

    @BindView(R.id.tv_diploma)
    TextView tvDiploma;

    @BindView(R.id.et_elma_mater)
    EditText etAlmaMater;

    @BindView(R.id.iv_diploma)
    ImageView ivDiploma;

    @BindViews({R.id.tv_begin_time, R.id.tv_end_time})
    List<TextView> tvTime;

    @BindView(R.id.btn_submit)
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diaploma_info);
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

        initDiplomaPicker();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 接收Matisse选择的头像
//        if(requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
//            List<Uri> mSelected = Matisse.obtainResult(data);
//            Glide.with(this).load(mSelected.get(0)).error(R.drawable.ic_noicon).into(mImageViewIcon);
//        }
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

    private void initDiplomaPicker() {
        ArrayList listOfDiploma = new ArrayList();
        listOfDiploma.add("博士");
        listOfDiploma.add("硕士");
        listOfDiploma.add("本科");
        listOfDiploma.add("大专");
        listOfDiploma.add("其他");
        diplomaPicker = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            tvDiploma.setText(listOfDiploma.get(options1).toString());
            diplomaLevel = options1 + "";
        }).build();
        diplomaPicker.setPicker(listOfDiploma);
        diplomaPicker.setTitleText("请选择教授阶段");
    }

    /**
     * 配置学历选择器
     */
    @OnClick(R.id.layout_my_diaploma)
    void doSetDiploma() {
        diplomaPicker.show();
    }

    /**
     * 配置时间选择器
     */
    private TimePickerView initTimePicker(TextView beginTv) {
        TimePickerView timePickerView = new TimePickerBuilder(DiplomaIndentifyActivity.this,
                (date, v) -> {
                    beginTv.setText((date.getYear() + 1900 + ""));
                    if (isInfoOk()) btnSubmit.setEnabled(true);
                })
                .setType(new boolean[]{true, false, false, false, false, false})
                .build();
        return timePickerView;
    }

    @OnClick({R.id.tv_begin_time, R.id.tv_end_time})
    void doSetTime(View view) {
        switch (view.getId()) {
            case R.id.tv_begin_time:
                initTimePicker(tvTime.get(0)).show();
                break;
            case R.id.tv_end_time:
                initTimePicker(tvTime.get(1)).show();
                break;
        }
    }

    @OnClick(R.id.layout_upload_diaploma)
    void doSetEduPhoto() {
        DiplomaBottomSheetDialog diplomaBottomSheetDialog = new DiplomaBottomSheetDialog();
        diplomaBottomSheetDialog.show(getSupportFragmentManager(), "edu_photo_dialog");
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

    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }

    @OnTextChanged(R.id.et_elma_mater)
    void checkAlmaMater() {
        if (isInfoOk()) btnSubmit.setEnabled(true);
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
     * 检查学历信息完整度
     *
     * @return 布尔值 所有信息完整则为true
     */
    private boolean isInfoOk() {
        return (!etAlmaMater.getText().toString().isEmpty())
                && (!tvTime.get(0).getText().toString().equals("开始时间"))
                && (!tvTime.get(1).getText().toString().equals("结束时间"));
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
                    JSONObject jsonObject = new JSONObject(jsonargs);
                    String resultArgs = jsonObject.getString("result");
                    JSONObject resultOject = new JSONObject(resultArgs);
                    newDiplomaUrl = resultOject.getString("url");
                    Glide.with(getApplicationContext()).load(newDiplomaUrl).into(ivDiploma);
                    toastMessage("学历证明上传成功");
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    // 提交学历认证资料到服务器
    @OnClick(R.id.btn_submit)
    void doSubmit() {
        JSONObject jsonParams = new JSONObject();
        almaMaterName = etAlmaMater.getText().toString();
        schoolTime = tvTime.get(0).getText().toString() + "-" + tvTime.get(1).getText().toString();
        //KLog.a("学历认证","本人学历: " + diplomaLevel + "\n 毕业院校: " + almaMaterName + "\n 在校时间: " + schoolTime + "\n 学历图片: " + newDiplomaUrl);
        try {
            jsonParams.put("education", diplomaLevel);
            jsonParams.put("school", almaMaterName);
            jsonParams.put("schooltime", schoolTime);
            jsonParams.put("education_photo", newDiplomaUrl);
            jsonParams.put("examine_status", 1);
        } catch (Exception e) {
            KLog.e(e);
        }
        UploadInfoApi.getInstance().uploadResult(jsonParams, new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String resultArgs = responseBody.string();
                    KLog.json(resultArgs);
                    JSONObject resultJson = new JSONObject(resultArgs);
                    if (resultJson.getBoolean("result")) {
                        Intent intent = new Intent(DiplomaIndentifyActivity.this, ReviewStatusActivity.class);
                        startActivity(intent);
                    }
                    else toastMessage("资料提交失败");
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    /**
     * 显示一个短时间的提示信息
     *
     * @param message 将要提示的信息 String类型
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //点击空白处收起键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

}
