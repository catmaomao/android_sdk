package com.heyijoy.gamesdk.widget;



import com.heyijoy.gamesdk.util.Logger;
import com.heyijoy.gamesdk.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



/**
 * @author msh
 * 2014-10-09
 */
	public class EditTextWithErr extends LinearLayout   {  
	    private EditText et;  
	    private TextView tv_error;
	    private char[]numberChars;//
	    private String type;//
	    private String hint;
	    private int maxlength;
	    private ImageView  imgIcon;
	    private ImageView imglineLeft ;
	    private ImageView imglineRight ;
	    private ImageView imglineBottom ;
	    View layout = null;
	    public void init(Context context, String tag ,String des) {  
		        type = tag.split("#")[0];
		        try {
		        	if(tag.split("#").length>1){
			        	maxlength = Integer.parseInt(tag.split("#")[1]);
			        }
				} catch (Exception e) {
					maxlength = 100;
				}
		        
		        if(des!=null){
		        	hint =des;
		        }else{
		        	hint = "";
		        }
		        int  hint_color = 0;
		        String digits = "charNoSpecial";
		        boolean ispassword = false;
		        if("user".equals(type)){
		        }else if("phone".equals(type)){
		        	digits = "integer";
		        }else if("password".equals(type)){
		        	ispassword = true;
		        }else if("verifyno".equals(type)){
		        	digits = "integer";
		        }else if("password_visible".equals(type)){
		        	digits = "charAll";
		        }else if("user_special".equals(type)){
		        	digits = "charSpecial";
		        }else if("user_phone".equals(type)){
		        	digits = "integer";
		        }else if("all".equals(type)){
		        	digits = "all";
		        }
		        
		        
		       
		        if(digits!=null&&"integer".equals(digits)){
		        	layout = LayoutInflater.from(context).inflate(R.layout.hy_edittext_witherr_integer, this,true);
		        }else{
		        	 layout = LayoutInflater.from(context).inflate(R.layout.hy_edittext_witherr, this,true); 
		        }
		        this.imgIcon = (ImageView) layout.findViewById(R.id.hy_edit_icon);
		        et = (EditText) layout.findViewById(R.id.hy_witherr_edit); 
		        imglineLeft = (ImageView) layout.findViewById(R.id.pass_left_line);
		        imglineRight = (ImageView) layout.findViewById(R.id.pass_right_line);
		        imglineBottom = (ImageView) layout.findViewById(R.id.pass_mid_line);
		        setNormalPreIcon();//
		        
		       
		       
		        
		        if(digits!=null&&"integer".equals(digits)){
		        	et.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		        }
		        et.addTextChangedListener(tw);
		        if(hint!=null&&!"".equals(hint)){
		        	et.setHint(hint);
		        }
		        if(hint_color!=0){
		        	et.setHintTextColor(hint_color);
		        }
		        if(maxlength>0){
				InputFilter[] filters = new InputFilter[1];
				filters[0] = new InputFilter.LengthFilter(maxlength);
		        	 et.setFilters(filters);//setMaxEms(maxlength);
		        }
		        if(ispassword){
		        	 et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		        }
		        if(digits!=null&&"charNoSpecial".equals(digits)){
		        		numberChars = new char[]{'0','1','2','3','4','5','6','7','8','9','.','!','@','#','*','(',')','?','\'',
		        				'{','}','[',']','`','~',',','_','-','a','b','c','d','e',
		        				'f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','I','M','N','O','P',
		        				'Q','R','S','T','U','V','W','X','Y','Z'};
		        		
		        	et.setKeyListener(new NumberKeyListener() {
						@Override
						protected char[] getAcceptedChars() {
							return numberChars;
						}
						@Override
						public int getInputType() {
							return InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
						}
					});
		        }
		        if(digits!=null&&"charAll".equals(digits)){
		        	numberChars = new char[]{'0','1','2','3','4','5','6','7','8','9','~','!','@','#','$','%','^','&','*','(',')','{','}','[',']',':','"',';','\'',',','.','?','|','a','b','c','d','e',
	        				'f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','I','M','N','O','P',
	        				'Q','R','S','T','U','V','W','X','Y','Z'};
	        		
	        	et.setKeyListener(new NumberKeyListener() {
					@Override
					protected char[] getAcceptedChars() {
						return numberChars;
					}
					@Override
					public int getInputType() {
						return InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
					}
				});
	        }
		        
		        if(digits!=null&&"all".equals(digits)){
		        
	        }
		        
		        if(digits!=null&&"charSpecial".equals(digits)){
	        		numberChars = new char[]{'0','1','2','3','4','5','6','7','8','9','_','-','a','b','c','d','e',
	        				'f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','A','B','C','D','E','F','G','H','I','J','K','L','I','M','N','O','P',
	        				'Q','R','S','T','U','V','W','X','Y','Z'};
	        		
	        	et.setKeyListener(new NumberKeyListener() {
					@Override
					protected char[] getAcceptedChars() {
						return numberChars;
					}
					@Override
					public int getInputType() {
						return InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
					}
				});
	        }
	    }  
	    public EditTextWithErr(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	        String tag = "";
	        String des = "";
	        for(int i=0;i<attrs.getAttributeCount();i++){
	        	String att = attrs.getAttributeName(i);
	        	if("tag".equals(att)){
	        		tag = attrs.getAttributeValue(i);
	        	}
	        	if("contentDescription".equals(att)){
//	        		des = attrs.getAttributeValue(i);
	        		int des_int = attrs.getAttributeResourceValue(i, 0);
	        		des = context.getResources().getString(des_int);
	        	}
	        }
	        init(context,tag,des);
	    }
	    
	    public EditTextWithErr(LinearLayout linearLayout) {  
	        super(linearLayout.getContext());  
	        String tag = linearLayout.getTag().toString();
	        String des = linearLayout.getContentDescription().toString();
	       /* for(int i=0;i<attrs.getAttributeCount();i++){
	        	String att = attrs.getAttributeName(i);
	        	if("tag".equals(att)){
	        		tag = attrs.getAttributeValue(i);
	        	}
	        	if("contentDescription".equals(att)){
//	        		des = attrs.getAttributeValue(i);
	        		int des_int = attrs.getAttributeResourceValue(i, 0);
	        		des = context.getResources().getString(des_int);
	        	}
	        }*/
	        init(linearLayout.getContext(),tag,des);
	    }
	    
	    TextWatcher tw = new TextWatcher() {  
	                       @Override  
	                       public void onTextChanged(CharSequence s, int start, int before, int count) {  
	                    	   setNormal();
	                       }  
	                       @Override  
	                       public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
	                       }  
	                       @Override  
	                       public void afterTextChanged(Editable s) {  
	                       }  
	                   };  
	  
	public void addTextChangedListener(TextWatcher textWatcher){
		et.removeTextChangedListener(tw);
		et.addTextChangedListener(textWatcher);
	}
	public void setOnFocusChangeListener(View.OnFocusChangeListener listener){
		et.setOnFocusChangeListener(listener);
	}
	
	public void setError(String errStr){
		tv_error.setAnimation(null);//
		tv_error.setText(errStr);
		tv_error.setVisibility(View.VISIBLE);
		 int color = Integer.parseInt("#ff5a5a".replace("#", ""), 16); 
         int red = (color & 0xff0000) >> 16;  
         int green = (color & 0x00ff00) >> 8;  
         int blue = (color & 0x0000ff); 
          
//		et.setTextColor(Color.rgb(red, green, blue));
        et.setTextColor(0xFF666666);
		setErrPreIcon();
		imglineLeft.setBackgroundColor(Color.rgb(red, green, blue));
		imglineRight.setBackgroundColor(Color.rgb(red, green, blue));
		imglineBottom.setBackgroundColor(Color.rgb(red, green, blue));
	}
	
	@SuppressLint("ResourceAsColor")
	public void setNormal(){
		if(tv_error!=null){
			tv_error.setVisibility(View.INVISIBLE);
		}
		
		et.setTextColor(0xFF666666);
		setNormalPreIcon();
		int color_line_blue = Integer.parseInt("#58abff".replace("#", ""), 16); 
        int color_line_blue_red = (color_line_blue & 0xff0000) >> 16;  
        int color_line_blue_green = (color_line_blue & 0x00ff00) >> 8;  
        int color_line_blue_blue = (color_line_blue & 0x0000ff); 
		imglineLeft.setBackgroundColor(Color.rgb(color_line_blue_red, color_line_blue_green, color_line_blue_blue));
		imglineRight.setBackgroundColor(Color.rgb(color_line_blue_red, color_line_blue_green, color_line_blue_blue));
		imglineBottom.setBackgroundColor(Color.rgb(color_line_blue_red, color_line_blue_green, color_line_blue_blue));
	}
	
	public void setErrTextView(TextView textView){
		this.tv_error = textView;
	}
	
	public Editable getText(){
		return et.getText();
	}
	public void setText(String text){
		et.setText(text);
	}
	
	private void setNormalPreIcon(){
		 if(this.type!=null&&!"".equals(this.type)){
	        	if("password".equals(type)){
	        		Logger.e(this.imgIcon+"22222222");
	        		this.imgIcon.setBackgroundResource(R.drawable.nav_btn_code);
	        	}else if("user".equals(type)){
	        		this.imgIcon.setBackgroundResource(R.drawable.nav_btn_figure);
	        	}else if("phone".equals(type)){
//	        		this.imgIcon.setBackgroundResource(R.drawable.hy_edt_pre_phone);
	        		this.imgIcon.setBackgroundResource(R.drawable.nav_btn_figure);
	        	}else if("verifyno".equals(type)){
	        		this.imgIcon.setBackgroundResource(R.drawable.hy_edt_pre_verifyno);
	        	}else if("password_visible".equals(type)){
	        		this.imgIcon.setBackgroundResource(R.drawable.nav_btn_code);
		        }else if("user_special".equals(type)){
		        	this.imgIcon.setBackgroundResource(R.drawable.nav_btn_figure);
		        }else if("user_phone".equals(type)){
		        	this.imgIcon.setVisibility(View.GONE);
		        	et.setPadding(15, 0, 0, 0);
		        }else if("all".equals(type)){
		        	this.imgIcon.setVisibility(View.GONE);
		        	et.setPadding(15, 0, 0, 0);
		        }
	        }
	}
	
	private void setErrPreIcon(){
		 if(this.type!=null&&!"".equals(this.type)){
	        	if("password".equals(type)){
	        		this.imgIcon.setBackgroundResource(R.drawable.hy_edt_pre_pw_er);
	        	}else if("user".equals(type)){
	        		this.imgIcon.setBackgroundResource(R.drawable.hy_edt_pre_user_er);
	        	}else if("phone".equals(type)){
	        		this.imgIcon.setBackgroundResource(R.drawable.hy_edt_pre_phone_er);
	        	}else if("verifyno".equals(type)){
	        		this.imgIcon.setBackgroundResource(R.drawable.hy_edt_pre_verifyno_er);
	        	}else if("password_visible".equals(type)){
	        		this.imgIcon.setBackgroundResource(R.drawable.hy_edt_pre_pw_er);
		        }else if("user_special".equals(type)){
		        	this.imgIcon.setBackgroundResource(R.drawable.hy_edt_pre_user_er);
		        }else if("user_phone".equals(type)){
		        	this.imgIcon.setVisibility(View.GONE);
		        	et.setPadding(2, 7, 15, 20);
		        }else if("all".equals(type)){
		        	this.imgIcon.setVisibility(View.GONE);
		        	et.setPadding(1, 5, 10, 10);
		        }
	        }
	}
	
	public EditText getEd(){
		return et;
	}
	
	}
