package club.emperorws.orm.result;

import club.emperorws.orm.reflection.factory.ObjectFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认的Mapper结果处理器
 *
 * @author: EmperorWS
 * @date: 2023/4/27 17:05
 * @description: DefaultResultHandler: 默认的Mapper结果处理器
 */
public class DefaultResultHandler implements ResultHandler<Object> {

    private final List<Object> list;

    public DefaultResultHandler() {
        list = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public DefaultResultHandler(ObjectFactory objectFactory) {
        list = objectFactory.create(List.class);
    }

    @Override
    public void handleResult(ResultContext<?> context) {
        list.add(context.getResultObject());
    }

    public List<Object> getResultList() {
        return list;
    }
}
