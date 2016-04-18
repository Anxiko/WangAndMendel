package core;

/**
 * Created by jcozar on 10/02/16.
 */

    import weka.core.AttributeStats;
    import weka.core.Instances;
    import weka.core.Utils;

    import java.io.FileReader;
    import java.util.Arrays;

/**
 * Esta clase sirve para representar un conjunto de datos.
 *
 * Utiliza la clase Instances de weka.
 *
 * Utilizamos variables extra para simplificar el código.
 * Disponemos de una variable que indica si estamos ante un problema de
 * clasificación o de regresión.
 *
 */

public class Dataset{

    /** Contiene toda la información sobre la base de datos. */
    protected Instances data;

    /**
     * Contiene los datos como un array para facilitar el acceso.
     * La estructura es redundante pero permite hacer accesos m�s r�pidos
     */
    protected double[][] simpleData;

    /** Array que contiene los valores máximo y mínimo para cada variable. */
    protected double[][] ranges;

    /** Número de variables de entrada. */
    protected int nInputVars;

    /** Indice de la variable clase. */
    protected int classVar;

    /** Número total de variables.  */
    protected int nVars;

    /** Número de instancias del conjunto de datos. */
    protected int size;

    /** Variable to know if the output is a real value or a class*/
    protected boolean outputClass;

    /**
     * Constructor
     *
     * Construye el objeto Dataset a partir de un conjunto de Instancias.
     */
    public Dataset(Instances instances){
        data = instances;
        nVars = instances.numAttributes();
        size = instances.numInstances();
        // By default, there is only one class
        nInputVars=nVars-1;
        classVar = nVars-1;
        // Now, it obtains the ranges of the variables
        ranges = new double[nVars][2];
        AttributeStats attributeStats;
        for (int i = 0; i < nVars; i++) {
            attributeStats = data.attributeStats(i);
            ranges[i][0]=attributeStats.numericStats.min;
            ranges[i][1]=attributeStats.numericStats.max;
        }
        data.setClassIndex(nVars-1);

        // Extrae los valores al vector para proporcionar un acceso más rápido.
        simpleData = new double[size][];
        for (int i = 0; i < size; i++) {
            double[] example = data.instance(i).toDoubleArray();
            simpleData[i]=Arrays.copyOf(example,example.length);
        }
    }

    /**
     * Constructor
     *
     * Construye el objeto Dataset a partir de un conjunto archivo arff.
     */
    public Dataset(String file) throws Exception {
        FileReader reader = new FileReader(file);
        data = new Instances(reader);
        nVars = data.numAttributes();
        size = data.numInstances();
        // By default, there is only one class
        nInputVars=nVars-1;
        classVar = nVars-1;
        // Now, it obtains the ranges of the variables
        ranges = new double[nVars][2];
        AttributeStats attributeStats;
        for (int i = 0; i < nVars-1; i++) {
            attributeStats = data.attributeStats(i);
            ranges[i][0] = attributeStats.numericStats.min;
            ranges[i][1] = attributeStats.numericStats.max;
        }
        attributeStats = data.attributeStats(nVars-1);
        if(data.attribute(nVars-1).isNominal()){
            //Lo trataremos como si fuesen enteros (de 0 a n), pero la predicción
            //se realizará por el máximo grado de importancia en lugar de la media
            // de las salidas ponderada por sus grados de importancia
            ranges[nVars-1][0] = 0;
            ranges[nVars-1][1] = attributeStats.nominalCounts.length-1;
            outputClass = true;
        }
        else{
            ranges[nVars-1][0] = attributeStats.numericStats.min;
            ranges[nVars-1][1] = attributeStats.numericStats.max;
            outputClass = false;
        }
        data.setClassIndex(nVars-1);

        // Extrae los valores al vector para proporcionar un acceso m�s r�pido.
        simpleData = new double[size][];
        for (int i = 0; i < size; i++) {
            double[] example = data.instance(i).toDoubleArray();
            simpleData[i]=Arrays.copyOf(example,example.length);
        }
    }

    public boolean isClassification(){
        return outputClass;
    }

    /**
     * Devuelve una instancia como un vector de números reales.
     */
    public double[] getInstanceAsArray(int i){
        return simpleData[i];
    }

    /**
     * Devuelve el tamaño del conjunto de datos.
     */
    public int size(){
        return data.numInstances();
    }

    /**
     * Imprime el conjunto de datos.
     */
    public String toString(){
        String result = data.toString();
        result += "\n\n"+"Rangos";
        for (int i = 0; i < ranges.length; i++) {
            result+="\n"+i+" -> "+Utils.arrayToString(ranges[i]);
        }
        return result+"\n";
    }

    public int getNVars() { return nVars; }

    public double[][] getRanges() { return ranges;}

}

