package club.emperorws.orm.mapper;

import club.emperorws.orm.annotations.Param;
import club.emperorws.orm.annotations.Select;
import club.emperorws.orm.entity.Student;
import club.emperorws.orm.mapping.SqlSource;

import java.util.List;

/**
 * StudentMapper
 *
 * @author: EmperorWS
 * @date: 2023/5/29 14:21
 * @description: StudentMapper: StudentMapper
 */
public interface StudentMapper {

    @Select(resultType = "club.emperorws.orm.entity.Student")
    List<Student> selectList(SqlSource sqlSource, @Param("keyword") String keyword);
}
