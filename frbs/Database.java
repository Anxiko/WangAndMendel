package frbs;


import core.Dataset;
import java.util.logging.Level;
import java.util.logging.Logger;
import nrc.fuzzy.*;


public class Database {

    /** Rango de valores para cada variable */
    protected double[][] rangesVar;
    /** Variables */
    protected FuzzyVariable[] variables;

    /** Número de variables del problema */
    protected int numVars;
    /** Número de etiquetas por variable */
    protected int numLabels;

    public Database(Dataset ds, int numLabels){
        this.numLabels = numLabels;
        buildFromDataset(ds);
    }

    public FuzzyVariable varAt(int i) {
        return variables[i];
    }

    public FuzzyValue getValue(int i, int j) throws Exception {
        return new FuzzyValue(variables[i],variables[i].getSet(j));
    }

    public int getNVars(){
        return numVars;
    }

    //Esta función construye la partición de las variables generando numLabels conjuntos difusos distribuidos
    //equiespacialmente a lo largo del dominio de cada variable. En los extremos se usarán conjuntos abiertos.
    //En el caso de ser un problema de clasificación, la variable de salida tendrá nClases conjuntos difusos en lugar
    //de numLabels.
    public void buildFromDataset(Dataset dataset){
        try{
            numVars = dataset.getNVars();
            variables = new FuzzyVariable[numVars];
            // Fija el rango para cada variable y lo crea.
            rangesVar = new double[numVars][2];
            double[][] ranges = dataset.getRanges();

            for (int i = 0; i < numVars; i++){
                rangesVar[i][0] = ranges[i][0];
                rangesVar[i][1] = ranges[i][1];
            }

            //Primero las entradas (la salida es la variable numVars-1.
            for (int i = 0; i < numVars-1; i++)
            {
                //Esta función crea las numLabels particiones para la variable i.
                createSimTriangularPartition(i, numLabels, true);
            }
            //Ahora a variable de salida.
            //Si estamos ante un problema de clasificación, creamos tantos conjuntos como clases haya
            if(dataset.isClassification())
                createSimTriangularPartition(numVars-1,(int)rangesVar[numVars-1][1]+1,false);
            //Si no, el número de conjuntos especificado para todas las variables.
            else
                createSimTriangularPartition(numVars-1,numLabels,true);
        }
        catch (Exception e) {System.out.println("Impossible to build dataset: "+e);}
    }


    /**
     * Crea una partición triangular simétrica de la variable.
     * OpenRange determina si en los extremos del dominio, las particiones continúan se cortan.
     */
    private void createSimTriangularPartition(int var, int nSets, boolean OpenRange){
        /* Por ejemplo, en un dominio de [0 - 2] y 3 conjuntos, tendríamos el primero conjunto (0,0,1), el segundo
         (0,1,2), y el tercero (1,2,2).
         Para acceder a los rangos de las variables: rangesVar[var] --> array de dos posiciones (0->min, 1->max)
         La partición se debe crear utilizando la función setPartition de la clase FuzzyVariable. Ésta debe ser implementada.
         Esta función recibe un array de particiones difusas (un array de TriangleFuzzySets).
        */

        //Array de objetos TriangleFuzzySet. Por ejemplo:
        // sets[0] = new TriangleFuzzySet(a,b,c);
        FuzzySet[] sets = new FuzzySet[nSets];
        
        /*
            NOTA:
            OpenRange = 
                true    => Los triángulos de los bordes sobrepasan el dominio.
                false   => Los triángulos de los bordes no sobrepasan el dominio, tienen la mitad de area.
        */
        
        /*
            Siendo r el rango de la variable, y n el número de conjuntos a generar:
                - La base del triángulo es 2*r/(n-1)
                - El centro se calcula como i*(r/(n-1)), donde 0<=i<n
                - Los triángulos dónde i == 0 y i == n-1 son los de los extremos, que pueden tener la mita de ancho
        */
        
        double dist=(rangesVar[var][1]-rangesVar[var][0])/(nSets-1);//Distancia del centro del triángulo a uno de los bordes

        //Se generan primero los triángulos de los extremos, pues son casos especiales ya que pueden tener la mitad del area
        
        if (OpenRange)//Los triángulos no se cortan
        {
            double centro;//Centro del triángulo
            
            //Primer triángulo
            centro=rangesVar[var][0];
            
            try {
                sets[0]=new TriangleFuzzySet(centro-dist,centro,centro+dist);
            } catch (XValuesOutOfOrderException ex) {
                System.out.println("ERROR: Al crear el triangulo inicial, las coordenadas X no están en orden. ¿nsets<=0?");
            }
            
            //Último triángulo
            
            centro=rangesVar[var][1];
            
            try {
                sets[nSets-1]=new TriangleFuzzySet(centro-dist,centro,centro+dist);
            } catch (XValuesOutOfOrderException ex) {
                System.out.println("ERROR: Al crear el triangulo final, las coordenadas X no están en orden. ¿nsets<=0?");
            }
        }
        else
        {
            double centro;//Centro del triángulo
            
            //Primer triángulo
            centro=rangesVar[var][0];
            
            try {
                sets[0]=new TriangleFuzzySet(centro,centro,centro+dist);
            } catch (XValuesOutOfOrderException ex) {
                System.out.println("ERROR: Al crear el triangulo inicial, las coordenadas X no están en orden. ¿nsets<=0?");
            }
            
            //Último triángulo
            
            centro=rangesVar[var][1];
            
            try {
                sets[nSets-1]=new TriangleFuzzySet(centro-dist,centro,centro);
            } catch (XValuesOutOfOrderException ex) {
                System.out.println("ERROR: Al crear el triangulo final, las coordenadas X no están en orden. ¿nsets<=0?");
            }
        }
        
        //Ahora se generan los triángulos intermedios usando la fórmula general
        double centro=rangesVar[var][0]+dist;
        for (int i=1;i<nSets-1;++i)
        {
            try {
                sets[i]=new TriangleFuzzySet(centro-dist,centro,centro+dist);
            } catch (XValuesOutOfOrderException ex) {
                System.out.println("ERROR: Al crear un triángulo intermedio, las coordenadas X no están en orden. ¿nsets inválido");
            }
            centro += dist;
        }

        //Fijamos la partición a la variable difusa.
        try {
            if (OpenRange)
                variables[var]=new FuzzyVariable("Var" + Integer.toString(var),rangesVar[var][0]-dist,rangesVar[var][1]+dist);
            else
                variables[var]=new FuzzyVariable("Var" + Integer.toString(var),rangesVar[var][0],rangesVar[var][1]);
            variables[var].setPartition(sets);
        } catch (Exception e) {
            System.out.println("Error al fijar la partición a la variable "+var+". Excepción: "+e);
        }

    }

    /**
     * Imprime la base de datos.
     */
    public String toString(){
        String string = new String("Input variables\n");
        for (int i = 0; i < numVars; i++) {
            string += variables[i].toString();
        }
        string += new String("\nOutput variables\n");
        string+=variables[numVars-1].toString();

        return string+"\n";
    }
}
