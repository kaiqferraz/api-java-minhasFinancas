package com.kaiqferaz.minhasfinancas.exceptions;

public class RegraNegocioException extends RuntimeException {

    //METODO USADO NA CLASSE UsuarioServiceImpl
    public RegraNegocioException(String msg){
        super(msg);
    }

}
