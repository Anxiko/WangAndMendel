package frbs;

import nrc.fuzzy.FuzzyValue;
import nrc.fuzzy.SingletonFuzzySet;

/**
 * Created by jcozar on 10/02/16.
 */
public class SingletonFuzzification {
    //Fuzzificación mediante conjuntos singleton. Si nos salimos del dominio de la variable,
    //fuzzificamos utilizando el máximo o el mínimo valor del dominio.
    public FuzzyValue[] fuzzify(double[] instance, Database database) throws Exception{
        FuzzyValue[] fuzzyInstance = new FuzzyValue[instance.length];
        for (int i = 0; i < fuzzyInstance.length; i++) {
            try{
                fuzzyInstance[i] = new FuzzyValue(database.varAt(i), new SingletonFuzzySet(instance[i]));
            }
            catch (Exception e){
                FuzzyVariable var = database.varAt(i);
                if (instance[i]>var.getMaxUOD()){
                    fuzzyInstance[i] = new FuzzyValue(var, new SingletonFuzzySet(var.getMaxUOD()));
                }
                else
                    fuzzyInstance[i] = new FuzzyValue(var, new SingletonFuzzySet(var.getMinUOD()));
            }
        }
        return fuzzyInstance;
    }
}
