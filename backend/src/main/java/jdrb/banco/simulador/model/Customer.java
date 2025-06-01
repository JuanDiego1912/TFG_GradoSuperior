package jdrb.banco.simulador.model;

import jdrb.banco.simulador.model.enums.CustomerStates;

public class Customer {

    private Long id;
    private String name;
    private String lastname;
    private String dni;
    private String email;
    private String phone;
    private String password;
    private long creationDate;
    private CustomerStates state;

    public Customer() {}

    public Customer(long id,
                    String name,
                    String lastname,
                    String dni,
                    String email,
                    String customerPhone,
                    String password,
                    long registrationDate,
                    CustomerStates state) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.email = email;
        this.phone = customerPhone;
        this.password = password;
        this.creationDate = registrationDate;
        this.state = state;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getPhone() {
        return phone;
}

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public CustomerStates getState() {
        return state;
    }

    public void setState(CustomerStates state) {
        this.state = state;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
