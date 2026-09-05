package com.nexo.nexomart.util;

import com.nexo.nexomart.model.OrderStatus;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * O2: PENDING -> CONFIRMED -> SHIPPED -> DELIVERED, plus CANCELLED as a side branch out
 * of the two earliest states. DELIVERED and CANCELLED are terminal. Kept as a small,
 * swappable strategy class (Section 12's Strategy pattern requirement) rather than
 * scattering if/else transition checks across the service layer.
 */
public final class OrderStatusWorkflow {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            OrderStatus.PENDING,   EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED,   EnumSet.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class)
    );

    private OrderStatusWorkflow() { }

    public static boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return ALLOWED.getOrDefault(from, Set.of()).contains(to);
    }

    /** Only a buyer can request CANCELLED (and only while PENDING/CONFIRMED); every
     *  other forward transition (CONFIRMED/SHIPPED/DELIVERED) is a seller/admin action. */
    public static boolean requiresBuyer(OrderStatus to) {
        return to == OrderStatus.CANCELLED;
    }
}
