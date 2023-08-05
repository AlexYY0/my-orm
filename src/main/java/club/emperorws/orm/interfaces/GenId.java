package club.emperorws.orm.interfaces;

/**
 * 通用ID生成策略接口
 *
 * @author EmperorWS
 * @date 2022.10.18 16:44
 **/
public interface GenId<T> {

    T genId();

    public static class NULL implements GenId {

        public NULL() {
        }

        @Override
        public Object genId() {
            return new UnsupportedOperationException();
        }
    }
}
