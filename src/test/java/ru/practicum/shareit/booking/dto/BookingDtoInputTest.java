package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoInputTest {

    @Autowired
    private JacksonTester<BookingDtoInput> json;

    private final LocalDateTime testTime = LocalDateTime.of(2022, 2, 2, 0, 0,0);

    @Test
    void testTimeToJson() throws IOException {
        BookingDtoInput booking = new BookingDtoInput();
        booking.setStart(testTime);
        JsonContent<BookingDtoInput> result = this.json.write(booking);

        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-02-02T00:00:00");
    }

    @Test
    void testJsonToTime() throws IOException {
        String jsonContent = "{\"start\": \"2022-02-02T00:00:00\"}";
        BookingDtoInput result = this.json.parse(jsonContent).getObject();

        assertThat(result.getStart()).isEqualTo(testTime);
    }

}