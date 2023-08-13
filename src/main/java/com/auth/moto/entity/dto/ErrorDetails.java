package com.auth.moto.entity.dto;

import java.util.Date;

public record ErrorDetails(
    Date timestamp,
    String message,
    String details
) {

}
