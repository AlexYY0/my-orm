package club.emperorws.orm.entity;

import club.emperorws.orm.annotations.AnnModel;

import java.util.Date;

/**
 * 表名：学生表
 *
 * @author CodeGenerator
 */
@AnnModel.Table(tableName = "student", pkName = "id")
public class Student {

    @AnnModel.AnnField(column = "id", property = "id", jdbcType = "INTEGER")
    private Integer id;

    @AnnModel.AnnField(column = "name", property = "name", jdbcType = "VARCHAR")
    private String name;

    @AnnModel.AnnField(column = "pwd", property = "pwd", jdbcType = "VARCHAR")
    private String pwd;

    @AnnModel.AnnField(column = "sex", property = "sex", jdbcType = "VARCHAR")
    private String sex;

    @AnnModel.AnnField(column = "birthday", property = "birthday", jdbcType = "TIMESTAMP")
    private Date birthday;

    @AnnModel.AnnField(column = "address", property = "address", jdbcType = "VARCHAR")
    private String address;

    @AnnModel.AnnField(column = "email", property = "email", jdbcType = "VARCHAR")
    private String email;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pwd='" + pwd + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
