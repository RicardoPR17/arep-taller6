package co.edu.escuelaing.aws;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class AppRoundRobin {

    private static final String[] LOG_SERVICE_URL = { "http://logs-1:5000/logfacade?message=",
            "http://logs-2:5000/logfacade?message=", "http://logs-3:5000/logfacade?message=" };

    public static void main(String[] args) {
        RemoteLogServiceInvoker invoke = new RemoteLogServiceInvoker(LOG_SERVICE_URL);
        port(getPort());
        staticFiles.location("/public");
        get("/logservicefacade", (req, res) -> {
            String message = req.queryParams("msg");
            res.type("application/json");
            return invoke.invoke(message);
        });
    }

    /**
     * Give the port for the server
     * 
     * @return If the PORT enviroment variable is define, return his value.
     *         Otherwise, 5000
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
