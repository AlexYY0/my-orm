package club.emperorws.orm.mapping;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.logging.Log;
import club.emperorws.orm.logging.LogFactory;

/**
 * 一条sql语句的装载体，包括其相关信息
 *
 * @author: EmperorWS
 * @date: 2023/4/28 11:36
 * @description: MappedStatement: 一条sql语句的装载体
 */
public class MappedStatement {

    private Configuration configuration;

    private String id;

    /**
     * Mapper Method资源信息
     */
    private String resource;

    /**
     * 驱动的结果集获取数量
     */
    private Integer fetchSize;
    private Integer timeout;

    /**
     * 哪种SQL模式的执行
     */
    private StatementType statementType;

    /**
     * SQL语句的执行类型
     */
    private SqlCommandType sqlCommandType;

    /**
     * 返回结果类型，现阶段默认为DEFAULT
     */
    private ResultSetType resultSetType;

    /**
     * 返回结果映射信息
     */
    private ResultMap resultMap;

    /**
     * 该Mapper method的执行日志log
     */
    private Log statementLog;

    /**
     * 保存原始SQL语句实体
     */
    private SqlSource sqlSource;

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, SqlSource sqlSource, SqlCommandType sqlCommandType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = sqlSource.getMappedStatementId();
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.statementType = StatementType.PREPARED;
            mappedStatement.resultSetType = ResultSetType.DEFAULT;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.resource = sqlSource.getMapperInterface().getName() + "." + sqlSource.getMethod().getName();
            mappedStatement.statementLog = LogFactory.getLog(mappedStatement.resource);
        }

        public Builder id(String id) {
            mappedStatement.id = id;
            return this;
        }

        public Builder resource(String resource) {
            mappedStatement.resource = resource;
            return this;
        }

        public Builder fetchSize(Integer fetchSize) {
            mappedStatement.fetchSize = fetchSize;
            return this;
        }

        public Builder timeout(Integer timeout) {
            mappedStatement.timeout = timeout;
            return this;
        }

        public Builder statementType(StatementType statementType) {
            mappedStatement.statementType = statementType;
            return this;
        }

        public Builder resultSetType(ResultSetType resultSetType) {
            mappedStatement.resultSetType = resultSetType == null ? ResultSetType.DEFAULT : resultSetType;
            return this;
        }

        public Builder resultMap(ResultMap resultMap) {
            mappedStatement.resultMap = resultMap;
            return this;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            assert mappedStatement.sqlSource != null;
            return mappedStatement;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public String getResource() {
        return resource;
    }

    public StatementType getStatementType() {
        return statementType;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public ResultSetType getResultSetType() {
        return resultSetType;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public ResultMap getResultMap() {
        return resultMap;
    }

    public Log getStatementLog() {
        return statementLog;
    }

    /**
     * 获取SQL语句装载体
     *
     * @param parameterObject 请求参数
     * @return SQL语句装载体
     */
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(configuration, parameterObject);
    }
}
