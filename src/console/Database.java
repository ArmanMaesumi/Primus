package console;

import logic.Parser;
import objects.Function;
import objects.PrimusObject;
import objects.TemporaryVariable;
import objects.Variable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Database {

    private static Database db;
    private ArrayList<PrimusObject> defs = new ArrayList<>();

    private boolean init;

    private static final Map<String, BigDecimal> constants = new HashMap<String, BigDecimal>(){{
        put("pi", new BigDecimal("3.1415926535897932384626433832795028841971693993751058209749"));
        put("e", new BigDecimal("2.718281828459045235360287471352662497757247093699959574966"));
    }};

    private Database(){

    }

    public static Database getDatabase(){
        if (db == null)
            db = new Database();
        return db;
    }


    public ArrayList<PrimusObject> getDefs() {
        return defs;
    }


    public void resetDatabase(){
        defs.clear();
        init = false;
        populateDefaultDefinitions();
    }


    public void populateDefaultDefinitions(){
        if (init)
            return;

        for (Map.Entry<String, BigDecimal> entry : constants.entrySet()) {
            defineVariable(entry.getKey(), entry.getValue().toPlainString());
        }
        init = true;
    }


    public PrimusObject getPrimusObjectById(String id){
        for (PrimusObject o : defs){
            if (o.getId().equals(id))
                return o;
        }
        return null;
    }


    public String getValueOfObjectById(String id){
        PrimusObject o = getPrimusObjectById(id);
        if (o != null)
            return o.getValue();
        throw new IllegalArgumentException("PrimusObject not found: " + id);
    }


    public boolean isPrimusObject(String id){
        PrimusObject o = getPrimusObjectById(id);
        return o != null;
    }


    public void removePrimusObjectById(String id){
        PrimusObject o = getPrimusObjectById(id);
        if (o != null)
            defs.remove(o);
    }


    public int getIndexOfPrimusObjectById(String id){
        for (int i = 0; i < defs.size(); i++) {
            if (defs.get(i).getId().equals(id))
                return i;
        }
        return -1;
    }


    public void removeExpiredTemporaryVariables(){
        Iterator<PrimusObject> i = defs.iterator();
        while (i.hasNext()){
            PrimusObject o = i.next();
            if (o.getClass() == TemporaryVariable.class){
                if (((TemporaryVariable) o).isExpired())
                    defs.remove(o);
            }
        }
    }


    public void defineVariable(String id, String val) {
        // Compute new value before deleting old value.
        // Reason: defVar x = x + 1
        BigDecimal newVal = Parser.eval(val);
        removePrimusObjectById(id);
        defs.add(new Variable(id, newVal));
    }


    public void defineFunction(String id, String[] args, String exp) {
        removePrimusObjectById(id);
        defs.add(new Function(id, args, exp));
    }


    public void defineTemporaryVariable(String id, String val) {
        // Compute new value before deleting old value.
        // Reason: defVar x = x + 1
        BigDecimal newVal = Parser.eval(val);
        removePrimusObjectById(id);
        defs.add(new TemporaryVariable(id, newVal));
    }


    public void addAllPrimusObjects(ArrayList<PrimusObject> coll){
        for (PrimusObject o : coll){
            removePrimusObjectById(o.getId());
            defs.add(o);
        }
    }


    public void removeAllPrimusObjects(ArrayList<PrimusObject> coll){
        for (PrimusObject o : coll){
            removePrimusObjectById(o.getId());
        }
    }
//    public void replacePrimusObjectById(String id, PrimusObject o){
//        int index = getIndexOfPrimusObjectById(id);
//        if (index > -1)
//            defs.set(index, );
//    }
}
