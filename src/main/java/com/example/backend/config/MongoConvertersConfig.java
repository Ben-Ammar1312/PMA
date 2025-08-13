package com.example.backend.config;

import com.example.backend.model.Measurement;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.List;

@Configuration
public class MongoConvertersConfig {

    @Bean
    MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
                new StringToMeasurement(),
                new NumberToMeasurement(),
                new DocumentToMeasurement() // in case some docs are already objects
        ));
    }

    @ReadingConverter
    static class StringToMeasurement implements Converter<String, Measurement> {
        @Override public Measurement convert(String source) {
            if (source == null) return null;
            String s = source.trim();
            if (s.isEmpty() || s.equals("-")) return null;
            String[] parts = s.split("\\s+", 2); // "5", "5 kg"
            Measurement m = new Measurement();
            m.setValue(parts[0]);
            if (parts.length > 1) m.setUnit(parts[1]);
            return m;
        }
    }

    @ReadingConverter
    static class NumberToMeasurement implements Converter<Number, Measurement> {
        @Override public Measurement convert(Number n) {
            Measurement m = new Measurement();
            m.setValue(n.toString());
            return m;
        }
    }

    @ReadingConverter
    static class DocumentToMeasurement implements Converter<Document, Measurement> {
        @Override public Measurement convert(Document d) {
            if (d == null) return null;
            Measurement m = new Measurement();
            Object v = d.get("value");
            if (v != null) m.setValue(v.toString());
            m.setUnit(d.getString("unit"));
            return m;
        }
    }
}