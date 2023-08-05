package club.emperorws.orm.result;

/**
 * 默认的ORM结果上下文存储器
 *
 * @author: EmperorWS
 * @date: 2023/4/27 15:39
 * @description: DefaultResultContext: 默认的ORM结果上下文存储器
 */
public class DefaultResultContext<T> implements ResultContext<T> {

    private T resultObject;
    private int resultCount;
    private boolean stopped;

    public DefaultResultContext() {
        resultObject = null;
        resultCount = 0;
        stopped = false;
    }

    @Override
    public T getResultObject() {
        return resultObject;
    }

    @Override
    public int getResultCount() {
        return resultCount;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    public void nextResultObject(T resultObject) {
        resultCount++;
        this.resultObject = resultObject;
    }

    @Override
    public void stop() {
        this.stopped = true;
    }
}
