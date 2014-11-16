package com.fzxt;

import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * 增加一些实用方法的adapter
 * @param <T>
 */
public abstract class WantupAdapter<T> extends ArrayAdapter<T>{
	private LayoutInflater inflater;
	private boolean haveLoadAllDatas = false;
	
	protected List<T> dataList;
	protected Context context;
	protected int rowLayout;
	
	/**
	 * 是否每行都使用同一个view
	 */
	private boolean singleRowView = true;
	
	public WantupAdapter(Context context, int rowLayout, List<T> tList){
		super(context, 0, tList);
		dataList = tList;
		this.context = context;
		this.rowLayout = rowLayout;
		Activity activity = (Activity) getContext();
		inflater = activity.getLayoutInflater();
	}
	
	/**
	 * 构造方法
	 * @param context
	 * @param rowLayout
	 * @param tList
	 * @param singleRowView	是否每行都使用同一个view
	 */
	public WantupAdapter(Context context, int rowLayout, List<T> tList, boolean singleRowView){
		this(context, rowLayout, tList);
		this.singleRowView = singleRowView;
	}
	
	/**
	 * 必须重写的方法，相当于adapter的getView
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public abstract View getRowView(int position, View convertView, ViewGroup parent);

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(singleRowView){
			if(convertView == null){
				convertView = inflater.inflate(rowLayout, null);
			}
		}else{
			convertView = inflater.inflate(rowLayout, null);
		}
		return getRowView(position, convertView, parent);
	}
	
	/**
	 * 加载一页数据,若传过来的list大小小于一页的大小，则认为所有数据已加载完
	 * @param pageLimit 一页数据的条数，为0表示无限制
	 * @param sms 短信数据list
	 * @throws Exception
	 */
	public void loadList(int pageLimit, List<T> list){
		dataList.addAll(list);
		this.notifyDataSetChanged();
		if(pageLimit == 0 || list.size() < pageLimit){
			//已经加载完全部数据
			haveLoadAllDatas = true;
		}
	}
	
	/**
	 * 移除数据
	 * @param list
	 */
	public void removeList(List<T> list){
		dataList.removeAll(list);
		this.notifyDataSetChanged();
	}
	
	public void removeAll() {
		dataList.clear();
		this.notifyDataSetChanged();
	}
	
	/**
	 * 是否已加载完所有数据
	 * @return
	 */
	public boolean hasLoadAllDatas(){
		return haveLoadAllDatas;
	}
}
