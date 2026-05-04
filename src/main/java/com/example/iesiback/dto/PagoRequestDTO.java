package com.example.iesiback.dto;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.PagoDetalle;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagoRequestDTO {

    private Pago pago;
    private List<PagoDetalle> detalles;

}