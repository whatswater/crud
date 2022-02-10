package com.whatswater.curd.project.common;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.Future;
import io.vertx.core.impl.future.FailedFuture;
import io.vertx.ext.sql.assist.SqlAssist;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CrudUtils {
    public static <T> T readValue(String content, TypeReference<T> type) {
        try {
            return ObjectMapperHolder.mapper.readValue(content, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readValue(String content, Class<T> type) {
        try {
            return ObjectMapperHolder.mapper.readValue(content, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object obj) {
        try {
            return ObjectMapperHolder.mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static LocalDateTime parseSqlDateTimeFormat(String time) {
        if (time.length() == 16) {
            return LocalDateTime.parse(time.replaceAll("T", " "), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        return LocalDateTime.parse(time.replaceAll("T", " "), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static LocalDate parseDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String formatDate(LocalDate localDate) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate);
    }

    public static String formatDateTime(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
    }

    private CrudUtils() {

    }

    private static final List<?> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<>());


    public static <T> List<T> emptyList() {
        return (List<T>) EMPTY_LIST;
    }

    public static boolean notZero(Long value) {
        return value != null && value != 0L;
    }

    public static boolean gtZero(Integer value) {
        return value != null && value > 0;
    }
    public static boolean gtZero(int value) {
        return value > 0;
    }

    public static boolean gtZero(Long value) {
        return value != null && value > 0L;
    }
    public static boolean gtZero(long value) {
        return value > 0L;
    }

    private static Pattern pattern = Pattern.compile("([A-Z])");
    public static String humpToUnderline(String str) {
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            String target = matcher.group();
            str = str.replaceAll(target, "_"+target.toLowerCase());
        }
        return str;
    }


    public static <T> Future<T> failedFuture(ErrorCodeEnum errorCodeEnum) {
        return new FailedFuture<>(errorCodeEnum.toException());
    }

    public static final String PASSWORD_SALT = "qzkj-flow";
    // 暂时简单加salt，sha256算法
    public static String hashPassword(String originPassword) {
        return DigestUtils.sha256Hex(PASSWORD_SALT + originPassword);
    }

    private static char[] RANDOM_SPACE = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm' };
    private static int RANDOM_SPACE_LENGTH = RANDOM_SPACE.length;
    public static String randomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomValue = ThreadLocalRandom.current().nextInt(0, RANDOM_SPACE_LENGTH);
            stringBuilder.append(RANDOM_SPACE[randomValue]);
        }
        return stringBuilder.toString();
    }

    public static SqlAssist andIn(String columnName, List<? extends Object> valueList) {
        SqlAssist sqlAssist = new SqlAssist();
        return andIn(sqlAssist, columnName, valueList);
    }

    public static SqlAssist andIn(SqlAssist sqlAssist,  String columnName, List<? extends Object> valueList) {
        Object[] value = new Object[valueList.size()];
        StringBuilder in = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            in.append("?,");
            value[i] = valueList.get(i);
        }
        in.replace(in.length() - 1, in.length(), StrUtil.EMPTY);
        sqlAssist.customCondition("and " + columnName + " in (" + in + ")", value);
        return sqlAssist;
    }

    public static <T1, T2> Future<Tuple2<T1, T2>> successFuture(T1 t1, T2 t2) {
        return Future.succeededFuture(Tuple2.of(t1, t2));
    }

    public interface SameFutureBuilder<T> {
        Future<T> buildTask(List<T> resultList);
    }

    public static <T> Future<List<T>> serialTask(List<SameFutureBuilder<T>> sameFutureBuilderList) {
        Future<List<T>> future = Future.succeededFuture(new ArrayList<>(sameFutureBuilderList.size()));
        for (SameFutureBuilder<T> sameFutureBuilder : sameFutureBuilderList) {
            future = future.compose(resultList -> sameFutureBuilder.buildTask(resultList).map(result -> {
                    resultList.add(result);
                    return resultList;
                })
            );
        }
        return future;
    }

    public interface TaskBuilder<T> {
        Future<T> buildTask(Tuple tuple);
    }

    public static <T1, T2> Future<Tuple2<T1, T2>> serialTask(TaskBuilder<T1> builder1, TaskBuilder<T2> builder2) {
        Future<Tuple2<T1, T2>> future = Future.succeededFuture(new Tuple2<>());
        return future.compose(tuple1 -> builder1.buildTask(tuple1).map(result -> {
            tuple1._1 = result;
            return tuple1;
        })).compose(tuple2 -> builder2.buildTask(tuple2).map(result -> {
            tuple2._2 = result;
            return tuple2;
        }));
    }

    public static <T1, T2, T3> Future<Tuple3<T1, T2, T3>> serialTask(TaskBuilder<T1> builder1, TaskBuilder<T2> builder2,  TaskBuilder<T3> builder3) {
        Future<Tuple3<T1, T2, T3>> future = Future.succeededFuture(new Tuple3<>());
        return future.compose(tuple1 -> builder1.buildTask(tuple1).map(result -> {
            tuple1._1 = result;
            return tuple1;
        })).compose(tuple2 -> builder2.buildTask(tuple2).map(result -> {
            tuple2._2 = result;
            return tuple2;
        })).compose(tuple3 -> builder3.buildTask(tuple3).map(result -> {
            tuple3._3 = result;
            return tuple3;
        }));
    }

    public static <T1, T2, T3, T4> Future<Tuple4<T1, T2, T3, T4>> serialTask(TaskBuilder<T1> builder1, TaskBuilder<T2> builder2, TaskBuilder<T3> builder3, TaskBuilder<T4> builder4) {
        Future<Tuple4<T1, T2, T3, T4>> future = Future.succeededFuture(new Tuple4<>());
        return future.compose(tuple1 -> builder1.buildTask(tuple1).map(result -> {
            tuple1._1 = result;
            return tuple1;
        })).compose(tuple2 -> builder2.buildTask(tuple2).map(result -> {
            tuple2._2 = result;
            return tuple2;
        })).compose(tuple3 -> builder3.buildTask(tuple3).map(result -> {
            tuple3._3 = result;
            return tuple3;
        })).compose(tuple4 ->  builder4.buildTask(tuple4).map(result -> {
            tuple4._4 = result;
            return tuple4;
        }));
    }

    public interface Tuple {
        int size();
    }

    public static class Tuple2<T1, T2> implements Tuple {
        public T1 _1;
        public T2 _2;

        public Tuple2() {

        }

        public T1 first() {
            return _1;
        }

        public T2 second() {
            return _2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
            return Objects.equals(_1, tuple2._1) && Objects.equals(_2, tuple2._2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_1, _2);
        }

        @Override
        public int size() {
            return 2;
        }

        public static <T1, T2> Tuple2<T1, T2> of(T1 t1, T2 t2) {
            Tuple2<T1, T2> tuple2 = new Tuple2<>();
            tuple2._1 = t1;
            tuple2._2 = t2;
            return tuple2;
        }
    }

    public static class Tuple3<T1, T2, T3> implements Tuple {
        public T1 _1;
        public T2 _2;
        public T3 _3;

        public Tuple3() {

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;
            return Objects.equals(_1, tuple3._1) && Objects.equals(_2, tuple3._2) && Objects.equals(_3, tuple3._3);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_1, _2, _3);
        }

        @Override
        public int size() {
            return 3;
        }

        public static <T1, T2, T3> Tuple3<T1, T2, T3> of(Tuple2<T1, T2> t2, T3 t3) {
            return of(t2._1, t2._2, t3);
        }

        public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
            Tuple3<T1, T2, T3> tuple3 = new Tuple3<>();
            tuple3._1 = t1;
            tuple3._2 = t2;
            tuple3._3 = t3;
            return tuple3;
        }
    }

    public static class Tuple4<T1, T2, T3, T4> implements Tuple {
        public T1 _1;
        public T2 _2;
        public T3 _3;
        public T4 _4;

        public Tuple4() {

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;
            return Objects.equals(_1, tuple4._1)
                && Objects.equals(_2, tuple4._2)
                && Objects.equals(_3, tuple4._3)
                && Objects.equals(_4, tuple4._4);
        }

        @Override
        public int hashCode() {
            return Objects.hash(_1, _2, _3, _4);
        }

        @Override
        public int size() {
            return 4;
        }
    }
}
