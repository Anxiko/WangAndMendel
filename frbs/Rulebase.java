package frbs;

/**
 * Base de reglas. Utiliza una tabla hash para comprobar rápidamente si una regla con el mismo antecedente existe ya o
 * no, y saber en qué posición está dentro del array de reglas.
 */

import nrc.fuzzy.MamdaniMinMaxMinRuleExecutor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Rulebase {

    private ArrayList<FuzzyRule> rules;
    private Hashtable<String,Integer> rulesByAntecedent;
    private MamdaniMinMaxMinRuleExecutor executor;

    public ArrayList<FuzzyRule> getRules(){ return rules;}

    public Rulebase(){
        //Create the executor for the Fuzzy Rules to be created.
        executor = new MamdaniMinMaxMinRuleExecutor();
        //Create the fuzzy rules over the database
        rules = new ArrayList();
        //Create the set of antecedents to check if they already has been created or no.
        rulesByAntecedent = new Hashtable();
    }


    //Esta función es la encargada de añadir una nueva regla.
    //Debe comprobar si existe una con el mismo antecedente. En dicho caso, debe comparar
    //los grados de importancia, y quedarse únicamente con la regla con mayor grado de importancia.
    public void addRule(int[] code, Database db, double id) throws Exception{
        //Creo la nueva regla
        FuzzyRule newRule = new FuzzyRule(code, executor, db);

        //Establezco su grado de importancia (id)
        newRule.setImportanceDegree(id);

        //Si la regla existe en la base de reglas
        if (rulesByAntecedent.containsKey(newRule.getAntecedentRepresentation()))
        {
            //Obtengo el índice de la regla en la base de reglas
            int indice = rulesByAntecedent.get(newRule.getAntecedentRepresentation());

            //Si el grado de importancia de la nueva regla es mayor
            if (rules.get(indice).getImportanceDegree() < newRule.getImportanceDegree())
            {
                //Actualizo el valor de la regla (ocupa la posición indice en el ArrayList)
                rules.set(indice, newRule);
                //No es necesario actualizarla en el HashTable ya que son los mismos antecendentes e índice
            }
        }

        //Si la regla no existe en la base de reglas
        else
        {
            //La añado a las reglas
            rules.add(newRule);
            //Creo la entrada en la base de reglas con el antecedente de la regla como clave y el índice que ocupa en el Array como valor
            rulesByAntecedent.put(newRule.getAntecedentRepresentation(), rules.indexOf(newRule));
        }

    }



    public String toString(){
        String s = "";
        for (int i=0;i<rules.size();i++){
            s+=rules.get(i).toString()+"\n";
        }
        return s;
    }
}
