package logic;

import console.Database;
import console.Settings;
import objects.Function;
import objects.Method;
import objects.PrimusObject;
import objects.Variable;
import org.nevec.rjm.BigDecimalMath;
import org.nevec.rjm.Factorial;
import org.nevec.rjm.Prime;
import utils.PrimusUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Random;

public class Parser {

    // Heavily inspired by StackOverflow user Boann:
    public static BigDecimal eval(final String str) {
        return new Object() {
            int pos = -1, ch;
            Random rand = new Random();
            Prime prime = new Prime();
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

            void eatArguments(){
                int parentheses = 1;
                while (parentheses > 0) {
                    nextChar();
                    if (ch == '(')
                        parentheses++;
                    else if (ch == ')')
                        parentheses--;
                }
            }

            BigDecimal parse() {
                System.out.println("Original input: " + str);
                nextChar();
                BigDecimal x = parseLogic();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            BigDecimal parseLogic() {
                BigDecimal x = parseExpression();
                for (; ; ) {
                    int startPos = this.pos;
                    if (ch == '>' || ch == '<' || ch == '=' || ch == '!') {
                        while (ch == '>' || ch == '<' || ch == '=' || ch == '!') nextChar();
                        String operator = str.substring(startPos, this.pos);
                        if (operator.equals(">")) {
                            int res = x.compareTo(parseExpression());
                            x = res > 0 ? new BigDecimal("1") : new BigDecimal("0");
                        } else if (operator.equals("<")) {
                            int res = x.compareTo(parseExpression());
                            x = res < 0 ? new BigDecimal("1") : new BigDecimal("0");
                        } else if (operator.equals("==")) {
                            int res = x.compareTo(parseExpression());
                            x = res == 0 ? new BigDecimal("1") : new BigDecimal("0");
                        } else if (operator.equals(">=")) {
                            BigDecimal temp = parseExpression();
                            if (x.compareTo(temp) > 0 || x.compareTo(temp) == 0)
                                x = new BigDecimal("1");
                            else
                                x = new BigDecimal("0");
                        } else if (operator.equals("<=")) {
                            BigDecimal temp = parseExpression();
                            if (x.compareTo(temp) < 0 || x.compareTo(temp) == 0)
                                x = new BigDecimal("1");
                            else
                                x = new BigDecimal("0");
                        } else if (operator.equals("!=")) {
                            int res = x.compareTo(parseExpression());
                            x = res != 0 ? new BigDecimal("1") : new BigDecimal("0");
                        } else {
                            return x;
                        }
                    }
                    return x;
                }
            }

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
                    else if (eat('%')) x = x.remainder(parseFactor(), mc); //modulus
                    else return x;
                }
            }

            BigDecimal parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return parseFactor().negate(); // unary minus

                BigDecimal x = new BigDecimal("0", mc);
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseLogic();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = new BigDecimal(str.substring(startPos, this.pos));
                } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) { // functions
                    while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (db.isPrimusObject(func)) {
                        PrimusObject obj = db.getPrimusObjectById(func);
                        if (obj instanceof Variable) {
                            x = new BigDecimal(db.getValueOfObjectById(func));
                        } else if (obj.getClass() == Function.class) {
                            startPos = this.pos;
                            eatArguments();
                            if (func.equals("eval")) {
                                obj.setValue(str.substring(startPos + 1, this.pos));
                                x = ((Function) obj).eval(new String[1]);
                            } else {
                                String arg = "(" + str.substring(startPos + 1, this.pos) + ")";
                                String[] argArr = PrimusUtils.getFunctionArgs2(arg);
                                String[] argValues = new String[argArr.length];

                                for (int i = 0; i < argArr.length; i++) {
                                    argValues[i] = Parser.eval(argArr[i]).toString();
                                }

                                x = ((Function) obj).eval(argValues);
                            }
                            eat(')');
                        } else if (obj.getClass() == Method.class){
                            startPos = this.pos;
                            eatArguments();

                            String arg = "(" + str.substring(startPos + 1, this.pos) + ")";
                            String[] argArr = PrimusUtils.getFunctionArgs2(arg);
                            String methodRet = ((Method) obj).runMethod(argArr);
                            x = new BigDecimal(methodRet);

                            eat(')');
                        }
                    } else {
                        x = parseFactor();
                        if (func.equals("sqrt")) x = BigDecimalMath.sqrt(x, mc);
                        else if (func.equals("sin")) x = new BigDecimal(Math.sin(x.doubleValue()), mc);
                        else if (func.equals("cos")) x = new BigDecimal(Math.cos(x.doubleValue()), mc);
                        else if (func.equals("tan")) x = new BigDecimal(Math.tan(x.doubleValue()), mc);
                        else if (func.equals("ln")) x = new BigDecimal(Math.log(x.doubleValue()), mc);
                        else if (func.equals("log")) x = new BigDecimal(Math.log10(x.doubleValue()), mc);
                        else if (func.equals("abs")) x = x.abs();
                        else if (func.equals("fact") || func.equals("factorial")) ;
                        else if (func.equals("isPrime"))
                            x = prime.contains(x.toBigInteger()) ? new BigDecimal("1") : new BigDecimal("0");
                        else if (func.equals("random")) x = new BigDecimal(rand.nextInt(x.intValueExact()));
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

