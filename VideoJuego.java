public abstract class VideoJuego {
    protected String nombre;
    protected String idioma;
    protected String estado;

    public VideoJuego();
    public String getNombre(){
        return nombre;
    }
    public String getIdioma(){
        return idioma;
    }
    public String getEstado(){
        return estado;
    }
    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    public void setIdioma(String idioma){
        this.idioma=idioma;
    }
    public void setEstado(String estado){
        this.estado=estado;
    }

    public void iniciar();
    public void salir();
    public void pausar();
    public void reanudar();
    public void ganar();
    public void detectarColision();
    public void reproducirSonido();
    public void aumentarVelSonido();
    public void aumentarVelocidad();
    public void visualizarRanking();
    public void modificarOrigenEnemigo();
    
}


