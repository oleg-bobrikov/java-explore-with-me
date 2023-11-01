package ru.practicum.ewm.util;


import org.springframework.data.domain.Sort;

public class PageRequestHelper extends org.springframework.data.domain.PageRequest {
    public PageRequestHelper(int from, int size) {
        super(from / size, size, Sort.unsorted());
    }

    public static PageRequestHelper of(int from, int size) {
        return new PageRequestHelper(from / size, size);
    }

}
