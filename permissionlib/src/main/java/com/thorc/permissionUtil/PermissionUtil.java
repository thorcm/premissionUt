package com.thorc.permissionUtil;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.List;

/**
 * Created by housenchao on 2018/10/15.
 */

public class PermissionUtil {
    private static final String TAG = PermissionUtil.class.getSimpleName();

    private PermissionFragment fragment;

    public PermissionUtil(Activity activity) {
        fragment = getRxPermissionsFragment(activity);
    }

    private PermissionFragment getRxPermissionsFragment(Activity activity) {
        PermissionFragment fragment = (PermissionFragment) activity.getFragmentManager().findFragmentByTag(TAG);
        try {
            if (fragment == null) {
                fragment = new PermissionFragment();
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(fragment, TAG)
                        .commitAllowingStateLoss();
                fragmentManager.executePendingTransactions();
            }
        }catch (Exception e){

        }
        return fragment;
    }


    //检查是否拥有权限
    public static boolean hasPermissions(Context context, String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // DANGER ZONE!!! Changing this will break the library.
            return true;
        }
        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 外部使用 申请权限
     *
     * @param permissions 申请授权的权限
     * @param listener    授权回调的监听
     */
    public void requestPermissions(String[] permissions, PermissionListener listener) {
        if(fragment!=null){
            fragment.setListener(listener);
            fragment.requestPermissions(permissions);
        }
    }


    //请求权限
    public static void reqPermissions(Activity activity, String[] permissions, final PermissionsListener listener) {
        //创建PermissionUtil对象，参数为继承自V4包的 FragmentActivity
        PermissionUtil permissionUtil = new PermissionUtil(activity);
        //调用requestPermissions
        permissionUtil.requestPermissions(permissions, new PermissionListener() {
            @Override
            public void onGranted() {
                if (listener != null) {
                    listener.onGranted();
                }
            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                if (listener != null) {
                    listener.onDenied(deniedPermission, false);
                }
            }

            @Override
            public void onShouldShowRationale(List<String> deniedPermission) {
                if (listener != null) {
                    listener.onDenied(deniedPermission, true);
                }
                // 这些权限被用户总是拒绝。
            }
        });
    }
}
