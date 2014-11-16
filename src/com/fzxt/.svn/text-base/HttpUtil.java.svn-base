package com.fzxt;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;


@SuppressLint("NewApi")
public class HttpUtil {

	
	//创建HttpClient对象
	public static HttpClient httpClient = new DefaultHttpClient();
	//服务跳转的URL不变的那部分
	public static String serverIp = "10.35.51.73";
	public static int socketServerPort = 9094;
	public static String webServerPort = "8080";
	
	public static String hostName = "192.168.12.14";
	public static String userName = "fzxtftp";
	public static String password = "fzxtftp";
	public static String localFilePath = "/mnt/sdcard/";
	
//	static{
//		Properties props = new Properties();     
//        InputStream in = HttpUtil.class.getResourceAsStream("netconfig.properties");     
//        try {     
//            props.load(in);
//            serverIp = props.getProperty("serverIp");
//            socketServerPort = Integer.parseInt(props.getProperty("socketServerPort"));
//            webServerPort = props.getProperty("webServerPort");
//        } catch (IOException e) {     
//            e.printStackTrace();     
//        }
//	}
	
	
	
	
//	public static final String split = "|";
	public static final String BASE_URL = "http://"+serverIp+":"+webServerPort+"/fzxt_tj/";
	public static final String serverurl = "http://"+serverIp+":"+webServerPort+"/fzxt_tj/update.xml";
	public static final String key = "1111111111111111";
	//请求超时时间20秒
	public static final long timeout = 20000;
	//系统版本号
	public static final int OS_VERSION = android.os.Build.VERSION.SDK_INT;	//15以下使用主线程同步请求
	//自定义的未登录时response返回的状态码，与服务端的 filter.LoginCheckFilter对应
	private static final int noLoginErrorCode = 99;
	public static final int limit = 10;
	/**
	 * 
	 *  url 发送请求的URL
	 *  服务器响应字符串
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 *  
	 */
	public static String getRequest(String url) throws ClientProtocolException, IOException{
		//创建HttpGet对象
		HttpGet get = new HttpGet(url);
		// 发送GET请求
		HttpResponse httpResponse =  httpClient.execute(get);
		// 如果服务器成功地返回响应
		if(httpResponse.getStatusLine().getStatusCode()==200){
			// 获取服务器响应字符串
			String result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		}
		return null;
	}
	
	private static String doPostRequest(String url,Map<String,String> rawParams) throws Exception  {
		// 创建HttpPost对象
		HttpPost post = new HttpPost(url);
		// 如果传递参数个数比较多的话可以对传递的参数进行封装
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for(String key:rawParams.keySet()){
			//封装请求参数
			params.add(new BasicNameValuePair(key,rawParams.get(key)));
		}
		// 设置请求参数
		post.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
		// 发送POST请求
		HttpResponse httpResponse = httpClient.execute(post);
		int returnCode = httpResponse.getStatusLine().getStatusCode();
		if(returnCode==200){
			// 获取服务器响应字符串
			String result = EntityUtils.toString(httpResponse.getEntity());
			return result;
		}else if(returnCode==noLoginErrorCode){
			throw new Exception();
		}
		return null;
	}
	
	/**
	 * 发送同步http请求
	 * @param url 发送请求的URL
	 * @param params 请求参数
	 * @return 服务器响应字符串
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String postRequest(String url,Map<String,String> rawParams) throws Exception     {
		if(OS_VERSION < 15){
			return doPostRequest(url, rawParams);
		}
		final Map retMap = new HashMap();
		final Object lock = new Object();
		postRequestAsyn(url, rawParams, new Callback(){
			
			@Override
			public void call(String result, Exception e) {
				retMap.put("result", result);
				retMap.put("exception", e);
				synchronized(lock){
					lock.notifyAll();
				}
			}
		});
		synchronized(lock){
			lock.wait(timeout);	//20秒超时
		}
		if(!retMap.containsKey("result")){
			throw new Exception("请求超时");
		}
		Exception e = (Exception)retMap.get("exception");
		if(e != null){
			throw e;
		}
		Object result = retMap.get("result");
		return result == null ? null : result.toString();
	}
	
	/**
	 * 异步发送http请求的回调接口
	 * 
	 */
	public static interface Callback{
		/**
		 * 回调方法
		 * @param result	服务器响应的字符串
		 * @param e		请求过程中抛出的异常，为null表示无异常
		 */
		public void call(String result, Exception e);
	}
	/**
	 * 异步发送http请求
	 * @param url	请求url
	 * @param rawParams		请求参数
	 * @param callback	回调方法
	 */
	public static void postRequestAsyn(final String url, final Map<String,String> rawParams, 
			final Callback callback) {
		new Thread(){
			public void run() {
				try {
					String result = doPostRequest(url, rawParams);
					callback.call(result, null);
				} catch (Exception e) {
					callback.call("", e);
				}
			}
		}.start();
	}
	
	
	
