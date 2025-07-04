package Models;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<CartItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public void add(Product product, int quantity) throws Exception {
        if (product == null) {
            throw new Exception("Product cannot be null");
        }

        if (quantity <= 0) {
            throw new Exception("Quantity must be positive");
        }

        if (!product.isAvailable(quantity)) {
            throw new Exception("Insufficient stock for " + product.getName() +
                    ". Available: " + product.getQuantity() + ", Requested: " + quantity);
        }

        if (product.isExpired()) {
            throw new Exception("Product " + product.getName() + " has expired");
        }

        // Check if product already exists in cart
        for (CartItem item : items) {
            if (item.getProduct().equals(product)) {
                int newQuantity = item.getQuantity() + quantity;
                if (!product.isAvailable(newQuantity)) {
                    throw new Exception("Insufficient stock for " + product.getName() +
                            ". Available: " + product.getQuantity() +
                            ", Total requested: " + newQuantity);
                }
                item.setQuantity(newQuantity);
                return;
            }
        }

        items.add(new CartItem(product, quantity));
    }

    public void remove(Product product) {
        items.removeIf(item -> item.getProduct().equals(product));
    }

    public List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getSubtotal() {
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    public void clear() {
        items.clear();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Cart is empty";
        }

        StringBuilder sb = new StringBuilder("Cart contents:\n");
        for (CartItem item : items) {
            sb.append("- ").append(item.toString()).append("\n");
        }
        sb.append("Subtotal: $").append(String.format("%.2f", getSubtotal()));
        return sb.toString();
    }
}