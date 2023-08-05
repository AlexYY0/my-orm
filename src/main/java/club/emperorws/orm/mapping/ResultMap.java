package club.emperorws.orm.mapping;

import club.emperorws.orm.Configuration;

import java.util.*;

/**
 * SQL执行结果类型
 *
 * @author: EmperorWS
 * @date: 2023/5/4 14:42
 * @description: ResultMap: SQL执行结果类型
 */
public class ResultMap {

    private Configuration configuration;

    private String id;

    /**
     * 返回结果的class类型
     */
    private Class<?> type;

    /**
     * 结果列映射集合
     */
    private List<ResultMapping> resultMappings;

    /**
     * 映射的记录列名
     */
    private Set<String> mappedColumns;

    /**
     * 映射的记录列对应的属性名
     */
    private Set<String> mappedProperties;

    /**
     * 是否开启自动映射的功能
     */
    private Boolean autoMapping;

    private ResultMap() {
    }

    public static class Builder {

        private ResultMap resultMap = new ResultMap();

        public Builder(Configuration configuration, String id, Class<?> type) {
            this(configuration, id, type, new ArrayList<>(), null);
        }

        public Builder(Configuration configuration, String id, Class<?> type, List<ResultMapping> resultMappings) {
            this(configuration, id, type, resultMappings, null);
        }

        public Builder addResultMapping(ResultMapping resultMapping) {
            resultMap.resultMappings.add(resultMapping);
            return this;
        }

        public Builder(Configuration configuration, String id, Class<?> type, List<ResultMapping> resultMappings, Boolean autoMapping) {
            resultMap.configuration = configuration;
            resultMap.id = id;
            resultMap.type = type;
            resultMap.resultMappings = resultMappings;
            resultMap.autoMapping = autoMapping;
        }

        public Class<?> type() {
            return resultMap.type;
        }

        public ResultMap build() {
            if (resultMap.id == null) {
                throw new IllegalArgumentException("ResultMaps must have an id");
            }
            resultMap.mappedColumns = new HashSet<>();
            resultMap.mappedProperties = new HashSet<>();
            for (ResultMapping resultMapping : resultMap.resultMappings) {
                final String column = resultMapping.getColumn();
                if (column != null) {
                    resultMap.mappedColumns.add(column.toUpperCase(Locale.ENGLISH));
                }
                final String property = resultMapping.getProperty();
                if (property != null) {
                    resultMap.mappedProperties.add(property);
                }
            }
            return resultMap;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public Class<?> getType() {
        return type;
    }

    public List<ResultMapping> getResultMappings() {
        return resultMappings;
    }

    public Set<String> getMappedColumns() {
        return mappedColumns;
    }

    public Set<String> getMappedProperties() {
        return mappedProperties;
    }

    public Boolean getAutoMapping() {
        return autoMapping;
    }
}
