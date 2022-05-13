package projectMathServer.Server;

import projectMathServer.Expression.Computator;
import projectMathServer.Exception.VariableValuesNotEqualSizeException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static projectMathServer.Server.ComputeServerUtils.computationRequestToComputator;
import static projectMathServer.Server.StatComputeServerUtils.isValidStatRequest;


public class ComputeServer {
    private final int port;
    private final String QUIT_COMMAND = "BYE";
    private int numberOfOkResponse;
    private List<Double> timesResponseMillisecond;
    private final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private final ExecutorService commandExecutor;

    /**
     * ComputeServer constructor
     *
     * @param port         port where the server process waiting for connection requests
     * @param maxNumThread maximun number of thread
     */
    public ComputeServer(int port, int maxNumThread) {
        this.port = port;
        this.numberOfOkResponse = 0;
        this.timesResponseMillisecond = new ArrayList<>();
        this.commandExecutor = Executors.newFixedThreadPool(maxNumThread);
    }

    public int getPort() {
        return port;
    }

    public List<Double> getTimesResponseMillisecond() {
        return timesResponseMillisecond;
    }

    public int getNumberOfOkResponse() {
        return numberOfOkResponse;
    }

    public String getQuitCommand() {
        return QUIT_COMMAND;
    }

    /**
     * This method starts the robust server.
     * Once that the connection to a client will be accepted creates a thread pool that will perform
     * processing on the request.
     *
     * @throws IOException
     */
    public void run() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    final Socket socket = serverSocket.accept();
                    System.out.printf("[%1$tY-%1$tm-%1$td %1$tT] Connection from %2$s.%n",
                            System.currentTimeMillis(),
                            socket.getInetAddress());
                    EXECUTOR_SERVICE.submit(() -> {
                        try (socket) {
                            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            while (true) {
                                String request = br.readLine();
                                System.out.println(request);
                                if (request == null) {
                                    System.out.printf("[%1$tY-%1$tm-%1$td %1$tT] Client %2$s abruptly closed connection.%n",
                                            System.currentTimeMillis(),
                                            socket.getInetAddress());
                                    break;
                                }
                                if (request.equals(QUIT_COMMAND)) {
                                    System.out.printf("[%1$tY-%1$tm-%1$td %1$tT] Client %2$s closed connection.%n",
                                            System.currentTimeMillis(),
                                            socket.getInetAddress());
                                    break;
                                } else if (isValidStatRequest(request)) {
                                    //if is stat request
                                    Instant timeStart = Instant.now();
                                    String statResponse = StatComputeServerUtils.statResponse(request, this);
                                    Instant timeStop = Instant.now();
                                    double timeTot = (double) Duration.between(timeStart, timeStop).toMillis();
                                    bw.write("OK;" +
                                            String.format(Locale.US, "%1.3f", timeTot / 1000) +
                                            ";" + statResponse +
                                            System.lineSeparator());
                                    bw.flush();
                                    timesResponseMillisecond.add(timeTot);
                                    numberOfOkResponse++;
                                } else {
                                    //if is not a stat request is a computation request
                                    try {
                                        Instant timeStart = Instant.now();
                                        Computator requestComputator = computationRequestToComputator(request);
                                        Future<Double> futureResult = commandExecutor.submit(() -> {
                                            Double asyncronousComputationResult = Double.NaN;
                                            try {
                                                //return Double.NaN if something wrong
                                                asyncronousComputationResult = requestComputator.compute();
                                            } catch (IllegalArgumentException | VariableValuesNotEqualSizeException e) {
                                                System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] Execution error for %2$s: %3$s.%n",
                                                        System.currentTimeMillis(),
                                                        socket.getInetAddress(),
                                                        e.getMessage());
                                                bw.write("ERR;" + e.getMessage() + System.lineSeparator());
                                                bw.flush();
                                            }
                                            return asyncronousComputationResult;
                                        });

                                        Double result = futureResult.get();
                                        if (!result.equals(Double.NaN)) {
                                            String resultRequest = String.format(Locale.US, "%1.5f", result);
                                            Instant timeStop = Instant.now();
                                            double timeTot = (double) Duration.between(timeStart, timeStop).toMillis();
                                            bw.write("OK;" +
                                                    String.format(Locale.US, "%1.3f", timeTot / 1000) +
                                                    ";" + resultRequest +
                                                    System.lineSeparator());
                                            bw.flush();
                                            numberOfOkResponse++;
                                            timesResponseMillisecond.add(timeTot);
                                        }
                                    } catch (IllegalArgumentException ex) {
                                        System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] Execution error for %2$s: %3$s.%n",
                                                System.currentTimeMillis(),
                                                socket.getInetAddress(),
                                                ex.getMessage());
                                        bw.write("ERR;" + ex.getMessage() + System.lineSeparator());
                                        bw.flush();
                                    } catch (InterruptedException ex) {
                                        System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] Execution error for %2$s: %3$s.%n",
                                                System.currentTimeMillis(),
                                                socket.getInetAddress(),
                                                ex.getMessage());
                                    } catch (ExecutionException ex) {
                                        System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] Execution error for %2$s: %3$s.%n",
                                                System.currentTimeMillis(),
                                                socket.getInetAddress(),
                                                ex.getMessage());
                                        bw.write("ERR;" +
                                                "the requested computation has been rejected- maybe it was too heavy" +
                                                System.lineSeparator());
                                        bw.flush();
                                    }
                                }
                            }
                        } catch (IOException e) {
                            System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] IO error for %2$s: %3$s.%n",
                                    System.currentTimeMillis(),
                                    socket.getInetAddress(),
                                    e.getMessage());
                        }
                    });
                } catch (IOException e) {
                    System.err.printf("[%1$tY-%1$tm-%1$td %1$tT] Cannot accept connection due to %2$s.%n",
                            System.currentTimeMillis(),
                            e.getMessage());
                }
            }
        } finally {
            commandExecutor.shutdown();
            EXECUTOR_SERVICE.shutdown();
        }
    }

}
