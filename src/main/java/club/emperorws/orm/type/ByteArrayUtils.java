package club.emperorws.orm.type;

/**
 * Byte[]与byte[]互转的工具类
 *
 * @author: EmperorWS
 * @date: 2023/4/23 15:06
 * @description: ByteArrayUtils: Byte[]与byte[]互转的工具类
 */
class ByteArrayUtils {

    private ByteArrayUtils() {
        // 避免初始化创建使用工具类
    }

    static byte[] convertToPrimitiveArray(Byte[] objects) {
        final byte[] bytes = new byte[objects.length];
        for (int i = 0; i < objects.length; i++) {
            bytes[i] = objects[i];
        }
        return bytes;
    }

    static Byte[] convertToObjectArray(byte[] bytes) {
        final Byte[] objects = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            objects[i] = bytes[i];
        }
        return objects;
    }
}
