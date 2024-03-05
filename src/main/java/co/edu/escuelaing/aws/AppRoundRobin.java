package co.edu.escuelaing.aws;

import static spark.Spark.get;
import static spark.Spark.staticFiles;

public class AppRoundRobin {

    private static final String LOG_SERVICE_URL = "http://localhost:5000/logfacade";
    public static void main(String[] args) {
        RemoteLogServiceInvoker invoke = new RemoteLogServiceInvoker(LOG_SERVICE_URL);
        staticFiles.location("/public");
        get("/logservicefacade", (req, res) -> {
            res.type("application/json");
            return invoke.invoke(args);
        });
    }
}
