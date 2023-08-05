package club.emperorws.orm.type;

import java.sql.*;

/**
 * SqlTimestamp数据库时间戳：年月日时分秒类型处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/23 11:05
 * @description: SqlTimestampTypeHandler: SqlTimestamp数据库时间戳：年月日时分秒类型处理器
 */
public class SqlTimestampTypeHandler extends BaseTypeHandler<Timestamp> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Timestamp parameter, JdbcType jdbcType) throws SQLException {
        ps.setTimestamp(i, parameter);
    }

    @Override
    public Timestamp getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getTimestamp(columnName);
    }

    @Override
    public Timestamp getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getTimestamp(columnIndex);
    }

    @Override
    public Timestamp getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getTimestamp(columnIndex);
    }
}
