package com.xingy.lib.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xingy.R;
import com.xingy.util.MyApplication;

public class InputDialog extends Dialog {

    public InputDialog(Context context, int theme) {
        super(context, theme);
    }

    public InputDialog(Context context) {
        super(context);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	if( null != mBuilder ){
    		mBuilder.onStart(getContext());
    	}
    }
    
    @Override
    protected void onStop() {
    	if( null != mBuilder ) {
    		mBuilder.onStop(getContext());
    	}
    	
    	super.onStop();
    }
    
    private Builder mBuilder = null;

    /**
     * Helper class for creating a custom dialog
     */
    public static class Builder {

        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private String neutralButtonText;
        private View contentView;
        private Button mPositive = null,
                       mNegative = null,
                       mNeutral = null;
        private EditText mEditor = null;
        private int    mPositiveTimeout = 0;

        private DialogInterface.OnClickListener
                        positiveButtonClickListener,
                        negativeButtonClickListener,
                        neutralButtonClickListener;
        
        private Handler  mHandler = null;
        private Runnable mRunnable = null;

        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Set the Dialog message from String
         * @param title
         * @return
         */
        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         * @param title
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
       
        /**
         * Set a custom content view for the Dialog.
         * If a message is set, the contentView is not
         * added to the Dialog...
         * @param v
         * @return
         */
        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }
        
        /**
         * getText
         * @return
         */
        public String getText()
        {
        	return (null != mEditor ? mEditor.getText().toString().trim() : "");
        }
        
        /**
         * Set the positive button resource and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener, int timeout)
        {
        	this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            mPositiveTimeout = timeout;
            return this;
        }
        
        /**
         * Set the positive button resource and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText, DialogInterface.OnClickListener listener, int timeout)
        {
        	this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            mPositiveTimeout = timeout;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the positive button text and it's listener
         * @param positiveButtonText
         * @param listener
         * @return
         */
        public Builder setPositiveButton(String positiveButtonText,
                DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button resource and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(int negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNegativeButton(String negativeButtonText,
                DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }
        
        /**
         * Set the negative button resource and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNeutralButton(int neutralButtonText,
                DialogInterface.OnClickListener listener) {
            this.neutralButtonText = (String) context
                    .getText(neutralButtonText);
            this.neutralButtonClickListener = listener;
            return this;
        }

        /**
         * Set the negative button text and it's listener
         * @param negativeButtonText
         * @param listener
         * @return
         */
        public Builder setNeutralButton(String neutralButtonText,
                DialogInterface.OnClickListener listener) {
            this.neutralButtonText = neutralButtonText;
            this.neutralButtonClickListener = listener;
            return this;
        }

        /**
         * Create the custom dialog
         */
        public InputDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final InputDialog dialog = new InputDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.input_dialog, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            
            mEditor = (EditText)layout.findViewById(R.id.input_dlg_editor);
            
            // set the confirm button
            mPositive = (Button) layout.findViewById(R.id.positiveButton);
            if (positiveButtonText != null) {
            	mPositive.setText(positiveButtonText);
            	mPositive.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    	if( null != positiveButtonClickListener ){
                    		positiveButtonClickListener.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                    	}
                    	else {
                    		dialog.dismiss();
                    	}
                    }
                });
            } else {
                // if no confirm button just set the visibility to GONE
            	mPositive.setVisibility(View.GONE);
            }
            // set the cancel button
            mNegative = (Button) layout.findViewById(R.id.negativeButton);
            if (negativeButtonText != null) {
            	mNegative.setText(negativeButtonText);
            	mNegative.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    	if( null != negativeButtonClickListener ){
                    		negativeButtonClickListener.onClick(dialog, DialogInterface.BUTTON_NEGATIVE);
                    	} else {
                    		dialog.dismiss();
                    	}
                    }
                });
            } else {
                // if no confirm button just set the visibility to GONE
            	mNegative.setVisibility(View.GONE);
            }
            
            // Set neutral button.
            mNeutral = (Button)layout.findViewById(R.id.neutralButton);
            if( null != neutralButtonText ){
            	mNeutral.setText(neutralButtonText);
            	mNeutral.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    	if( null != neutralButtonClickListener ){
                    		neutralButtonClickListener.onClick( dialog, DialogInterface.BUTTON_NEUTRAL);
                    	} else {
                    		dialog.dismiss();
                    	}
                    }
                });
            } else {
            	mNeutral.setVisibility(View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView,
                                new LayoutParams(
                                        LayoutParams.WRAP_CONTENT,
                                        LayoutParams.WRAP_CONTENT));
            }
            dialog.setContentView(layout);
            dialog.mBuilder = this;
            return dialog;
        }
        
        private void onStart(final Context aContext) {
        
        	if( null == mHandler ){
    			mHandler = new Handler();
    		}
        	
        	if( mPositiveTimeout > 0 ) {
        		if( null == mRunnable ) {
        			mRunnable = new Runnable(){
						@Override
						public void run() {
							mHandler.postDelayed(mRunnable, 1000);
							mPositiveTimeout--;
							setPositiveText(mPositiveTimeout);
						}
        			};
        		}
        		
        		mHandler.postDelayed(mRunnable, 1000);
        	}
        	
        	mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
		        	UiUtils.showSoftInput(MyApplication.app, mEditor);
				}
			}, 300);
        }
        
        private void onStop(Context aContext) {
        	stopCounting();
        	
        	UiUtils.hideSoftInput(aContext, mEditor);
        }
        
        private void stopCounting(){
        	if( null != mHandler && null != mRunnable ){
        		mHandler.removeCallbacks(mRunnable);
        	}
        }
        
        private void setPositiveText(int nCurrentTicks){
        	final int nVisibility = null != mPositive ? mPositive.getVisibility() : View.GONE;
        	if( View.GONE != nVisibility ){
        		String strText = positiveButtonText + (nCurrentTicks > 0 ? "(" + nCurrentTicks + ")" : "");
        		mPositive.setText(strText);
        		if( 0 >= nCurrentTicks ){
        			stopCounting();
        		}
        		
        		mPositive.setEnabled(0 >= nCurrentTicks);
        	}
        }
    }
}

