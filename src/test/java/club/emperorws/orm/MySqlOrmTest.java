package club.emperorws.orm;

import club.emperorws.orm.config.HikariDataSourceFactory;
import club.emperorws.orm.entity.Student;
import club.emperorws.orm.logging.Log;
import club.emperorws.orm.logging.LogFactory;
import club.emperorws.orm.mapper.StudentMapper;
import club.emperorws.orm.mapping.Environment;
import club.emperorws.orm.mapping.SqlSource;
import club.emperorws.orm.session.SqlSession;
import club.emperorws.orm.session.defaults.DefaultSqlSessionFactory;
import club.emperorws.orm.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.util.List;

/**
 * my-orm的MySQL执行测试
 *
 * @author: EmperorWS
 * @date: 2023/8/5 12:44
 * @description: MySqlOrmTest: my-orm的MySQL执行测试
 */
@Tag("MySQL执行测试@Tag")
@DisplayName("MySQL执行测试@DisplayName")
public class MySqlOrmTest {

    private static final Log log = LogFactory.getLog(MySqlOrmTest.class);

    private static SqlSession sqlSession;

    @BeforeAll
    static void setUp() throws Exception {
        //1. 初始化配置文件Configuration（包括数据源dataSource）
        Configuration configuration = new Configuration();
        //2. 获取数据库连接池
        DataSource dataSource = HikariDataSourceFactory.createDataSource();
        //3. 设置ORM环境
        Environment environment = new Environment(new JdbcTransactionFactory(), dataSource);
        configuration.setEnvironment(environment);
        //4. 添加Mapper扫描
        configuration.addMapperPackages("club.emperorws.orm.mapper");
        //5. 获取SqlSessionFactory
        DefaultSqlSessionFactory sqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        //6. 获取SqlSession
        sqlSession = sqlSessionFactory.openSession();
    }

    @DisplayName("MySQL的Select查询测试")
    @Test
    public void mysqlSelectTest() {
        try {
            String sql = "select * from student where name like concat('%',#{keyword},'%')";
            StudentMapper studentMapper = sqlSession.getMapper(StudentMapper.class);
            List<Student> studentList = studentMapper.selectList(new SqlSource.Builder(sql).build(), "a");
            studentList.forEach(student -> log.debug(student.toString()));
        } catch (Exception e) {
            log.error("mysqlSelectTest has an error.", e);
        }
    }
}
