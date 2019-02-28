package com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.view.TimePickerView;
import com.bumptech.glide.Glide;
import com.socks.library.KLog;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.api.UploadImageApi;
import com.xuetai.teacher.xuetaiteacher.api.UploadInfoApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.ui.activities.DiplomaIndentifyActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.MainActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.ReviewStatusActivity;
import com.xuetai.teacher.xuetaiteacher.utils.ScreenUtil;
import com.xuetai.teacher.xuetaiteacher.view.AvatarBottomSheetDialog;
import com.xuetai.teacher.xuetaiteacher.view.DiplomaBottomSheetDialog;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MyDiplomaIdentification extends AppCompatActivity {

    private final int REQUEST_CAMERA = 10;
    private final int REQUEST_ALBUM_OK = 11;
    private static final int CROP_REQUEST_CODE = 15;

    String diploma;
    String almaMaterName;
    String beginTime;
    String endTime;
    String newDiplomaUrl = "";

    private String mCropFilePath;//裁剪图片的输出路径，intent无法传递大图（bitmap）,只能通过uri传递
    private Uri carmerauri;
    File storageDir;
    private Bitmap bm = null;

    // 存储学历列表
    Map<String, String> dataOfDiploma = new HashMap<>();

    @BindView(R.id.tv_submit)
    TextView mTvSubmit;
    @BindView(R.id.tv_diploma)
    TextView tvDiploma;
    @BindView(R.id.et_alma_mater)
    EditText etAlmaMater;
    @BindView(R.id.tv_begin_time)
    TextView tvTimeBegin;
    @BindView(R.id.tv_end_time)
    TextView tvTimeEnd;
    @BindView(R.id.iv_diploma)
    ImageView ivDiploma;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_diploma_identification);

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

        initDatas();
        loadInfo();
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


    private void initDatas() {
        dataOfDiploma.put("0", "博士");
        dataOfDiploma.put("1", "硕士");
        dataOfDiploma.put("2", "本科");
        dataOfDiploma.put("3", "大专");
        dataOfDiploma.put("4", "其他");
    }

    // 从SharedPreferences中获取JSON并更新UI
    private void loadInfo() {
        Observable.create((Observable.OnSubscribe<JSONObject>) subscriber -> {
            try {
                SharedPreferences userPreference = getSharedPreferences("setting", Activity.MODE_MULTI_PROCESS);
                String userInfo = userPreference.getString("info", "no_info");
                JSONObject infoJson = new JSONObject(userInfo);
                KLog.json(userInfo);
                subscriber.onNext(infoJson);
                subscriber.onCompleted();
            } catch (Exception e) {
                KLog.e(e);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JSONObject infoJson) {
                        try {
                            diploma = infoJson.getString("education");
                            almaMaterName = infoJson.getString("school");
                            beginTime = infoJson.getString("schooltime").substring(0, 4);
                            endTime = infoJson.getString("schooltime").substring(5, 9);
                            newDiplomaUrl = infoJson.getString("education_photo");
                            loadDiplomaIntoTextView(diploma);
                            loadSchoolIntoEditText(almaMaterName);
                            loadTimeIntoTextView(tvTimeBegin, beginTime);
                            loadTimeIntoTextView(tvTimeEnd, endTime);
                            loadImageIntoImageView(newDiplomaUrl);
                        } catch (Exception e) {
                            KLog.e(e);
                        }
                    }
                });
    }

    private void loadDiplomaIntoTextView(String str) {
        tvDiploma.setText(dataOfDiploma.get(str));
    }

    private void loadSchoolIntoEditText(String str) {
        etAlmaMater.setText(str);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                almaMaterName = s.toString();
                setSubmitButtonEnable();
            }
        };
        etAlmaMater.addTextChangedListener(textWatcher);
    }

    private void loadTimeIntoTextView(TextView textView, String time) {
        textView.setText(time);
    }

    private void loadImageIntoImageView(String url) {
        Glide.with(this).load(url).into(ivDiploma);
    }

