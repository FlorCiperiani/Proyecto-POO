package lodeRunner;

import clasesCompartidas.ObjetoGrafico;

public abstract class ElementoMapa extends ObjetoGrafico {
    public ElementoMapa(String rutaRecurso, double x, double y) {
        super(rutaRecurso); // Llama al constructor de ObjetoGrafico que busca en recursos
        setPosicion(x, y);
    }
}

// Bloque de ladrillo común (rompible)
class Ladrillo extends ElementoMapa {
    public Ladrillo(double x, double y) {
        super("loderunner/ladrillo.png", x, y);
    }
    @Override
    public void update(double delta) {}
}

// Bloque de piedra (indestructible)
class Piedra extends ElementoMapa {
    public Piedra(double x, double y) {
        super("loderunner/piedra.png", x, y);
    }
    @Override
    public void update(double delta) {}
}

// Escalera para trepar
class Escalera extends ElementoMapa {
    public Escalera(double x, double y) {
        super("loderunner/escalera.png", x, y);
    }
    @Override
    public void update(double delta) {}
}

// Barra de mono / cuerda para colgarse
class Barra extends ElementoMapa {
    public Barra(double x, double y) {
        super("loderunner/barra.png", x, y);
    }
    @Override
    public void update(double delta) {}
}

// El oro que el jugador debe recolectar
class Oro extends ElementoMapa {
    public Oro(double x, double y) {
        super("loderunner/oro.png", x, y);
    }
    @Override
    public void update(double delta) {}
}