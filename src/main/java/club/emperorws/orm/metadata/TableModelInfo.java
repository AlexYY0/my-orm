package club.emperorws.orm.metadata;

import club.emperorws.orm.annotations.AnnModel;
import club.emperorws.orm.Configuration;
import club.emperorws.orm.mapping.ResultMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库表与实体模型的映射关系
 *
 * @author: EmperorWS
 * @date: 2023/5/11 10:44
 * @description: TableModelInfo: 数据库表与实体模型的映射关系
 */
public class TableModelInfo {

    private Configuration configuration;

    /**
     * 实体类型
     */
    private Class<?> entityType;

    /**
     * 表名称
     */
    private String tableName;

    /**
     * 主键在数据库中的名称（不支持联合主键，同时也请避免使用联合主键）
     * <p>具体的信息，可以通过{@link #fieldInfoMap}获取</p>
     */
    private String pkName;

    /**
     * 表字段信息列表
     */
    private List<TableModelFieldInfo> fieldInfoList = new ArrayList<>();

    /**
     * 有注解@AnnFileModel.AnnField，却不需要与数据库映射的字段，AnnFileModel.AnnField.require == false
     */
    private List<TableModelFieldInfo> unMappingFieldInfoList = new ArrayList<>();

    /**
     * 数据库列名称-列信息的映射
     */
    private Map<String, TableModelFieldInfo> fieldInfoMap = new HashMap<>();

    /**
     * 数据库列名称的集合
     */
    private List<String> columnList = new ArrayList<>();

    /**
     * 实体属性名称集合
     */
    private List<String> propertyList = new ArrayList<>();

    /**
     * 结果集映射关系
     */
    private ResultMap resultMap;

    public static class Builder {
        private final TableModelInfo tableModelInfo = new TableModelInfo();

        public Builder(Configuration configuration) {
            tableModelInfo.configuration = configuration;
        }

        public Builder entityType(Class<?> entityType) {
            tableModelInfo.entityType = entityType;
            return this;
        }

        public Builder tableName(String tableName) {
            tableModelInfo.tableName = tableName;
            return this;
        }

        public Builder pkName(String pkName) {
            tableModelInfo.pkName = pkName;
            return this;
        }

        public TableModelInfo build() {
            ResultMap.Builder resultMapBuilder = new ResultMap.Builder(tableModelInfo.configuration, tableModelInfo.entityType.getName(), tableModelInfo.entityType);
            Class<?> tempClass = tableModelInfo.entityType;
            while (tempClass != null) {
                Field[] declaredFields = tempClass.getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    AnnModel.AnnField annotation = declaredField.getAnnotation(AnnModel.AnnField.class);
                    //跳过没有映射属性的字段
                    if (annotation == null) {
                        continue;
                    }
                    //获取fieldInfoList、获取fieldInfoMap、获取columnList
                    TableModelFieldInfo tableModelFieldInfo = new TableModelFieldInfo.Builder(tableModelInfo, declaredField, tableModelInfo.configuration).build();
                    //处理不需要与数据库映射的字段
                    if (!annotation.require()) {
                        tableModelInfo.unMappingFieldInfoList.add(tableModelFieldInfo);
                        continue;
                    }
                    tableModelInfo.fieldInfoList.add(tableModelFieldInfo);
                    tableModelInfo.fieldInfoMap.put(tableModelFieldInfo.getColumn(), tableModelFieldInfo);
                    tableModelInfo.columnList.add(tableModelFieldInfo.getColumn());
                    tableModelInfo.propertyList.add(tableModelFieldInfo.getProperty());
                    //resultMap添加ResultMapping
                    if (tableModelFieldInfo.getResultMapping() != null) {
                        resultMapBuilder.addResultMapping(tableModelFieldInfo.getResultMapping());
                    }
                }
                //循环遍历同时获取父类的属性
                tempClass = tempClass.getSuperclass();
            }
            tableModelInfo.resultMap = resultMapBuilder.build();
            return tableModelInfo;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPkName() {
        return pkName;
    }

    public List<TableModelFieldInfo> getFieldInfoList() {
        return fieldInfoList;
    }

    public List<TableModelFieldInfo> getUnMappingFieldInfoList() {
        return unMappingFieldInfoList;
    }

    public Map<String, TableModelFieldInfo> getFieldInfoMap() {
        return fieldInfoMap;
    }

    public TableModelFieldInfo getTableModelFieldInfo(String column) {
        return fieldInfoMap.get(column);
    }

    public List<String> getColumnList() {
        return columnList;
    }

    public List<String> getPropertyList() {
        return propertyList;
    }

    public ResultMap getResultMap() {
        return resultMap;
    }
}
