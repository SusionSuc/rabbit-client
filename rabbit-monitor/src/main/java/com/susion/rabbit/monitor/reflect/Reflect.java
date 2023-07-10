package com.susion.rabbit.monitor.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * wangpengcheng.wpc create at 2023/7/4
 */

public class Reflect {

    /**
     * 封装Class.forName(name)
     * <p/>
     * 可以这样调用: <code>on(Class.forName(name))</code>
     *
     * @param name
     *            完整类名
     * @return 工具类自身
     * @throws ReflectException
     *             反射时发生的异常
     * @see #on(Class)
     */
    public static Reflect on(String name) throws ReflectException {
        return on(forName(name));
    }

    /**
     * 封装Class.forName(name)
     * <p/>
     * 可以这样调用: <code>on(Xxx.class)</code>
     *
     * @param clazz
     *            类
     * @return 工具类自身
     * @throws ReflectException
     *             反射时发生的异常
     * @see #on(Class)
     */
    public static Reflect on(Class<?> clazz) {
        return new Reflect(clazz);
    }

    /**
     * 包装起一个对象
     * <p/>
     * 当你需要访问实例的字段和方法时可以使用此方法 {@link Object}
     *
     * @param object
     *            需要被包装的对象
     * @return 工具类自身
     */
    public static Reflect on(Object object) {
        return new Reflect(object);
    }

    /**
     * 使受访问权限限制的对象转为不受限制。 一般情况下， 一个类的私有字段和方法是无法获取和调用的， 原因在于调用前Java会检查是否具有可访问权限，
     * 当调用此方法后， 访问权限检查机制将被关闭。
     *
     * @param accessible
     *            受访问限制的对象
     * @return 不受访问限制的对象
     */
    public static <T extends AccessibleObject> T accessible(T accessible) {
        if (accessible == null) {
            return null;
        }

        if (accessible instanceof Member) {
            Member member = (Member) accessible;
            if (Modifier.isPublic(member.getModifiers())
                    && Modifier.isPublic(member.getDeclaringClass().getModifiers())) {
                return accessible;
            }
        }

        // 默认为false,即反射时检查访问权限，
        // 设为true时不检查访问权限,可以访问private字段和方法
        if (!accessible.isAccessible()) {
            accessible.setAccessible(true);
        }

        return accessible;
    }
    // 成员
    /**
     * 被包装的对象
     */
    private final Object object;

    /**
     * 反射的是一个Class还是一个Object实例?
     */
    private final boolean isClass;

    // 构造器
    private Reflect(Class<?> type) {
        this.object = type;
        this.isClass = true;
    }

    private Reflect(Object object) {
        this.object = object;
        this.isClass = false;
    }

    // 洗尽铅华的卓越API :)
    /**
     * 得到当前包装的对象
     */
    @SuppressWarnings("unchecked")
    public <T> T get() {
        // 泛型的好处瞬间就体现出来了
        return (T) object;
    }

