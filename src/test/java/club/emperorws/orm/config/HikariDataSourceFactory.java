package club.emperorws.orm.config;

import club.emperorws.orm.logging.Log;
import club.emperorws.orm.logging.LogFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.net.URL;

/**
 * HikariCP数据库连接配置
 *
 * @author: EmperorWS
 * @date: 2023/3/4 12:00
 * @description: HikariDataSourceFactory: HikariCP数据库连接配置
 */
public class HikariDataSourceFactory {

    private static final Log log = LogFactory.getLog(HikariDataSourceFactory.class);

    public static DataSource createDataSource() {
        URL resource = HikariDataSourceFactory.class.getClassLoader().getResource("hikariPool.properties");
        log.debug("hikariPool.properties getPath:" + resource.getPath());
        log.debug("hikariPool.properties getFile:" + resource.getFile());
        HikariConfig config = new HikariConfig(resource.getPath());
        config.setMaximumPoolSize(5);
        return new HikariDataSource(config);
    }
}
