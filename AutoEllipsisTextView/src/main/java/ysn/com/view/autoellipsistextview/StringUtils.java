package ysn.com.view.autoellipsistextview;

import android.graphics.Paint;
import android.text.TextPaint;

/**
 * @Author yangsanning
 * @ClassName StringUtils
 * @Description 字符串工具类
 * @Date 2020/6/13
 */
class StringUtils {

    /**
     * 得到单个char的宽度
     */
    public static float getSingleCharWidth(TextPaint textPaint, char textChar) {
        float[] width = new float[1];
        textPaint.getTextWidths(new char[]{textChar}, 0, 1, width);
        return width[0];
    }

    /**
     * 在 originalText 末尾强行插入 ellipsis
     *
     * @param originalText 源文本
     * @param ellipsis     需要插入的字符串
     * @param maxLineWidth 最大行宽
     * @param textPaint    画笔
     * @return 插入 ellipsis 后的文本
     */
    public static String forcedTextToEnd(String originalText, String ellipsis, int maxLineWidth, Paint textPaint) {
        int lineTextWidth = (int) textPaint.measureText(originalText);
        if (lineTextWidth >= maxLineWidth) {
            return forcedTextToEnd(originalText.substring(0, originalText.length() - 1), ellipsis, maxLineWidth, textPaint);
        }
        return originalText + ellipsis;
    }
}
