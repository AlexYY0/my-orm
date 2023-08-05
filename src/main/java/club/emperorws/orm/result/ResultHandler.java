package club.emperorws.orm.result;

/**
 * 对获得的对象结果做进一步的处理（ResultSetHandler-->ResultHandler）
 *
 * @author: EmperorWS
 * @date: 2023/4/27 15:37
 * @description: ResultHandler: ORM结果处理器
 */
public interface ResultHandler<T> {

    void handleResult(ResultContext<? extends T> resultContext);

}
