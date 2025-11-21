package com.example.lab06.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistroService {
    @POST("registro")
    Call<RegistroResponse> validarRegistro(@Body RegistroRequest request);
}
