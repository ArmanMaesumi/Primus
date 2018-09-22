package logic;

import console.Database;
import objects.Function;
import objects.PrimusObject;
import objects.Variable;
import utils.PrimusUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

public class Parser {

    // Heavily inspired by StackOverflow user Boann:
    public static BigDecimal eval(final String str) {
        return new Object() {
            int pos = -1, ch;
            Database db = Database.getDatabase();
            MathContext mc = new MathContext(15, RoundingMode.HALF_UP);
            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            BigDecimal parse() {
                System.out.println("Original input: " + str);
                nextChar();
                BigDecimal x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            BigDecimal parseExpression() {
                BigDecimal x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x = x.add(parseTerm(), mc); // addition
                    else if (eat('-')) x = x.subtract(parseTerm()); // subtraction
                    else return x;
                }
            }

            BigDecimal parseTerm() {
                BigDecimal x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x = x.multiply(parseFactor(), mc); // multiplication
                    else if (eat('/')) x = x.divide(parseFactor(), mc); // division
                    else return x;
                }
            }

            BigDecimal parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return parseFactor().negate(); // unary minus

                BigDecimal x = new BigDecimal("0", mc);
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = new BigDecimal(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    System.out.println("Func:" + func);
                    System.out.println("db:" + db.getDefs().toString());
                    if (db.isPrimusObject(func)) {
                        System.out.println(func + " is a primus object");
                        PrimusObject obj = db.getPrimusObjectById(func);
                        if (obj instanceof Variable) {
                            System.out.println(func + " is a primus variable");
                            x = new BigDecimal(db.getValueOfObjectById(func));
                        } else if (obj.getClass() == Function.class) {
                            startPos = this.pos;
                            int parentheses = 1;
                            while(parentheses > 0){
                                nextChar();
                                if (ch == '(')
                                    parentheses++;
                                else if (ch == ')')
                                    parentheses--;
                            }
                            //while (ch != ')') nextChar();
                            String arg = "("+str.substring(startPos + 1, this.pos)+")";
                            String[] argArr = PrimusUtils.getFunctionArgs2(arg);
                            System.out.println("args:" + Arrays.toString(argArr));
                            String[] argValues = new String[argArr.length];
                            for (int i = 0; i < argArr.length; i++) {
                                argValues[i] = Parser.eval(argArr[i]).toString();
                            }
                            System.out.println("ARG Values " + Arrays.toString(argValues));
                            x = ((Function) obj).eval(argValues);
                            eat(')');
                        }
                    } else {
                        x = parseFactor();
                        if (func.equals("sqrt")) x = BigDecimalMath.sqrt(x, mc);
                        else if (func.equals("sin")) x = BigDecimalMath.sin(x);
                        else if (func.equals("cos")) x = BigDecimalMath.cos(x);
                        else if (func.equals("tan")) x = BigDecimalMath.tan(x);
                        else throw new RuntimeException("Unknown function: " + func);
                    }
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^'))
                    x = BigFunctions.exp(BigFunctions.ln(x, 10).multiply(parseFactor()), 10); // exponentiation

                return x;
            }
        }.parse();
    }
}

