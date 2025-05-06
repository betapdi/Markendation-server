package com.markendation.server.classes;

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
    String fileName;
    String modelType = "image";
}
