package jdrb.banco.simulador.model;

import jdrb.banco.simulador.model.enums.CustomerStates;

public class Customer {

    private String id;
    private String name;
    private String lastname;
    private String dni;
    private String email;
    private String customerPhone;
    private Long registrationDate;
    private CustomerStates state;

    public Customer() {}

    public Customer(String id,
                    String name,
                    String lastname,
                    String dni,
                    String email,
                    String customerPhone,
                    Long registrationDate,
                    CustomerStates state) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.email = email;
        this.customerPhone = customerPhone;
        this.registrationDate = registrationDate;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public Long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public CustomerStates getState() {
        return state;
    }

    public void setState(CustomerStates state) {
        this.state = state;
    }
}
