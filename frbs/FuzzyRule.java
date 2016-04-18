package frbs;

import nrc.fuzzy.FuzzyRuleExecutor;
import nrc.fuzzy.FuzzyValue;


public class FuzzyRule extends nrc.fuzzy.FuzzyRule{
    /**
     * La regla se representa por un antecedente y un consecuente.
     * El antecedente está compuesto por un vector de enteros (para la variable i, la partición j)
     * El consecuente es un entero (para la variable de salida, la partición j)
     * Además disponemos de una representación del antecedente en forma de string, utilizada por Rulebase para saber
     * si una regla con el mismo antecedente existe en la base de reglas o no (utiliza una tabla hash).
     */
    private int[] antecedente;
    private int consecuente;
    private String representacionAntecedente;
    private double importanceDegree;

    public FuzzyRule(int[] code, FuzzyRuleExecutor ruleExecutor, Database db) throws Exception {
        buildAntecedentFromCode(db,code);
        setRuleExecutor(ruleExecutor);
    }

    public double getImportanceDegree(){
        return importanceDegree;
    }
    public void setImportanceDegree(double id){ importanceDegree = id; }

    public String getAntecedentRepresentation(){ return representacionAntecedente; }
    public int getConsecuente(){
        return consecuente;
    }

    public FuzzyValue fireRule(FuzzyValue[] inputs) throws Exception {
        // Borra todas las entradas de la regla.
        removeAllInputs();
        // Añade las nuevas entradas.
        for (int i=0;i<inputs.length;i++)
            addInput(inputs[i]);


        // Ejecuta la regla si las entradas "encajan".
        if (testRuleMatching()){
            FuzzyValue aux =  new FuzzyValue(execute().fuzzyValueAt(0));
            try{
                aux.weightedAverageDefuzzify();
                return aux;
            }
            catch(Exception e){
                return null;
            }
        }
        // Si las entradas no "encajan" devuelve un null.
        else
            return null;
    }

    private void buildAntecedentFromCode(Database database, int[] code) throws Exception {
        // Primero mira si el vector es correcto
        if (database.getNVars() !=code.length){
            throw new Exception("No se puede crear la regla porque el número de variables del vector no es correcto ");
        }
        // Copia la clave y añade los antecedentes y consecuentes, que generalmente será uno.
        int nVar;
        this.antecedente = new int[code.length-1];
        for (nVar=0; nVar < code.length-1;nVar++){
            this.antecedente[nVar]=code[nVar];
            addAntecedent(database.getValue(nVar, code[nVar]));
        }
        //Ahora el consecuente
        consecuente = code[nVar];
        addConclusion(database.getValue(nVar, code[nVar]));

        representacionAntecedente = antecedentRepresentation();
    }

    private String antecedentRepresentation(){
        String s = ""+antecedente[0];
        for(int i=1;i<antecedente.length;i++)
            s += ","+antecedente[i];
        return s;
    }

    public String toString(){
        String rule = "";
        int nVar;
        rule+="[";
        for (nVar=0; nVar < antecedente.length-1;nVar++){
            rule+= Integer.toString(antecedente[nVar]);
            rule+=",";
        }
        rule+= Integer.toString(antecedente[nVar++]);
        rule+=" => "+consecuente+" ]";
        return rule;
    }

}
