package projectMathServer;

import projectMathServer.Server.ComputeServer;

import java.io.IOException;

public class TestServerMain {
    /**
     * maximum number of thread is fix to the maximum number of thread that our computer can generate
     */
    public final static int MAXIMUM_NUMBER = Runtime.getRuntime().availableProcessors();

    /**
     * @param args insert port number
     */
    public static void main(String[] args) {
        //get the port number, given with the first input parameter
        int portNumber = 0;
        switch (args.length) {
            case 0:
                System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] The port number has to be "
                        + " given as a parameter.%n", System.currentTimeMillis());
                System.exit(0);
                break;

            case 1:
                try {
                    portNumber = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] Impossible to parse the port "
                                    + " number '%2$s' you have chosen.%n", System.currentTimeMillis(),
                            args[0]);
                    System.exit(0);
                }
                break;
            default:
                System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] The number of parameters is "
                                + " not valid. Please provide only the port number.%n",
                        System.currentTimeMillis());
                System.exit(0);
                break;
        }

        System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] Starting of the server, on the "
                        + "chosen port number %2$d.%n", System.currentTimeMillis(),
                portNumber);
        ComputeServer compServer = new ComputeServer(portNumber, MAXIMUM_NUMBER);

        try {
            compServer.run();
        } catch (IOException ex) {
            System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] Error in starting the server: "
                    + "%2$s .%n", System.currentTimeMillis(), ex.getMessage());
        }
    }
}
