package com.example.iesiback.services;

import com.example.iesiback.entities.ConstanciaPrecio;
import com.example.iesiback.repositories.CursadaRepository;
import com.example.iesiback.repositories.PersonaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Único lugar donde se decide el descuento de un curso de Capacitación.
 * La web, la ventanilla y la caja usan este servicio: la regla no se repite en ningún otro lado.
 *
 * Regla: se evalúan todas las promociones que apliquen y se usa SOLO EL MAYOR porcentaje.
 *
 *   - Persona registrada: el DNI está en la tabla persona  → % de constancia_precios.descuento
 *   - Más de un curso: ya tiene otro curso de Capacitación de este año (pagado) → app.promociones.varios-cursos
 *   - Grupo de 2 personas                                   → app.promociones.grupo-2
 *   - Grupo de 3 o más personas                             → app.promociones.grupo-3
 */
@Service
public class PromocionService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final ZoneId ZONA = ZoneId.of("America/Argentina/Jujuy");
    private final LegajoService legajoService;

    /** Resultado del cálculo: precio de lista, % aplicado, motivo y precio final. */
    public record PrecioFinal(BigDecimal base, BigDecimal descuentoPct, String motivo, BigDecimal total) {
        public boolean conDescuento() {
            return descuentoPct.signum() > 0;
        }

        /** Texto para el detalle del pago, ej.: "Curso IA (Más de un curso -25%)". */
        public String etiqueta(String nombreConcepto) {
            if (!conDescuento()) return nombreConcepto;
            return nombreConcepto + " (" + motivo + " -" + descuentoPct.stripTrailingZeros().toPlainString() + "%)";
        }
    }

    private record Candidato(BigDecimal pct, String motivo) {}

    private final PersonaRepository personaRepository;
    private final CursadaRepository cursadaRepository;

    @Value("${app.preinscripcion.carrera-id}")
    private String carreraCapacitacionId;

    @Value("${app.promociones.varios-cursos:25}")
    private BigDecimal pctVariosCursos;

    @Value("${app.promociones.grupo-2:25}")
    private BigDecimal pctGrupo2;

    @Value("${app.promociones.grupo-3:30}")
    private BigDecimal pctGrupo3;

    public PromocionService(PersonaRepository personaRepository, CursadaRepository cursadaRepository, LegajoService legajoService) {
        this.personaRepository = personaRepository;
        this.cursadaRepository = cursadaRepository;
        this.legajoService = legajoService;
    }

    /** Precio online (precio_mp) para una persona que se inscribe sola. */
    public PrecioFinal calcularOnline(ConstanciaPrecio concepto, Long dni, Integer ofertaId) {
        return calcular(concepto.getPrecioMp(), concepto, dni, ofertaId, 1);
    }

    /** Precio presencial (precio) para una persona que se inscribe sola. */
    public PrecioFinal calcularPresencial(ConstanciaPrecio concepto, Long dni, Integer ofertaId) {
        return calcular(concepto.getPrecio(), concepto, dni, ofertaId, 1);
    }

    /**
     * Cálculo general.
     *
     * @param personasEnGrupo cantidad de personas que se inscriben juntas (1 si es individual).
     *                        La inscripción grupal, cuando se implemente, pasa acá el tamaño del grupo.
     */
    public PrecioFinal calcular(BigDecimal base, ConstanciaPrecio concepto, Long dni,
                                Integer ofertaId, int personasEnGrupo) {

        List<Candidato> candidatos = new ArrayList<>();

        // 1) Persona registrada → % del concepto
//        BigDecimal pctConcepto = porcentajeValido(concepto.getDescuento());
//        if (pctConcepto.signum() > 0 && dni != null && personaRepository.findByPersonaDni(dni).isPresent()) {
//            candidatos.add(new Candidato(pctConcepto, "Persona registrada"));
//        }

        // 1) Estudiante o egresado del IESI (legajo en una carrera que no sea Capacitación) → % del concepto
        BigDecimal pctConcepto = porcentajeValido(concepto.getDescuento());
        if (pctConcepto.signum() > 0 && dni != null
                && legajoService.tieneLegajoEnOtraCarrera(dni, "CYDL-2026")) {
            candidatos.add(new Candidato(pctConcepto, "Estudiante IESI"));
        }

        // 2) Más de un curso: ya tiene otro curso de Capacitación de este año
        if (dni != null && ofertaId != null && pctVariosCursos.signum() > 0) {
            int anio = LocalDate.now(ZONA).getYear();
            long otros = cursadaRepository.contarOtrosCursosDelAnio(dni, carreraCapacitacionId, ofertaId, anio);
            if (otros > 0) {
                candidatos.add(new Candidato(pctVariosCursos, "Más de un curso"));
            }
        }

        // 3) Grupo
        if (personasEnGrupo >= 3) {
            candidatos.add(new Candidato(pctGrupo3, "Grupo de " + personasEnGrupo));
        } else if (personasEnGrupo == 2) {
            candidatos.add(new Candidato(pctGrupo2, "Grupo de 2"));
        }

        // Solo el mayor
        Candidato elegido = candidatos.stream()
                .max(Comparator.comparing(Candidato::pct))
                .orElse(new Candidato(BigDecimal.ZERO, null));

        BigDecimal total = elegido.pct().signum() > 0
                ? base.multiply(CIEN.subtract(elegido.pct())).divide(CIEN, 2, RoundingMode.HALF_UP)
                : base;

        return new PrecioFinal(base, elegido.pct(), elegido.motivo(), total);
    }

    /** descuento como porcentaje entre 0 y 100; cualquier otro valor se ignora. */
    private static BigDecimal porcentajeValido(Double d) {
        if (d == null || d <= 0 || d > 100) return BigDecimal.ZERO;
        return BigDecimal.valueOf(d);
    }


}