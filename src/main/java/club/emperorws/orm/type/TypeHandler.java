package club.emperorws.orm.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 最顶层的数据类型处理器接口
 *
 * @author: EmperorWS
 * @date: 2023/4/20 10:55
 * @description: TypeHandler: 最顶层的数据类型处理器接口
 */
public interface TypeHandler<T> {

    /**
     * sql请求参数设置
     */
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;

    T getResult(ResultSet rs, String columnName) throws SQLException;

    T getResult(ResultSet rs, int columnIndex) throws SQLException;

    /**
     * 存储过程，暂未开发，ignore
     */
    T getResult(CallableStatement cs, int columnIndex) throws SQLException;
}
