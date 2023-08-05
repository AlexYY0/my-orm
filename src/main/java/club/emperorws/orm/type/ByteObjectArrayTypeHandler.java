package club.emperorws.orm.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Byte对象数组类型处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/23 15:05
 * @description: ByteObjectArrayTypeHandler: Byte对象数组类型处理器
 */
public class ByteObjectArrayTypeHandler extends BaseTypeHandler<Byte[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Byte[] parameter, JdbcType jdbcType) throws SQLException {
        ps.setBytes(i, ByteArrayUtils.convertToPrimitiveArray(parameter));
    }

    @Override
    public Byte[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte[] bytes = rs.getBytes(columnName);
        return getBytes(bytes);
    }

    @Override
    public Byte[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte[] bytes = rs.getBytes(columnIndex);
        return getBytes(bytes);
    }

    @Override
    public Byte[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte[] bytes = cs.getBytes(columnIndex);
        return getBytes(bytes);
    }

    private Byte[] getBytes(byte[] bytes) {
        Byte[] returnValue = null;
        if (bytes != null) {
            returnValue = ByteArrayUtils.convertToObjectArray(bytes);
        }
        return returnValue;
    }
}
