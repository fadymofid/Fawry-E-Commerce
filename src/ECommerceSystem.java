import Models.*;
import Services.ShippingService;
import Services.ShippingServiceImp;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class ECommerceSystem {
    private final ShippingService shippingService;

    public ECommerceSystem(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    public void checkout(Customer customer, ShoppingCart cart) throws Exception {
        validateCart(cart);
        double subtotal = cart.getSubtotal();

        List<Shippable> shippables   = collectShippables(cart);
        Map<String,Integer> qtys     = collectQuantities(cart);
        ShippingService.ShippingResult shipRes =
                shippingService.processShipment(shippables, qtys, customer.getAddress());

        double total = subtotal + shipRes.getShippingFee();
        processPayment(customer, total);
        updateInventory(cart);
        printReceipt(customer, cart, subtotal, shipRes);

        cart.clear();
    }

    private void validateCart(ShoppingCart cart) throws Exception {
        if (cart.isEmpty()) throw new Exception("Cart is empty");
        for (CartItem item : cart.getItems()) {
            Product p = item.getProduct();
            if (p.isExpired())
                throw new Exception("Product " + p.getName() + " has expired");
            if (!p.isAvailable(item.getQuantity()))
                throw new Exception("Product " + p.getName()
                        + " out of stock. Available: " + p.getQuantity()
                        + ", Requested: " + item.getQuantity());
        }
    }

    private List<Shippable> collectShippables(ShoppingCart cart) {
        return cart.getItems().stream()
                .filter(i -> i.getProduct().requiresShipping())
                .map(i -> {
                    Product p = i.getProduct();
                    if (p instanceof PerishableProduct) {
                        return (Shippable)p;
                    } else {
                        return new NonPerishableShippableAdapter((NonPerishableProduct)p);
                    }
                })
                .collect(Collectors.toList());
    }

    private Map<String,Integer> collectQuantities(ShoppingCart cart) {
        return cart.getItems().stream()
                .collect(Collectors.toMap(
                        i -> i.getProduct().getName(),
                        CartItem::getQuantity
                ));
    }

    private void processPayment(Customer customer, double total) throws Exception {
        customer.deductBalance(total);
    }

    private void updateInventory(ShoppingCart cart) {
        cart.getItems().forEach(i -> {
            Product p = i.getProduct();
            p.setQuantity(p.getQuantity() - i.getQuantity());
        });
    }

    private void printReceipt(Customer customer,
                              ShoppingCart cart,
                              double subtotal,
                              ShippingService.ShippingResult shipRes) {
        System.out.println("** Checkout receipt **");
        cart.getItems().forEach(i ->
                System.out.println(i.getQuantity() + "x " + i.getProduct().getName()
                        + "  " + (int)i.getTotalPrice())
        );
        System.out.println("----------------------");
        System.out.println("Subtotal " + String.format("%.2f", subtotal));
        System.out.println("Shipping " + String.format("%.2f", shipRes.getShippingFee()));
        System.out.println("Amount   " + String.format("%.2f", subtotal + shipRes.getShippingFee()));
        System.out.println(customer.getName() + "'s balance after payment: $"
                + String.format("%.2f", customer.getBalance()));
    }

    public static void main(String[] args) {

        ShippingService shipSvc = new ShippingServiceImp();
        ECommerceSystem app = new ECommerceSystem(shipSvc);

        try {
            // Create dummy products
            PerishableProduct cheese = new PerishableProduct("Cheese", 100.0, 10,
                    LocalDate.now().plusDays(7), 0.2);
            PerishableProduct biscuits = new PerishableProduct("Biscuits", 150.0, 5,
                    LocalDate.now().plusDays(30), 0.7);
            NonPerishableProduct tv = new NonPerishableProduct("TV", 500.0, 3, true, 15.0);
            NonPerishableProduct mobile = new NonPerishableProduct("Mobile", 800.0, 5, true, 0.3);
            NonPerishableProduct scratchCard = new NonPerishableProduct(
                    "Mobile Scratch Card", 10.0, 100, false, 0.0);

            Customer customer = new Customer(
                    "Fady", "Fady@gmail.com", 2000.0, "New Cairo, Cairo");

            ShoppingCart cart = new ShoppingCart();

            System.out.println("=== E‑Commerce System Demo ===\n");

            // —— Test Case 1: Successful checkout
            System.out.println("Test Case 1: Successful checkout");
            System.out.println(customer.getName() +
                    "'s initial balance: $" +
                    String.format("%.2f", customer.getBalance()));
            cart.add(cheese, 2);
            cart.add(biscuits, 1);
            cart.add(tv, 1);
            cart.add(scratchCard, 1);
            System.out.println(cart);
            app.checkout(customer, cart);


            // Test Case 2: Empty cart error
            System.out.println("\n\nTest Case 2: Empty cart error");
            try {
                app.checkout(customer, cart);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }


            // Test Case 3: Insufficient balance
            System.out.println("\n\nTest Case 3: Insufficient balance");
            cart.add(tv, 2);
            cart.add(mobile, 2);
            try {
                app.checkout(customer, cart);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            cart.clear();


            // Test Case 4: Out of stock
            System.out.println("\n\nTest Case 4: Out of stock error");
            try {
                cart.add(tv, 5);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }


            // Test Case 5: Expired product
            System.out.println("\n\nTest Case 5: Expired product");
            PerishableProduct expiredMilk = new PerishableProduct(
                    "Milk", 50.0, 5, LocalDate.now().minusDays(1), 1.0);
            try {
                cart.add(expiredMilk, 1);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }


            // Test Case 6: Digital products only
            System.out.println("\n\nTest Case 6: Digital products only");
            cart.add(scratchCard, 5);
            System.out.println(cart);
            app.checkout(customer, cart);


            // Test Case 7: Multiple validation errors
            System.out.println("\n\nTest Case 7: Multiple validation errors");
            PerishableProduct expiredOutOfStock = new PerishableProduct(
                    "Bread", 25.0, 0, LocalDate.now().minusDays(2), 0.5);
            try {
                cart.add(expiredOutOfStock, 1);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }


            // Test Case 8: Zero quantity
            System.out.println("\n\nTest Case 8: Zero quantity error");
            try {
                cart.add(cheese, 0);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }


            // Test Case 9: Negative quantity
            System.out.println("\n\nTest Case 9: Negative quantity error");
            try {
                cart.add(cheese, -5);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }


            // Test Case 10: Mixed valid/invalid products
            System.out.println("\n\nTest Case 10: Mixed valid/invalid products");
            cart.clear();
            cart.add(scratchCard, 3);
            try {
                cart.add(tv, 10);
            } catch (Exception e) {
                System.out.println("Error adding TV: " + e.getMessage());
            }
            if (!cart.isEmpty()) {
                System.out.println("valid items:");
                System.out.println(cart);
                app.checkout(customer, cart);
            }


            // Test Case 11: Insufficient balance for shipping
            System.out.println("\n\nTest Case 11: Insufficient balance for shipping");
            Customer poorCustomer = new Customer(
                    "Ali", "Ali@gmail.com", 50.0, "6th of October, Giza");
            ShoppingCart expensiveCart = new ShoppingCart();
            expensiveCart.add(cheese, 1);
            try {
                app.checkout(poorCustomer, expensiveCart);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }


            // Test Case 12: Exact balance match
            System.out.println("\n\nTest Case 12: Exact balance match");
            Customer exactCustomer = new Customer(
                    "Ahmed", "Ahmed@gmail.com", 10.0, "Shubra, Cairo");
            ShoppingCart exactCart = new ShoppingCart();
            exactCart.add(scratchCard, 1);
            try {
                app.checkout(exactCustomer, exactCart);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }


            // Final inventory & balances
            System.out.println("\n\n=== Final Product Inventory ===");
            System.out.println("Cheese remaining: " + cheese.getQuantity());
            System.out.println("Biscuits remaining: " + biscuits.getQuantity());
            System.out.println("TV remaining: " + tv.getQuantity());
            System.out.println("Mobile remaining: " + mobile.getQuantity());
            System.out.println("Scratch Cards remaining: " + scratchCard.getQuantity());

            System.out.println("\n=== Final Customer Balances ===");
            System.out.println("Fady: $" +
                    String.format("%.2f", customer.getBalance()));
            System.out.println("Ali: $" +
                    String.format("%.2f", poorCustomer.getBalance()));
            System.out.println("Ahmed: $" +
                    String.format("%.2f", exactCustomer.getBalance()));

        } catch (Exception e) {
            System.err.println("Demo error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}