package com.markendation.server.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ImageModelEvent {
    String correlationId;
    String eventType = "image";
    String imageUrl;
}
