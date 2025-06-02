package jdrb.banco.simulador.controller;

import jdrb.banco.simulador.model.Account;
import jdrb.banco.simulador.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<String> registerAccount(@RequestBody Account cuenta) {
        try {
            boolean creada = accountService.registerAccount(cuenta);
            return creada ? ResponseEntity.ok("Cuenta creada")
                    : ResponseEntity.badRequest().body("No se pudo crear la cuenta");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        try {
            Account cuenta = accountService.getAccountById(id);
            return cuenta != null ? ResponseEntity.ok(cuenta) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Account>> getAccountsByCustomerId(@PathVariable Long idCliente) {
        List<Account> cuentas = accountService.getAccountsByCustomerId(idCliente);
        return cuentas != null ? ResponseEntity.ok(cuentas)
                : ResponseEntity.notFound().build();
    }

    @PutMapping
    public ResponseEntity<String> updateAccount(@RequestBody Account cuenta) {
        try {
            boolean actualizada = accountService.updateAccount(cuenta);
            return actualizada ? ResponseEntity.ok("Cuenta actualizada")
                    : ResponseEntity.badRequest().body("No se pudo actualizar la cuenta");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        try {
            boolean eliminada = accountService.deleteAccount(id);
            return eliminada ? ResponseEntity.ok("Cuenta eliminada")
                    : ResponseEntity.badRequest().body("No se pudo eliminar la cuenta");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/cliente/{idCliente}/cuenta/{idCuenta}")
    public ResponseEntity<String> deleteAccountForClient(@PathVariable Long idCliente, @PathVariable Long idCuenta) {
        try {
            boolean eliminada = accountService.deleteAccountForClient(idCliente, idCuenta);
            return eliminada ? ResponseEntity.ok("Cuenta eliminada para el cliente")
                    : ResponseEntity.badRequest().body("No se pudo eliminar la cuenta");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}