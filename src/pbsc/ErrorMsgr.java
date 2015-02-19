package pbsc;

public class ErrorMsgr {

    private boolean m_hasError = false;

    public boolean hasError() { return m_hasError; }

    public void error(int line, String message) {
        System.out.println("ERROR: line " + pos);
        System.out.println(message);
        m_hasError = true;
    }

    public void warning(int line, String message) {
        System.out.println("Warning: line " + pos);
        System.out.println(message);
    }

}
