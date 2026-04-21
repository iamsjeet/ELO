package api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import model.*;
import engine.*;
import service.*;
import persistence.PersistenceManager;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {

    private static List<User> users = new ArrayList<>();
    private static List<Product> products = new ArrayList<>();

    private static EloEngine global = new EloEngine();
    private static PersonalizedEloEngine personal = new PersonalizedEloEngine();
    private static RecommendationService rec = new RecommendationService();

    public static void start() throws Exception {

        // 🔥 USERS
        for (int i = 1; i <= 5; i++) {
            users.add(new User(i, "User_" + i));
        }

        // 🔥 PRODUCTS (3 categories)
        products.add(new Product(1, "Thriller Novel", "Books"));
        products.add(new Product(2, "Mystery Book", "Books"));
        products.add(new Product(3, "Sci-Fi Book", "Books"));

        products.add(new Product(4, "Bose Headphones", "Electronics"));
        products.add(new Product(5, "PS5 Controller", "Electronics"));
        products.add(new Product(6, "Smart Speaker", "Electronics"));

        products.add(new Product(7, "Leather Jacket", "Fashion"));
        products.add(new Product(8, "Sneakers", "Fashion"));

        // 🔥 LOAD SAVED DATA
        PersistenceManager.loadGlobal(products);
        PersistenceManager.loadUsers(users);

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 🔥 GET PRODUCTS
        server.createContext("/products", (exchange) -> {
            sendResponse(exchange, getProductsJSON());
        });

        // 🔥 SELECT PRODUCT (ELO UPDATE)
        server.createContext("/select", (exchange) -> {

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, "Invalid method");
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            Map<String, String> params = parse(body);

            String userName = params.get("user");
            int productId = Integer.parseInt(params.get("id"));

            User user = users.stream()
                    .filter(u -> u.getName().equals(userName))
                    .findFirst()
                    .orElse(null);

            if (user == null) {
                sendResponse(exchange, "User not found");
                return;
            }

            //  RECOMMENDATION BASE
            List<Product> recommendations = rec.recommend(user, products);

            Product selected = products.stream()
                    .filter(p -> p.getId() == productId)
                    .findFirst()
                    .orElse(null);

            if (selected == null) {
                sendResponse(exchange, "Product not found");
                return;
            }

            // 🔥 ELO UPDATE
            for (Product p : recommendations) {
                if (p.getId() != selected.getId()) {
                    global.update(selected, p, true);
                    personal.update(user, selected.getId(), p.getId(), true);
                }
            }

            // 🔥 SAVE
            PersistenceManager.saveGlobal(products);
            PersistenceManager.saveUsers(users);

            sendResponse(exchange, getProductsJSON());
        });

        server.start();
        System.out.println("Server running on http://localhost:8080");
    }

    // 🔥 JSON RESPONSE
    private static String getProductsJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);

            sb.append("{")
              .append("\"id\":").append(p.getId()).append(",")
              .append("\"name\":\"").append(p.getName()).append("\",")
              .append("\"rating\":").append(p.getRating()).append(",")
              .append("\"category\":\"").append(p.getCategory()).append("\"")
              .append("}");

            if (i < products.size() - 1) {
                sb.append(",");
            }
        }

        sb.append("]");
        return sb.toString();
    }

    // 🔥 SEND RESPONSE (CORS ENABLED)
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // 🔥 PARSE BODY
    private static Map<String, String> parse(String body) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = body.split("&");

        for (String p : pairs) {
            String[] kv = p.split("=");
            map.put(kv[0], kv[1]);
        }

        return map;
    }
}