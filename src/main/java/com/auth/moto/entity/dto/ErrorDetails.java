package com.auth.moto.entity.dto;

import java.util.Date;
import lombok.Getter;

public record ErrorDetails(
  Date timestamp,
  String message,
  String details
) {

}
