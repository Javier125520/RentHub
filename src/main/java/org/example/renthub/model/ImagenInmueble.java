package org.example.renthub.model;

public class ImagenInmueble {
    private int id;
    private Inmueble inmueble;
    private String url;

    public ImagenInmueble() { }

    public ImagenInmueble(int id, Inmueble inmuebleId, String url) {
        this.id = id;
        this.inmueble = inmuebleId;
        this.url = url;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        if (this.inmueble != null) {
            this.inmueble.removeImagen(this);
        }
        this.inmueble = inmueble;
        if (inmueble != null) {
            inmueble.addImagen(this);
        }
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
                ", inmuebleId=" + inmueble +
                ", url='" + url + '\'' +
                '}';
    }
}
