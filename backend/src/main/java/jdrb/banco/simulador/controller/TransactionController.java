package jdrb.banco.simulador.controller;

import jdrb.banco.simulador.model.Transaction;
import jdrb.banco.simulador.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<String> registrarTransaccion(@RequestBody Transaction transaction) {
        try {
            boolean registrada = transactionService.registerTransaction(transaction);
            if (registrada) {
                return ResponseEntity.ok("Transacción registrada correctamente");
            } else {
                return ResponseEntity.badRequest().body("No se pudo registrar la transacción");
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> obtenerTransaccionPorId(@PathVariable Long id) {
        try {
            Transaction transaccion = transactionService.getTransactionById(id);
            if (transaccion != null) {
                return ResponseEntity.ok(transaccion);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/origen/{idCuenta}")
    public ResponseEntity<List<Transaction>> obtenerTransaccionesPorCuentaOrigen(@PathVariable Long idCuenta) {
        try {
            List<Transaction> lista = transactionService.getTransactionsBySourceAccount(idCuenta);
            if (lista.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(lista);
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/destino/{idCuenta}")
    public ResponseEntity<List<Transaction>> obtenerTransaccionesPorCuentaDestino(@PathVariable Long idCuenta) {
        try {
            List<Transaction> lista = transactionService.getTransactionsByDestinationAccount(idCuenta);
            if (lista.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(lista);
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<Transaction>> obtenerTransaccionesPorFechas(
            @RequestParam Long idCuenta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date hasta
    ) {
        try {
            List<Transaction> lista = transactionService.getTransactionsBetweenDates(idCuenta, desde, hasta);
            if (lista.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(lista);
            }
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
