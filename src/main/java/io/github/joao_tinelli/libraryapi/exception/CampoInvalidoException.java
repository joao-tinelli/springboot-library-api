package io.github.joao_tinelli.libraryapi.exception;

import lombok.Getter;

public class CampoInvalidoException extends RuntimeException{

    @Getter
    private String campo;
    public CampoInvalidoException(String campo, String msg){
        super(msg);
        this.campo = campo;
    }
}
