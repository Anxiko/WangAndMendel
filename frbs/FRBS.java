package frbs;


import core.Dataset;
import nrc.fuzzy.FuzzyValue;
import java.util.ArrayList;

public class FRBS {

    //El FRBS se compone de una interfaz de fuzzificacion, una Database y una Rulebase
    public SingletonFuzzification fuzzification;
    public Database db;
    public Rulebase rb;
    //Esta variable se utiliza para saber si estamos en un problema de clasificación o de reggresión
    public boolean isClassification;


    public FRBS(boolean isClassification){
        //Creamos una base de reglas vacía
        rb = new Rulebase();
        //Utilizamos conjuntos singleton para la fuzzificación
        fuzzification = new SingletonFuzzification();
        this.isClassification = isClassification;
    }

    //Esta función utiliza la proporcionada por la base de datos para crear la partición basada en conjuntos triangulares
    public void generateTriangularDB(Dataset ds, int numLabels){
        db = new Database(ds, numLabels);
    }

    //Realiza una predicción. Si es un problema de clasifiación, devuelve el índice de la clase
    //Si es un problema de regresión, devuelve el valor numérico predicho.
    public double getOutput(double[] fuzzy_input){
        if(isClassification)
            return getOutputClassification(fuzzy_input);
        else
            return getOutputPrediction(fuzzy_input);
    }

    private double getOutputPrediction(double[] fuzzy_input){
        try {
            return FITA_Prediction(rb.getRules(), fuzzification.fuzzify(fuzzy_input, db));
        } catch (Exception e) {
            e.printStackTrace();
            return Double.NaN;
        }
    }

    private int getOutputClassification(double[] fuzzy_input){
        try {
            return FITA_Classification(rb.getRules(), fuzzification.fuzzify(fuzzy_input, db));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    //Salidas individuales ponderadas por los grados de importancia de las reglas
    private double FITA_Prediction(ArrayList<FuzzyRule> rules, FuzzyValue[] fuzzy_input) throws Exception {
        //Some variables for statistics
        double ruleOutputVal;
        double fireLevel;
        double globalOutput = 0;
        double sumFireLevels = 0;
        int numFiredRules=0;

        FuzzyValue ruleOutput;
        //If rules is null, no rule can be fired so it returns a NaN
        if (rules==null){
            return Double.NaN;
        }

        for (int i=0;i<rules.size();i++){
            // If any rule is null, it continues
            if (rules.get(i)==null){
                System.out.println("ERROR: Attempt to use a null object as a FuzzyRule.");
                continue;
            }

            //Inference
            ruleOutput = rules.get(i).fireRule(fuzzy_input);
            // If ruleOutput is not null, we know it as a valid output
            if (ruleOutput!=null && ruleOutput.getMaxY()>0){
                fireLevel = ruleOutput.getMaxY(); // The cut level
                ruleOutputVal = ruleOutput.momentDefuzzify(); //Defuzzify
                globalOutput+= ruleOutputVal*fireLevel;
                sumFireLevels+=fireLevel;
                numFiredRules++;
            }
        }

        // If no rules has been fired, it returns a NaN
        if (numFiredRules==0)
            return Double.NaN;
        // Else, it returns the average of all the individual
        return globalOutput/sumFireLevels;
    }

    //Implementa la decisión por el máximo grado de importancia
    private int FITA_Classification(ArrayList<FuzzyRule> rules, FuzzyValue[] fuzzy_input) throws Exception {
        //Some variables for statistics
        double fireLevel;
        double globalOutputFireLevel = 0;
        int globalOutputSet = -1;

        FuzzyValue ruleOutput;
        //If rules is null, no rule can be fired so it returns a NaN
        if (rules==null){
            return -1;
        }

        for (int i=0;i<rules.size();i++){
            // If any rule is null, it continues
            if (rules.get(i)==null){
                System.out.println("ERROR: Attempt to use a null object as a FuzzyRule.");
                continue;
            }

            //Inference
            ruleOutput = rules.get(i).fireRule(fuzzy_input);
            // If ruleOutput is not null, we know it as a valid output
            if (ruleOutput!=null && ruleOutput.getMaxY()>0){
                fireLevel = ruleOutput.getMaxY(); // The cut level
                if(fireLevel>globalOutputFireLevel)
                {
                    globalOutputFireLevel = fireLevel;
                    globalOutputSet=rules.get(i).getConsecuente();
                }
            }
        }

        return globalOutputSet;
    }

    //Método toString para imprimir un objeto FRBS
    public String toString(){
        String s = "Interfaz de fuzzificación: Singleton\n";
        s += db.toString()+"\n";
        s += rb.toString()+"\n";
        return s;
    }
}
