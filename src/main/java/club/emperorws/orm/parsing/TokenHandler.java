package club.emperorws.orm.parsing;

/**
 * 动态SQL片段解析接口
 *
 * @author: EmperorWS
 * @date: 2023/5/6 16:41
 * @description: TokenHandler: 动态SQL片段解析接口
 */
public interface TokenHandler {

    /**
     * 动态sql语句解析
     *
     * @param dynamicSql 原sql语句
     * @return 解析后的sql语句
     */
    String handleToken(String dynamicSql);
}
