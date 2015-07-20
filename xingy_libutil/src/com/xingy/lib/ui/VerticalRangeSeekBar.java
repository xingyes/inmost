package com.xingy.lib.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import com.xingy.R;
import com.xingy.util.DPIUtil;

import java.math.BigDecimal;
import java.util.ArrayList;

public class VerticalRangeSeekBar<T extends Number> extends ImageView {

    public static final Integer DEFAULT_MINIMUM = 0;
    public static final Integer DEFAULT_MAXIMUM = 100;
    public static final int WIDTH_IN_DP = 100;
    public static final int TEXT_LATERAL_PADDING_IN_DP = 3;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap thumbImage;
    private Bitmap thumbPressedImage;
    private Bitmap thumbDisabledImage;
    private float thumbWidth;
    private float thumbHalfWidth ;
    private float thumbHalfHeight;
    private float insidePadding;
    private float lineThick = 1;

    private T absoluteMinValue, absoluteMaxValue;
    private NumberType numberType;
    private double absoluteMinValuePrim, absoluteMaxValuePrim;
    public double normalizedMinValue = 0d;
    public double normalizedMaxValue = 1d;
    private Thumb pressedThumb = null;
    private boolean notifyWhileDragging = false;
    private OnRangeSeekBarChangeListener<T> listener;
    private ArrayList<String> mLabels;  /*  100,200,300 .... min head*/
    private int    mStep = 1;
    private int    barBgColor;
    private NinePatchDrawable barBgBitmap = null;
//    private NinePatchDrawable nineDrawable;
    private int    barColor;
    private boolean  bHideMarkText = true;
    private boolean  bMinMaxSame = true;
    /**
     * Default color of a {@link com.xingy.lib.ui.VerticalRangeSeekBar}, #FF33B5E5. This is also known as "Ice Cream Sandwich" blue.
     */
    public static final int DEFAULT_COLOR = Color.argb(0xFF, 0x33, 0xB5, 0xE5);
    /**
     * An invalid pointer id.
     */
    public static final int INVALID_POINTER_ID = 255;

    // Localized constants from MotionEvent for compatibility
    // with API < 8 "Froyo".
    public static final int ACTION_POINTER_UP = 0x6, ACTION_POINTER_INDEX_MASK = 0x0000ff00, ACTION_POINTER_INDEX_SHIFT = 8;

    private float mDownMotionY;

    private int mActivePointerId = INVALID_POINTER_ID;

    private int mScaledTouchSlop;

    private boolean mIsDragging;

    private int mTextOffset;
    private int mTextSize;
    private int mDistanceToTop;
    private RectF mRect;

    private static final int DEFAULT_TEXT_SIZE_IN_DP = 14;
    private static final int DEFAULT_TEXT_DISTANCE_TO_BUTTON_IN_DP = 8;
    private static final int DEFAULT_TEXT_DISTANCE_TO_TOP_IN_DP = 8;
    private boolean mSingleThumb;

    public VerticalRangeSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public VerticalRangeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VerticalRangeSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private T extractNumericValueFromAttributes(TypedArray a, int attribute, int defaultValue) {
        TypedValue tv = a.peekValue(attribute);
        if (tv == null) {
            return (T) Integer.valueOf(defaultValue);
        }

        int type = tv.type;
        if (type == TypedValue.TYPE_FLOAT) {
            return (T) Float.valueOf(a.getFloat(attribute, defaultValue));
        } else {
            return (T) Integer.valueOf(a.getInteger(attribute, defaultValue));
        }
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs == null) {
            setRangeToDefaultValues();
        } else {
            if (null == context)
                return;

            TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.RangeSeekBar, 0, 0);
            setRangeValues(
                    extractNumericValueFromAttributes(typeArray, R.styleable.RangeSeekBar_absoluteMinValue, DEFAULT_MINIMUM),
                    extractNumericValueFromAttributes(typeArray, R.styleable.RangeSeekBar_absoluteMaxValue, DEFAULT_MAXIMUM));
            mSingleThumb = UiUtils.getBoolean(context, typeArray, R.styleable.RangeSeekBar_singleThumb, false);
            lineThick = DPIUtil.dip2px(UiUtils.getDimension(context, typeArray, R.styleable.RangeSeekBar_lineWidth));
            if(lineThick<=0)
                lineThick = 1;
            insidePadding = DPIUtil.dip2px(
                    UiUtils.getDimension(this.getContext(), typeArray, R.styleable.RangeSeekBar_insidePadding));

