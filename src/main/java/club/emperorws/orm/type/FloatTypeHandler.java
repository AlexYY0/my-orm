package club.emperorws.orm.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Float类型处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/21 17:06
 * @description: FloatTypeHandler: Float类型处理器
 */
public class FloatTypeHandler extends BaseTypeHandler<Float> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Float parameter, JdbcType jdbcType) throws SQLException {
        ps.setFloat(i, parameter);
    }

    @Override
    public Float getNullableResult(ResultSet rs, String columnName) throws SQLException {
        float result = rs.getFloat(columnName);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Float getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        float result = rs.getFloat(columnIndex);
        return result == 0 && rs.wasNull() ? null : result;
    }

    @Override
    public Float getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        float result = cs.getFloat(columnIndex);
        return result == 0 && cs.wasNull() ? null : result;
    }
}
