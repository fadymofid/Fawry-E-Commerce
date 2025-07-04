
package Services;

import Models.Shippable;

import java.util.List;
import java.util.Map;

public interface ShippingService {
    class ShippingResult {
        private final double shippingFee;
        private final double totalWeight;

        public ShippingResult(double shippingFee, double totalWeight) {
            this.shippingFee = shippingFee;
            this.totalWeight = totalWeight;
        }
        public double getShippingFee()    { return shippingFee; }
        public double getTotalWeight()   { return totalWeight; }
    }

    ShippingResult processShipment(List<Shippable> items,
                                   Map<String,Integer> quantities,
                                   String address);
}