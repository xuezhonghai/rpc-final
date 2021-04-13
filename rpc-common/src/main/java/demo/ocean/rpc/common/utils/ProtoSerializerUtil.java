package demo.ocean.rpc.common.utils;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtoSerializerUtil {

    /**
     * 序列化对象信息缓存
     */
    private final static Map<Class<?>, Schema<?>> CLASS_SCHEMA_MAP = new ConcurrentHashMap<>();

    /**
     * 负责实例化对象， 支持缓存
     */
    private final static Objenesis OBJENESIS = new ObjenesisStd(true);

    /**
     * 序列化对象接口
     * @param t
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T t) {
        Class<T> cls = (Class<T>) t.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getClassSchema(cls);
            //序列化
            return ProtobufIOUtil.toByteArray(t, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化对象接口
     * @param bytes
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] bytes, Class<T> cls) {
        try {
            Schema<T> schema = getClassSchema(cls);
            T message = OBJENESIS.newInstance(cls);
            //反序列化
            ProtobufIOUtil.mergeFrom(bytes, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 获取序列化对象的信息
     * @param cls
     * @param <T>
     * @return
     */
    private static <T> Schema<T> getClassSchema(Class<T> cls) {
        Schema<T> classSchema = null;
        if (CLASS_SCHEMA_MAP.containsKey(cls)) {
            classSchema = (Schema<T>) CLASS_SCHEMA_MAP.get(cls);
        } else {
            classSchema = RuntimeSchema.getSchema(cls);
            if (classSchema != null) {
                CLASS_SCHEMA_MAP.put(cls, classSchema);
            }
        }
        return classSchema;
    }

    @Data
    @AllArgsConstructor
    static class User {

        private Long id;

        private String name;

        private Integer age;
    }


    public static void main(String[] args) {

        User user = new User(1L, "张三", 15);

        byte[] bytes = ProtoSerializerUtil.serialize(user);

        System.out.println(bytes.length);


        User deserialize = ProtoSerializerUtil.deserialize(bytes, user.getClass());

        System.out.println(deserialize);


    }




}
