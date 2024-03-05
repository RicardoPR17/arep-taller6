package co.edu.escuelaing.aws;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class LogService {
    public static void main(String... args) {
        port(getPort());
        staticFiles.location("/public");
        get("hello", (req, res) -> "Hello Docker!");
        get("login/:msg", (req, res) -> "");
    }

    /**
     * Give the port for the server
     * 
     * @return If the PORT enviroment variable is define, return his value.
     *         Otherwise, 4567
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
