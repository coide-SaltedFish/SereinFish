package sereinfish.bot.entity.sf.msg.code.SFMsgCodeImp;

import sereinfish.bot.entity.sf.msg.code.SFMsgCode;
import sereinfish.bot.entity.sf.msg.code.SFMsgCodeContact;
import sereinfish.bot.entity.sf.msg.code.annotation.SFMsgCodeInfo;
import sereinfish.bot.entity.sf.msg.code.entity.Parameter;
import sereinfish.bot.utils.math.Calculator;

import java.util.Map;
import java.util.Stack;

@SFMsgCodeInfo("math")
public class SFMath implements SFMsgCode {
    @Override
    public String error(Exception e) {
        return null;
    }

    @Override
    public String code(SFMsgCodeContact codeContact, Parameter parameter) throws Exception {
        String para = parameter.getString(0);

        //变量替换
        for (Map.Entry<String, Object> entry:codeContact.getMap().entrySet()){
            para = para.replace(entry.getKey(), entry.getValue() + "");
        }

        double result = Calculator.conversion(para);;    //对格式化之后的等式进行计算
        if (result == (int) result){
            return String.format("%d", (int) result);
        }
        return String.format("%.2f", result);
    }

    private static int EvulateExpression(String expression) {
        Stack<Character> opertorStack = new Stack<>();    //操作符栈 用来存放操作符
        Stack<Integer> numberStack = new Stack<>();       //数字栈 存放数字

        String[] tokens = expression.split(" ");              //用" "对格式化之后的表达式进行切割
        for (String token : tokens) {                               //遍历token
            if (token.length() == 0) {
                continue;
            } else if (token.charAt(0) == '+' || token.charAt(0) == '-') {    //当遇到加减时
                while (!opertorStack.isEmpty() && (opertorStack.peek() == '+' || opertorStack.peek() == '-'
                        || opertorStack.peek() == '*'
                        || opertorStack.peek() == '/')) {
                    processAnOperator(opertorStack, numberStack);
                }
                opertorStack.push(token.charAt(0));
            } else if (token.charAt(0) == '*' || token.charAt(0) == '/') {     //当遇到乘除
                while (!opertorStack.isEmpty() && (opertorStack.peek() == '*' || opertorStack.peek() == '/')) {
                    processAnOperator(opertorStack, numberStack);
                }
                opertorStack.push(token.charAt(0));
            } else if (token.charAt(0) == '(') {                               //左括号
                opertorStack.push(token.charAt(0));
            } else if (token.charAt(0) == ')') {                               //右括号
                while (opertorStack.peek() != '(') {
                    processAnOperator(opertorStack, numberStack);
                }
                opertorStack.pop();
            } else {
                numberStack.push(new Integer(token));
            }
        } while(!opertorStack.isEmpty()){                                     //确保数字栈空
            processAnOperator(opertorStack, numberStack);
        }
        return numberStack.pop();
    }

    private static void processAnOperator(Stack<Character> opertorStack, Stack<Integer> numberStack) {
        //这个方法用来计算
        int num1 = numberStack.pop();
        int num2 = numberStack.pop();
        char c = opertorStack.pop();
        switch (c) {
            case '+':
                numberStack.push(num2 + num1);
                break;
            case '-':
                numberStack.push(num2 - num1);
                break;
            case '*':
                numberStack.push(num2 * num1);
                break;
            case '/':
                numberStack.push(num2 / num1);
                break;

        }
    }

    private static String FormatExpreesion(String expression) {
        //用来格式化表达式
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(' || c == ')' || c == '+' || c == '-' || c == '*' || c == '/') {
                sb.append(' ');
                sb.append(c);
                sb.append(' ');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
