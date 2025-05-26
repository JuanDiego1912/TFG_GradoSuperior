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
            return registrada
                    ? ResponseEntity.ok(("Transacción registrada correctamente"))
                    : ResponseEntity.badRequest().body("No se pudo registrar la transacción");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> obtenerTransaccionPorId(@PathVariable String id) {
        try {
            Transaction transaccion = transactionService.getTransactionById(id);
            return transaccion != null
                    ? ResponseEntity.ok(transaccion)
                    : ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/origen/{idCuenta}")
    public List<Transaction> obtenerTransaccionesPorCuentaOrigen(@PathVariable String idCuenta) {
        return transactionService.getTransactionsBySourceAccount(idCuenta);
    }

    @GetMapping("/destino/{idCuenta}")
    public List<Transaction> obtenerTransaccionesPorCuentaDestino(@PathVariable String idCuenta) {
        return transactionService.getTransactionsByDestinationAccount(idCuenta);
    }

    @GetMapping("/fechas")
    public List<Transaction> obtenerTransaccionesPorFechas(
            @RequestParam String idCuenta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date hasta
    ) {
        return transactionService.getTransactionsBetweenDates(idCuenta, desde, hasta);
    }
}
