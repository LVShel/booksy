package com.shelest.booksy.service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {

    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null) return false;

        String trimmed = isbn.replaceAll("[^0-9X]", "");
        return trimmed.length() >= 10;
        // Later full ISBN validation could be added here, See: https://en.wikipedia.org/wiki/International_Standard_Book_Number#Check_digits
    }
}