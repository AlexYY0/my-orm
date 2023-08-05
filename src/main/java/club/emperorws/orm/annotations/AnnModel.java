package club.emperorws.orm.annotations;

import club.emperorws.orm.interfaces.GenId;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据文件转对象DO映射注解，ORM实体映射注解
 *
 * @author: EmperorWS
 * @date: 2023/2/2 14:08
 * @description: AnnFileModel: 数据文件转对象DO映射注解，
 */
public class AnnModel {

    /**
     * 实体类注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Table {

        /**
         * 数据库表名称
         *
         * @return 数据库表名称
         */
        String tableName();

        /**
         * 主键对应的数据库列名称（不支持联合主键，同时也请避免使用联合主键）
         *
         * @return 主键对应的数据库列名称
         */
        String pkName();
    }

    /**
     * 实体字段属性注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface AnnField {

        /**
         * 数据库是否有此字段
         *
         * @return 默认数据库有此字段，必须入库
         */
        boolean require() default true;

        /**
         * 字段类型（数据库存储的类型）
         *
         * @return 需要转化的字段类型
         */
        String jdbcType() default "";

        /**
         * 实体字段类型（实体存储的类型）
         *
         * @return java实体属性类型
         */
        String javaType() default "";

        /**
         * 字段的处理器typeHandler
         *
         * @return 字段的ORM处理器typeHandler
         */
        String typeHandler() default "";

        /**
         * 数据库列名
         *
         * @return 数据库列名
         */
        String column() default "";

        /**
         * 映射的实体属性名称
         *
         * @return 映射的实体属性名称
         */
        String property() default "";

        /**
         * 是否为主键自动生成
         * 如果是主键自动生成，则使用{@link #genId()}类生成
         *
         * @return 默认不是主键，不自动生成
         */
        boolean isPkGen() default false;

        /**
         * 自定义主键Id生成策略
         *
         * @return 主键ID生成策略
         */
        Class<? extends GenId> genId() default GenId.NULL.class;

        /**
         * 用于判断实体数据是新增还是更新的字段
         *
         * @return true：该字段用于条件判断；false：该字段不用于条件判断
         */
        boolean isInsertOrUpdateCondition() default false;
    }
}
