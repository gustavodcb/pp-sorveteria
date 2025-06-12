package factory;

public class SorveteFactory {

    public Sorvete criarSorvete(String tipo) {
        if (tipo.equals("Picole")) {
            return new Picole();
        } else if (tipo.equals("Milkshake")) {
            return new Milkshake();
        } else if (tipo.equals("Massa")) {
            return new SorveteMassa();
        } else {
            throw new IllegalArgumentException("Tipo não identificado");
        }
    }
    
}