            int rid = UiUtils.getResId(context,typeArray,R.styleable.RangeSeekBar_thumbPress);
            if(rid > 0)
                thumbPressedImage = BitmapFactory.decodeResource(getResources(), rid);
            rid = UiUtils.getResId(context,typeArray,R.styleable.RangeSeekBar_thumbDisable);
            if(rid > 0)
                thumbDisabledImage = BitmapFactory.decodeResource(getResources(), rid);

            rid = UiUtils.getResId(context,typeArray,R.styleable.RangeSeekBar_thumbNormal);
            if(rid > 0) {
                thumbImage = BitmapFactory.decodeResource(getResources(), rid);
                if (null == thumbPressedImage)
                    thumbPressedImage = thumbImage;
                if (null == thumbDisabledImage)
                    thumbDisabledImage = thumbImage;
            }
            else {
                thumbImage = BitmapFactory.decodeResource(getResources(), R.drawable.seek_thumb_normal);
                thumbPressedImage = BitmapFactory.decodeResource(getResources(), R.drawable.seek_thumb_pressed);
                thumbDisabledImage = BitmapFactory.decodeResource(getResources(), R.drawable.seek_thumb_disabled);
            }
            barBgColor = UiUtils.getColor(context,typeArray,R.styleable.RangeSeekBar_barBgColor,Color.GRAY);
            int bgBitmapid = UiUtils.getResId(context,typeArray,R.styleable.RangeSeekBar_barBgBitmapResid);
            if(bgBitmapid > 0)
            {
                barBgBitmap = (NinePatchDrawable) context.getResources().getDrawable(bgBitmapid);

                lineThick = barBgBitmap.getMinimumWidth() - 20;
            }

            barColor = UiUtils.getColor(context,typeArray,R.styleable.RangeSeekBar_barColor,DEFAULT_COLOR);
            bHideMarkText = UiUtils.getBoolean(context,typeArray,R.styleable.RangeSeekBar_hideThumbMarkText);
            bMinMaxSame = UiUtils.getBoolean(context,typeArray,R.styleable.RangeSeekBar_minSameMax);
            typeArray.recycle();
        }

        thumbWidth = thumbImage.getWidth();
        thumbHalfWidth = 0.5f * thumbWidth;
        thumbHalfHeight = 0.5f * thumbImage.getHeight();

        setValuePrimAndNumberType();

        mTextSize = DPIUtil.dip2px(DEFAULT_TEXT_SIZE_IN_DP);
        mDistanceToTop = DPIUtil.dip2px(DEFAULT_TEXT_DISTANCE_TO_TOP_IN_DP);
        mTextOffset = this.mTextSize + DPIUtil.dip2px(DEFAULT_TEXT_DISTANCE_TO_BUTTON_IN_DP) + this.mDistanceToTop;

        mRect = new RectF(insidePadding,
                insidePadding,
                lineThick +  insidePadding,
                getHeight() - insidePadding);


