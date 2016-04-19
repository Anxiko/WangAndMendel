package frbs;

import nrc.fuzzy.FuzzySet;
import nrc.fuzzy.InvalidFuzzyVariableNameException;
import nrc.fuzzy.InvalidUODRangeException;

/**
 * Extiende la definición de FuzzyVariable de fuzy jess, para incluir las variables nSets y sets.
 * También implementa la función highestMembershipIndex que determina el set que maximiza el grado de pertenencia
 * dado un valor del dominio de la variable.
 */
public class FuzzyVariable extends nrc.fuzzy.FuzzyVariable{
    /** Referencias a los conjuntos difusos. */
    protected FuzzySet[] sets;

    /** Número de conjuntos difusos. */
    protected int nSets;

    public FuzzyVariable(String name, double minUoD, double maxUoD) throws InvalidFuzzyVariableNameException, InvalidUODRangeException {
        super(name,minUoD,maxUoD);
    }


    /**
     * Fija la partición a partir de un vector de conjuntos
     * difusos
     */
    public void setPartition(FuzzySet[] pSets) throws Exception{
        // Borra los términos que puede haber anteriormente en la variable difusa (simplemente buena costumbre).
        super.removeTerms();
        sets = pSets;
        nSets = sets.length;       
        for (int i = 0; i < pSets.length; i++) {
            // Añadir los términos (FuzzySets) que definen las particiones de la variable difusa i
            //Añado el término a la variable difusa, uso la variable i como nombre de etiqueta y el conjunto difuso del array   
            this.addTerm(Integer.toString(i), sets[i]);
        }
    }

    /**
     * Devuelve la posición del conjunto difuso para el que x tiene un mayor
     * grado de pertenencia.
     *
     * Si éste no es mayor que 0 para ninguno, devuelve -1.
     */
    public int highestMembershipIndex(double x){
        //Para cada conjunto difuso, almacenado en "sets[]" (array de FuzzySet), podemos obtener el grado de pertenencia
        //con la función sets[i].getMembership(x).
        
        //Conjunto difuso elegido
        int chosenSet=-1;
        //Valor del grado de pertenencia del conjunto difuso actual
        double actual;
        //Valor del máximo grado de pertenencia
        double maximo = 0;
        
        //Para todos los conjuntos
        for (int i = 0; i < nSets; ++i)
        {
            //Cálculo del grado de pertenencia al conjunto difuso
            actual = sets[i].getMembership(x);
            //Si el valor actual es mayor que el maximo
            if (maximo < actual)
            {
                maximo = actual;    //Actualizo el máximo
                chosenSet = i;      //Conjunto elegido es el que está en la posición i           
            }
        }
        
        //Hay que devolver el índice del conjunto difuso que maximiza el grado de pertenencia.
        return chosenSet;
    }

    /**
     * Retorna el conjunto difuso en una posición determinada.
     */
    public FuzzySet getSet(int i){
        return sets[i];
    }

}
