package com.imkp.kafkapp;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@SpringBootApplication
public class KafkApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkApplication.class, args);
    }

    @Bean
    public Consumer<String> logger() {
        return log::info;
    }

    @Bean
    public Function<String, Weather> deserialize() {
        return this::deserialize;
    }

    public Weather deserialize(String input) {
        String[] split = input.split(" ");
        Weather weather = Weather.builder()
                .dateTime(LocalDateTime.parse(split[0] + " " + split[1], DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss", Locale.ENGLISH)))
                .temperature(Double.parseDouble(split[2]))
                .humidity(Long.parseLong(split[3]))
                .windStrenght(Double.parseDouble(split[6]))
                .windBearing(Long.parseLong(split[7]))
                .solar(Double.parseDouble(split[8]))
                .uv(Double.parseDouble(split[9]))
                .pressure(Double.parseDouble(split[10]))
                .rain(Double.parseDouble(split[19]))
                .rainYear(Double.parseDouble(split[20]))
                .build();
        log.info("Deserialized: {}", weather);
        return weather;
    }

    @Bean
    public Consumer<Weather> logDeserialized() {
        return this::logDeserialized;
    }
    public void logDeserialized(Weather weather) {
        log.info("Weather: {}", weather);
    }

    @Bean
    public Function<KStream<String, Weather>, KStream<String, Aggregate>> aggregateTemperature() {
        Serde<Aggregate> aggregateSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Aggregate.class));
        return input -> input
                .map((key, value) -> new KeyValue<>(value.getDateTime().toLocalDate().toString(), value.getTemperature()))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
                .aggregate(() -> Aggregate.of("temperature"),
                        (date, newValue, aggregate) -> aggregate.update(newValue),
                        Materialized.with(Serdes.String(), aggregateSerde))
                .toStream();
    }

    @Bean
    public Function<KStream<String, Weather>, KTable<String, Aggregate>> aggregateTemperatureTable() {
        Serde<Aggregate> aggregateSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Aggregate.class));
        return input -> input
                .map((key, value) -> new KeyValue<>(value.getDateTime().toLocalDate().toString(), value.getTemperature()))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
                .aggregate(() -> Aggregate.of("temperature"),
                        (date, newValue, aggregate) -> aggregate.update(newValue),
                        Materialized.with(Serdes.String(), aggregateSerde));
    }

    @Bean
    public Function<KStream<String, Weather>, KStream<String, Aggregate>> aggregateWind() {
        Serde<Aggregate> aggregateSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Aggregate.class));
        return input -> input
                .map((key, value) -> new KeyValue<>(value.getDateTime().toLocalDate().toString(), value.getWindStrenght()))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
                .aggregate(() -> Aggregate.of("wind"),
                        (date, newValue, aggregate) -> aggregate.update(newValue),
                        Materialized.with(Serdes.String(), aggregateSerde))
                .toStream();
    }

    @Bean
    public Function<KStream<String, Weather>, KStream<String, Aggregate>> aggregatePressure() {
        Serde<Aggregate> aggregateSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Aggregate.class));
        return input -> input
                .map((key, value) -> new KeyValue<>(value.getDateTime().toLocalDate().toString(), value.getPressure()))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
                .aggregate(() -> Aggregate.of("pressure"),
                        (date, newValue, aggregate) -> aggregate.update(newValue),
                        Materialized.with(Serdes.String(), aggregateSerde))
                .toStream();
    }

    @Bean
    public Function<KStream<String, Weather>, KStream<String, Aggregate>> aggregateRainYear() {
        Serde<Aggregate> aggregateSerde = Serdes.serdeFrom(new JsonSerializer<>(), new JsonDeserializer<>(Aggregate.class));
        return input -> input
                .map((key, value) -> new KeyValue<>(value.getDateTime().toLocalDate().toString(), value.getRainYear()))
                .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
                .aggregate(() -> Aggregate.of("rainYear"),
                        (date, newValue, aggregate) -> aggregate.update(newValue),
                        Materialized.with(Serdes.String(), aggregateSerde))
                .toStream();
    }
}
