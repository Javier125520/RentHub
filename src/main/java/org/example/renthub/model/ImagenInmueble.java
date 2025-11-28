package org.example.renthub.model;

public class ImagenInmueble {
    private int id;
    private Inmueble inmuebleId;
    private String url;

    public ImagenInmueble() { }

    public ImagenInmueble(int id, Inmueble inmuebleId, String url) {
        this.id = id;
        this.inmuebleId = inmuebleId;
        this.url = url;
    }

    // Getters y Setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Inmueble getInmuebleId() {
        return inmuebleId;
    }

    public void setInmuebleId(Inmueble inmuebleId) {
        this.inmuebleId = inmuebleId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ImagenInmueble{" +
                "id=" + id +
                ", inmuebleId=" + inmuebleId +
                ", url='" + url + '\'' +
                '}';
    }
}
