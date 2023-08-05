package club.emperorws.orm.metadata;

import club.emperorws.orm.annotations.AnnModel;
import club.emperorws.orm.Configuration;
import club.emperorws.orm.mapping.ResultMapping;
import club.emperorws.orm.type.JdbcType;
import club.emperorws.orm.type.TypeHandler;
import club.emperorws.orm.util.BuilderUtil;
import club.emperorws.orm.util.StringUtils;

import java.lang.reflect.Field;

/**
 * 数据库表列与实体对象的属性之间的映射关系
 *
 * @author: EmperorWS
 * @date: 2023/5/11 10:47
 * @description: TableModelFieldInfo: 数据库表列与实体对象的属性之间的映射关系
 */
public class TableModelFieldInfo {

    private Configuration configuration;

    private BuilderUtil builderUtil;

    private TableModelInfo tableModelInfo;

    /**
     * 属性
     */
    private Field field;

    /**
     * 数据库字段名
     */
    private String column;

    /**
     * 实体属性名
     */
    private String property;

    /**
     * 实体属性类型==javaType
     */
    private Class<?> propertyType;

    /**
     * 数据库字段实际存储的类型
     */
    private JdbcType jdbcType;

    /**
     * 类型处理器，方便数据库与实体属性的映射
     */
    private TypeHandler<?> typeHandler;

    /**
     * 结果集-字段映射关系
     */
    private ResultMapping resultMapping;

    public static class Builder {
        private final TableModelFieldInfo tableModelFieldInfo = new TableModelFieldInfo();

        public Builder(TableModelInfo tableModelInfo, Field field, Configuration configuration) {
            tableModelFieldInfo.tableModelInfo = tableModelInfo;
            tableModelFieldInfo.configuration = configuration;
            tableModelFieldInfo.builderUtil = configuration.getBuilderUtil();
            tableModelFieldInfo.field = field;
        }

        public TableModelFieldInfo build() {
            AnnModel.AnnField annField = tableModelFieldInfo.field.getAnnotation(AnnModel.AnnField.class);
            tableModelFieldInfo.property = annField.property();
            if (StringUtils.isNotBlank(annField.javaType())) {
                tableModelFieldInfo.propertyType = tableModelFieldInfo.builderUtil.resolveClass(annField.javaType());
            } else {
                tableModelFieldInfo.propertyType = tableModelFieldInfo.builderUtil.resolveResultJavaType(tableModelFieldInfo.tableModelInfo.getEntityType(), tableModelFieldInfo.property, tableModelFieldInfo.propertyType);
            }
            //特殊处理require字段：数据库是否有此字段
            if (!annField.require()) {
                return tableModelFieldInfo;
            }
            tableModelFieldInfo.column = annField.column();
            tableModelFieldInfo.jdbcType = JdbcType.valueOf(annField.jdbcType());
            if (StringUtils.isNotBlank(annField.typeHandler())) {
                tableModelFieldInfo.typeHandler = tableModelFieldInfo.builderUtil.resolveTypeHandler(tableModelFieldInfo.propertyType, annField.typeHandler());
            }
            //构建ResultMapping
            ResultMapping.Builder resultMappingBuilder = new ResultMapping.Builder(tableModelFieldInfo.configuration, tableModelFieldInfo.property)
                    .column(tableModelFieldInfo.column)
                    .javaType(tableModelFieldInfo.propertyType)
                    .jdbcType(tableModelFieldInfo.jdbcType)
                    .typeHandler(tableModelFieldInfo.typeHandler);
            tableModelFieldInfo.resultMapping = resultMappingBuilder.build();
            return tableModelFieldInfo;
        }
    }

    public Field getField() {
        return field;
    }

    public String getColumn() {
        return column;
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public ResultMapping getResultMapping() {
        return resultMapping;
    }
}
