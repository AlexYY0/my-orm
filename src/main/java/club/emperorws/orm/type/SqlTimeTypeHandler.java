package club.emperorws.orm.type;

import java.sql.*;

/**
 * SqlTime数据库时间：时分秒类型处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/23 11:03
 * @description: SqlTimeTypeHandler: SqlTime数据库时间：时分秒类型处理器
 */
public class SqlTimeTypeHandler extends BaseTypeHandler<Time> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Time parameter, JdbcType jdbcType) throws SQLException {
        ps.setTime(i, parameter);
    }

    @Override
    public Time getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getTime(columnName);
    }

    @Override
    public Time getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getTime(columnIndex);
    }

    @Override
    public Time getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getTime(columnIndex);
    }
}
