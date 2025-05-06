package com.markendation.server.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TextModelEvent {
    String correlationId;
    String eventType = "text";
    String description;
}
