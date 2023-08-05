package club.emperorws.orm.parsing;

import club.emperorws.orm.Configuration;
import club.emperorws.orm.reflection.MetaObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ${}请求参数直接拼接到SQL的解析器
 *
 * @author: EmperorWS
 * @date: 2023/5/6 16:46
 * @description: ParameterJoinTokenHandler: ${}请求参数直接拼接到SQL的解析器
 */
public class ParamJoinTokenHandler implements TokenHandler {

    private static final Pattern SQL_REPLACE_PATTERN = Pattern.compile("$\\{.+?}");

    private final Configuration configuration;

    private DynamicContext context;

    public ParamJoinTokenHandler(Configuration configuration, DynamicContext context) {
        this.configuration = configuration;
        this.context = context;
    }

    /**
     * 动态sql语句解析
     *
     * @param dynamicSql 原sql语句
     * @return 解析后的sql语句
     */
    @Override
    public String handleToken(String dynamicSql) {
        //获取原请求参数
        Object parameter = context.getBindings().get("_parameter");
        StringBuffer sqlStr = new StringBuffer();
        Matcher matcher = SQL_REPLACE_PATTERN.matcher(dynamicSql);
        while (matcher.find()) {
            //获取content片段
            String sqlSegment = matcher.group()
                    .replace("${", "")
                    .replace("}", "");
            //获取content对应的值
            String value;
            if (context.getBindings().containsKey(sqlSegment)) {
                value = String.valueOf(context.getBindings().get(sqlSegment));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameter);
                value = String.valueOf(metaObject.getValue(sqlSegment));
            }
            matcher.appendReplacement(sqlStr, value);
        }
        matcher.appendTail(sqlStr);
        return sqlStr.toString();
    }
}
