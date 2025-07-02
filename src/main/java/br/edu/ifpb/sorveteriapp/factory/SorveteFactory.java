package br.edu.ifpb.sorveteriapp.factory;

import br.edu.ifpb.sorveteriapp.model.Milkshake;
import br.edu.ifpb.sorveteriapp.model.Picole;
import br.edu.ifpb.sorveteriapp.model.Sorvete;
import br.edu.ifpb.sorveteriapp.model.SorveteMassa;

public class SorveteFactory {

    public Sorvete criarSorvete(String tipo) {
        // Agora podemos instanciar diretamente, sem classes anônimas!
        if ("Picole".equalsIgnoreCase(tipo)) {
            return new Picole();
        } else if ("Milkshake".equalsIgnoreCase(tipo)) {
            return new Milkshake();
        } else if ("Massa".equalsIgnoreCase(tipo)) {
            return new SorveteMassa();
        } else {
            throw new IllegalArgumentException("Tipo de sorvete não identificado: " + tipo);
        }
    }
}