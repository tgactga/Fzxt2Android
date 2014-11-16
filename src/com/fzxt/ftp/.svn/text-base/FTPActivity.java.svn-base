/*package com.fzxt.ftp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import com.fzxt.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

*//**
 * 测试Activity.
 * 
 * @author cui_tao
 *
 *//*
public class FTPActivity extends Activity {
    *//**
     * 标签.
     *//*
    static final String TAG = "FTPActivity";

    *//**
     * FTP.
     *//*
    private FTP ftp;

    *//**
     * FTP文件集合.
     *//*
    private List<FTPFile> remoteFile;

    *//**
     * 本地文件集合.
     *//*
    private List<File> localFile;

    *//**
     * 本地根目录.
     *//*
    private static final String LOCAL_PATH = "/mnt/sdcard/";

    *//**
     * 当前选中项.
     *//*
    private int position = 0;

    *//**
     * ListView.
     *//*
    private ListView listMain;

    *//**
     * 切换到本地按钮.
     *//*
    private Button buttonChangeLocal;

    *//**
     * 切换到FTP按钮.
     *//*
    private Button buttonChangeRemote;

    *//**
     * 下载按钮.
     *//*
    private Button buttonDownload;

    *//**
     * 上传按钮.
     *//*
    private Button buttonUploading;

    *//**
     * 断开连接按钮.
     *//*
    private Button buttonClose;

    *//**
     * 服务器名.
     *//*
    private String hostName;

    *//**
     * 用户名.
     *//*
    private String userName;

    *//**
     * 密码.
     *//*
    private String password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化视图
        initView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 关闭服务
        try {
            ftp.closeConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    *//**
     * 初始化视图.
     *//*
    private void initView() {
        // 初始化控件
        // 获取登录信息
        loginConfig();
        // ListView单击
        listMain.setOnItemClickListener(listMainItemClick);
        // ListView选中项改变
        listMain.setOnItemSelectedListener(listMainItemSelected);
        // 切换到本地
        buttonChangeLocal.setOnClickListener(buttonChangeLocalClick);
        // 切换到FTP
        buttonChangeRemote.setOnClickListener(buttonChangeRemoteClick);
        // 下载
        buttonDownload.setOnClickListener(buttonDownloadClick);
        // 上传
        buttonUploading.setOnClickListener(buttonUploadingClick);
        // 断开FTP服务
        buttonClose.setOnClickListener(buttonCloseClick);
        // 加载FTP视图
        loadRemoteView();
    }

    *//**
     * 获取登录信息.
     *//*
    private void loginConfig() {
        Intent intent = getIntent();
        hostName = intent.getStringExtra("hostName");
        userName = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");
    }

    *//**
     * 加载FTP视图.
     *//*
    private void loadRemoteView() {
        try {
            if (ftp != null) {
                // 关闭FTP服务
                ftp.closeConnect();
            }
            // 初始化FTP
            Log.e("hostName", hostName);
            Log.e("userName", userName);
            Log.e("passwrod", password);
            ftp = new FTP(hostName, userName, password);
            // 打开FTP服务
            ftp.openConnect();
            // 初始化FTP列表
            remoteFile = new ArrayList<FTPFile>();
            // 更改控件可见
            buttonChangeLocal.setVisibility(Button.VISIBLE);
            buttonChangeRemote.setVisibility(Button.INVISIBLE);
            buttonDownload.setVisibility(Button.VISIBLE);
            buttonUploading.setVisibility(Button.INVISIBLE);
            // 加载FTP列表
            remoteFile = ftp.listFiles(FTP.REMOTE_PATH);
            // FTP列表适配器
            RemoteAdapter adapter = new RemoteAdapter(this, remoteFile);
            // 加载数据到ListView
            listMain.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    *//**
     * 加载本地视图.
     *//*
    private void loadLocalView() {
        // 初始化本地列表
        localFile = new ArrayList<File>();
        // 更改控件可见
        buttonChangeLocal.setVisibility(Button.INVISIBLE);
        buttonChangeRemote.setVisibility(Button.VISIBLE);
        buttonDownload.setVisibility(Button.INVISIBLE);
        buttonUploading.setVisibility(Button.VISIBLE);
        // 加载本地列表
        getFileDir(LOCAL_PATH);
        // 本地列表适配器
        LocalAdapter adapter = new LocalAdapter(this, localFile);
        // 加载数据到ListView
        listMain.setAdapter(adapter);
    }

    *//**
     * 加载本地列表.
     * @param filePath 文件目录
     *//*
    private void getFileDir(String filePath) {
        // 获取根目录
        File f = new File(filePath);
        // 获取根目录下所有文件
        File[] files = f.listFiles();
        // 循环添加到本地列表
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isHidden() || file.getName().equals("LOST.DIR")) {
                continue;
            }
            localFile.add(file);
        }
    }

    *//**
     * ListView单击事件.
     *//*
    private OnItemClickListener listMainItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int location, long arg3) {
            if (buttonChangeLocal.getVisibility() == Button.VISIBLE) {
                Toast.makeText(FTPActivity.this, remoteFile.get(location).getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FTPActivity.this, localFile.get(location).getName(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    *//**
     * ListView选中项改变事件.
     *//*
    private OnItemSelectedListener listMainItemSelected = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View view, int location, long arg3) {
            // 获取当前选中项
            FTPActivity.this.position = location;
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

        }

    };

    *//**
     * 切换到本地.
     *//*
    private OnClickListener buttonChangeLocalClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // 加载本地视图
            loadLocalView();
        }
    };

    *//**
     * 切换到FTP.
     *//*
    private OnClickListener buttonChangeRemoteClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // 加载FTP视图
            loadRemoteView();
        }
    };

    *//**
     * 下载.
     *//*
    private OnClickListener buttonDownloadClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Result result = null;
            try {
                // 下载
                result = ftp.download(FTP.REMOTE_PATH, remoteFile.get(position).getName(), LOCAL_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (result.isSucceed()) {
                Log.e(TAG, "download ok...time:" + result.getTime() + " and size:" + result.getResponse());
                Toast.makeText(FTPActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "download fail");
                Toast.makeText(FTPActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    *//**
     * 上传.
     *//*
    private OnClickListener buttonUploadingClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Result result = null;
            try {
                // 上传
                result = ftp.uploading(localFile.get(position), FTP.REMOTE_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (result.isSucceed()) {
                Log.e(TAG, "uploading ok...time:" + result.getTime() + " and size:" + result.getResponse());
                Toast.makeText(FTPActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "uploading fail");
                Toast.makeText(FTPActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    *//**
     * 断开服务.
     *//*
    private OnClickListener buttonCloseClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            try {
                // 关闭FTP服务
                ftp.closeConnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
        }
    };

}*/