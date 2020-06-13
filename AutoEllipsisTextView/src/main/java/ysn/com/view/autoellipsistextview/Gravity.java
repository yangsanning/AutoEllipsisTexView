package ysn.com.view.autoellipsistextview;

/**
 * @Author yangsanning
 * @ClassName Gravity
 * @Description 文本显示模式(溢出才生效)
 * @Date 2020/6/13
 */
enum Gravity {

    /**
     * 从左上角开始
     */
    NONE(0),

    /**
     * 垂直居中
     */
    CENTER_VERTICAL(1);

    public final int mode;

    Gravity(final int mode) {
        this.mode = mode;
    }

    public static Gravity getValue(int value) {
        switch (value) {
            case 1:
                return CENTER_VERTICAL;
            default:
                return NONE;
        }
    }
}
