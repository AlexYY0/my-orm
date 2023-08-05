package club.emperorws.orm.mapping;

import club.emperorws.orm.cursor.Cursor;

import java.sql.ResultSet;

/**
 * 返回结果的类型
 *
 * @author: EmperorWS
 * @date: 2023/4/28 16:55
 * @description: ResultSetType: 返回结果的类型
 */
public enum ResultSetType {
    /**
     * 一般是default，普通sql使用
     */
    DEFAULT(-1),

    /**
     * 流式查询游标使用{@link Cursor}，游标只能向下滚动
     */
    FORWARD_ONLY(ResultSet.TYPE_FORWARD_ONLY),

    /**
     * 流式查询游标使用{@link Cursor}，游标可以上下滚动，当数据库变化时，当前数据集不变
     */
    SCROLL_INSENSITIVE(ResultSet.TYPE_SCROLL_INSENSITIVE),

    /**
     * 流式查询游标使用{@link Cursor}，游标可以上下滚动，当数据库变化时，当前数据集同步改变
     */
    SCROLL_SENSITIVE(ResultSet.TYPE_SCROLL_SENSITIVE);

    private final int value;

    ResultSetType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
