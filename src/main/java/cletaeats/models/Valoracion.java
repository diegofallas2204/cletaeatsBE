package cletaeats.models;

public class Valoracion {
    private int id;
    private int pedidoId;
    private int clienteId;
    private int rating;
    private String comentario;
    private String fecha;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPedidoId() { return pedidoId; }
    public void setPedidoId(int pedidoId) { this.pedidoId = pedidoId; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}
