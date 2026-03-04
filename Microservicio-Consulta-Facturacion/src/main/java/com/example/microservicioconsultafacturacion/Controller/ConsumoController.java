package com.example.microservicioconsultafacturacion.Controller;

import com.example.microservicioconsultafacturacion.client.FacturacionClient;
import com.example.microservicioconsultafacturacion.DTO.FacturaDTO;
import com.example.microservicioconsultafacturacion.DTO.PagoDTO;
import com.example.microservicioconsultafacturacion.DTO.VentaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/consumo")
@CrossOrigin(origins = "*")
public class ConsumoController {

    private final FacturacionClient facturacionClient;

    public ConsumoController(FacturacionClient facturacionClient) {
        this.facturacionClient = facturacionClient;
    }

    @GetMapping("/ventas")
    public ResponseEntity<List<VentaDTO>> obtenerVentas() {
        return ResponseEntity.ok(facturacionClient.obtenerVentas());
    }

    @GetMapping("/facturas")
    public ResponseEntity<List<FacturaDTO>> obtenerFacturas() {
        return ResponseEntity.ok(facturacionClient.obtenerFacturas());
    }

    @GetMapping("/facturas/{id}")
    public ResponseEntity<FacturaDTO> obtenerFacturaPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(facturacionClient.obtenerFacturaPorId(id));
    }

    @GetMapping("/facturas/{id}/pagos")
    public ResponseEntity<List<PagoDTO>> obtenerPagos(@PathVariable Integer id) {
        return ResponseEntity.ok(facturacionClient.obtenerPagosPorFactura(id));
    }
}
