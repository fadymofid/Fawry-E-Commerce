package Models;

public class Customer {
    private String name;
    private String email;
    private double balance;
    private String address;

    public Customer(String name, String email, double balance, String address) {
        this.name = name;
        this.email = email;
        this.balance = balance;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getBalance() {
        return balance;
    }

    public void deductBalance(double amount) throws Exception {
        if (amount > balance) {
            throw new Exception("Insufficient balance. Available: $" +
                    String.format("%.2f", balance) +
                    ", Required: $" + String.format("%.2f", amount));
        }
        balance -= amount;
    }

    public void addBalance(double amount) {
        balance += amount;
    }

    @Override
    public String toString() {
        return "Customer: " + name + " (" + email + ") - Balance: $" + String.format("%.2f", balance) +
                "\nAddress: " + address;
    }
}