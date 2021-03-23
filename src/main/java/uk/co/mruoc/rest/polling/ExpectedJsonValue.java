package uk.co.mruoc.rest.polling;

import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ExpectedJsonValue implements ExpectedValue {

    private final JsonPath jsonPath;
    private final String expectedValue;

    public ExpectedJsonValue(String jsonPath, String expectedValue) {
        this(JsonPath.compile(jsonPath), expectedValue);
    }

    @Override
    public boolean isPresent(String json) {
        String value = jsonPath.read(json);
        boolean valid = expectedValue.equals(value);
        log.info("valid {} expected {} actual {} at {}", valid, expectedValue, value, jsonPath.toString());
        return valid;
    }

}