    /**
     * 修改一个字段的值
     * <p/>
     * 等价于 {@link java.lang.reflect.Field#set(Object, Object)}. 如果包装的对象是一个
     * {@link Class}, 那么修改的将是一个静态字段， 如果包装的对象是一个{@link Object}, 那么修改的就是一个实例字段。
     *
     * @param name
     *            字段名
     * @param value
     *            字段的值
     * @return 完事后的工具类
     * @throws ReflectException
     */
    public Reflect set(String name, Object value) throws ReflectException {
        try {
            Field field = field0(name);
            field.set(object, unwrap(value));
            return this;
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 得到字段对值
     *
     * @param name
     *            字段名
     * @return The field value
     * @throws ReflectException
     * @see # field(String)
     */
    public <T> T get(String name, Class<?>... classOf) throws ReflectException {
        return field(name, classOf).<T> get();
    }

    /**
     * 取得字段
     *
     * @param name
     *            字段名
     * @return 字段
     * @throws ReflectException
     */
    public Reflect field(String name, Class<?>... classOf) throws ReflectException {
        try {
            Field field = field0(name, classOf);
            return on(field.get(object));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    public Field exactField(String name, Class<?>... classOf) throws ReflectException {
        return field0(name, classOf);
    }

    private Field field0(String name, Class<?>... classOf) throws ReflectException {
        Class<?> type = type();
        // 尝试作为公有字段处理
        if (classOf != null && classOf.length > 0) {
            do {
                try {
                    Field[] fields = type.getDeclaredFields();
                    for (Field field : fields) {
                        Class<?> fieldClass = field.getType();
                        if (fieldClass != null && fieldClass.getName().equals(classOf[0].getName())
                                && field.getName().equals(name)) {
                            return accessible(field);
                        }
                    }
                } catch (Exception ignore) {
                }
                type = type.getSuperclass();
            } while (type != null);
            throw new ReflectException("NoSuchFieldException");
        }
        try {
            return type.getField(name);
        } catch (NoSuchFieldException e) {
            // 尝试以私有方式处理
            do {
                try {
                    return accessible(type.getDeclaredField(name));
                } catch (NoSuchFieldException ignore) {
                }

                type = type.getSuperclass();
            } while (type != null);
            throw new ReflectException(e);
        }
    }

    /**
     * 将一个对象的所有对象映射到一个Map中,key为字段名。
     *
     * @return 包含所有字段的map
     */
    public Map<String, Reflect> fields() {
        Map<String, Reflect> result = new LinkedHashMap<String, Reflect>();
        Class<?> type = type();
        do {
            for (Field field : type.getDeclaredFields()) {
                if (!isClass ^ Modifier.isStatic(field.getModifiers())) {
                    String name = field.getName();
                    if (!result.containsKey(name))
                        result.put(name, field(name));
                }
            }
            type = type.getSuperclass();
        } while (type != null);
        return result;
    }

    /**
     * 给定方法名称，调用无参方法
     * <p/>
     * 等价于 <code>call(name, new Object[0])</code>
     *
     * @param name
     *            方法名
     * @return 工具类自身
     * @throws ReflectException
     * @see # call(String, Object...)
     */
    public Reflect call(String name) throws ReflectException {
        return call(name, null, new Object[0]);
    }

    /**
     * 给定方法名和参数，调用一个方法。
     * <p/>
     * 封装自 {@link java.lang.reflect.Method#invoke(Object, Object...)}, 可以接受基本类型
     *
     * @param name
     *            方法名
     * @param args
     *            方法参数
     * @return 工具类自身
     * @throws ReflectException
     */
    public Reflect call(String name, Class<?>[]types, Object... args) throws ReflectException {
        // 尝试调用方法
        try {
            Method method = exactMethod(name, types, args);
            return on(method, object, args);
        } catch (NoSuchMethodException e) {
            // 如果没有符合参数的方法，
            // 则匹配一个与方法名最接近的方法。
            try {
                Method method = similarMethod(name, types);
                return on(method, object, args);
            } catch (NoSuchMethodException e1) {
                throw new ReflectException(e1);
            }
        }
    }

    /**
     * 根据方法名和方法参数得到该方法。
     */
    public Method exactMethod(String name, Class<?>[] types, Object... args) throws NoSuchMethodException {
        Class<?> type = type();
        // 先尝试直接调用
        try {
            return type.getMethod(name, types);
        } catch (NoSuchMethodException e) {
            // 也许这是一个私有方法
            do {
                try {
                    return type.getDeclaredMethod(name, types);
                } catch (NoSuchMethodException ignore) {
                }
                type = type.getSuperclass();
            } while (type != null);

            type = type();
            Method[] methods = type.getMethods();
            for (Method method : methods) {
                if (name.equals(method.getName())) {
                    Class<?>[] paramTypes = method.getParameterTypes();
                    if (paramTypes != null && paramTypes.length == types.length) {
                        return method;
                    }
                }
            }

            throw new NoSuchMethodException();
        }
    }

    /**
     * 给定方法名和参数，匹配一个最接近的方法
     */
    private Method similarMethod(String name, Class<?>[] types) throws NoSuchMethodException {
        Class<?> type = type();
        // 对于公有方法:
        for (Method method : type.getMethods()) {
            if (isSimilarSignature(method, name, types)) {
                return method;
            }
        }
        do {
            // 对于私有方法：
            for (Method method : type.getDeclaredMethods()) {
                if (isSimilarSignature(method, name, types)) {
                    return method;
                }
            }

            type = type.getSuperclass();
        } while (type != null);
        throw new NoSuchMethodException("No similar method " + name + " with params " + Arrays.toString(types)
                + " could be found on type " + type() + ".");
    }

    /**
     * 再次确认方法签名与实际是否匹配， 将基本类型转换成对应的对象类型， 如int转换成Int
     */
    private boolean isSimilarSignature(Method possiblyMatchingMethod, String desiredMethodName,
                                       Class<?>[] desiredParamTypes) {
        return possiblyMatchingMethod.getName().equals(desiredMethodName)
                && match(possiblyMatchingMethod.getParameterTypes(), desiredParamTypes);
    }

    /**
     * 调用一个无参构造器
     * <p/>
     * 等价于 <code>create(new Object[0])</code>
     *
     * @return 工具类自身
     * @throws ReflectException
     * @see #create(Object...)
     */
    public Reflect create() throws ReflectException {
        return create(new Object[0]);
    }

    /**
     * 调用一个有参构造器
     *
     * @param args
     *            构造器参数
     * @return 工具类自身
     * @throws ReflectException
     */
    public Reflect create(Object... args) throws ReflectException {
        Class<?>[] types = types(args);
        try {
            Constructor<?> constructor = type().getDeclaredConstructor(types);
            return on(constructor, args);
        } catch (NoSuchMethodException e) {
            // 这种情况下，构造器往往是私有的，多用于工厂方法，刻意的隐藏了构造器。
            // private阻止不了反射的脚步:)
            for (Constructor<?> constructor : type().getDeclaredConstructors()) {
                if (match(constructor.getParameterTypes(), types)) {
                    return on(constructor, args);
                }
            }
            throw new ReflectException(e);
        }
    }

    /**
     * 为包装的对象创建一个代理。
     *
     * @param proxyType
     *            代理类型
     * @return 包装对象的代理者。
     */
    @SuppressWarnings("unchecked")
    public <P> P as(Class<P> proxyType) {
        final boolean isMap = (object instanceof Map);
        final InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                try {
                    return on(object).call(name, types(args), args).get();
                } catch (ReflectException e) {
                    if (isMap) {
                        Map<String, Object> map = (Map<String, Object>) object;
                        int length = (args == null ? 0 : args.length);
                        if (length == 0 && name.startsWith("get")) {
                            return map.get(property(name.substring(3)));
                        } else if (length == 0 && name.startsWith("is")) {
                            return map.get(property(name.substring(2)));
                        } else if (length == 1 && name.startsWith("set")) {
                            map.put(property(name.substring(3)), args[0]);
                            return null;
                        }
                    }

                    throw e;
                }
            }
        };

        return (P) Proxy.newProxyInstance(proxyType.getClassLoader(), new Class[] { proxyType }, handler);
    }

    private static String property(String string) {
        int length = string.length();

        if (length == 0) {
            return "";
        } else if (length == 1) {
            return string.toLowerCase();
        } else {
            return string.substring(0, 1).toLowerCase() + string.substring(1);
        }
    }

    // 对象API
    private boolean match(Class<?>[] declaredTypes, Class<?>[] actualTypes) {
        if (declaredTypes.length == actualTypes.length) {
            for (int i = 0; i < actualTypes.length; i++) {
                if (actualTypes[i] == NULL.class)
                    continue;
                if (wrapper(declaredTypes[i]).isAssignableFrom(wrapper(actualTypes[i])))
                    continue;
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return object.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Reflect) {
            return object.equals(((Reflect) obj).get());
        }
        return false;
    }

    @Override
    public String toString() {
        return object.toString();
    }

    // 内部工具方法
    private static Reflect on(Constructor<?> constructor, Object... args) throws ReflectException {
        try {
            return on(accessible(constructor).newInstance(args));
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    private static Reflect on(Method method, Object object, Object... args) throws ReflectException {
        try {
            accessible(method);
            if (method.getReturnType() == void.class) {
                method.invoke(object, args);
                return on(object);
            } else {
                return on(method.invoke(object, args));
            }
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 内部类，使一个对象脱离包装
     */
    private static Object unwrap(Object object) {
        if (object instanceof Reflect) {
            return ((Reflect) object).get();
        }
        return object;
    }

    /**
     * 内部类， 给定一系列参数，返回它们的类型
     *
     * @see Object#getClass()
     */
    private static Class<?>[] types(Object... values) {
        if (values == null) {
            return new Class[0];
        }
        Class<?>[] result = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            result[i] = value == null ? NULL.class : value.getClass();
        }
        return result;
    }

    /**
     * 加载一个类
     *
     * @see Class#forName(String)
     */
    private static Class<?> forName(String name) throws ReflectException {
        try {
            return Class.forName(name);
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }

    /**
     * 获取包装的对象的类型
     *
     * @see Object#getClass()
     */
    public Class<?> type() {
        if (isClass) {
            return (Class<?>) object;
        } else {
            return object.getClass();
        }
    }

    /**
     * 得到包装的对象的类型， 如果是基本类型,像int,float,boolean这种, 那么将被转换成相应的对象类型。
     */
    public static Class<?> wrapper(Class<?> type) {
        if (type == null) {
            return null;
        } else if (type.isPrimitive()) {
            if (boolean.class == type) {
                return Boolean.class;
            } else if (int.class == type) {
                return Integer.class;
            } else if (long.class == type) {
                return Long.class;
            } else if (short.class == type) {
                return Short.class;
            } else if (byte.class == type) {
                return Byte.class;
            } else if (double.class == type) {
                return Double.class;
            } else if (float.class == type) {
                return Float.class;
            } else if (char.class == type) {
                return Character.class;
            } else if (void.class == type) {
                return Void.class;
            }
        }

        return type;
    }

    /**
     * 定义了一个null类型
     */
    public static class NULL {
    }
}

