package eu.purse.bench.model;

import java.time.LocalDate;

public record Customer(Long id, String firstName, String lastName, String email, LocalDate birthDate) {}
