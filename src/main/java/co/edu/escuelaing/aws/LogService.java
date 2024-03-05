package co.edu.escuelaing.aws;

import static spark.Spark.get;
import static spark.Spark.port;

public class LogService {
    public static void main(String... args) {
        port(getPort());
        get("hello", (req, res) -> "Hello Docker!");
        get("/logfacade", (req, res) -> "{\"msg\": \"test\", \"date\": \"20-02-2024\"}");
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
        return 5000;
    }
}
