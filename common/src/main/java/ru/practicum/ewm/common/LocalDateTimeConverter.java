package ru.practicum.ewm.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.ewm.common.Constant.DATE_TIME_PATTERN;

public class LocalDateTimeConverter {
        private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        public static LocalDateTime toLocalDateTime(String timestamp){
            return LocalDateTime.parse(timestamp, dateTimeFormatter);
        }
}
