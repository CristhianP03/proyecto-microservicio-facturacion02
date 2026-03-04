package com.example.microservicioconsultafacturacion.client;

import com.example.microservicioconsultafacturacion.DTO.FacturaDTO;
import com.example.microservicioconsultafacturacion.DTO.PagoDTO;
import com.example.microservicioconsultafacturacion.DTO.VentaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
public class FacturacionClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public FacturacionClient(
            RestTemplate restTemplate,
            @Value("${facturacion.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<VentaDTO> obtenerVentas() {
        return restTemplate.exchange(
                baseUrl + "/ventas",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<VentaDTO>>() {}
        ).getBody();
    }

    public List<FacturaDTO> obtenerFacturas() {
        return restTemplate.exchange(
                baseUrl + "/facturas",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<FacturaDTO>>() {}
        ).getBody();
    }

    public FacturaDTO obtenerFacturaPorId(Integer id) {
        return restTemplate.getForObject(
                baseUrl + "/facturas/" + id,
                FacturaDTO.class
        );
    }

    public List<PagoDTO> obtenerPagosPorFactura(Integer idFactura) {
        return restTemplate.exchange(
                baseUrl + "/facturas/" + idFactura + "/pagos",
                HttpMethod.GET, null,
                new ParameterizedTypeReference<List<PagoDTO>>() {}
        ).getBody();
    }
}
