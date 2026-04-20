package com.example.iesiback.services;

import com.example.iesiback.dto.PaymentDTO;
import com.example.iesiback.entities.Pago;
import com.example.iesiback.dto.ProductoDTO;
import com.example.iesiback.repositories.PagoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PagoServiceImpl implements PagoService {

    private final MercadoPagoService mercadoPagoService;
    private final TramiteService tramiteService;

    @Value("${mercadopago.access-token}")
    private String accessToken;
    @Autowired
    private PagoRepository pagoRepository;

    @Override
    public Pago guardar(Pago pago) {
        return pagoRepository.save(pago);
    }

    @Override
    public Optional<Pago> buscarPorId(Integer id) {
        return pagoRepository.findById(id);
    }

    @Override
    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    @Override
    public void eliminar(Integer id) {
        pagoRepository.deleteById(id);
    }

    @Override
    public Optional<Pago> findByAtencionId(Integer id) {
        return pagoRepository.findByAtencionId(id);
    }




    public PagoServiceImpl(MercadoPagoService mercadoPagoService, TramiteService tramiteService) {
        this.mercadoPagoService = mercadoPagoService;
        this.tramiteService = tramiteService;
    }

    @Override
    public Map<String, String> crearPreferencia(ProductoDTO producto) {
        MercadoPagoConfig.setAccessToken(accessToken);

        try {
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id("1234")
                    .title(producto.getNombre())
                    .description(producto.getDescripcion())
                    .pictureUrl(producto.getImagenUrl())
                    .categoryId("games")
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(producto.getPrecio())
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            Map<String, String> datos = new HashMap<>();
            datos.put("preferenceId", preference.getId());
            datos.put("init_point", preference.getInitPoint()); // ✅ agregar esto
            return datos;

        } catch (MPApiException e) {
            System.err.println("⚠️ API ERROR: " + e.getApiResponse().getContent());
            throw new RuntimeException("Mercado Pago API error: " + e.getApiResponse().getContent());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la preferencia: " + e.getMessage());
        }
    }


    @Override
    public void procesarWebhook(Map<String, Object> payload) throws Exception {
        String topic = (String) payload.get("type");
        Map<String, Object> data = (Map<String, Object>) payload.get("data");
        Long paymentId = Long.valueOf(data.get("id").toString());

        // Consultar datos reales del pago desde Mercado Pago
        PaymentDTO payment = mercadoPagoService.consultarPagoPorId(paymentId);

        String preferenceId = payment.getPreference_id();
        String externalRef = payment.getExternal_reference();

        // Buscar o crear entidad Pago
        Pago pago = pagoRepository.findByPreferenceId(preferenceId)
                .orElse(new Pago());
        pago.setMpPaymentId(paymentId);
        pago.setPreferenceId(preferenceId);
        pago.setExternalReference(externalRef);
        pago.setEstado(payment.getStatus());
        pago.setStatusDetail(payment.getStatus_detail());
        pago.setMetodoPago(payment.getPayment_method_id());
        pago.setTipoPago(payment.getPayment_type_id());
        pago.setMontoTotal(payment.getTransaction_amount());
        pago.setMoneda(payment.getCurrency_id());
        pago.setFechaPago(payment.getDate_approved());
        // Convertimos el DTO a Map para guardar en rawResponse (opcional, pero útil para debug)
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> rawMap = mapper.convertValue(payment, Map.class);
        pago.setRawResponse(rawMap);
        pagoRepository.save(pago);
    }



}
