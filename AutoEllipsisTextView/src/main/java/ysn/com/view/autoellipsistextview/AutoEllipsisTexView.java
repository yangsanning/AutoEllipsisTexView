package ysn.com.view.autoellipsistextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yangsanning
 * @ClassName AutoEllipsisTexView
 * @Description 一个当容器装不下文本时，显示 ... 的 TextView
 * @Date 2020/6/13
 */
public class AutoEllipsisTexView extends View {

    /**
     * padding: 内边距
     * rowWidth: 行宽
     * textColor: 字体颜色
     * textSize: 字体大小
     * textCharArray: 文本字符
     */
    private int padding;
    private int rowWidth;
    private String text;
    private int textColor;
    private int textSize;
    private String ellipsis;
    private Gravity gravity;

    private TextPaint textPaint;
    private int maxLineWidth, maxLineHeight;
    private int totalHeight;
    private boolean isOverflow;

    /**
     * lineTextList: 每行文字的集合
     * lineRectList: 每行的 Rect 集合
     */
    private List<String> lineTextList = new ArrayList<>();
    private List<Rect> lineRectList = new ArrayList<>();

    public AutoEllipsisTexView(Context context) {
        this(context, null);
    }

    public AutoEllipsisTexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoEllipsisTexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoEllipsisTexView);

        padding = typedArray.getDimensionPixelSize(R.styleable.AutoEllipsisTexView_padding, 30);
        rowWidth = typedArray.getDimensionPixelSize(R.styleable.AutoEllipsisTexView_rowWidth, 15);
        text = typedArray.getString(R.styleable.AutoEllipsisTexView_text);
        textColor = typedArray.getColor(R.styleable.AutoEllipsisTexView_textColor, Color.parseColor("#333333"));
        textSize = typedArray.getDimensionPixelSize(R.styleable.AutoEllipsisTexView_textSize, 56);
        ellipsis = typedArray.getString(R.styleable.AutoEllipsisTexView_ellipsis);
        gravity = Gravity.getValue(typedArray.getInt(R.styleable.AutoEllipsisTexView_gravity, Gravity.CENTER_VERTICAL.mode));

        typedArray.recycle();

        if (ellipsis == null) {
            ellipsis = "...";
        }
    }

    private void initPaint() {
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        maxLineWidth = w - 2 * padding;
        maxLineHeight = h - 2 * padding;

        initText();
    }

    /**
     * 初始化文本
     */
    private void initText() {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        // 文本字符串
        char[] textCharArray = text.toCharArray();
        lineTextList.clear();
        lineRectList.clear();
        totalHeight = 0;
        isOverflow = Boolean.FALSE;

        int currentTextWidth = 0;
        StringBuilder lineStringBuilder = new StringBuilder();
        for (int i = 0, length = textCharArray.length; i < length; i++) {
            char textChar = textCharArray[i];
            currentTextWidth += StringUtils.getSingleCharWidth(textPaint, textChar);
            if (currentTextWidth > maxLineWidth) {
                // 当行宽度大于该控件宽度时，进行行文本以及Rect保存

                String lineText = lineStringBuilder.toString();
                Rect lineTextRect = new Rect();
                textPaint.getTextBounds(lineText, 0, lineText.length(), lineTextRect);
                if ((totalHeight + (lineTextRect.height() + rowWidth)) < maxLineHeight) {
                    totalHeight += lineTextRect.height() + rowWidth;

                    // 当总高度小于控件高度时，进行保存
                    lineTextList.add(lineText);
                    lineRectList.add(lineTextRect);
                    lineStringBuilder.delete(0, lineStringBuilder.length());
                    currentTextWidth = 0;

                    // 因这个字符未添加，故进行回退
                    i--;
                } else {
                    if ((totalHeight + lineTextRect.height()) < maxLineHeight) {
                        totalHeight += lineTextRect.height();
                        // 最后一行去除行高如果还能放进文本, 则进行保存
                        lineTextList.add(lineText);
                        lineRectList.add(lineTextRect);
                    }
                    // 当总高度大于控件高度时，溢出，结束循环
                    isOverflow = Boolean.TRUE;
                    break;
                }
            } else {
                // 当行宽度小于控件宽度时，继续添加进该行
                lineStringBuilder.append(textChar);

                // 如果是最后一行，进行判断添加
                if (i == length - 1) {
                    String lineText = lineStringBuilder.toString();
                    Rect lineTextRect = new Rect();
                    textPaint.getTextBounds(lineText, 0, lineText.length(), lineTextRect);
                    // 当总高度小于控件高度时，则进行添加
                    if ((totalHeight + lineTextRect.height()) < maxLineHeight) {
                        totalHeight += lineTextRect.height();
                        lineTextList.add(lineText);
                        lineRectList.add(lineTextRect);
                    } else {
                        // 当总高度大于控件高度时，溢出
                        isOverflow = Boolean.TRUE;
                    }
                }
            }
        }

        if (isOverflow && !lineTextList.isEmpty()) {
            // 进行末尾插入文本
            int lastIndex = lineTextList.size() - 1;
            lineTextList.set(lastIndex, StringUtils.forcedTextToEnd(lineTextList.get(lastIndex), ellipsis, maxLineWidth, textPaint));
            textPaint.getTextBounds(lineTextList.get(lastIndex), 0, lineTextList.get(lastIndex).length(), lineRectList.get(lastIndex));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lineTextList.size() == 0) {
            return;
        }

        int marginTop = padding;
        if (isOverflow && gravity == Gravity.CENTER_VERTICAL) {
            marginTop += (maxLineHeight - totalHeight) / 2;
        }

        // 绘制第一行
        marginTop += lineRectList.get(0).height();
        canvas.drawText(lineTextList.get(0), padding, marginTop, textPaint);

        // 绘制剩余行
        for (int i = 1; i < lineRectList.size(); i++) {
            marginTop += lineRectList.get(i).height() + rowWidth;
            canvas.drawText(lineTextList.get(i), padding, marginTop, textPaint);
        }
    }

    /**
     * 设置文本
     */
    public AutoEllipsisTexView setText(String text) {
        this.text = text;
        if (TextUtils.isEmpty(text)) {
            return this;
        }
        initText();
        invalidate();
        return this;
    }
}