	/***************************************提交异步任务工具******************************************/
	/**
	 * 异步任务参数，暂时没用到
	 */
	private static Object[] taskParam = new Object[]{};
	
	/**
	 * 提交异步任务
	 * @author chris
	 * @param context
	 * @param netTask	网络工作任务
	 * @param uiTask	UI工作任务
	 * @param loadMask	是否使用遮罩
	 * @param checkLogin 是否检查登录状态
	 */
	@SuppressWarnings("unchecked")
	public static void postTask(Context context,  NetTask netTask, 
			UiTask uiTask, boolean loadMask){
		WantupAsyncTask  task = new WantupAsyncTask (context, netTask, uiTask, loadMask);
		task.execute(taskParam);
	}
	
	@SuppressWarnings("rawtypes")
	public static class WantupAsyncTask  extends AsyncTask{
		private NetTask netTask;
		private UiTask uiTask;
		private boolean loadMask = false;
		private MyDialog proDia;	//加载遮罩
		private Exception exception;	//异常处理
		private Context context;
		
		WantupAsyncTask(Context context, NetTask netTask, UiTask uiTask, 
				boolean loadMask){
			this.context = context;
			this.netTask = netTask;
			this.uiTask = uiTask;
			this.loadMask = loadMask;
		}
		@Override
		protected Object doInBackground(Object... arg0) {
			//后台执行网络任务
			try{
				if(netTask != null){
					return this.netTask.execute();
				}
				return null;
			}catch(Exception e){
				if(proDia != null){
					proDia.dismiss();
				}
				this.exception = e;
				return null;
			}
		}
		@Override
		protected void onCancelled() {
			if(proDia != null){
				proDia.dismiss();
			}
			super.onCancelled();
		}
		@Override
		protected void onPostExecute(Object result) {
			//后台网络任务执行完成后的动作
			if(uiTask != null){
				this.uiTask.execute(result, exception);
			}
			if(proDia != null){
				try{
					proDia.dismiss();
				}catch(IllegalArgumentException e){
					//屏幕旋转时可能产生该异常
					if(e.getMessage() != null){
						Log.w("onPostExecute", e.getMessage());
					}
				}
			}
		}
		@Override
		protected void onPreExecute() {
			//启动任务之前的动作
			if(loadMask && context != null){
				try {
					proDia = new MyDialog(context, R.style.MyDialogStyle, R.layout.loading, false).create();
					proDia.setCancelable(true);
					proDia.setOnCancelListener(new android.content.DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							WantupAsyncTask.this.cancel(true);
						}
					});
					proDia.show();
				} catch (Exception e) {
					if(proDia != null){
						proDia.dismiss();
					}
				}
			}
		}
		@SuppressWarnings("unchecked")
		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
		}
	}
	
	public static interface NetTask<T>{
		/**
		 * 网络任务
		 * @author chris
		 * @return	返回值将自动传递给ui任务
		 * @throws Exception
		 */
		public T execute() throws Exception;
	}
	
	public static interface UiTask<T>{
		/**
		 * ui工作任务
		 * @author chris
		 * @param result	网络任务返回的值
		 * @param e			网络任务抛出的异常，若为null，则表示无异常
		 */
		public void execute(T result, Exception e);
	}
	

}
