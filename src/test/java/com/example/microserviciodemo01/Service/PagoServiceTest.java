package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.FacturaRepository;
import com.example.microserviciodemo01.Repository.PagoRepository;
import com.example.microserviciodemo01.models.Pago;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private FacturaRepository facturaRepository; // Mock necesario para el constructor de PagoService

    @InjectMocks
    private PagoService pagoService;

    @Test
    void obtenerTodos_deberiaRetornarListaDePagos() {
        // 1. Arrange (Preparar)
        Pago pago = new Pago();
        pago.setIdPago(1);
        List<Pago> pagosMock = List.of(pago);

        // Definimos el comportamiento del mock
        when(pagoRepository.findAll()).thenReturn(pagosMock);

        // 2. Act (Actuar)
        List<Pago> resultado = pagoService.obtenerTodos();

        // 3. Assert (Verificar)
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(pagoRepository).findAll(); // Verifica que se llamó al repo una vez
    }
}