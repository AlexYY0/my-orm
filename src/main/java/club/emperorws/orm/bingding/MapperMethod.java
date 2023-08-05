package club.emperorws.orm.bingding;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.annotations.*;
import club.emperorws.orm.cursor.Cursor;
import club.emperorws.orm.exception.OrmException;
import club.emperorws.orm.mapping.*;
import club.emperorws.orm.reflection.MetaObject;
import club.emperorws.orm.reflection.ParamNameResolver;
import club.emperorws.orm.reflection.TypeParameterResolver;
import club.emperorws.orm.result.ResultHandler;
import club.emperorws.orm.session.SqlSession;
import club.emperorws.orm.util.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Mapper方法的真正执行者
 *
 * @author: EmperorWS
 * @date: 2023/4/21 16:48
 * @description: MapperMethod: Mapper方法的真正执行者
 */
public class MapperMethod {

    private final SqlCommand command;
    private final MethodSignature method;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
        //获取SQL语句的执行ID和执行类型
        this.command = new SqlCommand(mapperInterface, method);
        //获取Mapper方法的相关信息，
        this.method = new MethodSignature(config, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result;
        switch (command.getType()) {
            case INSERT: {
                Object param = method.convertArgsToSqlCommandParam(args);
                SqlSource sqlSource = new SqlSource.Builder(method.extractSqlSource(args), command).build();
                result = rowCountResult(sqlSession.insert(sqlSource, param));
                break;
            }
            case UPDATE: {
                Object param = method.convertArgsToSqlCommandParam(args);
                SqlSource sqlSource = new SqlSource.Builder(method.extractSqlSource(args), command).build();
                result = rowCountResult(sqlSession.update(sqlSource, param));
                break;
            }
            case DELETE: {
                Object param = method.convertArgsToSqlCommandParam(args);
                SqlSource sqlSource = new SqlSource.Builder(method.extractSqlSource(args), command).build();
                result = rowCountResult(sqlSession.delete(sqlSource, param));
                break;
            }
            case SELECT:
                if (method.returnsVoid() && method.hasResultHandler()) {
                    //返回结果是void，但是有@ResultType(Xxx.class)注解，且有ResultHandler，具体看@ResultType注解用法
                    executeWithResultHandler(sqlSession, args);
                    result = null;
                } else if (method.returnsMany()) {
                    //返回结果是集合或数组
                    result = executeForMany(sqlSession, args);
                } else if (method.returnsMap()) {
                    //@MapKey("xxx")定义的返回结果为Map：key为属性名称（MapKey.value），value为返回结果对象（一行记录）
                    result = executeForMap(sqlSession, args);
                } else if (method.returnsCursor()) {
                    //游标、流式查询，暂未实现ignore
                    result = executeForCursor(sqlSession, args);
                } else {
                    //返回结果是一个对象
                    Object param = method.convertArgsToSqlCommandParam(args);
                    SqlSource sqlSource = new SqlSource.Builder(method.extractSqlSource(args), command).build();
                    result = sqlSession.selectOne(sqlSource, param);
                    //返回结果允许为Optional
                    if (method.returnsOptional() && (result == null || !method.getReturnType().equals(result.getClass()))) {
                        result = Optional.ofNullable(result);
                    }
                }
                break;
            case FLUSH:
                //Batch批处理模式下的真正的执行者（其实不用手动调用，SqlSession会自动执行，除非需要获取BATCH执行结果）
                result = sqlSession.flushStatements();
                break;
            default:
                throw new OrmException("Unknown execution method for: " + command.getName());
        }
        if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
            throw new OrmException("Mapper method '" + command.getName()
                    + " attempted to return null from a method with a primitive return type (" + method.getReturnType() + ").");
        }
        return result;
    }

    /**
     * SQL执行受影响的行数结果封装
     *
     * @param rowCount SQL执行受影响的行数
     * @return SQL执行受影响的行数结果封装
     */
    private Object rowCountResult(int rowCount) {
        final Object result;
        if (method.returnsVoid()) {
            result = null;
        } else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
            result = rowCount;
        } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
            result = (long) rowCount;
        } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
            result = rowCount > 0;
        } else {
            throw new OrmException("Mapper method '" + command.getName() + "' has an unsupported return type: " + method.getReturnType());
        }
        return result;
    }

    private void executeWithResultHandler(SqlSession sqlSession, Object[] args) {
        SqlSource sqlSource = new SqlSource.Builder(method.extractSqlSource(args), command).build();
        MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(sqlSource);
        if (!StatementType.CALLABLE.equals(ms.getStatementType()) && void.class.equals(ms.getResultMap().getType())) {
            throw new OrmException("method " + command.getName()
                    + " needs a @ResultType annotation, so a ResultHandler can be used as a parameter.");
        }
        Object param = method.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            sqlSession.select(sqlSource, param, rowBounds, method.extractResultHandler(args));
        } else {
            sqlSession.select(sqlSource, param, method.extractResultHandler(args));
        }
    }

    private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
        List<E> result;
        Object param = method.convertArgsToSqlCommandParam(args);
        SqlSource sqlSource = new SqlSource.Builder(method.extractSqlSource(args), command).build();
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.selectList(sqlSource, param, rowBounds);
        } else {
            result = sqlSession.selectList(sqlSource, param);
        }
        // 支持集合和数组
        if (!method.getReturnType().isAssignableFrom(result.getClass())) {
            if (method.getReturnType().isArray()) {
                return convertToArray(result);
            } else {
                return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
            }
        }
        return result;
    }

    private <T> Cursor<T> executeForCursor(SqlSession sqlSession, Object[] args) {
        Cursor<T> result;
        Object param = method.convertArgsToSqlCommandParam(args);
        SqlSource sqlSource = new SqlSource.Builder(method.extractSqlSource(args), command).build();
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.selectCursor(sqlSource, param, rowBounds);
        } else {
            result = sqlSession.selectCursor(sqlSource, param);
        }
        return result;
    }

    private <E> Object convertToDeclaredCollection(Configuration config, List<E> list) {
        Object collection = config.getObjectFactory().create(method.getReturnType());
        MetaObject metaObject = config.newMetaObject(collection);
        metaObject.addAll(list);
        return collection;
    }

    @SuppressWarnings("unchecked")
    private <E> Object convertToArray(List<E> list) {
        Class<?> arrayComponentType = method.getReturnType().getComponentType();
        Object array = Array.newInstance(arrayComponentType, list.size());
        if (arrayComponentType.isPrimitive()) {
            for (int i = 0; i < list.size(); i++) {
                Array.set(array, i, list.get(i));
            }
            return array;
        } else {
            return list.toArray((E[]) array);
        }
    }

    private <K, V> Map<K, V> executeForMap(SqlSession sqlSession, Object[] args) {
        Map<K, V> result;
        Object param = method.convertArgsToSqlCommandParam(args);
        SqlSource sqlSource = new SqlSource.Builder(method.extractSqlSource(args), command).build();
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.selectMap(sqlSource, param, method.getMapKey(), rowBounds);
        } else {
            result = sqlSession.selectMap(sqlSource, param, method.getMapKey());
        }
        return result;
    }

    /****************************************************Mapper执行方法的请求参数的封装*******************************************************/

    public static class ParamMap<V> extends HashMap<String, V> {

        private static final long serialVersionUID = -2212268410512043556L;

        @Override
        public V get(Object key) {
            if (!super.containsKey(key)) {
                throw new OrmException("Parameter '" + key + "' not found. Available parameters are " + keySet());
            }
            return super.get(key);
        }
    }

    /****************************************************Mapper执行方法SQL语句类型的说明*******************************************************/

    public static class SqlCommand {

        private final String name;
        private SqlCommandType type;
        private final Method mapperMethod;
        private final Class<?> mapperInterface;
        private String annotationResultType;

        public SqlCommand(Class<?> mapperInterface, Method method) {
            mapperMethod = method;
            this.mapperInterface = mapperInterface;
            final String methodName = method.getName();
            //final Class<?> declaringClass = method.getDeclaringClass();
            //获取SQL语句执行类型等相关参数
            getAnnotationAndSth(method);
            if (type == null) {
                if (method.getAnnotation(Flush.class) != null) {
                    name = null;
                    type = SqlCommandType.FLUSH;
                } else {
                    throw new OrmException("Invalid bound statement (not found): " + mapperInterface.getName() + "." + methodName);
                }
            } else {
                name = mapperInterface.getName() + "." + methodName;
                if (type == SqlCommandType.UNKNOWN) {
                    throw new OrmException("Unknown execution method for: " + name);
                }
            }
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }

        public Method getMapperMethod() {
            return mapperMethod;
        }

        public Class<?> getMapperInterface() {
            return mapperInterface;
        }

        public String getAnnotationResultType() {
            return annotationResultType;
        }

        /**
         * 获取注解的一些参数与信息
         *
         * @param method Mapper的方法
         */
        private void getAnnotationAndSth(Method method) {
            //需要扫描的注解方法（对于@Flush，不会扫描）
            Select select = method.getAnnotation(Select.class);
            if (select != null) {
                type = SqlCommandType.SELECT;
                //跳过BaseMapper的空注解，自己去找返回值类型
                annotationResultType = StringUtils.isNotBlank(select.resultType()) ? select.resultType() : null;
                return;
            }
            Insert insert = method.getAnnotation(Insert.class);
            if (insert != null) {
                type = SqlCommandType.INSERT;
                //跳过BaseMapper的空注解，自己去找返回值类型
                annotationResultType = StringUtils.isNotBlank(insert.resultType()) ? insert.resultType() : null;
                return;
            }
            Update update = method.getAnnotation(Update.class);
            if (update != null) {
                type = SqlCommandType.UPDATE;
                //跳过BaseMapper的空注解，自己去找返回值类型
                annotationResultType = StringUtils.isNotBlank(update.resultType()) ? update.resultType() : null;
                return;
            }
            Delete delete = method.getAnnotation(Delete.class);
            if (delete != null) {
                type = SqlCommandType.DELETE;
                //跳过BaseMapper的空注解，自己去找返回值类型
                annotationResultType = StringUtils.isNotBlank(delete.resultType()) ? delete.resultType() : null;
                return;
            }
        }
    }

    /****************************************************Mapper执行方法的相关信息封装*******************************************************/

    public static class MethodSignature {

        private final boolean returnsMany;
        private final boolean returnsMap;
        private final boolean returnsVoid;
        private final boolean returnsCursor;
        private final boolean returnsOptional;
        private final Class<?> returnType;
        private final String mapKey;
        private final Integer resultHandlerIndex;
        private final Integer rowBoundsIndex;
        private final Integer sqlSourceIndex;
        private final ParamNameResolver paramNameResolver;

        public MethodSignature(Configuration configuration, Class<?> mapperInterface, Method method) {
            //获取method返回值class类型
            Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
            if (resolvedReturnType instanceof Class<?>) {
                this.returnType = (Class<?>) resolvedReturnType;
            } else if (resolvedReturnType instanceof ParameterizedType) {
                this.returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
            } else {
                this.returnType = method.getReturnType();
            }
            this.returnsVoid = void.class.equals(this.returnType);
            this.returnsMany = configuration.getObjectFactory().isCollection(this.returnType) || this.returnType.isArray();
            this.returnsCursor = Cursor.class.equals(this.returnType);
            this.returnsOptional = Optional.class.equals(this.returnType);
            //@MapKey结果的封装
            this.mapKey = getMapKey(method);
            this.returnsMap = this.mapKey != null;
            this.rowBoundsIndex = getUniqueParamIndex(method, RowBounds.class);
            this.resultHandlerIndex = getUniqueParamIndex(method, ResultHandler.class);
            this.sqlSourceIndex = getUniqueParamIndex(method, SqlSource.class);
            //Mapper方法请求参数的处理器
            this.paramNameResolver = new ParamNameResolver(configuration, method);
        }

        /**
         * 获取封装后的参数
         *
         * @param args 原Mapper方法的所有参数
         * @return 封装后的参数（key：参数名称，value：参数）
         */
        public Object convertArgsToSqlCommandParam(Object[] args) {
            return paramNameResolver.getNamedParams(args);
        }

        public boolean hasRowBounds() {
            return rowBoundsIndex != null;
        }

        public RowBounds extractRowBounds(Object[] args) {
            return hasRowBounds() ? (RowBounds) args[rowBoundsIndex] : null;
        }

        public boolean hasResultHandler() {
            return resultHandlerIndex != null;
        }

        public ResultHandler extractResultHandler(Object[] args) {
            return hasResultHandler() ? (ResultHandler) args[resultHandlerIndex] : null;
        }

        public boolean hasSqlSource() {
            return sqlSourceIndex != null;
        }

        public SqlSource extractSqlSource(Object[] args) {
            return hasSqlSource() ? (SqlSource) args[sqlSourceIndex] : null;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public boolean returnsMany() {
            return returnsMany;
        }

        public boolean returnsMap() {
            return returnsMap;
        }

        public boolean returnsVoid() {
            return returnsVoid;
        }

        public boolean returnsCursor() {
            return returnsCursor;
        }

        /**
         * return whether return type is {@code java.util.Optional}.
         *
         * @return return {@code true}, if return type is {@code java.util.Optional}
         */
        public boolean returnsOptional() {
            return returnsOptional;
        }

        /**
         * 获取paramType类型在method方法参数的index
         *
         * @param method    Mapper的method
         * @param paramType 需要知道的参数类型
         * @return paramType类型在method方法参数的index
         */
        private Integer getUniqueParamIndex(Method method, Class<?> paramType) {
            Integer index = null;
            final Class<?>[] argTypes = method.getParameterTypes();
            for (int i = 0; i < argTypes.length; i++) {
                if (paramType.isAssignableFrom(argTypes[i])) {
                    if (index == null) {
                        index = i;
                    } else {
                        throw new OrmException(method.getName() + " cannot have multiple " + paramType.getSimpleName() + " parameters");
                    }
                }
            }
            return index;
        }

        public String getMapKey() {
            return mapKey;
        }

        private String getMapKey(Method method) {
            String mapKey = null;
            if (Map.class.isAssignableFrom(method.getReturnType())) {
                final MapKey mapKeyAnnotation = method.getAnnotation(MapKey.class);
                if (mapKeyAnnotation != null) {
                    mapKey = mapKeyAnnotation.value();
                }
            }
            return mapKey;
        }
    }
}
