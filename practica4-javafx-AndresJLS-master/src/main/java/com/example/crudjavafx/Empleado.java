package com.example.crudjavafx;

public class Empleado {
    private final int id; //id debe ser único e inmutable
    private String nombre;
    private String puesto;
    private double salario;

    public Empleado(int id, String nombre, String puesto, double salario) {
        this.id = id;
        this.nombre = nombre;
        this.puesto = puesto;
        this.salario = salario;
    }

    public int getId(){
        return id;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString(){
        return "ID: " + id + ", Nombre: " + nombre + ", Puesto: " + puesto + ", Salario: " + salario;
    }
}
