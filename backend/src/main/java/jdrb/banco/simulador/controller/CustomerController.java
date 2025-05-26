package jdrb.banco.simulador.controller;

import jdrb.banco.simulador.model.Customer;
import jdrb.banco.simulador.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String id) {
        Customer customer = customerService.getCustomerById(id);
        return customer != null ? ResponseEntity.ok(customer) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<String> registerCustomer(@RequestBody Customer customer) {
        boolean registered = customerService.registerCustomer(customer);
        return registered ? ResponseEntity.ok("Cliente registrado")
                : ResponseEntity.badRequest().body("Error al registrar cliente");
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PutMapping
    public ResponseEntity<String> updateCustomer(@RequestBody Customer customer) {
        boolean updated = customerService.updateCustomer(customer);
        return updated ? ResponseEntity.ok("Cliente actualizado")
                : ResponseEntity.badRequest().body("Error al actualizar el cliente");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable String id) {
        boolean deleted = customerService.deleteCustomer(id);
        return deleted ? ResponseEntity.ok("Cliente eliminado")
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Customer> login(@RequestBody LoginRequest login) {
        try {
            Customer customer = customerService.login(
                    login.getEmail(),
                    login.getPassword()
            );
            return ResponseEntity.ok(customer);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(null);
        }
    }

    private static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email = email;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

}
