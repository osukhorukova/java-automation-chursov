import olenzing.CargoDimension;
import olenzing.Delivery;
import olenzing.ServiceWorkload;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
        double actualPrice = delivery.calculateDeliveryCost();
        assertEquals(1120, actualPrice);
    }

    @ParameterizedTest
    @Tag("Positive")
    @DisplayName("Distance boundary values")
    @CsvSource({
            "2,SMALL,false,NORMAL,400.0",
            "9,LARGE,true,HIGH,840.0",
            "10,SMALL,false,NORMAL,400.0",
            "11,LARGE,true,HIGH,980.0",
            "29,SMALL,true,VERY_HIGH,960.0",
            "30,LARGE,false,INCREASED,480.0",
    })
    void distanceBoundaryValuesTest(int distance, CargoDimension cargoDimension, boolean isFragile, ServiceWorkload serviceWorkload, double expectedPrice) {
        Delivery delivery = new Delivery(distance, cargoDimension, isFragile, serviceWorkload);
        double actualPrice = delivery.calculateDeliveryCost();
        assertEquals(expectedPrice, actualPrice);
    }

    @Test
    @Tag("Positive")
    @DisplayName("The cargo is fragile and the distance is 30 km")
    void fragileAndShortDistancePriceTest() {
        Delivery delivery = new Delivery(30, CargoDimension.SMALL, true, ServiceWorkload.INCREASED);
        double actualPrice = delivery.calculateDeliveryCost();
        assertEquals(720, actualPrice);
    }

    @Test
    @Tag("Negative")
    @DisplayName("Workload cannot be null")
    void nullPointerExceptionTest() {
        Delivery delivery = new Delivery(1, CargoDimension.SMALL, false, null);
        Throwable exception = assertThrows(
                NullPointerException.class,
                delivery::calculateDeliveryCost
        );
        assertEquals("Cannot invoke \"olenzing.ServiceWorkload.getDeliveryRate()\" because \"this.deliveryServiceWorkload\" is null", exception.getMessage());
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
