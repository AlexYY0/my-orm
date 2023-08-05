package club.emperorws.orm.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;

/**
 * Year年份时间类型处理器（数据库实际存的是数字）
 *
 * @author: EmperorWS
 * @date: 2023/4/23 11:36
 * @description: YearTypeHandler: Year年份时间类型处理器（数据库实际存的是数字）
 */
public class YearTypeHandler extends BaseTypeHandler<Year> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Year year, JdbcType type) throws SQLException {
        ps.setInt(i, year.getValue());
    }

    @Override
    public Year getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int year = rs.getInt(columnName);
        return year == 0 && rs.wasNull() ? null : Year.of(year);
    }

    @Override
    public Year getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int year = rs.getInt(columnIndex);
        return year == 0 && rs.wasNull() ? null : Year.of(year);
    }

    @Override
    public Year getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int year = cs.getInt(columnIndex);
        return year == 0 && cs.wasNull() ? null : Year.of(year);
    }
}
