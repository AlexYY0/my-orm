package club.emperorws.orm.reflection.property;

import java.util.Iterator;

/**
 * 自定义的属性迭代器（属性分词器）
 * <p>e.g:user[1].linkman.name</p>
 * <p>name=user</p>
 * <p>indexedName=user[1]</p>
 * <p>index=1</p>
 * <p>children=linkman.name</p>
 *
 * @author: EmperorWS
 * @date: 2023/4/24 14:06
 * @description: PropertyTokenizer: 自定义的属性迭代器（属性分词器）
 */
public class PropertyTokenizer implements Iterator<PropertyTokenizer> {

    /**
     * 属性名称
     * <p>e.g:user[1].linkman.name</p>
     * <p>name=user</p>
     */
    private String name;

    /**
     * 带索引的名称
     * <p>e.g:user[1].linkman.name</p>
     * <p>indexedName=user[1]</p>
     */
    private final String indexedName;

    /**
     * 索引
     * <p>e.g:user[1].linkman.name</p>
     * <p>index=1</p>
     */
    private String index;

    /**
     * 剩余的子属性
     * <p>e.g:user[1].linkman.name</p>
     * <p>children=linkman.name</p>
     */
    private final String children;

    public PropertyTokenizer(String fullname) {
        int delim = fullname.indexOf('.');
        if (delim > -1) {
            name = fullname.substring(0, delim);
            children = fullname.substring(delim + 1);
        } else {
            name = fullname;
            children = null;
        }
        indexedName = name;
        delim = name.indexOf('[');
        if (delim > -1) {
            index = name.substring(delim + 1, name.length() - 1);
            name = name.substring(0, delim);
        }
    }

    public String getName() {
        return name;
    }

    public String getIndex() {
        return index;
    }

    public String getIndexedName() {
        return indexedName;
    }

    public String getChildren() {
        return children;
    }

    @Override
    public boolean hasNext() {
        return children != null;
    }

    @Override
    public PropertyTokenizer next() {
        return new PropertyTokenizer(children);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported, as it has no meaning in the context of properties.");
    }
}
