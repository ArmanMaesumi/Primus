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

    public Map<String, BigDecimal> constants = new HashMap<String, BigDecimal>(){{
        put("pi", new BigDecimal("3.14159265358979323846264338327950288419716939937510582097494459230781640628620899862803482534211706798214808651328230664709384460955058223172535940812848111745028410270193852110555964462294895493038196442881097566593344612847564823378678316527120190914564856692346034861045432664821339360726024914127372458700660631558817488152092096282925409171536436789259036001133053054882046652138414695194151160943305727036575959195309218611738193261179310511854807446237996274956735188575272489122793818301194912983367336244065664308602139494639522473719070217986094370277053921717629317675238467481846766940513200056812714526356082778577134275778960917363717872146844090122495343014654958537105079227968925892354201995611212902196086403441815981362977477130996051870"));
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


    public void populateDefaultDefinitions(){
        for (Map.Entry<String, BigDecimal> entry : constants.entrySet()) {
            defineVariable(entry.getKey(), entry.getValue().toPlainString());
        }
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
