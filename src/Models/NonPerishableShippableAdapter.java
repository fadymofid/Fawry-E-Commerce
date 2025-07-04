package Models;

public class NonPerishableShippableAdapter implements Shippable {
    private final NonPerishableProduct product;

    public NonPerishableShippableAdapter(NonPerishableProduct product) {
        this.product = product;
    }

    @Override
    public String getName() {
        return product.getName();
    }

    @Override
    public double getWeight() {
        return product.getWeight();
    }
}