package club.emperorws.orm.type;

import java.sql.*;

/**
 * SqlDate数据库日期：年月日类型处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/23 11:01
 * @description: SqlDateTypeHandler: SqlDate数据库日期：年月日类型处理器
 */
public class SqlDateTypeHandler extends BaseTypeHandler<Date> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
        ps.setDate(i, parameter);
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getDate(columnName);
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getDate(columnIndex);
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getDate(columnIndex);
    }
}
