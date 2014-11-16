package com.fzxt;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class ReadyActivity extends WantupBaseActivity {
	String ip = "0.0.0.0";
	TextView current_numView;
	ComputerInfo info = new ComputerInfo();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ready);
		
		ip = getLocalIpAddress();
        
		postTask(new NetTask() {

			@Override
			public Object execute() throws Exception {
				
				Map<String,String> paramsMap = new HashMap<String,String>();
				paramsMap.put("ip", ip);
				paramsMap.put("start", "0");
				paramsMap.put("end", "10");
				String result = HttpUtil.postRequest(HttpUtil.BASE_URL+"/fzxtAction!findComputerInfoByComputerIp.do", paramsMap);
				
				if(result != null && result.length()>0){
					List<Map> listMap = CkxTrans.getList(result);
					for(Map map :listMap){
						info.setWait_ip(map.get("wait_ip")+"");
						info.setView_model(map.get("view_model")+"");
						info.setClinicid(map.get("clinicid")+"");
					}
				}
				
				return null;
			}
		}, new UiTask() {

			@Override
			public void execute(Exception e, Object result) {
				if(e != null){
					Toast.makeText(ReadyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
				}else{
					//TODO
					if("1".equals(info.getView_model())){
						
					}
					Intent intent = new Intent();
					intent.setClass(ReadyActivity.this, MainActivity.class);
					intent.putExtra("waitIp", info.getWait_ip());
					intent.putExtra("localIp", ip);
					intent.putExtra("clinicId", info.getClinicid());
					startActivity(intent);
					finish();
				}
			}
		}, true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	
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
	    
}
