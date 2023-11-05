package eu.purse.bench.discount;

import java.time.LocalDate;

public record Customer(Long id, String firstName, String lastName, String email, LocalDate birthDate) {}
