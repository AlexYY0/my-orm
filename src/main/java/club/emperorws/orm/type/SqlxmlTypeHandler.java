package club.emperorws.orm.type;

import java.sql.*;

/**
 * Sqlxml的类型处理器（貌似是SQL Server的SQLXML）
 * Convert <code>String</code> to/from <code>SQLXML</code>.
 *
 * @author: EmperorWS
 * @date: 2023/4/23 11:53
 * @description: SqlxmlTypeHandler: Sqlxml的类型处理器（貌似是SQL Server的SQLXML）
 */
public class SqlxmlTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        SQLXML sqlxml = ps.getConnection().createSQLXML();
        try {
            sqlxml.setString(parameter);
            ps.setSQLXML(i, sqlxml);
        } finally {
            sqlxml.free();
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return sqlxmlToString(rs.getSQLXML(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return sqlxmlToString(rs.getSQLXML(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return sqlxmlToString(cs.getSQLXML(columnIndex));
    }

    protected String sqlxmlToString(SQLXML sqlxml) throws SQLException {
        if (sqlxml == null) {
            return null;
        }
        try {
            return sqlxml.getString();
        } finally {
            sqlxml.free();
        }
    }
}
