package club.emperorws.orm;

import club.emperorws.orm.bingding.MapperRegistry;
import club.emperorws.orm.exception.ReuseExecutor;
import club.emperorws.orm.executor.BatchExecutor;
import club.emperorws.orm.executor.Executor;
import club.emperorws.orm.executor.SimpleExecutor;
import club.emperorws.orm.logging.Log;
import club.emperorws.orm.logging.LogFactory;
import club.emperorws.orm.mapping.*;
import club.emperorws.orm.metadata.TableModelInfo;
import club.emperorws.orm.metadata.TableModelInfoHelper;
import club.emperorws.orm.parameter.DefaultParameterHandler;
import club.emperorws.orm.parameter.ParameterHandler;
import club.emperorws.orm.plugin.Interceptor;
import club.emperorws.orm.plugin.InterceptorChain;
import club.emperorws.orm.reflection.DefaultReflectorFactory;
import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.reflection.ReflectorFactory;
import club.emperorws.orm.reflection.factory.DefaultObjectFactory;
import club.emperorws.orm.reflection.factory.ObjectFactory;
import club.emperorws.orm.reflection.wrapper.DefaultObjectWrapperFactory;
import club.emperorws.orm.reflection.wrapper.ObjectWrapperFactory;
import club.emperorws.orm.result.AutoMappingBehavior;
import club.emperorws.orm.result.DefaultResultSetHandler;
import club.emperorws.orm.result.ResultHandler;
import club.emperorws.orm.result.ResultSetHandler;
import club.emperorws.orm.session.ExecutorType;
import club.emperorws.orm.session.SqlSession;
import club.emperorws.orm.statement.RoutingStatementHandler;
import club.emperorws.orm.statement.StatementHandler;
import club.emperorws.orm.transaction.Transaction;
import club.emperorws.orm.type.JdbcType;
import club.emperorws.orm.type.TypeAliasRegistry;
import club.emperorws.orm.type.TypeHandlerRegistry;
import club.emperorws.orm.util.BuilderUtil;

import java.util.List;

/**
 * orm的所有配置单例
 *
 * @author: EmperorWS
 * @date: 2023/4/17 12:04
 * @description: Configuration: orm的所有配置单例
 */
public class Configuration {

    protected Environment environment;

    /*************************************************some configuration**********************************************************/

    /**
     * 数据库名称，暂未实现多数据库切换，ignore
     */
    protected String databaseId = "ORACLE";

    /**
     * 是否使用（建议为true）
     * true：getColumnLabel（获取数据列的别名）
     * false：getColumnName（获取数据列的原始列名）
     */
    protected boolean useColumnLabel = true;
    /**
     * 是否直接使用方法的参数名称当作DAO（Mapper）方法的参数名
     */
    protected boolean useActualParamName = true;

    /**
     * 如果结果集为null，是否set null，建议为true，原mybatis为false
     */
    protected boolean callSettersOnNulls = true;

    /**
     * 当返回结果所有列都为空时，是返回空实例还是null，默认为false，返回null
     */
    protected boolean returnInstanceForEmptyRow = false;

    /**
     * 是否开启驼峰自动映射，默认为true
     */
    protected boolean mapUnderscoreToCamelCase = true;

    /**
     * 默认Statement的超时时间
     */
    protected Integer defaultStatementTimeout;

    /**
     * 默认Statement的同步大小
     */
    protected Integer defaultFetchSize;

    /**
     * Mapper的动态代理注册器
     */
    protected final MapperRegistry mapperRegistry = new MapperRegistry(this);

    /**
     * JavaType别名注册器
     */
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    /**
     * TypeHandler注册器
     */
    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry(this);

    /**
     * 插件拦截器执行链
     */
    protected final InterceptorChain interceptorChain = new InterceptorChain();

    /**
     * 自动映射的方式：默认为简单映射
     */
    protected AutoMappingBehavior autoMappingBehavior = AutoMappingBehavior.PARTIAL;

    /**
     * 默认的SQL执行类型
     */
    protected ExecutorType defaultExecutorType = ExecutorType.SIMPLE;

    /**
     * 自定义的日志打印工具
     */
    protected Class<? extends Log> logImpl;

    /*************************************************tool**********************************************************/

    protected BuilderUtil builderUtil = new BuilderUtil(this);

    /*************************************************initial**********************************************************/

    public Configuration(Environment environment) {
        this();
        this.environment = environment;
    }

    public Configuration() {
    }

    /*************************************************some default information**********************************************************/

    protected JdbcType jdbcTypeForNull = JdbcType.OTHER;

    /**
     * 元对象处理的一些默认工厂
     */
    protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    /*************************************************get、set some property**********************************************************/

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    public boolean isUseActualParamName() {
        return useActualParamName;
    }

    public void setUseActualParamName(boolean useActualParamName) {
        this.useActualParamName = useActualParamName;
    }

