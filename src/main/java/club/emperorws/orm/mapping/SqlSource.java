package club.emperorws.orm.mapping;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.bingding.MapperMethod;
import club.emperorws.orm.parsing.DynamicContext;
import club.emperorws.orm.parsing.DynamicSqlParser;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 保存最原始的sql语句对象
 *
 * @author: EmperorWS
 * @date: 2023/4/28 13:57
 * @description: SqlSource: 保存最原始的sql语句对象
 */
public class SqlSource {

    /**
     * 原动态sql
     */
    private String dynamicSql;

    /**
     * SQL执行的类型（主要用于透传）
     */
    private SqlCommandType sqlCommandType;

    /**
     * 执行的mapper类型（主要用于透传）
     */
    private Class<?> mapperInterface;

    /**
     * 执行的方法（主要用于透传）
     */
    private Method method;

    /**
     * 返回值类型，可能是别名，可能是class名称（主要用于透传）
     */
    private String resultType;

    /**
     * MappedStatement的唯一标识符id
     */
    private String mappedStatementId;

    public static class Builder {

        private SqlSource sqlSource = new SqlSource();

        public Builder(String dynamicSql) {
            sqlSource.dynamicSql = dynamicSql;
        }

        public Builder(SqlSource sqlSource, MapperMethod.SqlCommand command) {
            this.sqlSource = sqlSource;
            sqlSource.sqlCommandType = command.getType();
            sqlSource.mapperInterface = command.getMapperInterface();
            sqlSource.method = command.getMapperMethod();
            sqlSource.resultType = command.getAnnotationResultType();
            sqlSource.mappedStatementId = createId();
        }

        public Builder sqlCommandType(SqlCommandType sqlCommandType) {
            sqlSource.sqlCommandType = sqlCommandType;
            return this;
        }

        public Builder mapperInterface(Class<?> mapperInterface) {
            sqlSource.mapperInterface = mapperInterface;
            return this;
        }

        public Builder method(Method method) {
            sqlSource.method = method;
            return this;
        }

        public String createId() {
            StringBuilder idBuilder = new StringBuilder(sqlSource.getMapperInterface().getName()).append(".")
                    .append(sqlSource.getMethod().getName()).append("(");
            for (Class<?> parameterType : sqlSource.getMethod().getParameterTypes()) {
                idBuilder.append(parameterType.getName()).append(",");
            }
            idBuilder.append(sqlSource.getMethod().getReturnType().getName()).append(")").append(":").append(UUID.nameUUIDFromBytes(sqlSource.getDynamicSql().getBytes(StandardCharsets.UTF_8)));
            return idBuilder.toString();
        }

        public SqlSource build() {
            return sqlSource;
        }
    }

    public String getDynamicSql() {
        return dynamicSql;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public Class<?> getMapperInterface() {
        return mapperInterface;
    }

    public Method getMethod() {
        return method;
    }

    public String getResultType() {
        return resultType;
    }

    public String getMappedStatementId() {
        return mappedStatementId;
    }

    /**
     * 获取SQL语句装载体
     *
     * @param configuration   全局配置单例
     * @param parameterObject 请求参数
     * @return SQL语句装载体
     */
    public BoundSql getBoundSql(Configuration configuration, Object parameterObject) {
        //准备参数
        DynamicContext context = new DynamicContext(configuration, parameterObject);
        DynamicSqlParser sqlParser = new DynamicSqlParser();
        //开始解析
        sqlParser.parse(dynamicSql, parameterObject, context);
        //返回结果
        BoundSql boundSql = new BoundSql(configuration, sqlParser.getSql(), sqlParser.getParameterMappingList(), parameterObject);
        context.getBindings().forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }
}
