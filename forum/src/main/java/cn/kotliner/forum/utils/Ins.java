package cn.kotliner.forum.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Ins {

    public static <T> ArrayList<T> list(Creator<ArrayList<T>> creator) {
        ArrayList<T> list = new ArrayList<>();
        creator.onCreate(list);
        return list;
    }

    public static LinkedHashMap<String, Object> dict(Creator<LinkedHashMap<String, Object>> creator) {
        LinkedHashMap<String, Object> dict = new LinkedHashMap<>();
        creator.onCreate(dict);
        return dict;
    }

    public static LinkedHashMap<String, String> strDict(Creator<LinkedHashMap<String, String>> creator) {
        LinkedHashMap<String, String> dict = new LinkedHashMap<>();
        creator.onCreate(dict);
        return dict;
    }

    public static <T> T of(Class<T> type, Creator<T> creator) {
        try {
            T ins = type.newInstance();
            creator.onCreate(ins);
            return ins;
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T apply(T object, Creator<T> creator) {
        creator.onCreate(object);
        return object;
    }

    public interface Creator<T> {
        void onCreate(T ins);
    }

    public static <T> Predicate<T> distinctBy(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static <T> Predicate<T> ifThenDistinctBy(Predicate<T> predicate, Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> predicate.test(t) && seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
