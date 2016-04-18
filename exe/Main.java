package exe;


import core.Dataset;
import frbs.FRBS;
import learning.WAM;

import java.util.Arrays;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        //Cargamos el dataset .arff: i.e.: "src/data/iris.arff" (clasificación) o "src/data/ele1.arff" (regresión)
        Dataset ds = null;
        try {
            ds = new Dataset(args[0]);
        } catch (Exception e) {
            System.out.println("Error al cargar el dataset: "+e);
            return;
        }

        //Creamos el objeto Wang y Mendel. Le pasamos el dataset y el número de etiquetas por variable (y aprende el modelo)
        WAM wm = new WAM(ds,Integer.parseInt(args[1]));
        //Obtenemos el FRBS aprendido.
        FRBS frbs = wm.getFRBS();
        //Mostramos por pantalla el FRBS aprendido
        System.out.println(frbs);

        //Hacemos 10 predicciones de 10 instancias aleatorias:
        System.out.println("Predicciones de 10 instancias aleatorias:\n=========================================");
        double[] input;
        double output;
        double[] instance;
        Random r = new Random(1234);
        int numInstances = ds.size();
        for(int i=0;i<10;i++) {
            instance = ds.getInstanceAsArray(r.nextInt(numInstances));
            input = Arrays.copyOf(instance, instance.length - 1);
            output = instance[instance.length - 1];
            //Si el problema es clasificación, utiliza la predicción de la regla con mayor grado de importancia.
            //SI el problema es regresión, devuelve la media ponderada de las salidas por el grado de importancia.
            double prediction = frbs.getOutput(input);
            System.out.println("Valor real: " + output + ". Predicción: " + prediction);
        }
    }
}
