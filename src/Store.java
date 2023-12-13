import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Collections;

import java.util.List;
import java.util.Comparator;

public class Store {
    private List<Products> products;
    private List<String> purchaseHistory;
    private boolean isReceiptPaid;

    public Store() {
        products = FileService.loadProductsFromFile("products.txt");
        purchaseHistory = new ArrayList<>();
        isReceiptPaid = false;
    }

    public List<Products> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public void addProduct(Products product) {
        products.add(product);
    }

    public void sellProducts(List<Products> productsToSell, String customer) {
        if (isReceiptPaid) {
            throw new IllegalStateException("Receipt cannot be edited.");
        }
        double totalCost = 0.0;
        StringBuilder receiptComment = new StringBuilder("# " + customer + " items: ");
        StringBuilder historyComment = new StringBuilder(customer + " bought");

        for (Products product : productsToSell) {
            totalCost += product.getPrice();
            historyComment.append(" ").append(product.getName());
            if (product.getType() == ProductType.MEAT || product.getType() == ProductType.FISH) {
                receiptComment.append("Do not forget to keep ").append(product.getName()).append(" in a fridge!");
            }
            if (product.getType() == ProductType.VEGETABLE || product.getType() == ProductType.FRUIT) {
                receiptComment.append(product.getName()).append("(added package)").append(", ");
            }
        }
        if (receiptComment.length() > 0) {
            purchaseHistory.add(receiptComment.toString());
        }
        purchaseHistory.add(historyComment.toString() + " by price " + totalCost);
    }



    public void editProduct(Products oldProduct, Products newProduct) {
        if (isReceiptPaid) {
            throw new IllegalStateException("Paid receipt cannot be edited.");
        }
        int index = products.indexOf(oldProduct);
        if (index != -1) {
            products.set(index, newProduct);
        }
    }

    public void generateReceipt(String filePath) {
        if (isReceiptPaid) {
            throw new IllegalStateException("Paid receipt cannot be edited.");
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Receipt\n");
            writer.write("================\n");

            for (String entry : purchaseHistory) {
                if (entry.startsWith("# ")) {
                    writer.write(entry.substring(2) + "\n");
                }
            }
            writer.write("================\n");
            writer.write("Total Cost: " + calculateTotalCost() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPurchaseHistory() {
        return Collections.unmodifiableList(purchaseHistory);
    }

    private double calculateTotalCost() {
        return purchaseHistory.stream()
                .filter(entry -> entry.startsWith("Customer"))
                .mapToDouble(entry -> {
                    String[] parts = entry.split(" ");
                    return Double.parseDouble(parts[parts.length - 1]);
                })
                .sum();
    }

    public List<Products> filterAndSortProductsByPrice(double minPrice, double maxPrice) {
        return products.stream()
                .filter(product -> product.getPrice() >= minPrice && product.getPrice() <= maxPrice)
                .sorted(Comparator.comparingDouble(Products::getPrice))
                .collect(Collectors.toList());
    }

    public double calculateAveragePrice() {
        return products.stream()
                .mapToDouble(Products::getPrice)
                .average()
                .orElse(0.0);
    }
}
