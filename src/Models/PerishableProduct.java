package Models;

import java.time.LocalDate;

public class PerishableProduct extends Product implements Shippable {
    private LocalDate expirationDate;
    private double weight; //in Kg

    public PerishableProduct(String name, double price, int quantity, LocalDate expirationDate, double weight) {
        super(name, price, quantity);
        this.expirationDate = expirationDate;
        this.weight = weight;
    }

    @Override
    public boolean isExpired() {
        return LocalDate.now().isAfter(expirationDate);
    }

    @Override
    public boolean requiresShipping() {
        return true;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public String toString() {
        return super.toString() + " (Expires: " + expirationDate + ", Weight: " + weight + "kg)";
    }
}