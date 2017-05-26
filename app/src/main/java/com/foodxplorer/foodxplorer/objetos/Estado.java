package com.foodxplorer.foodxplorer.objetos;

/**
 * Created by ALUMNEDAM on 25/05/2017.
 */

public class Estado {
    private long idEstado;
    private String nomEstado;
    private double tiempo;

    public Estado() {
    }

    public Estado(long idEstado, String nomEstado, double tiempo) {
        this.idEstado = idEstado;
        this.nomEstado = nomEstado;
        this.tiempo = tiempo;
    }

    public long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(long idEstado) {
        this.idEstado = idEstado;
    }

    public String getNomEstado() {
        return nomEstado;
    }

    public void setNomEstado(String nomEstado) {
        this.nomEstado = nomEstado;
    }

    public double getTiempo() {
        return tiempo;
    }

    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }

    @Override
    public String toString() {
        return "Estado{" +
                "idEstado=" + idEstado +
                ", nomEstado='" + nomEstado + '\'' +
                ", tiempo=" + tiempo +
                '}';
    }
}
