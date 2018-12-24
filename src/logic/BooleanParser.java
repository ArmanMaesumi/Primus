package logic;

import console.Database;
import objects.Bool;
import objects.Function;
import objects.PrimusObject;
import org.nevec.rjm.Prime;
import utils.PrimusUtils;

import java.math.BigDecimal;
import java.util.Arrays;

public class BooleanParser {

    public static boolean eval(final String str) {
        return new Object() {
            private int pos = -1, ch;
            Prime prime = new Prime();
            private Database db = Database.getDatabase();

            private void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            private boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            private boolean parse() {
                System.out.println("Original input: " + str);
                nextChar();
                boolean b = parseExpression();
                System.out.println("EXP:" + b);
                System.out.println("char:" + ch);
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return b;
            }

            private boolean parseExpression() {
                boolean b = parseTerm();
                for (; ; ) {
                    if (eat('&')) b = b & parseFactor();
                    else if (eat('|')) b = b | parseFactor();
                    else return b;
                }
            }

            private boolean parseTerm() {
                boolean b = parseFactor();
                for (; ; ) {
                    if (eat('<')) ;
                    else if (eat('>')) ;
                    else return b;
                }
            }

            private boolean parseFactor() {
                if (eat('!')) return !parseFactor(); // unary minus

                boolean b = false;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    b = parseExpression();
                    eat(')');
                } else if (eat('T')) {
                    while (ch == 'T') nextChar();
                    b = true;
                } else if (eat('F')) {
                    while (ch == 'F') nextChar();
                    b = false;
                } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) { // functions
                    while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) nextChar();
                    String func = str.substring(startPos, this.pos);
                    System.out.println("Func:" + func);
                    if (db.isPrimusObject(func)) {
                        System.out.println(func + " is a primus object");
                        PrimusObject obj = db.getPrimusObjectById(func);
                        if (obj instanceof Bool) {
                            System.out.println(func + " is a primus bool");
                            b = ((Bool) obj).getBool();
                        } else if (obj.getClass() == Function.class) {
                            startPos = this.pos;
                            int parentheses = 1;
                            while (parentheses > 0) {
                                nextChar();
                                if (ch == '(')
                                    parentheses++;
                                else if (ch == ')')
                                    parentheses--;
                            }
                            //while (ch != ')') nextChar();
                            if (func.equals("eval")) {
                                obj.setValue(str.substring(startPos + 1, this.pos));
                                BigDecimal res = ((Function) obj).eval(new String[1]);
                                b = res.compareTo(new BigDecimal("1")) == 0;
                            } else {
                                String arg = "(" + str.substring(startPos + 1, this.pos) + ")";
                                String[] argArr = PrimusUtils.getFunctionArgs2(arg);
                                System.out.println("args:" + Arrays.toString(argArr));
                                String[] argValues = new String[argArr.length];
                                for (int i = 0; i < argArr.length; i++) {
                                    argValues[i] = Parser.eval(argArr[i]).toString();
                                }
                                System.out.println("ARG Values " + Arrays.toString(argValues));
                                //b = ((Function) obj).eval(argValues);
                            }
                            eat(')');
                        }
                    } else {
                        BigDecimal eval;
                        //  if (func.equals("eval")) eval = db.eval();
                        b = parseFactor();
                        System.out.println("Factor:" + b);
                        if (func.equals("isPrime")) b = false;
                        else throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return b;
            }
        }.parse();
    }

}
