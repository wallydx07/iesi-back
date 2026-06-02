package com.example.iesiback.services;

import com.example.iesiback.entities.Pago;
import com.example.iesiback.entities.Tramite;
import com.example.iesiback.entities.User;
import com.example.iesiback.repositories.PagoRepository;
import com.example.iesiback.repositories.TramiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class TramiteServiceImpl implements TramiteService {

    private final TramiteRepository repository;
    private final UserService userService;
    private final PagoService pagoService;
    public TramiteServiceImpl(TramiteRepository repository, UserService userService, PagoService pagoService) {
        this.repository = repository;
        this.userService = userService;
        this.pagoService = pagoService;
    }

    public String obtenerUser() {
        Optional<User> optionalUser = userService.getAuthenticatedUser();
        return optionalUser.map(User::getUserApellido).orElse("Alumno");
    }


    @Override
    public List<Tramite> findAll() {
        return repository.findAllByOrderByIdDesc();
//        return repository.findAll();
    }

    @Override
    public Optional<Tramite> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Tramite> findByCodigoSeguimiento(String codigo) {
        return repository.findByCodigoSeguimiento(codigo);
    }

    @Override
    public Tramite save(Tramite atencion) {
         // Asigna el usuario logueado automáticamente
        atencion.setTramiteUsuario(obtenerUser());
        // Genera el código de seguimiento
        atencion.setCodigoSeguimiento(generarCodigoSeguimiento());
        // Obtiene la secuencia según el tipo de atención
        String secuencia = getSecuenciaPorTipo(atencion.getTramiteTipo());
        Long numero = obtenerSiguienteNumero(secuencia);
        atencion.setNumeroTipo(numero);
        // 🔎 Control para evitar error de referencia transitoria en Legajo
        if (atencion.getLegajoId() != null && atencion.getLegajoId() == null) {
            System.out.println("Legajo sin ID detectado, se establece en null para evitar error de Hibernate");
            atencion.setLegajoId(null);
        }



        Tramite devolver=repository.save(atencion);


        System.out.println("__________________________________________________");
        System.out.println("Pagos recibidos");
        for (Pago pago : atencion.getPagos()) {
            pago.setTramite(devolver);
            pagoService.guardar(pago);
            System.out.println("tramite if : " + pago.getTramite().getId());
            System.out.println("ID: " + pago.getId());
            System.out.println("Monto: " + pago.getMontoTotal());
            System.out.println("Estado: " + pago.getEstado());
            System.out.println("Referencia: " + pago.getExternalReference());
            System.out.println("--------------------------------");
        }
        return devolver;
    }


    @Override
    public Tramite update(Tramite atencion) {
        atencion.setTramiteUsuario(obtenerUser());
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
    public List<Tramite> findByDni(Long dni) {
        return repository.findByTramiteDni(dni);
    }

    @Override
    public List<Tramite> findByApellidoNombre(String apellidoNombre) {
        return repository.findByTramiteApellidoNombreContainingIgnoreCase(apellidoNombre);
    }

    @Override
    public List<Tramite> findByCorreo(String correo) {
        return repository.findByTramiteCorreoContainingIgnoreCase(correo);
    }

    @Override
    public List<Tramite> findByDestino(String destino) {
        return repository.findByTramiteDestino(destino);
    }

    @Override
    public List<Tramite> findByUsuario(String usuario) {
        return repository.findByTramiteUsuario(usuario);
    }

    @Override
    public List<Tramite> findByResuelto(String resuelto) {
        return repository.findByTramiteEstado(resuelto);
    }

    @Override
    public List<Tramite> findByFecha(LocalDate fecha) {
        return repository.findByTramiteFecha(fecha);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PagoRepository pagoRepository;

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
    public List<Tramite> obtenerPorGestor(Long gestorDni) {
        return repository.findByGestorDni(gestorDni);
    }


    @Override
    public List<Tramite> findByAtencionReferencia(Integer referencia) {
        return repository.findByTramiteReferencia(referencia);
    }

    @Override
    public List<Tramite> findAllByOrderByAtencionFechaDesc() {
        return repository.findAllByOrderByTramiteFechaDescNumeroTipoDesc();
    }

    @Override
    public List<Tramite> findByAtencionLegajoId(String legajoId) {
        return repository.findByLegajoId(legajoId);
    }
}