package com.customers.dto;

public record BirthRateByMonthResponse(
        int mes,
        int anio,
        long cantidad,
        double tasaNatalidad
) {
}
