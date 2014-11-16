package com.fzxt;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class MyDialog extends Dialog {
	private int layout;
	private int theme;
	private Context context;
	private Boolean outTouchClose;
	
	public MyDialog(Context context) {
		super(context);
		this.context = context;
	}

	public MyDialog(Context context,int theme){
		super(context,theme);
		this.context = context;
		this.theme = theme;
	}
	
	
	public MyDialog(Context context,int theme,int layout,Boolean outTouchClose){
		super(context,theme);
		this.layout = layout;
		this.context = context;
		this.theme = theme;
		this.outTouchClose = outTouchClose;
	}
	
	@SuppressWarnings("deprecation")
	public MyDialog create(){
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		MyDialog dialog = new MyDialog(context, theme);
		View view = inflater.inflate(layout,null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		
		dialog.setContentView(view, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		dialog.outTouchClose = this.outTouchClose;
		return dialog;
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(outTouchClose){
			this.dismiss();
			return false;
		}
		return super.onTouchEvent(event);
	}
}
