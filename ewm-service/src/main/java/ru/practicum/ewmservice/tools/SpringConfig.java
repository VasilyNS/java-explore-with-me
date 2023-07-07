package ru.practicum.ewmservice.tools;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Необходимо прописать в конфигурационном классе через @ComponentScan
 * где искать бины из другого модуля !!!
 * По ТЗ клиент статистики должен быть в другом модуле
 */
@Configuration
@ComponentScan(basePackages = "ru.practicum.statclient")
public class SpringConfig {
}