    public boolean isCallSettersOnNulls() {
        return callSettersOnNulls;
    }

    public void setCallSettersOnNulls(boolean callSettersOnNulls) {
        this.callSettersOnNulls = callSettersOnNulls;
    }

    public boolean isReturnInstanceForEmptyRow() {
        return returnInstanceForEmptyRow;
    }

    public void setReturnInstanceForEmptyRow(boolean returnEmptyInstance) {
        this.returnInstanceForEmptyRow = returnEmptyInstance;
    }

    public boolean isMapUnderscoreToCamelCase() {
        return mapUnderscoreToCamelCase;
    }

    public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
        this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
    }

    public boolean isUseColumnLabel() {
        return useColumnLabel;
    }

    public void setUseColumnLabel(boolean useColumnLabel) {
        this.useColumnLabel = useColumnLabel;
    }

    public Integer getDefaultStatementTimeout() {
        return defaultStatementTimeout;
    }

    public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
        this.defaultStatementTimeout = defaultStatementTimeout;
    }

    public Integer getDefaultFetchSize() {
        return defaultFetchSize;
    }

    public void setDefaultFetchSize(Integer defaultFetchSize) {
        this.defaultFetchSize = defaultFetchSize;
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }

    public List<Interceptor> getInterceptors() {
        return interceptorChain.getInterceptors();
    }

    public AutoMappingBehavior getAutoMappingBehavior() {
        return autoMappingBehavior;
    }

    public void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
        this.autoMappingBehavior = autoMappingBehavior;
    }

    public ExecutorType getDefaultExecutorType() {
        return defaultExecutorType;
    }

    public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
        this.defaultExecutorType = defaultExecutorType;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public void setReflectorFactory(ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
        this.objectWrapperFactory = objectWrapperFactory;
    }

    public JdbcType getJdbcTypeForNull() {
        return jdbcTypeForNull;
    }

    public void setJdbcTypeForNull(JdbcType jdbcTypeForNull) {
        this.jdbcTypeForNull = jdbcTypeForNull;
    }

    /**************************************************tools********************************************************/

    public void addInterceptor(Interceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
    }

    public void addMappers(String packageName, Class<?> superType) {
        mapperRegistry.addMappers(packageName, superType);
    }

    public void addMapperPackages(String... packageNames) {
        for (String packageName : packageNames) {
            addMappers(packageName);
        }
    }

    public void addMappers(String packageName) {
        mapperRegistry.addMappers(packageName);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public Class<? extends Log> getLogImpl() {
        return logImpl;
    }

    public void setLogImpl(Class<? extends Log> logImpl) {
        if (logImpl != null) {
            this.logImpl = logImpl;
            LogFactory.useCustomLogging(this.logImpl);
        }
    }

    public BuilderUtil getBuilderUtil() {
        return builderUtil;
    }

    public MetaObject newMetaObject(Object object) {
        return MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }

    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
        return parameterHandler;
    }

    public ResultSetHandler newResultSetHandler(MappedStatement mappedStatement, RowBounds rowBounds, ResultHandler resultHandler) {
        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(mappedStatement, resultHandler, rowBounds);
        resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
        return resultSetHandler;
    }

    public StatementHandler newStatementHandler(MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler statementHandler = new RoutingStatementHandler(mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
        return statementHandler;
    }

    /**
     * 创建一个SQL执行器
     *
     * @param transaction  事务信息
     * @param executorType SQL执行类型-->创建对应的SQL执行器
     * @return executorType对应的SQL执行器
     */
    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        executorType = executorType == null ? defaultExecutorType : executorType;
        executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
        Executor executor;
        if (ExecutorType.BATCH == executorType) {
            executor = new BatchExecutor(this, transaction);
        } else if (ExecutorType.REUSE == executorType) {
            executor = new ReuseExecutor(this, transaction);
        } else {
            executor = new SimpleExecutor(this, transaction);
        }
        executor = (Executor) interceptorChain.pluginAll(executor);
        return executor;
    }

    public MappedStatement getMappedStatement(SqlSource sqlSource) {
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(this, sqlSource, sqlSource.getSqlCommandType())
                .resultMap(buildResultMap(sqlSource));
        return statementBuilder.build();
    }

    protected ResultMap buildResultMap(SqlSource sqlSource) {
        // 1. 获取resultType
        String resultTypeStr = sqlSource.getResultType();
        Class<?> resultType = builderUtil.resolveClass(resultTypeStr);
        if (resultType == null) {
            resultType = builderUtil.getReturnType(sqlSource.getMethod(), sqlSource.getMapperInterface());
        }
        // 2. 获取ResultMapping字段映射
        TableModelInfo tableInfo = TableModelInfoHelper.getTableInfo(resultType);
        if (tableInfo == null) {
            // method返回结果非实体类型，自己组装ResultMap
            return new ResultMap.Builder(this, resultType.getName(), resultType).build();
        }
        return tableInfo.getResultMap();
    }
}