//    @OnTextChanged(R.id.et_alma_mater)
//    void doSetAlMatar() {
//        almaMaterName = etAlmaMater.getText().toString();
//    }

    /**
     * 显示学历选择对话框
     */
    private void showDiplomaListDialog() {
        final String[] items = {"博士", "硕士", "本科", "大专", "其他"};
        AlertDialog.Builder listDialog =
                new AlertDialog.Builder(MyDiplomaIdentification.this);
        listDialog.setTitle("请选择学历");
        listDialog.setItems(items, (dialog, which) -> {
            // which 下标从0开始
            diploma = which + "";
            loadDiplomaIntoTextView(diploma);

        });
        listDialog.show();
    }

    /**
     * 配置起始时间选择器
     * @return 一个 Time Picker View 对象， 可以直接调用 show() 方法
     */
    private TimePickerView initBeginTimePicker() {
        TimePickerView timePickerView = new TimePickerBuilder(MyDiplomaIdentification.this,
                (date, v) -> {
                    beginTime = (date.getYear() + 1900 + "");
                    loadTimeIntoTextView(tvTimeBegin, beginTime);
                })
                .setType(new boolean[]{true, false, false, false, false, false})
                .setTitleText("设置起始时间")
                .build();
        return timePickerView;
    }

    /**
     * 配置结束时间选择器
     * @return 一个 Time Picker View 对象， 可以直接调用 show() 方法
     */
    private TimePickerView initEndTimePicker() {
        TimePickerView timePickerView = new TimePickerBuilder(MyDiplomaIdentification.this,
                (date, v) -> {
                    endTime = (date.getYear() + 1900 + "");
                    loadTimeIntoTextView(tvTimeEnd, endTime);
                })
                .setType(new boolean[]{true, false, false, false, false, false})
                .setTitleText("设置结束时间")
                .build();
        return timePickerView;
    }

    /**
     * 弹出底部window上传照片
     */
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

    private void uploadInfo() {
        System.out.println("学历 " + diploma);
        System.out.println("母校 " + almaMaterName);
        System.out.println("时间 " + beginTime + " - " + endTime);
        System.out.println("证明 " + newDiplomaUrl);
        // 弹出第一个确认对话框
        showFirstDialog();
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

    /**
     * 显示第一个对话框
     */
    private void showFirstDialog() {
        // 判空
        if (diploma.isEmpty() || almaMaterName.isEmpty() || beginTime.isEmpty() ||
                endTime.isEmpty() ) {
            toastMessage("信息不完整 请重新填写");
        } else {
            /*
             * @setTitle 设置对话框标题
             * @setMessage 设置对话框消息提示
             * setXXX方法返回Dialog对象，因此可以链式设置属性
             */
            final AlertDialog.Builder normalDialog =
                    new AlertDialog.Builder(MyDiplomaIdentification.this);
            normalDialog.setTitle("提交")// 设置标题
                    .setMessage("本页面新提交的资料提交后需要工作人员审核通过后才展示，是否确认提交？") // 设置文本内容
                    .setPositiveButton("确定提交",
                            (dialog, which) -> {
                                updateInfo();//...To-do
                            })
                    .setNegativeButton("关闭",
                            (dialog, which) -> {
                                //...To-do
                            })
                    .show();// 显示
        }
    }

    /**
     * 将所有信息上传到服务器 等待审核
     */
    private void updateInfo() {
        JSONObject jsonParams = new JSONObject();
        almaMaterName = etAlmaMater.getText().toString();
        String schoolTime = beginTime + "-" + endTime;
        try {
            jsonParams.put("education", diploma);
            jsonParams.put("school", almaMaterName);
            jsonParams.put("schooltime", schoolTime);
            if (!newDiplomaUrl.isEmpty()) jsonParams.put("education_photo", newDiplomaUrl);
            jsonParams.put("examine_status", 1);
        } catch (Exception e) {
            KLog.e(e);
        }
        NormalApi.getInstance().getResult(MethodCode.UpDateInfo, jsonParams, new Subscriber<ResponseBody>() {
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
//                    KLog.json("请求结果", resultArgs);
                    showSecondDialog();
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    /**
     * 显示第二个对话框
     */
    private void showSecondDialog() {
        /*
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MyDiplomaIdentification.this);
        normalDialog.setTitle("提交完成"); // 设置标题
        normalDialog.setMessage("请等待客服审核资料。审核通过前您的资料不会有变化。"); // 设置文本内容
        normalDialog.setNegativeButton("关闭",
                (dialog, which) -> {
                    //...To-do
                    finish();
                });
        // 显示
        normalDialog.show();
    }

    @OnClick({R.id.iv_back_arrow, R.id.tv_submit, R.id.ly_diploma, R.id.ly_school,
            R.id.tv_begin_time, R.id.tv_end_time, R.id.layout_upload_diploma})
    void doDealWithLayouts(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.tv_submit:
                uploadInfo();
                break;
            case R.id.ly_diploma:
                showDiplomaListDialog();
                break;
            case R.id.ly_school:
                break;
            case R.id.tv_begin_time:
                initBeginTimePicker().show();
                setSubmitButtonEnable();
                break;
            case R.id.tv_end_time:
                initEndTimePicker().show();
                setSubmitButtonEnable();
                break;
            case R.id.layout_upload_diploma:
                doSetEduPhoto();
                setSubmitButtonEnable();
                break;
        }
    }

    // 将提交按钮设为可用
    void setSubmitButtonEnable() {
        mTvSubmit.setTextColor(Color.BLACK);
        mTvSubmit.setEnabled(true);
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
