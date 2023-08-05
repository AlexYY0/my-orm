package club.emperorws.orm.metadata;

import club.emperorws.orm.annotations.AnnModel;
import club.emperorws.orm.Configuration;
import club.emperorws.orm.io.ResolverUtil;
import club.emperorws.orm.type.SimpleTypeRegistry;
import club.emperorws.orm.util.ClassUtils;
import club.emperorws.orm.util.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库表与实体对象映射的辅助类
 *
 * @author: EmperorWS
 * @date: 2023/5/11 10:50
 * @description: TableModelInfoHelper: 数据库表与实体对象映射的辅助类
 */
public class TableModelInfoHelper {

    /**
     * 储存反射类表信息
     */
    private static final Map<Class<?>, TableModelInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * 储存表名对应的反射类表信息
     */
    private static final Map<String, TableModelInfo> TABLE_NAME_INFO_CACHE = new ConcurrentHashMap<>();

    /**
     * <p>
     * 获取实体映射表信息
     * </p>
     *
     * @param clazz 反射实体类
     * @return 数据库表反射信息
     */
    public static TableModelInfo getTableInfo(Class<?> clazz) {
        if (clazz == null || clazz.isPrimitive() || SimpleTypeRegistry.isSimpleType(clazz) || clazz.isInterface()) {
            return null;
        }
        //如果是代理类，找出真正的类（目测没有代理类）
        Class<?> targetClass = ClassUtils.getUserClass(clazz);
        TableModelInfo tableInfo = TABLE_INFO_CACHE.get(targetClass);
        if (null != tableInfo) {
            return tableInfo;
        }
        //尝试获取父类缓存
        Class<?> currentClass = clazz;
        while (null == tableInfo && Object.class != currentClass) {
            currentClass = currentClass.getSuperclass();
            tableInfo = TABLE_INFO_CACHE.get(ClassUtils.getUserClass(currentClass));
        }
        //把父类的移到子类中来
        if (tableInfo != null) {
            TABLE_INFO_CACHE.put(targetClass, tableInfo);
        }
        return tableInfo;
    }

    /**
     * <p>
     * 根据表名获取实体映射表信息
     * </p>
     *
     * @param tableName 表名
     * @return 数据库表反射信息
     */
    public static TableModelInfo getTableInfo(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return null;
        }
        return TABLE_NAME_INFO_CACHE.get(tableName);
    }

    /**
     * 添加一组TableModelInfo
     *
     * @param configuration 配置单例
     * @param packageNames  包名称数组
     */
    public static void scanPackageAddTableModelInfo(Configuration configuration, String... packageNames) {
        //创建工具类
        ResolverUtil<Object> resolverUtil = new ResolverUtil<>();
        //查找符合条件的实体
        resolverUtil.findAnnotated(AnnModel.Table.class, packageNames);
        Set<Class<?>> tableModelSet = resolverUtil.getClasses();
        for (Class<?> clazz : tableModelSet) {
            addTableModelInfo(configuration, clazz);
        }
    }

    /**
     * 添加一个TableModelInfo
     *
     * @param configuration 配置单例
     * @param entityClass   TableModelInfo的class类型
     */
    public static void addTableModelInfo(Configuration configuration, Class<?> entityClass) {
        TableModelInfo tableModelInfo = TABLE_INFO_CACHE.get(entityClass);
        if (tableModelInfo != null) {
            return;
        }
        AnnModel.Table annTable = entityClass.getAnnotation(AnnModel.Table.class);
        if (annTable != null) {
            tableModelInfo = new TableModelInfo.Builder(configuration)
                    .entityType(entityClass)
                    .pkName(annTable.pkName())
                    .tableName(annTable.tableName())
                    .build();
            TABLE_INFO_CACHE.put(entityClass, tableModelInfo);
            TABLE_NAME_INFO_CACHE.put(annTable.tableName(), tableModelInfo);
        }
    }
}
