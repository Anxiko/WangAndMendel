package learning;

import core.Dataset;
import frbs.FRBS;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WAM {
    private FRBS frbs;

    public WAM(Dataset ds, int numLabels){
        //Creamos el objeto FRBS indicando si el problema es clasificación o regresión
        frbs = new FRBS(ds.isClassification());
        //Generamos la base de datos
        generateDatabase(ds,numLabels);
        //Generamos la base de reglas
        generateRulebase(ds);
    }

    //Función para generar la base de datos en base al dataset.
    //Se generarán numLabels conjuntos triangulares distribuidos equiespacialmente.
    //En los extremos, se usarán triángulos abiertos (la mitad del mismo caerá fuera del dominio de la variable).
    protected void generateDatabase(Dataset ds, int numLabels){
        frbs.generateTriangularDB(ds,numLabels);
    }

    protected void generateRulebase(Dataset ds) {
        //Para cada ejemplo, generamos una regla candidata.
        for(int i=0;i<ds.size();i++)
        {
            //Vemos la regla generada por este ejemplo. Para ello trabajamos con índices. Por ejemplo:
            //[1,2,1,0] representa la regla con la etiqueta 1 para la variable 0, etiqueta 2 para la variable 1, y así sucesivamente.
            int code[] = new int[frbs.db.getNVars()];
            double instance[] = ds.getInstanceAsArray(i);
            double importanceDegree = 1.0;

            for(int nVar=0;nVar<instance.length;nVar++){
                //Obtenemos el código (array de índices de etiquetas) que maximiza el grado de pertenencia.
                //En "importanceDegree" almacenamos el grado de importancia de esta regla con esta instancia.
                //...
                //Nota: Hacer uso de la función "highestMembershipIndex" de la clase FuzzyVariable. La variable nVar
                //es accesible mediante "frbs.db.varAt(nVar)"
                int index=frbs.db.varAt(nVar).highestMembershipIndex(instance[nVar]);//Para la variable nVar, obtengo la etiqueta con el meyor grado de pertenencia
                importanceDegree*=frbs.db.varAt(nVar).getSet(index).getMembership(instance[nVar]);//El valor de importancia de la regla es la multiplicación de los valores de pertenencia de los valores de los ejemplos a los conjuntos difusos
                code[nVar]=index;//Se guarda el índice de la etiqueta con mayor grado de pertenencia
            }
            //Añadimos la regla a la base de reglas. En esta función se determinará si se añade o se desecha en base a importanceDegree
            //y a las reglas almacenadas en la base de reglas (frbs.rb)
            //...
            try {
                frbs.rb.addRule(code, frbs.db, importanceDegree);
                
                //Nota: hacer uso de la función "addRule" de la clase Rulebase. La base de reglas se puede acceder mediante "frbs.rb".
            } catch (Exception ex) {
                System.out.println("ERROR: al añadir la regla "+i);
            }
            
        }
    }

    public FRBS getFRBS(){
        return frbs;
    }
}
