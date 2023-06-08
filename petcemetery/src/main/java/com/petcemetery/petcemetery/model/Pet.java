package com.petcemetery.petcemetery.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "Pet")
@Table(name = "Pet")
public class Pet {
    
    @Id
    @Column(name = "id_pet")
    private long id;
    
    @Column(name = "nome_pet")
    private String nomePet;
    
    @Column(name = "data_enterro")
    private LocalDate dataEnterro;

    @Column(name = "hora_enterro")
    private LocalTime horaEnterro;
    
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    
    @Column(name = "especie")
    private String especie;
    
    public Pet() {
        // Construtor padrão sem argumentos
    }
    public Pet(long id, String nomePet, String especie) {
        this.id = id;
        this.nomePet = nomePet;
        this.especie = especie;
    }
    public Pet(long id, String nomePet, LocalDate dataNascimento, String especie) {
        this.id = id;
        this.nomePet = nomePet;
        this.dataNascimento = dataNascimento;
        this.especie = especie;
    }
    public Pet(long id, String nomePet, LocalDate dataEnterro, LocalDate dataNascimento, String especie, LocalTime hora_enterro) {
        this.id = id;
        this.nomePet = nomePet;
        this.dataEnterro = dataEnterro;
        this.dataNascimento = dataNascimento;
        this.especie = especie;
        this.horaEnterro = hora_enterro;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getNomePet() {
        return nomePet;
    }
    public void setNomePet(String nomePet) {
        this.nomePet = nomePet;
    }
    public LocalDate getDataEnterro() {
        return dataEnterro;
    }
    public void setDataEnterro(LocalDate dataEnterro) {
        this.dataEnterro = dataEnterro;
    }
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }
    public String getEspecie() {
        return especie;
    }
    public void setEspecie(String especie) {
        this.especie = especie;
    }
    public LocalTime getHoraEnterro() {
        return horaEnterro;
    }
    public void setHoraEnterro(LocalTime horaEnterro) {
        this.horaEnterro = horaEnterro;
    }

    
}
