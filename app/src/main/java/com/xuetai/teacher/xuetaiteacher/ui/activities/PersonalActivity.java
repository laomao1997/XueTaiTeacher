package com.xuetai.teacher.xuetaiteacher.ui.activities;

import android.Manifest;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
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
import com.xuetai.teacher.xuetaiteacher.api.CachingControlInterceptor;
import com.xuetai.teacher.xuetaiteacher.api.NormalApi;
import com.xuetai.teacher.xuetaiteacher.api.UploadImageApi;
import com.xuetai.teacher.xuetaiteacher.api.UploadInfoApi;
import com.xuetai.teacher.xuetaiteacher.constant.MethodCode;
import com.xuetai.teacher.xuetaiteacher.models.ProvinceBean;
import com.xuetai.teacher.xuetaiteacher.utils.ScreenUtil;
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
import okhttp3.ResponseBody;
import rx.Subscriber;

public class PersonalActivity extends AppCompatActivity {

    private final int REQUEST_CAMERA = 10;
    private final int REQUEST_ALBUM_OK = 11;
    private static final int CROP_REQUEST_CODE = 15;

    String realName;
    String newAvatarUrl;
    String sex = "";
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

    @BindView(R.id.iv_icon)
    ImageView mImageViewIcon;
    @BindView(R.id.et_real_name)
    EditText mEditTextRealName;
    @BindView(R.id.tv_location)
    TextView mTextViewAddress;
    @BindView(R.id.tv_sex)
    TextView mTextViewSex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        ButterKnife.bind(this);

        File file = new File("/mnt/sdcard/XueTaiTeacher/DingNiuFuDao");
        // 判断文件夹是否存在如果不存在就创建, 否则不创建
        if (!file.exists()) {
            // 通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file.mkdirs();
        }
        storageDir = file;
        mCropFilePath = storageDir + "tmp.jpg";

        checkLogin();

        initPhotoError();

        requestPermissions();

        initSexPicker();

        initAddressPicker();

    }

    private void checkLogin() {
        SharedPreferences userPreference = getSharedPreferences("setting", 0);
        String info = userPreference.getString("logged", "No Info");
        String sid = userPreference.getString("sid", "default");
        CachingControlInterceptor cachingControlInterceptor = new CachingControlInterceptor();
        cachingControlInterceptor.setUserToken(sid);
    }

    /**
     * 配置性别picker
     */
    private void initSexPicker() {
        sexList.add("男");
        sexList.add("女");

        sexOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {

            String sexPicked = sexList.get(options1);
            mTextViewSex.setText(sexPicked);
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

        pvOptions = new OptionsPickerBuilder(PersonalActivity.this, (options1, options2, options3, v) -> {

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
            mTextViewAddress.setText(address);
            mTextViewAddress.setTextColor(Color.rgb(48, 48, 48));
        }).build();

        // 设置三级联动效果
        pvOptions.setPicker(provinceNameList, cityList, districtList);

        // 设置默认选中的三级项目
        pvOptions.setSelectOptions(7, 0, 0);

        // 设置标题
        pvOptions.setTitleText("请选择城市");
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
     * 请求相关权限
     * 使用了RxPermissions库
     */
    private void requestPermissions() {
        RxPermissions rxPermissions = new RxPermissions(PersonalActivity.this);
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

    @OnClick(R.id.layout_upload_icon)
    public void doUploadNewAvatar() {
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
    }

    @OnClick(R.id.image_back)
    public void doBack() {
        finish();
    }

    @OnClick(R.id.layout_location)
    public void doChooseLocation() {
        pvOptions.show();
    }

    @OnClick(R.id.layout_set_sex)
    public void doSetSex() {
        sexOptions.show();
    }

    @OnClick(R.id.tv_next_step_button)
    public void doNextStep() {
        completePersonalInfo();
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

        String a = ScreenUtil.ImageCompressL(64.00, bm);

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

                    upLoadImage(msg.getData().getString("bitmap"), mImageViewIcon, bm);

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
                    Glide.with(getApplicationContext()).load(newAvatarUrl).into(mImageViewIcon);
                    toastMessage("头像上传成功");
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
    }

    /**
     * 完成第一步个人信息的填写
     * 并上传到服务器
     */
    private void completePersonalInfo() {
        realName = mEditTextRealName.getText().toString();
        if (mTextViewSex.getText().toString().equals("男")) sex = "1";
        if (mTextViewSex.getText().toString().equals("女")) sex = "0";
        cityName = mTextViewAddress.getText().toString();
        if (realName.equals("")) toastMessage("请输入真实姓名");
        else if (sex.equals("")) toastMessage("请设置性别");
        else if (cityName.equals("请选择城市")) toastMessage("请选择城市");
        else {
            JSONObject jsonParams = new JSONObject();
            try {
                jsonParams.put("head_photo", newAvatarUrl);
                jsonParams.put("real_name", realName);
                jsonParams.put("gender", sex);
                jsonParams.put("location", cityName);
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
                        startActivity(new Intent(PersonalActivity.this, EducationInfoActivity.class));
                    } catch (Exception e) {
                        KLog.e(e);
                    }
                }
            });
        }
    }

    /**
     * 将所有信息提交至服务器
     * 以待审核
     * @param url 头像URL的地址
     * @param name 名字
     * @param gender 性别 0-女 1-男
     * @param location 所在地区
     */
    private void updateInfo(String url, String name, String gender, String location) {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("real_name", name);
            jsonParams.put("head_photo", url);
            jsonParams.put("gender", gender);
            jsonParams.put("location", location);
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
                } catch (Exception e) {
                    KLog.e(e);
                }
            }
        });
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

    /**
     * 显示一个短时间的提示信息
     *
     * @param message 将要提示的信息 String类型
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
