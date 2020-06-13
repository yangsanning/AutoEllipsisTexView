package ysn.com.view.autoellipsistextview;

import android.text.TextPaint;

/**
 * @Author yangsanning
 * @ClassName CharUtils
 * @Description 字符工具类
 * @Date 2020/6/13
 */
class CharUtils {

    /**
     * 得到单个char的宽度
     */
    public static float getSingleCharWidth(TextPaint textPaint, char textChar) {
        float[] width = new float[1];
        textPaint.getTextWidths(new char[]{textChar}, 0, 1, width);
        return width[0];
    }
}
