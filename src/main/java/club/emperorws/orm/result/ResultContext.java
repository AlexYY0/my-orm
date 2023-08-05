package club.emperorws.orm.result;

/**
 * ORM结果上下文存储器接口
 *
 * @author: EmperorWS
 * @date: 2023/4/27 15:38
 * @description: ResultContext: ORM结果上下文存储器接口
 */
public interface ResultContext<T> {

    T getResultObject();

    int getResultCount();

    boolean isStopped();

    void stop();
}
