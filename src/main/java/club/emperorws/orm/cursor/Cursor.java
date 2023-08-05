package club.emperorws.orm.cursor;

import java.io.Closeable;

/**
 * todo 后续处理
 * Cursor游标流式查询
 *
 * @author: EmperorWS
 * @date: 2023/4/28 14:09
 * @description: Cursor: Cursor游标查询
 */
public interface Cursor<T> extends Closeable, Iterable<T> {

    /**
     * @return true if the cursor has started to fetch items from database.
     */
    boolean isOpen();

    /**
     *
     * @return true if the cursor is fully consumed and has returned all elements matching the query.
     */
    boolean isConsumed();

    /**
     * Get the current item index. The first item has the index 0.
     *
     * @return -1 if the first cursor item has not been retrieved. The index of the current item retrieved.
     */
    int getCurrentIndex();
}
