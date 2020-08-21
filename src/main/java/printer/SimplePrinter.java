package printer;

public class SimplePrinter implements Printer {
    @Override
    public void print(String text) {
        System.out.println(text);
    }
}
