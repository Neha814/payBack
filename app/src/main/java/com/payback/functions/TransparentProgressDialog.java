package com.payback.functions;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.payback.R;

public class TransparentProgressDialog
	extends Dialog
	{
	private ImageView iv;
	
	public TransparentProgressDialog(Context paramContext, int paramInt)
	{
	  super(paramContext, R.style.TransparentProgressDialog);
	  WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
	  localLayoutParams.gravity = 1;
	  getWindow().setAttributes(localLayoutParams);
	  setTitle(null);
	  setCancelable(false);
	  setOnCancelListener(null);
	  RelativeLayout localRelativeLayout = new RelativeLayout(paramContext);
	  RelativeLayout.LayoutParams localLayoutParams1 = new RelativeLayout.LayoutParams(-2, -2);
	  iv = new ImageView(paramContext);
	  iv.setImageResource(paramInt);
	  localRelativeLayout.addView(this.iv, localLayoutParams1);
	  addContentView(localRelativeLayout, localLayoutParams1);
	}
	
	public void show()
	{
	  super.show();
	  RotateAnimation localRotateAnimation = new RotateAnimation(0.0F, 360.0F, 1, 0.5F, 1, 0.5F);
	  localRotateAnimation.setInterpolator(new LinearInterpolator());
	  localRotateAnimation.setRepeatCount(-1);
	  localRotateAnimation.setDuration(1000L);
	  iv.setAnimation(localRotateAnimation);
	  iv.startAnimation(localRotateAnimation);
	}
	}