package ru.practicum.ewm.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrintLogs {
    private final ObjectMapper objectMapper;

    public void printObject(Object dto, String message) {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
            log.info("{}: {}", message, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getLocalizedMessage());
        }
    }

    public void printUrl(HttpServletRequest httpServletRequest){
        log.info("{}: {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
    }
}
