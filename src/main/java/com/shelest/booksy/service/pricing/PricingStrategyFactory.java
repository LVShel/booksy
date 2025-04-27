package com.shelest.booksy.service.pricing;

import com.shelest.booksy.domain.model.enumeration.BookType;

public class PricingStrategyFactory {
    public static PricingStrategy getStrategy(BookType type) {
        return switch (type) {
            case NEW_RELEASE -> new NewReleasePricingStrategy();
            case REGULAR -> new RegularPricingStrategy();
            case OLD_EDITION -> new OldEditionPricingStrategy();
        };
    }
}
