package club.emperorws.orm.type;

import java.io.Reader;
import java.sql.*;

/**
 * ClobReader类型处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/21 17:21
 * @description: ClobReaderTypeHandler: ClobReader类型处理器
 */
public class ClobReaderTypeHandler extends BaseTypeHandler<Reader> {

    /**
     * Set a {@link Reader} into {@link PreparedStatement}.
     *
     * @see PreparedStatement#setClob(int, Reader)
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Reader parameter, JdbcType jdbcType) throws SQLException {
        ps.setClob(i, parameter);
    }

    /**
     * Get a {@link Reader} that corresponds to a specified column name from {@link ResultSet}.
     *
     * @see ResultSet#getClob(String)
     */
    @Override
    public Reader getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toReader(rs.getClob(columnName));
    }

    /**
     * Get a {@link Reader} that corresponds to a specified column index from {@link ResultSet}.
     *
     * @see ResultSet#getClob(int)
     */
    @Override
    public Reader getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toReader(rs.getClob(columnIndex));
    }

    /**
     * Get a {@link Reader} that corresponds to a specified column index from {@link CallableStatement}.
     *
     * @see CallableStatement#getClob(int)
     */
    @Override
    public Reader getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toReader(cs.getClob(columnIndex));
    }

    private Reader toReader(Clob clob) throws SQLException {
        if (clob == null) {
            return null;
        } else {
            return clob.getCharacterStream();
        }
    }

}
