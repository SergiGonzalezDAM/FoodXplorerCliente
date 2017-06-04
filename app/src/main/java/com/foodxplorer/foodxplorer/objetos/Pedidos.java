package com.foodxplorer.foodxplorer.objetos;


public class Pedidos {
    private long idPedido;
    private String fechaPedido;
    private long idDireccion = -3;
    private long idEstado;
    private String user;

    /**
     * Generamos la clase Pedidos con un constructor vacio y otro con los datos necesarios para poder recuperarlos de la base de datos
     * lo usaremos básicamente para obtener todos los pedidos de un usuario
     */
    public Pedidos() {
    }

    public Pedidos(long idPedido, String fechaPedido, long idDireccion, long idEstado) {
        this.idPedido = idPedido;
        this.fechaPedido = fechaPedido;
        this.idDireccion = idDireccion;
        this.idEstado = idEstado;
    }

    public String getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(String fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public long getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(long idPedido) {
        this.idPedido = idPedido;
    }

    public long getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(long idDireccion) {
        this.idDireccion = idDireccion;
    }

    public long getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(long idEstado) {
        this.idEstado = idEstado;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Pedidos{" +
                "idPedido=" + idPedido +
                ", fechaPedido='" + fechaPedido + '\'' +
                ", idDireccion=" + idDireccion +
                ", idEstado=" + idEstado +
                '}';
    }
}
