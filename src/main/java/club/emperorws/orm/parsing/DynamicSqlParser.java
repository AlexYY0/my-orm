package club.emperorws.orm.parsing;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.mapping.ParameterMapping;

import java.util.List;

/**
 * 动态SQL语句的解析
 *
 * @author: EmperorWS
 * @date: 2023/5/6 16:29
 * @description: DynamicSqlParser: 动态SQL语句的解析
 */
public class DynamicSqlParser {

    private String sql;

    private List<ParameterMapping> parameterMappingList;

    public void parse(String dynamicSql, Object parameterObject, DynamicContext context) {
        Configuration configuration = context.getConfiguration();
        //先解析${}
        ParamJoinTokenHandler paramJoinTokenHandler = new ParamJoinTokenHandler(configuration, context);
        //再解析#{}
        Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
        ParamPlaceHolderTokenHandler paramPlaceHolderTokenHandler = new ParamPlaceHolderTokenHandler(configuration, parameterType, context.getBindings());
        sql = paramPlaceHolderTokenHandler.handleToken(paramJoinTokenHandler.handleToken(dynamicSql));
        parameterMappingList = paramPlaceHolderTokenHandler.getParameterMappingList();
    }

    public String getSql() {
        return sql;
    }

    public List<ParameterMapping> getParameterMappingList() {
        return parameterMappingList;
    }
}
