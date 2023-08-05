package club.emperorws.orm.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 请求参数处理器接口
 *
 * @author: EmperorWS
 * @date: 2023/4/27 17:14
 * @description: ParameterHandler: 请求参数处理器接口
 */
public interface ParameterHandler {

    Object getParameterObject();

    void setParameters(PreparedStatement ps) throws SQLException;
}
