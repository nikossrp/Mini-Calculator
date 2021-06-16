import java.io.IOException;
import java.io.InputStream;

class Evaluator {
    private final InputStream in;
    private int lookahead;
    private int counter;        //count the characters in input

    public Evaluator(InputStream in) throws IOException {
        this.in = in;
        this.lookahead = in.read();
        this.counter = 0;
    }

    private void consume(int symbol) throws IOException, ParseError {
        if (lookahead == symbol) {
            lookahead = in.read();
            counter++;
        }
        else
            throw  new ParseError();
    }

    private boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }


    private boolean isRparenthesis(int c) {
        return c == '(';
    }

    private boolean isLparenthesis(int c) {
        return c == ')';
    }

    private static int pow(int base, int exponent) {
        if (exponent < 0)
            return 0;

        if (exponent == 0)
            return 1;

        if (exponent == 1)
            return base;    

        if (exponent % 2 == 0) //even exp -> b ^ exp = (b^2)^(exp/2)
            return pow(base * base, exponent/2);
        else                   //odd exp -> b ^ exp = b * (b^2)^(exp/2)
            return base * pow(base * base, exponent/2);
    }
    private boolean isAddOp(int c) {
        return (c == '+' || c == '-');
    }

    public int eval() throws IOException, ParseError {
        int value = Exp();      //the result
        if (lookahead != -1 && lookahead != '\n')
            throw new ParseError();
        return value;
    }


    private int Exp() throws IOException, ParseError {
        int symbol = 0;
        int symbol_addSub = 0;

        if (isDigit(lookahead)) {
            symbol = term();     //edw epistrefete eite o arxikos arithmos eite h praxh ths dunamis ulopoihmenh
            return exp2(symbol);
        }
        else if (isRparenthesis(lookahead)) {          //1 - > start

            //in case which at first we have '(', it is a special case in my implementation
            int specialCase = counter;
            consume('(');

            symbol = term();

            int finalSymbol = exp2(symbol);

            consume(')');

            if (specialCase == 0) {
                finalSymbol = term2(finalSymbol);
                finalSymbol = exp2(finalSymbol);
            }

            return finalSymbol;

        }

        throw  new ParseError();
    }


    private int exp2(int symbol) throws IOException, ParseError {
        int left;
        int right;

        switch (lookahead) {
            case '+':
                left = symbol;
                consume('+');
                right = term();

                return exp2(left + right);

            case '-':
                left = symbol;
                consume('-');
                right = term();

                return exp2(left - right);

            case ')':
            case -1:
            case '\n':
                return symbol;
        }

        throw new ParseError();
    }

    private int term() throws IOException, ParseError {
        int symbol;

        if (isDigit(lookahead)) {
            symbol = factor();
            return term2(symbol);
        }
        else if (isRparenthesis(lookahead)) {
            symbol = factor();

            return term2(symbol);        //open '(' should close at Exp()
        }

        throw new ParseError();
    }

    private int term2(int symbol) throws IOException, ParseError {
        int left;
        int right;

        switch (lookahead) {
            case '*':       //case "**"
                consume('*');
                consume('*');

                left = symbol;
                right = factor();

                int pow = pow(left, term2(right));
                return term2(pow);

            case '+' :
            case '-':
            case ')':
            case -1:
            case '\n':
                return symbol;
        }

        throw new ParseError();
    }


    private int factor() throws IOException, ParseError {

        if (isDigit(lookahead)) {
            String number_str = num((char) lookahead);

            //if number is not decimal
            if(number_str.charAt(0) == '0' && number_str.length() > 1)
                throw new ParseError();

            int number = Integer.parseInt(number_str);
            

            return number;
        }
        else if (isRparenthesis(lookahead)) {
            return Exp();
        }

        throw new ParseError();
    }


    private String num(char symbol) throws IOException, ParseError {

        if (isDigit(symbol))
            return rest(symbol);

        throw new ParseError();
    }

    private String rest (char symbol) throws IOException, ParseError {

        if (isDigit(symbol)) {
            return digit(symbol) + rest((char)lookahead);

        } else {
            switch (lookahead) {
                case '+':
                case '-':
                case '*':
                case ')':
                case '\n':
                case -1:
                    return "";
            }
        }

        throw new ParseError();
    }

    private String digit(char symbol) throws IOException, ParseError {

        if (isDigit(symbol)) {
            consume(lookahead);
            return String.valueOf(symbol);
        }

        throw new ParseError();
    }

}