//        mRect = new RectF(padding,
//                mTextOffset + thumbHalfHeight - lineThick / 2,
//                getWidth() - padding,
//                mTextOffset + thumbHalfHeight + lineThick / 2);


        // make RangeSeekBar focusable. This solves focus handling issues in case EditText widgets are being used along with the RangeSeekBar within ScollViews.
        setFocusable(true);
        setFocusableInTouchMode(true);
        mScaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setStep(int step)
    {
        mStep = step;
    }
    public int getStep()
    {return mStep;}
    public void setRangeValues(T minValue, T maxValue) {
        this.absoluteMinValue = minValue;
        this.absoluteMaxValue = maxValue;
        setValuePrimAndNumberType();
    }

    public void setLabels(ArrayList<String> abl)
    {
        if(null == mLabels)
            mLabels = new ArrayList<String>();
        mLabels.clear();
        mLabels.addAll(abl);
    }

    @SuppressWarnings("unchecked")
    // only used to set default values when initialised from XML without any values specified
    private void setRangeToDefaultValues() {
        this.absoluteMinValue = (T) DEFAULT_MINIMUM;
        this.absoluteMaxValue = (T) DEFAULT_MAXIMUM;
        setValuePrimAndNumberType();
    }

    private void setValuePrimAndNumberType() {
        absoluteMinValuePrim = absoluteMinValue.doubleValue();
        absoluteMaxValuePrim = absoluteMaxValue.doubleValue();
        numberType = NumberType.fromNumber(absoluteMinValue);
    }

    public void resetSelectedValues() {
        setSelectedMinValue(absoluteMinValue);
        setSelectedMaxValue(absoluteMaxValue);
    }

    public boolean isNotifyWhileDragging() {
        return notifyWhileDragging;
    }

    /**
     * Should the widget notify the listener callback while the user is still dragging a thumb? Default is false.
     *
     * @param flag
     */
    public void setNotifyWhileDragging(boolean flag) {
        this.notifyWhileDragging = flag;
    }

    /**
     * Returns the absolute minimum value of the range that has been set at construction time.
     *
     * @return The absolute minimum value of the range.
     */
    public T getAbsoluteMinValue() {
        return absoluteMinValue;
    }

    /**
     * Returns the absolute maximum value of the range that has been set at construction time.
     *
     * @return The absolute maximum value of the range.
     */
    public T getAbsoluteMaxValue() {
        return absoluteMaxValue;
    }

    /**
     * Returns the currently selected min value.
     *
     * @return The currently selected min value.
     */
    public T getSelectedMinValue() {
        return normalizedToValue(normalizedMinValue);
    }

    /**
     * Sets the currently selected minimum value. The widget will be invalidated and redrawn.
     *
     * @param value The Number value to set the minimum value to. Will be clamped to given absolute minimum/maximum range.
     */
    public void setSelectedMinValue(T value) {
        // in case absoluteMinValue == absoluteMaxValue, avoid division by zero when normalizing.
        if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
            setNormalizedMinValue(0d);
        } else {
            setNormalizedMinValue(valueToNormalized(value));
        }
    }

    /**
     * Returns the currently selected max value.
     *
     * @return The currently selected max value.
     */
    public T getSelectedMaxValue() {
        return normalizedToValue(normalizedMaxValue);
    }

    /**
     * Sets the currently selected maximum value. The widget will be invalidated and redrawn.
     *
     * @param value The Number value to set the maximum value to. Will be clamped to given absolute minimum/maximum range.
     */
    public void setSelectedMaxValue(T value) {
        // in case absoluteMinValue == absoluteMaxValue, avoid division by zero when normalizing.
        if (0 == (absoluteMaxValuePrim - absoluteMinValuePrim)) {
            setNormalizedMaxValue(1d);
        } else {
            setNormalizedMaxValue(valueToNormalized(value));
        }
    }

    /**
     * Registers given listener callback to notify about changed selected values.
     *
     * @param listener The listener to notify about changed selected values.
     */
    public void setOnRangeSeekBarChangeListener(OnRangeSeekBarChangeListener<T> listener) {
        this.listener = listener;
    }

    /**
     * Handles thumb selection and movement. Notifies listener callback on certain events.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled()) {
            return false;
        }

        int pointerIndex;

        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:
                // Remember where the motion event started
                mActivePointerId = event.getPointerId(event.getPointerCount() - 1);
                pointerIndex = event.findPointerIndex(mActivePointerId);
                mDownMotionY = event.getY(pointerIndex);

                pressedThumb = evalPressedThumb(mDownMotionY);

                // Only handle thumb presses.
                if (pressedThumb == null) {
                    return super.onTouchEvent(event);
                }

                setPressed(true);
                invalidate();
                onStartTrackingTouch();
                trackTouchEvent(event);
                attemptClaimDrag();

                break;
            case MotionEvent.ACTION_MOVE:
                if (pressedThumb != null) {

                    if (mIsDragging) {
                        trackTouchEvent(event);
                    } else {
                        // Scroll to follow the motion event
                        pointerIndex = event.findPointerIndex(mActivePointerId);
                        final float y = event.getY(pointerIndex);

                        if (Math.abs(y - mDownMotionY) > mScaledTouchSlop) {
                            setPressed(true);
                            invalidate();
                            onStartTrackingTouch();
                            trackTouchEvent(event);
                            attemptClaimDrag();
                        }
                    }

                    if (notifyWhileDragging && listener != null) {
                        listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                    setPressed(false);
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should be interpreted as a tap-seek to that location.
                    onStartTrackingTouch();
                    trackTouchEvent(event);
                    onStopTrackingTouch();
                }

                adjustThumb();

                pressedThumb = null;
                invalidate();
                if (listener != null) {
                    listener.onRangeSeekBarValuesChanged(this, getSelectedMinValue(), getSelectedMaxValue());
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int index = event.getPointerCount() - 1;
                // final int index = ev.getActionIndex();
                mDownMotionY = event.getY(index);
                mActivePointerId = event.getPointerId(index);
                invalidate();
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mIsDragging) {
                    onStopTrackingTouch();
                    setPressed(false);
                }
                invalidate(); // see above explanation
                break;
        }
        return true;
    }

    private void adjustThumb() {
        if(mIsDragging || mStep <=1)
            return;
        if (Thumb.MIN.equals(pressedThumb) && !mSingleThumb) {
            T value = normalizedToValue(normalizedMinValue);
            int tmp = (value.intValue()  + mStep/2)/mStep * mStep;
            setNormalizedMinValue(valueToNormalized(tmp));
        } else if (Thumb.MAX.equals(pressedThumb)) {
            T value = normalizedToValue(normalizedMaxValue);
            int tmp = (value.intValue()  + mStep/2)/mStep * mStep;
            setNormalizedMaxValue(valueToNormalized(tmp));
        }
        invalidate();

    }

    private final void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = (ev.getAction() & ACTION_POINTER_INDEX_MASK) >> ACTION_POINTER_INDEX_SHIFT;

        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose
            // a new active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mDownMotionY = ev.getY(newPointerIndex);
            mActivePointerId = ev.getPointerId(newPointerIndex);
        }
    }

    private final void trackTouchEvent(MotionEvent event) {
        final int pointerIndex = event.findPointerIndex(mActivePointerId);
        final float y = event.getY(pointerIndex);

        if (Thumb.MIN.equals(pressedThumb) && !mSingleThumb) {
            setNormalizedMinValue(screenToNormalized(y));
        } else if (Thumb.MAX.equals(pressedThumb)) {
            setNormalizedMaxValue(screenToNormalized(y));
        }
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any ancestors from stealing events in the drag.
     */
    private void attemptClaimDrag() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    /**
     * This is called when the user has started touching this widget.
     */
    void onStartTrackingTouch() {
        mIsDragging = true;
    }

    /**
     * This is called when the user either releases his touch or the touch is canceled.
     */
    void onStopTrackingTouch() {
        mIsDragging = false;
    }

    /**
     * Ensures correct size of the widget.
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 200;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }

        int width = thumbImage.getWidth() + DPIUtil.dip2px(WIDTH_IN_DP);
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
        }
        setMeasuredDimension(width, height);
    }

    /**
     * Draws the widget on the given canvas.
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setTextSize(mTextSize);
        paint.setStyle(Style.FILL);
        paint.setColor(barBgColor);
        paint.setAntiAlias(true);

        // draw min and max labels


        if(null == mLabels) {
            mLabels = new ArrayList<String>();
            mLabels.add("Min");
            mLabels.add("Max");
        }


        float minBarThick = thumbWidth + insidePadding;
        float textSize = paint.getTextSize();


        /**
         * max head draw
         */
        canvas.drawText(mLabels.get(mLabels.size()-1), minBarThick, insidePadding + textSize, paint);
        canvas.drawText(mLabels.get(0), minBarThick,getHeight() - insidePadding, paint);

        float step = (getHeight() - insidePadding  - insidePadding - textSize)/(mLabels.size()-1);
        for(int i = 1; i+1 < mLabels.size(); i++) {
            canvas.drawText(mLabels.get(i), minBarThick, getHeight() - insidePadding -  step*i, paint);
        }

        // draw seek bar background line
