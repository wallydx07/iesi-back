package com.example.iesiback.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "personal")
public class Personal {
    @Id
    @Column(name = "personal_dni", nullable = false)
    private Long id;

    @Size(max = 50)
    @Column(name = "personal_nombre", length = 50)
    private String personalNombre;

    @Size(max = 50)
    @Column(name = "personal_apellido", length = 50)
    private String personalApellido;

    @Size(max = 50)
    @Column(name = "personal_correo", length = 50)
    private String personalCorreo;

    @Size(max = 50)
    @Column(name = "personal_celular", length = 50)
    private String personalCelular;

    @Size(max = 50)
    @Column(name = "personal_tipo", length = 50)
    private String personalTipo;

    @Column(name = "personal_rdni")
    private Boolean personalRdni;

    @Column(name = "personal_rresidencia")
    private Boolean personalRresidencia;

    @Column(name = "personal_rplanilla")
    private Boolean personalRplanilla;

    @Column(name = "personal_rsanitario")
    private Boolean personalRsanitario;

    @Column(name = "personal_rnacimiento")
    private Boolean personalRnacimiento;

    @Column(name = "declaracion")
    private Boolean declaracion;

    @Column(name = "cargo")
    private Boolean cargo;

    @Column(name = "cuil")
    private Boolean cuil;

    @Column(name = "titulo")
    private Boolean titulo;

    @Column(name = "curriculum")
    private Boolean curriculum;

}