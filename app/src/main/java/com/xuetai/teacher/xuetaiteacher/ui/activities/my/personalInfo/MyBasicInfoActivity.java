package com.xuetai.teacher.xuetaiteacher.ui.activities.my.personalInfo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
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

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.socks.library.KLog;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.xuetai.teacher.xuetaiteacher.R;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.api.UploadImageApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.models.ProvinceBean;
import com.xuetai.teacher.xuetaiteacher.ui.activities.MainActivity;
import com.xuetai.teacher.xuetaiteacher.ui.activities.PersonalActivity;
import com.xuetai.teacher.xuetaiteacher.utils.ScreenUtil;
import com.xuetai.teacher.xuetaiteacher.utils.StatusBarUtil;
import com.xuetai.teacher.xuetaiteacher.view.AvatarBottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

public class MyBasicInfoActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA = 10;
    private final int REQUEST_ALBUM_OK = 11;
    private static final int CROP_REQUEST_CODE = 15;

    String realName;
    String newAvatarUrl;
    String sex;
    String cityName;

    private String mCropFilePath;//裁剪图片的输出路径，intent无法传递大图（bitmap）,只能通过uri传递
    private Uri carmerauri;
    File storageDir;
    private Bitmap bm = null;

    OptionsPickerView pvOptions;
    OptionsPickerView sexOptions;

    // 性别
    ArrayList<String> sexList = new ArrayList<>();

    // 省
    ArrayList<ProvinceBean> provinceBeanList = new ArrayList<>();
    ArrayList<String> provinceNameList = new ArrayList<>();

    // 市
    ArrayList<String> cities;
    ArrayList<List<String>> cityList = new ArrayList<>();

    //  区/县
    ArrayList<String> district;
    ArrayList<List<String>> districts;
    ArrayList<List<List<String>>> districtList = new ArrayList<>();

    @BindView(R.id.tv_submit)
    TextView mTvSubmit;

    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.et_name)
    EditText mEtName;
    @BindView(R.id.tv_gender)
    TextView mTvGender;
    @BindView(R.id.tv_location)
    TextView mTvLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_basic_info);

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

        requestPermissions();
        loadInfo();
        initSexPicker();
        initAddressPicker();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CAMERA:
                if(carmerauri != null && resultCode !=0) {
                    startImageZoom(carmerauri);
                } else {
                    toastMessage("拍照失败");
                }
                break;
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
     * android 7.0系统解决拍照的问题
     */
    private void initPhotoError() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    upLoadImage(msg.getData().getString("bitmap"), mIvAvatar, bm);

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
                    newAvatarUrl = resultOject.getString("url");
                    Glide.with(getApplicationContext()).load(newAvatarUrl).into(mIvAvatar);
                    toastMessage("头像上传成功");
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    //图片压缩
    private void compressfile(String imagePath) {
        String thumImagePath =
                ScreenUtil.compressImage(imagePath,
                        "/mnt/sdcard/XueTaiTeacher/DingNiuFuDao"
                                + File.separator + new Date().getTime() + ".jpg", 30);

        bm = BitmapFactory.decodeFile(thumImagePath);

        String a = ScreenUtil.ImageCompressL(64.00, bm);

        Message message = new Message();
        message.what = 1;
        Bundle bundle = new Bundle();
        bundle.putString("bitmap", a);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    // 裁剪缩放图片
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
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("aspectX", 200);
        intent.putExtra("aspectY", 200);
        intent.putExtra("crop", true);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }


    /**
     * 配置性别picker
     */
    private void initSexPicker() {
        sexList.add("女");
        sexList.add("男");

        sexOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {

            String sexPicked = sexList.get(options1);
            mTvGender.setText(sexPicked);
            sex = options1 + "";
        }).build();

        sexOptions.setPicker(sexList);
        sexOptions.setTitleText("请选择性别");
    }

    /**
     * 配置三级联动地区picker
     */
    private void initAddressPicker() {
        // 获取JSON数据
        String province_data_json = getJson(this, "province_data.json");
        // 解析JSON数据
        parseJson(province_data_json);

        pvOptions = new OptionsPickerBuilder(MyBasicInfoActivity.this, (options1, options2, options3, v) -> {

            String city = provinceBeanList.get(options1).getPickerViewText();
            String address;
            //  如果是直辖市或者特别行政区只设置市和区/县
            if ("北京市".equals(city) || "上海市".equals(city) || "天津市".equals(city) || "重庆市".equals(city) || "澳门".equals(city) || "香港".equals(city)) {
                address = provinceBeanList.get(options1).getPickerViewText()
                        + "-" + districtList.get(options1).get(options2).get(options3);
            } else {
                address = provinceBeanList.get(options1).getPickerViewText()
                        + "-" + cityList.get(options1).get(options2)
                        + "-" + districtList.get(options1).get(options2).get(options3);
            }
            mTvLocation.setText(address);
            cityName = address;
        }).build();

        // 设置三级联动效果
        pvOptions.setPicker(provinceNameList, cityList, districtList);

        // 设置默认选中的三级项目
        pvOptions.setSelectOptions(7, 0, 0);

        // 设置标题
        pvOptions.setTitleText("请选择城市");
    }

    // 解析JSON填充集合
    public void parseJson(String str) {
        try {
            //  获取json中的数组
            JSONArray jsonArray = new JSONArray(str);
            //  遍历数据组
            for (int i = 0; i < jsonArray.length(); i++) {
                //  获取省份的对象
                JSONObject provinceObject = jsonArray.optJSONObject(i);
                //  获取省份名称放入集合
                String provinceName = provinceObject.getString("name");
                provinceBeanList.add(new ProvinceBean(provinceName));
                provinceNameList.add(provinceName);
                //  获取城市数组
                JSONArray cityArray = provinceObject.optJSONArray("city");
                cities = new ArrayList<>();//   声明存放城市的集合
                districts = new ArrayList<>();//声明存放区县集合的集合
                //  遍历城市数组
                for (int j = 0; j < cityArray.length(); j++) {
                    //  获取城市对象
                    JSONObject cityObject = cityArray.optJSONObject(j);
                    //  将城市放入集合
                    String cityName = cityObject.optString("name");
                    cities.add(cityName);
                    district = new ArrayList<>();// 声明存放区县的集合
                    //  获取区县的数组
                    JSONArray areaArray = cityObject.optJSONArray("area");
                    //  遍历区县数组，获取到区县名称并放入集合
                    for (int k = 0; k < areaArray.length(); k++) {
                        String areaName = areaArray.getString(k);
                        district.add(areaName);
                    }
                    //  将区县的集合放入集合
                    districts.add(district);
                }
                //  将存放区县集合的集合放入集合
                districtList.add(districts);
                //  将存放城市的集合放入集合
                cityList.add(cities);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getJson(Context context, String fileName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bufferedInputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toString();
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
                        String iconUrl;
                        String realName;
                        String gender;
                        String location;
                        String note;
                        try {
                            iconUrl = infoJson.getString("head_photo");
                            realName = infoJson.getString("real_name");
                            gender = infoJson.getString("gender");
                            location = infoJson.getString("location");
                            note = infoJson.getString("note");
                            loadAvatar(iconUrl, mIvAvatar);
                            loadName(realName, mEtName);
                            loadGender(gender, mTvGender);
                            loadLocation(location, mTvLocation);
                        } catch (Exception e) {
                            KLog.e(e);
                        }
                    }
                });
    }

    // 加载头像
    private void loadAvatar(String url, ImageView imageView) {
        if (!url.isEmpty()) {
            Glide.with(this).load(url).into(imageView);
            newAvatarUrl = url;
        }
    }

    // 加载姓名
    private void loadName(String name, EditText editText) {
        editText.setText(name);
        realName = name;

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                realName = s.toString();
                setSubmitButtonEnable();
            }
        };

        editText.addTextChangedListener(textWatcher);
    }

    // 加载性别
    private void loadGender(String gender, TextView textView) {
        if (gender.equals("0")) textView.setText("女");
        else textView.setText("男");
        sex = gender;
    }

    // 加载地区
    private void loadLocation(String location, TextView textView) {
        textView.setText(location);
        cityName = location;
    }

    /**
     * 请求相关权限
     * 使用了RxPermissions库
     */
    private void requestPermissions() {
        RxPermissions rxPermissions = new RxPermissions(MyBasicInfoActivity.this);
        rxPermissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (!aBoolean) {
                        toastMessage("需要权限！！！！！！！");
                        finish();
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

    // 返回上一级页面
    @OnClick(R.id.iv_back_arrow)
    void doBack() {
        finish();
    }

    // 提交
    @OnClick(R.id.tv_submit)
    void doSubmit() {
        if (!(mEtName.length() == 0)) showFirstDialog();
        else toastMessage("姓名不能为空");
    }

    /**
     * 上传信息至服务器
     */
    private void updateInfo() {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("head_photo", newAvatarUrl);
            jsonParams.put("real_name", realName);
            jsonParams.put("gender", sex);
            jsonParams.put("location", cityName);
            KLog.json(jsonParams.toString());
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
                    KLog.json(resultArgs);
                    showSecondDialog();
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    // 处理各项点击事件
    @OnClick({R.id.ly_my_avatar, R.id.ly_my_gender, R.id.ly_my_introduction, R.id.ly_my_location})
    void doDealWithThem(View view) {
        switch (view.getId()) {
            case R.id.ly_my_avatar:
                AvatarBottomSheetDialog avatarBottomSheetDialog = new AvatarBottomSheetDialog();
                avatarBottomSheetDialog.show(getSupportFragmentManager(), "Dialog");
                avatarBottomSheetDialog.setClicklistener(new AvatarBottomSheetDialog.ClickListenerInterface() {
                    @Override
                    public void doCamera() {
                        showCamera();
                    }

                    @Override
                    public void doGallery() {
                        initGallery();
                    }
                });
                break;
            case R.id.ly_my_gender:
                sexOptions.show();
                break;
            case R.id.ly_my_introduction:
                Intent intent = new Intent(this, MyIntroductionActivity.class);
                startActivity(intent);
                break;
            case R.id.ly_my_location:
                pvOptions.show();
                break;
        }
        setSubmitButtonEnable();
    }

//    @OnTextChanged(R.id.et_name)
//    void doNameChanging() {
//        realName = mEtName.getText().toString();
//    }

    /**
     * 显示第一个对话框
     */
    private void showFirstDialog() {
        /*
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(MyBasicInfoActivity.this);
        normalDialog.setTitle("提交"); // 设置标题
        normalDialog.setMessage("本页面新提交的资料提交后需要工作人员审核通过后才展示，是否确认提交？"); // 设置文本内容
        normalDialog.setPositiveButton("确定提交",
                (dialog, which) -> {
                    updateInfo();//...To-do
                });
        normalDialog.setNegativeButton("关闭",
                (dialog, which) -> {
                    //...To-do
                });
        // 显示
        normalDialog.show();
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
                new AlertDialog.Builder(MyBasicInfoActivity.this);
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
