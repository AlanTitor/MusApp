package org.AlanTitor.MusicApp.Dto.Exception;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
    private String error;
    private String message;
    private int status;
}
