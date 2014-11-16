package com.fzxt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.fzxt.ftp.FTP;
import com.fzxt.ftp.Result;
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends WantupBaseActivity {
	public  String mStrMSG = "";
	private String split = "&";
	private TextView current_numView;
	private TextView next_numView;
	private TextView pre_numView;
	private ImageView imageView;
	private Socket mSocket;
	
	private FTP ftp;
	private File imageFile;
	
	private String localIp = "0.0.0.0";
	private String waitIp;
	private String clinicId;
	
	
//	String localIp ;
	private AsyncImageLoader asyncImageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
//		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
//        //判断wifi是否开启  
//        if (!wifiManager.isWifiEnabled()) {  
//        	wifiManager.setWifiEnabled(true);    
//        }  
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();       
//        int ipAddress = wifiInfo.getIpAddress();   
//        ip = intToIp(ipAddress);   
        
		localIp = this.getIntent().getStringExtra("localIp");
		waitIp = this.getIntent().getStringExtra("waitIp");
		clinicId = this.getIntent().getStringExtra("clinicId");
		
		Thread receiveThread = new Thread(receiveRunnable);
		receiveThread.start();
		
		Thread heartbeatThread = new Thread(heartbeatRunnable);
		heartbeatThread.start();
		
		
		current_numView = (TextView) findViewById(R.id.current_num);
		next_numView = (TextView) findViewById(R.id.next_num);
		pre_numView = (TextView) findViewById(R.id.pre_num);
		imageView = (ImageView)findViewById(R.id.doctor_head_image);
		
		
		asyncImageLoader = new AsyncImageLoader();
		
		try {
        	ftp = new FTP(HttpUtil.hostName, HttpUtil.userName, HttpUtil.password);
			ftp.openConnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event){
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN) {
			exitSystem();
			return true;
		}
		return super.dispatchKeyEvent(event);
    }
	private void exitSystem(){
		MessageSocket.sendMessageToServer("exit");
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//线程:监听服务器发来的消息
	private Runnable receiveRunnable = new Runnable() {
		@Override
		public void run(){
			while (true){
				try {
					if(MessageSocket.getmSocket() != null){ // 如果socket信息丢失，在这里再次向服务器发起请求
						if(MessageSocket.mBufferedReader != null){
							MessageSocket.mBufferedReader.mark(8192);
							if ( (mStrMSG = MessageSocket.mBufferedReader.readLine()) != null ) {
								//消息换行
								mStrMSG += "\n";
								mHandler.sendMessage(mHandler.obtainMessage());
							}
						}
					}else{
						mSocket = new Socket(waitIp, HttpUtil.socketServerPort);
		    			//取得输入、输出流
		    			BufferedReader mBufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream(),"UTF-8"));
		    			PrintWriter mPrintWriter = new PrintWriter(mSocket.getOutputStream(), true);
		    			MessageSocket.setmSocket(mSocket);
		    			MessageSocket.setmBufferedReader(mBufferedReader);
		    			MessageSocket.setmPrintWriter(mPrintWriter);
						
		    			MessageSocket.sendMessageToServer("login");
					}
					Thread.sleep(3000);
				}catch (Exception e){
					
				}
			}
		}
	};
		
	public Handler mHandler	= new Handler(){
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			// 刷新
			try{
				if(mStrMSG != null && !mStrMSG.isEmpty() && mStrMSG.length()>1){
					System.out.println("收到来自服务器信息："+mStrMSG);
					String messageTmp = mStrMSG;
					String[] msgArr = messageTmp.split(split);
					if(msgArr.length > 1){
						
						String currentNum = msgArr[3];
						
						String currentName = msgArr[1];
						current_numView.setText(currentNum+"号"+currentName);
						String nextNum = msgArr[4];
						String nextName = msgArr[5];
						String preNum = msgArr[6];
						String preName = msgArr[7];
						
						next_numView.setText(nextNum+"号"+nextName);
						pre_numView.setText(preNum+"号"+preName);
						
						String fileName = "100400.jpg";
						imageFile = new File("/mnt/sdcard/"+fileName);
						if(!imageFile.exists()){
							
				            Result result = ftp.download(FTP.REMOTE_PATH, fileName, HttpUtil.localFilePath);
				            if (result.isSucceed()) {
				                Log.e("", "download ok...time:" + result.getTime() + " and size:" + result.getResponse());
				            } else {
				                Log.e("", "download fail");
				            }
						}
						if(imageFile.exists()){
							Bitmap cachedImage = asyncImageLoader.getLoacalBitmap(imageFile.getPath());//.loadImageFromUrl(imageUrl);
				            imageView.setImageBitmap(cachedImage);
						}
						/*Intent intent = new Intent();
						intent.setClass(MainActivity.this, ShowNameActivity.class);
						intent.putExtra("userinfo", messageTmp);
						startActivity(intent);*/
					}
				}
				
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	};
		
		
	//向服务器发送心跳包
	private Runnable heartbeatRunnable = new Runnable() {
		@Override
		public void run(){
			Boolean reconnect = false;
			while (true){
				
				try {
					BufferedReader mBufferedReader = null;
					PrintWriter mPrintWriter;
	        		//连接服务器
					Socket mSocket1 = new Socket(waitIp, HttpUtil.socketServerPort);
					if(mSocket1 != null){
		    			//取得输入、输出流
		    			mBufferedReader = new BufferedReader(new InputStreamReader(mSocket1.getInputStream(),"UTF-8"));
		    			mPrintWriter = new PrintWriter(mSocket1.getOutputStream(), true);
		    			if(mPrintWriter != null){
		    				mPrintWriter.print("heartbeat \n");
		    		    	mPrintWriter.flush();
		    		    	
		    			}
		    			if(reconnect){
							String heartBeat;
							/*if(mBufferedReader != null){
								mBufferedReader.mark(8192);
								if ( (heartBeat = mBufferedReader.readLine()) != null ) {
									System.out.println("自动重连成功...");
					    			MessageSocket.setmSocket(mSocket1);
					    			MessageSocket.setmBufferedReader(mBufferedReader);
					    			MessageSocket.setmPrintWriter(mPrintWriter);
								}
							}*/
							mSocket = new Socket(waitIp, HttpUtil.socketServerPort);
			    			//取得输入、输出流
			    			BufferedReader mBufferedReader1 = new BufferedReader(new InputStreamReader(mSocket.getInputStream(),"UTF-8"));
			    			PrintWriter mPrintWriter1 = new PrintWriter(mSocket.getOutputStream(), true);
			    			MessageSocket.setmSocket(mSocket);
			    			MessageSocket.setmBufferedReader(mBufferedReader1);
			    			MessageSocket.setmPrintWriter(mPrintWriter1);
							
			    			MessageSocket.sendMessageToServer("login ");
		    			}
		    			mSocket1.close();
					}
					
					Thread.sleep(30000);
					reconnect = false;
				}catch (Exception e){
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						
					}
					reconnect = true;
				}
			}
		}
	};
	
	public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }
    
    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }
    
}
