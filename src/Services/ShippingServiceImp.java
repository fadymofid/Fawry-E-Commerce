
package Services;

import Models.Shippable;

import java.util.*;

public class ShippingServiceImp implements ShippingService {
    private static final double RATE_PER_KG = 10.0;
    private static final double BASE_FEE   = 5.0;

    @Override
    public ShippingResult processShipment(List<Shippable> items,
                                          Map<String,Integer> quantities,
                                          String address) {
        if (items.isEmpty()) {
            return new ShippingResult(0, 0);
        }

        System.out.println("** Shipment notice **");
        System.out.println("Shipping to: " + address);

        double totalWeight = 0;
        for (Shippable item : items) {
            int qty = quantities.getOrDefault(item.getName(), 1);
            double w  = item.getWeight() * qty;
            totalWeight += w;
            System.out.println(qty + "x " + item.getName() + " " + (int)(w*1000) + "g");
        }
        System.out.println("Total package weight " + String.format("%.1f", totalWeight) + "kg");

        double fee = BASE_FEE + (totalWeight * RATE_PER_KG);
        return new ShippingResult(fee, totalWeight);
    }
}