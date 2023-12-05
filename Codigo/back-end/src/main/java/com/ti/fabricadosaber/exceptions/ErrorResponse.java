package com.ti.fabricadosaber.exceptions;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
public class ErrorResponse {
    // os valores serão exbidos na resposta
    private final int status;
    private final String message;
    private String stackTrace;
    private List<ValidationError> errors;

    public String toJson() {
        return "{\"status\": " + getStatus() + ", " +
                "\"message\": \"" + getMessage() + "\"}";
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    private static class ValidationError {
        private final String field;
        private final String message;
    }


    public void addValidationError(String field, String message) {
        if(Objects.isNull(errors)) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(new ValidationError(field, message)); //adicionando o erro na lista de erros
    }

}
