package com.example.iesiback.services;

import com.example.iesiback.entities.Atencion;
import com.example.iesiback.entities.User;
import com.example.iesiback.repositories.AtencionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AtencionServiceImpl implements AtencionService {

    private final AtencionRepository repository;
    private final UserService userService;
    public AtencionServiceImpl(AtencionRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public String obtenerUser() {
        Optional<User> optionalUser = userService.getAuthenticatedUser();
        return optionalUser.map(User::getUserApellido).orElse("Alumno");
    }


    @Override
    public List<Atencion> findAll() {
        return repository.findAllByOrderByIdDesc();
//        return repository.findAll();
    }

    @Override
    public Optional<Atencion> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Atencion> findByCodigoSeguimiento(String codigo) {
        return repository.findByCodigoSeguimiento(codigo);
    }

    @Override
    public Atencion save(Atencion atencion) {
        // Asigna el usuario logueado automáticamente
        atencion.setAtencionUsuario(obtenerUser());

        // Genera el código de seguimiento
        atencion.setCodigoSeguimiento(generarCodigoSeguimiento());

        // Obtiene la secuencia según el tipo de atención
        String secuencia = getSecuenciaPorTipo(atencion.getAtencionTipo());
        Long numero = obtenerSiguienteNumero(secuencia);
        atencion.setNumeroTipo(numero);

        // 🔎 Control para evitar error de referencia transitoria en Legajo
        if (atencion.getLegajoId() != null && atencion.getLegajoId() == null) {
            System.out.println("Legajo sin ID detectado, se establece en null para evitar error de Hibernate");
            atencion.setLegajoId(null);
        }

        return repository.save(atencion);
    }


    @Override
    public Atencion update(Atencion atencion) {
        atencion.setAtencionUsuario(obtenerUser());
        return repository.save(atencion);
    }

    private String generarCodigoSeguimiento() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) { // Por ejemplo: longitud 8
            codigo.append(caracteres.charAt(random.nextInt(caracteres.length())));
        }
        return codigo.toString();
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public List<Atencion> findByDni(Long dni) {
        return repository.findByAtencionDni(dni);
    }

    @Override
    public List<Atencion> findByApellidoNombre(String apellidoNombre) {
        return repository.findByAtencionApellidoNombreContainingIgnoreCase(apellidoNombre);
    }

    @Override
    public List<Atencion> findByCorreo(String correo) {
        return repository.findByAtencionCorreoContainingIgnoreCase(correo);
    }

    @Override
    public List<Atencion> findByDestino(String destino) {
        return repository.findByAtencionDestino(destino);
    }

    @Override
    public List<Atencion> findByUsuario(String usuario) {
        return repository.findByAtencionUsuario(usuario);
    }

    @Override
    public List<Atencion> findByResuelto(String resuelto) {
        return repository.findByAtencionResuelto(resuelto);
    }

    @Override
    public List<Atencion> findByFecha(LocalDate fecha) {
        return repository.findByAtencionFecha(fecha);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public String getSecuenciaPorTipo(String tipo) {
        switch (tipo) {
            case "Certificado Estudiante":
                return "seq_certificado_estudiante";
            case "Certificado Docente":
                return "seq_certificado_docente";
            case "Solicitud de Equivalencia":
                return "seq_solicitud_equivalencia";
            case "Ingreso de Nota":
                return "seq_nota_ingreso";
            case "Egreso de Nota":
                return "seq_nota_egreso";
            case "Consultas/Reclamos":
                return "seq_consulta_reclamo";
            case "Actividades":
                return "seq_actividades";
            default:
                throw new IllegalArgumentException("Tipo de trámite no reconocido: " + tipo);
        }
    }

    private Long obtenerSiguienteNumero(String secuencia) {
        return jdbcTemplate.queryForObject("SELECT nextval('" + secuencia + "')", Long.class);
    }


    @Override
    public List<Atencion> obtenerPorGestor(Long gestorDni) {
        return repository.findByGestorDni(gestorDni);
    }


    @Override
    public List<Atencion> findByAtencionReferencia(Integer referencia) {
        return repository.findByAtencionReferencia(referencia);
    }

    @Override
    public List<Atencion> findAllByOrderByAtencionFechaDesc() {
        return repository.findAllByOrderByAtencionFechaDescNumeroTipoDesc();
    }

    @Override
    public List<Atencion> findByAtencionLegajoId(String legajoId) {
        return repository.findByLegajoId(legajoId);
    }
}