//        padding = INITIAL_PADDING + minMaxLabelSize + thumbHalfWidth;

        mRect.top = insidePadding;
        mRect.bottom = getHeight() - insidePadding;
        if(barBgBitmap!=null) {
            barBgBitmap.setBounds((int)(mRect.left-8),
                    (int)(mRect.top - thumbHalfHeight),(int)mRect.right+8,(int)(mRect.bottom + thumbHalfHeight));
            barBgBitmap.draw(canvas);
//            canvas.drawBitmap(nineDrawable., mRect, paint);
        }
        else
            canvas.drawRect(mRect, paint);

        boolean selectedValuesAreDefault = false;
//        (getSelectedMinValue().equals(getAbsoluteMinValue()) &&
//                getSelectedMaxValue().equals(getAbsoluteMaxValue()));
//
        int colorToUseForButtonsAndHighlightedLine = selectedValuesAreDefault ?
                barBgColor :    // default values
                barColor; //non default, filter is active

        // draw seek bar active range line
        mRect.top = normalizedToScreen(normalizedMaxValue);
        mRect.bottom = normalizedToScreen(normalizedMinValue);

        paint.setColor(colorToUseForButtonsAndHighlightedLine);
        canvas.drawRect(mRect, paint);

        // draw minimum thumb if not a single thumb control
        if (!mSingleThumb) {
            drawThumb(normalizedToScreen(normalizedMinValue), Thumb.MIN.equals(pressedThumb), canvas,
                    selectedValuesAreDefault);
        }

        // draw maximum thumb
        drawThumb(normalizedToScreen(normalizedMaxValue), Thumb.MAX.equals(pressedThumb), canvas,
                selectedValuesAreDefault);

        // draw the text if sliders have moved from default edges
        if (!selectedValuesAreDefault && !bHideMarkText) {
            paint.setTextSize(mTextSize);
            paint.setColor(Color.WHITE);
            // give text a bit more space here so it doesn't get cut off
            int offset = DPIUtil.dip2px(TEXT_LATERAL_PADDING_IN_DP);

            String minText = String.valueOf(getSelectedMinValue());
            String maxText = String.valueOf(getSelectedMaxValue());
            float minTextWidth = paint.measureText(minText) + offset;
            float maxTextWidth = paint.measureText(maxText) + offset;

            if (!mSingleThumb) {
                canvas.drawText(minText,
                        mDistanceToTop + mTextSize,
                        normalizedToScreen(normalizedMinValue) - minTextWidth * 0.5f,
                        paint);

            }

            canvas.drawText(maxText,
                    mDistanceToTop + mTextSize,
                    normalizedToScreen(normalizedMaxValue) - maxTextWidth * 0.5f,
                    paint);
        }

    }

    /**
     * Overridden to save instance state when device orientation changes. This method is called automatically if you assign an id to the RangeSeekBar widget using the {@link #setId(int)} method. Other members of this class than the normalized min and max values don't need to be saved.
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("SUPER", super.onSaveInstanceState());
        bundle.putDouble("MIN", normalizedMinValue);
        bundle.putDouble("MAX", normalizedMaxValue);
        return bundle;
    }

    /**
     * Overridden to restore instance state when device orientation changes. This method is called automatically if you assign an id to the RangeSeekBar widget using the {@link #setId(int)} method.
     */
    @Override
    protected void onRestoreInstanceState(Parcelable parcel) {
        final Bundle bundle = (Bundle) parcel;
        super.onRestoreInstanceState(bundle.getParcelable("SUPER"));
        normalizedMinValue = bundle.getDouble("MIN");
        normalizedMaxValue = bundle.getDouble("MAX");
    }

    /**
     * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
     *
     * @param screenCoord The x-coordinate in screen space where to draw the image.
     * @param pressed     Is the thumb currently in "pressed" state?
     * @param canvas      The canvas to draw upon.
     */
    private void drawThumb(float screenCoord, boolean pressed, Canvas canvas, boolean areSelectedValuesDefault) {
        Bitmap buttonToDraw;
        if (areSelectedValuesDefault) {
            buttonToDraw = thumbDisabledImage;
        } else {
            buttonToDraw = pressed ? thumbPressedImage : thumbImage;
        }

        canvas.drawBitmap(buttonToDraw, insidePadding + lineThick/2 - thumbHalfWidth,
                screenCoord - thumbHalfWidth,
                paint);

//        canvas.drawBitmap(buttonToDraw, screenCoord - thumbHalfWidth,
//                mTextOffset,
//                paint);
    }

    /**
     * Decides which (if any) thumb is touched by the given x-coordinate.
     *
     * @param touchY The y-coordinate of a touch event in screen space.
     * @return The pressed thumb or null if none has been touched.
     */
    private Thumb evalPressedThumb(float touchY) {
        Thumb result = null;
        boolean minThumbPressed = isInThumbRange(touchY, normalizedMinValue);
        boolean maxThumbPressed = isInThumbRange(touchY, normalizedMaxValue);
        if (minThumbPressed && maxThumbPressed) {
            // if both thumbs are pressed (they lie on top of each other), choose the one with more room to drag. this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
            result = (touchY / getHeight() > 0.5f) ? Thumb.MAX : Thumb.MIN;
        } else if (minThumbPressed) {
            result = Thumb.MIN;
        } else if (maxThumbPressed) {
            result = Thumb.MAX;
        }
        return result;
    }

    /**
     * Decides if given x-coordinate in screen space needs to be interpreted as "within" the normalized thumb x-coordinate.
     *
     * @param touchY               The y-coordinate in screen space to check.
     * @param normalizedThumbValue The normalized y-coordinate of the thumb to check.
     * @return true if y-coordinate is in thumb range, false otherwise.
     */
    private boolean isInThumbRange(float touchY, double normalizedThumbValue) {
        return Math.abs(touchY - normalizedToScreen(normalizedThumbValue)) <= thumbHalfHeight;
    }

    /**
     * Sets normalized min value to value so that 0 <= value <= normalized max value <= 1. The View will get invalidated when calling this method.
     *
     * @param value The new normalized min value to set.
     */
    private void setNormalizedMinValue(double value) {
        normalizedMinValue = Math.max(0d, Math.min(1d, Math.min(value, normalizedMaxValue)));
        if(normalizedMinValue == normalizedMaxValue && !bMinMaxSame)
        {
            normalizedMinValue -= 1.0/(mLabels.size()-1);
        }
        invalidate();
    }

    /**
     * Sets normalized max value to value so that 0 <= normalized min value <= value <= 1. The View will get invalidated when calling this method.
     *
     * @param value The new normalized max value to set.
     */
    private void setNormalizedMaxValue(double value) {
        normalizedMaxValue = Math.max(0d, Math.min(1d, Math.max(value, normalizedMinValue)));
        if(normalizedMinValue == normalizedMaxValue && !bMinMaxSame)
        {
            normalizedMaxValue += 1.0/(mLabels.size()-1);
        }

        invalidate();
    }

    /**
     * Converts a normalized value to a Number object in the value space between absolute minimum and maximum.
     *
     * @param normalized
     * @return
     */
    @SuppressWarnings("unchecked")
    private T normalizedToValue(double normalized) {
        double v = absoluteMinValuePrim + normalized * (absoluteMaxValuePrim - absoluteMinValuePrim);
        // TODO parameterize this rounding to allow variable decimal points
        return (T) numberType.toNumber(Math.round(v * 100 ) / 100d);
    }

    /**
     * Converts the given Number value to a normalized double.
     *
     * @param value The Number value to normalize.
     * @return The normalized double.
     */
    private double valueToNormalized(T value) {
        if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            // prevent division by zero, simply return 0.
            return 0d;
        }
        return (value.doubleValue() - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim);
    }

    private double valueToNormalized(double value) {
        if (0 == absoluteMaxValuePrim - absoluteMinValuePrim) {
            // prevent division by zero, simply return 0.
            return 0d;
        }
        return (value - absoluteMinValuePrim) / (absoluteMaxValuePrim - absoluteMinValuePrim);
    }


    /**
     * Converts a normalized value into screen space.
     *
     * @param normalizedCoord The normalized value to convert.
     * @return The converted value in screen space.
     */
    private float normalizedToScreen(double normalizedCoord) {
        return (float) (insidePadding + (1d -normalizedCoord) * (getHeight() - 2 * insidePadding));
    }

    /**
     * Converts screen space y-coordinates into normalized values.
     *
     * @param screenCoord The y-coordinate in screen space to convert.
     * @return The normalized value.
     */
    private double screenToNormalized(float screenCoord) {
//        int height = getHeight();
//        if (height <= 2 * insidePadding) {
//            // prevent division by zero, simply return 0.
//            return 0d;
//        } else {
//            double result = (screenCoord - insidePadding) / (height - 2 * insidePadding);
//            return Math.min(1d, Math.max(0d, result));
//        }

        int height = getHeight();
        if (height <= 2 * insidePadding) {
            // prevent division by zero, simply return 1.  max
            return 1d;
        } else {
            double result = (screenCoord - insidePadding) / (height - 2 * insidePadding);
            return 1d - Math.min(1d, Math.max(0d, result));
        }
    }

    /**
     * Callback listener interface to notify about changed range values.
     *
     * @param <T> The Number type the RangeSeekBar has been declared with.
     * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
     */
    public interface OnRangeSeekBarChangeListener<T> {

        public void onRangeSeekBarValuesChanged(VerticalRangeSeekBar<?> bar, T minValue, T maxValue);
    }

    /**
     * Thumb constants (min and max).
     */
    private static enum Thumb {
        MIN, MAX
    }

    ;

    /**
     * Utility enumeration used to convert between Numbers and doubles.
     *
     * @author Stephan Tittel (stephan.tittel@kom.tu-darmstadt.de)
     */
    private static enum NumberType {
        LONG, DOUBLE, INTEGER, FLOAT, SHORT, BYTE, BIG_DECIMAL;

        public static <E extends Number> NumberType fromNumber(E value) throws IllegalArgumentException {
            if (value instanceof Long) {
                return LONG;
            }
            if (value instanceof Double) {
                return DOUBLE;
            }
            if (value instanceof Integer) {
                return INTEGER;
            }
            if (value instanceof Float) {
                return FLOAT;
            }
            if (value instanceof Short) {
                return SHORT;
            }
            if (value instanceof Byte) {
                return BYTE;
            }
            if (value instanceof BigDecimal) {
                return BIG_DECIMAL;
            }
            throw new IllegalArgumentException("Number class '" + value.getClass().getName() + "' is not supported");
        }

        public Number toNumber(double value) {
            switch (this) {
                case LONG:
                    return Long.valueOf((long) value);
                case DOUBLE:
                    return value;
                case INTEGER:
                    return Integer.valueOf((int) value);
                case FLOAT:
                    return Float.valueOf((float)value);
                case SHORT:
                    return Short.valueOf((short) value);
                case BYTE:
                    return Byte.valueOf((byte) value);
                case BIG_DECIMAL:
                    return BigDecimal.valueOf(value);
            }
            throw new InstantiationError("can't convert " + this + " to a Number object");
        }
    }

}