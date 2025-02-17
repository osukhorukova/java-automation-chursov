import olenzing.CargoDimension;
import olenzing.Delivery;
import olenzing.ServiceWorkload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Validate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeliveryTest {
    @Test
    @Tag("Positive")
    @DisplayName("Minimum delivery price")
    void minimumDeliveryPriceTest() {
        Delivery delivery = new Delivery(1, CargoDimension.SMALL, false, ServiceWorkload.NORMAL);
        assertEquals(400, delivery.calculateDeliveryCost());
    }

    @Test
    @Tag("Positive")
    @DisplayName("Maximum delivery price")
    void maximumDeliveryPriceTest() {
        Delivery delivery = new Delivery(30, CargoDimension.LARGE, true, ServiceWorkload.VERY_HIGH);
        assertEquals(1120, delivery.calculateDeliveryCost());
    }

    /*
    static Stream<Integer> provideDistance(){
        return Stream.of(2,3,9,10,11,29);
    }

    @ParameterizedTest
    @MethodSource("provideDistance")
    @Tag("Positive")
    @DisplayName("Distance boundary Values")
    void distanceBoundaryValuesTest(int distance) {
        List<Integer> expectedList = Arrays.asList()
        Delivery delivery = new Delivery(distance, CargoDimension.LARGE, true, ServiceWorkload.VERY_HIGH);
        assertEquals(1120, delivery.calculateDeliveryCost());
    }
     */

    @Test
    @Tag("Positive")
    @DisplayName("The cargo is fragile and the distance is less than 30 km")
    void fragileAndShortDistancePriceTest() {
        Delivery delivery = new Delivery(30, CargoDimension.SMALL, true, ServiceWorkload.INCREASED);
        assertEquals(720, delivery.calculateDeliveryCost());
    }

    @Test
    @Tag("Negative")
    @DisplayName("The cargo is fragile and the distance is more than 30 km")
    void fragileAndLongDistancePriceTest() {
        Delivery delivery = new Delivery(31, CargoDimension.LARGE, true, ServiceWorkload.NORMAL);
        Throwable exception = assertThrows(
                UnsupportedOperationException.class,
                delivery::calculateDeliveryCost
        );
        assertEquals("Fragile cargo cannot be delivered for the distance more than 30", exception.getMessage());
    }

    @Test
    @Tag("Negative")
    @DisplayName("Negative delivery distance")
    void testNegativeDistanceOrderCost() {
        Delivery delivery = new Delivery(-1, CargoDimension.SMALL, false, ServiceWorkload.NORMAL);
        Throwable exception = assertThrows(
                IllegalArgumentException.class,
                delivery::calculateDeliveryCost
        );
        assertEquals("destinationDistance should be a positive number!", exception.getMessage());
    }
}
