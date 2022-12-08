import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Main {
    final static String operands = "[+*-/]"; // condition 1: only +, -, * and / operands
    final static String romanDigits = "(V?I{0,3}|I[VX])"; // condition 3: input no more than 10 so cut regex to I,V,X
    final static String arabicDigits = "\\d"; // regex for Arabic digits


    public static void main(String[] args) throws Exception{
        String input;
        boolean isRomanB;
        boolean isArabicB;
        boolean stillWorking = true;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in)) ){
            while (stillWorking) {
                input = bufferedReader.readLine();
                input = input.replaceAll("\\s+","");
                String[] numbers;
                int[] numbersInt = new int[2];
                String operand = matcherCheck(operands,input);
                int resultInt;
                String resultString;
                isRomanB = isRoman(input);
                isArabicB = isArabic(input);

                if ( (isRomanB && isArabicB)  // condition 5: only Arabic or only Roman
                        ) throw new Exception("Both arabic and roman");// condition 8
                if (operand.length()!=1) throw new Exception("Condition 8 - wrong format");// condition 8
                numbers = input.split(operands);


                if (isRomanB) {
                    numbersInt[0] = romanToArabic(numbers[0]);
                    numbersInt[1] = romanToArabic(numbers[1]);
                } else {
                    try {
                        numbersInt[0] = Integer.parseInt(numbers[0]);
                        numbersInt[1] = Integer.parseInt(numbers[1]);
                    }catch (NumberFormatException|ArrayIndexOutOfBoundsException e)
                    {
                        throw new Exception("While parsing"); // condition 10
                    }
                }

                if ( (numbersInt[0] <0) || (numbersInt[1] < 0) ||
                        (numbersInt[0] >10) || (numbersInt[1] > 10) ) // condition 3
                        throw new Exception("Input out of bounds");

                resultInt = calc(numbersInt, operand); // main method

                if ( isRomanB ) {
                    if (resultInt<0) throw new Exception("Negative roman result"); // condition 10: no negative Roman
                    else resultString = arabicToRoman(resultInt);
                }else { // is Arabic
                    resultString = resultInt + "";
                }
                System.out.println(resultString);
            }
        }
    }
    public static String matcherCheck(String regex, String text){
        StringBuilder out = new StringBuilder();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            out.append(new StringBuilder(text.substring(matcher.start(), matcher.end())) );
        }
        return out.toString();
    }

    public static boolean isRoman(String string){
        return !matcherCheck(romanDigits,string).equals("");
    }

    public static boolean isArabic(String string){
        return !matcherCheck(arabicDigits,string).equals("");
    }

    public static int calc(int[] numbers,  String operand) throws Exception{
        int result;
        switch (operand) {
            case "+":
                result = numbers[0] + numbers[1];
                break;
            case "*":
                result = numbers[0] * numbers[1];
            break;
            case "-":
                result = numbers[0] - numbers[1];
            break;
            case "/":
                result = numbers[0] / numbers[1];
            break;
            default:
                throw new Exception("From calc"); // condition 8
        }
        return result;
    }

    enum RomanNumeral {
        I(1), IV(4), V(5), IX(9), X(10),XL(40), L(50), XC(90), C(100),
        CD(400), D(500), CM(900), M(1000);

        private int value;

        RomanNumeral(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static List<RomanNumeral> getReverseSortedValues() {
            return Arrays.stream(values())
                    .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
                    .collect(Collectors.toList());
        }
    }

    public static int romanToArabic(String input) throws Exception {
        String romanNumeral = input.toUpperCase();
        int result = 0;

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;

        while ((romanNumeral.length() > 0) && (i < romanNumerals.size())) {
            RomanNumeral symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name())) {
                result += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            } else {
                i++;
            }
        }

        if (romanNumeral.length() > 0) {
            throw new Exception(input + " cannot be converted to a Roman Numeral");
        }

        return result;
    }

    public static String arabicToRoman(int number) throws Exception {
        if ((number <= 0) || (number > 4000)) {
            throw new Exception(number + " is not in range (0,4000]");
        }

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((number > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= number) {
                sb.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            } else {
                i++;
            }
        }

        return sb.toString();
    }
}
