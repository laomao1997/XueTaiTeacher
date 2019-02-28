package com.xuetai.teacher.xuetaiteacher.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xuetai.teacher.xuetaiteacher.R;

import java.io.File;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AvatarBottomSheetDialog extends BottomSheetDialogFragment {

    int photoArgs = 0; // 操作参数 1-打开相机 2-打开图库

    private ClickListenerInterface clickListenerInterface;

    @BindView(R.id.tv_btn_add_photo_camera)
    TextView addCameraPhotoTextView;

    @BindView(R.id.tv_btn_add_photo_gallery)
    TextView addGalleryPhotoTextView;

    public static AvatarBottomSheetDialog newInstance() {
        return new AvatarBottomSheetDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_dialog_bottom_sheet_avatar, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick({R.id.tv_btn_add_photo_camera, R.id.tv_btn_add_photo_gallery})
    void doOpenCamera(View view) {
        switch (view.getId()) {
            case R.id.tv_btn_add_photo_camera: {
                clickListenerInterface.doCamera();
                break;
            }
            case R.id.tv_btn_add_photo_gallery: {
                clickListenerInterface.doGallery();
                break;
            }
        }
        System.out.println(photoArgs);
        this.dismiss();
    }

    // 接口 - 用来暴露给外部的activity，顺势使之可以处理fragment内部的onclick方法
    public interface ClickListenerInterface {

        void doCamera();

        void doGallery();

    }

    // 接口的实现，外界通过使用此方法便可以调用接口
    public void setClicklistener(ClickListenerInterface clickListenerInterface) {
        this.clickListenerInterface = clickListenerInterface;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
