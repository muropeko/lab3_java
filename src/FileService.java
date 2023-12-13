import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileService {
    public static List<Products> loadProductsFromFile(String filePath) {
        List<Products> products = new ArrayList<>();
        try (Scanner scanner = new Scanner(new FileReader(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    String name = parts[0].trim();
                    double price = Double.parseDouble(parts[1].trim());
                    ProductType type = ProductType.valueOf(parts[2].trim());
                    products.add(new Products(name, price, type));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static void savePurchaseHistoryToFile(String filePath, List<String> history) {
        try {
            java.nio.file.Files.write(java.nio.file.Path.of(filePath), history);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
