package com.example.microserviciodemo01.Service;

import com.example.microserviciodemo01.Repository.PagoRepository;
import com.example.microserviciodemo01.models.Pago;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void obtenerTodos_deberiaRetornarListaDePagos() {
        // 1. Datos simulados
        Pago pago = new Pago();
        List<Pago> pagosMock = List.of(pago);

        // 2. Comportamiento simulado del repository
        when(pagoRepository.findAll()).thenReturn(pagosMock);

        // 3. Llamar al método del service
        List<Pago> resultado = pagoService.obtenerTodos();

        // 4. Verificación
        assertEquals(1, resultado.size());
    }
}
