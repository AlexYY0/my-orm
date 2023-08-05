package club.emperorws.orm.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 参数名称工具
 *
 * @author: EmperorWS
 * @date: 2023/4/27 16:16
 * @description: ParamNameUtil: 参数名称工具
 */
public class ParamNameUtil {

    private ParamNameUtil() {
        super();
    }

    public static List<String> getParamNames(Method method) {
        return getParameterNames(method);
    }

    public static List<String> getParamNames(Constructor<?> constructor) {
        return getParameterNames(constructor);
    }

    private static List<String> getParameterNames(Executable executable) {
        return Arrays.stream(executable.getParameters()).map(Parameter::getName).collect(Collectors.toList());
    }
}
