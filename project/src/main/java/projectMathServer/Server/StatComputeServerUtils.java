package projectMathServer.Server;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class StatComputeServerUtils {
    public enum StatRequest {
        STAT_REQS,
        STAT_AVG_TIME,
        STAT_MAX_TIME
    }

    /**
     * @param request client request
     * @param server  ComputeServer object which is used
     * @return String that contains the the number of ok response
     * or the max/avg time of all ok responses (excluding this request) served by the ComputeServer
     * to all clients since it started
     */
    public static String statResponse(String request, ComputeServer server) {
        StatRequest statRequestKind = StatRequest.valueOf(request);
        List<Double> timeMillisecondResponse = server.getTimesResponseMillisecond();
        String response = "";
        switch (statRequestKind) {
            case STAT_REQS:
                response = Integer.toString(server.getNumberOfOkResponse());
                break;
            case STAT_MAX_TIME:
                if (timeMillisecondResponse.isEmpty()) {
                    response = "0";
                } else {
                    response = String.format(Locale.US, "%1.3f", (Collections.max(timeMillisecondResponse) / 1000));
                }
                break;
            case STAT_AVG_TIME:
                response = String.format(Locale.US, "%1.3f",
                        timeMillisecondResponse.stream().mapToDouble(Double::doubleValue).average().orElse(0) / 1000);
                break;
        }
        return response;
    }

    public static boolean isValidStatRequest(String request) {
        String patternStatRequest = "STAT_REQS|STAT_AVG_TIME|STAT_MAX_TIME";
        return isMatchingPattern(patternStatRequest, request);
    }

    public static boolean isMatchingPattern(final String regex, final String input) {
        return Pattern.compile(regex).matcher(input).matches();
    }


}
