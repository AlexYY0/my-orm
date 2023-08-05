package club.emperorws.orm.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Month;

/**
 * Month月份时间类型处理器（数据库实际存的是数字）
 *
 * @author: EmperorWS
 * @date: 2023/4/23 11:42
 * @description: MonthTypeHandler: Month月份时间类型处理器（数据库实际存的是数字）
 */
public class MonthTypeHandler extends BaseTypeHandler<Month> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Month month, JdbcType type) throws SQLException {
        ps.setInt(i, month.getValue());
    }

    @Override
    public Month getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int month = rs.getInt(columnName);
        return month == 0 && rs.wasNull() ? null : Month.of(month);
    }

    @Override
    public Month getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int month = rs.getInt(columnIndex);
        return month == 0 && rs.wasNull() ? null : Month.of(month);
    }

    @Override
    public Month getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int month = cs.getInt(columnIndex);
        return month == 0 && cs.wasNull() ? null : Month.of(month);
    }
}
