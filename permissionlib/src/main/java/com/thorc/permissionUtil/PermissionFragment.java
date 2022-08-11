package com.thorc.permissionUtil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


/**
 * @author housenchao
 */
public class PermissionFragment extends Fragment {

    Activity activity;

    /**
     * requestCode
     */
    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private PermissionListener listener;

    public void setListener(PermissionListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(@NonNull String[] permissions) {
        try {
            List<String> requestPermissionList = new ArrayList<>();
            //找出所有未授权的权限
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionList.add(permission);
                }
            }

            if (requestPermissionList.isEmpty()) {
                //已经全部授权
                permissionAllGranted();
            } else {
                //申请授权
                requestPermissions(requestPermissionList.toArray(new String[requestPermissionList.size()]), PERMISSIONS_REQUEST_CODE);
            }
        }catch (Exception e){
        }catch (Error e){

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PERMISSIONS_REQUEST_CODE) {
            return;
        }

        try{
            if (grantResults.length > 0) {
                List<String> deniedPermissionList = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissionList.add(permissions[i]);
                    }
                }

                if (deniedPermissionList.isEmpty()) {
                    //已经全部授权
                    permissionAllGranted();
                } else {

                    //勾选了对话框中”Don’t ask again”的选项, 返回false
                    for (String deniedPermission : deniedPermissionList) {
                        boolean flag = shouldShowRequestPermissionRationale(deniedPermission);
                        if (!flag) {
                            //拒绝授权
                            permissionShouldShowRationale(deniedPermissionList);
                            return;
                        }
                    }
                    //拒绝授权
                    permissionHasDenied(deniedPermissionList);
                }
            }
        }catch (Exception e){
        }
    }


    /**
     * 权限全部已经授权
     */
    private void permissionAllGranted() {
        if (listener != null) {
            listener.onGranted();
        }
    }

    /**
     * 权限被拒绝
     *
     * @param deniedList 被拒绝的权限List
     */
    private void permissionHasDenied(List<String> deniedList) {
        if (listener != null) {
            listener.onDenied(deniedList);
        }

    }

    /**
     * 权限被拒绝并且勾选了不在询问
     *
     * @param deniedList 勾选了不在询问的权限List
     */
    private void permissionShouldShowRationale(List<String> deniedList) {
        if (listener != null) {
            listener.onShouldShowRationale(deniedList);
        }
    }

}
