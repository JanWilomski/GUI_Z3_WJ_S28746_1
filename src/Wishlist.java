public class Wishlist extends ClientVehicleList{
    public Wishlist(Client owner) {
        super(owner);
    }

    @Override
    public String toString() {
        if(vehicleList.isEmpty()) {
            return "-- pusto";
        }

        StringBuilder result = new StringBuilder();
        for(int i = 0; i < vehicleList.size(); i++) {
            Vehicle v = vehicleList.get(i);
            result.append(formatVehicleForWishlist(v));
            if(i < vehicleList.size() - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    private String formatVehicleForWishlist(Vehicle v) {
        Pricelist pricelist = Pricelist.getPricelist();
        StringBuilder result = new StringBuilder(v.getName() + ", typ: " + v.vehicleType.toString() + ", ile: " + v.getDistance());

        // Dodajemy informację o cenie
        boolean foundPrice = false;
        for(Pricelist.EnteredCar car : pricelist.enteredCars) {
            if(car.getType() == v.vehicleType && car.getModelName().equals(v.getName())) {
                foundPrice = true;
                result.append(", cena ");

                if(v.vehicleType == VehicleType.VehicleType.FREE) {
                    result.append("0.00");
                } else if(owner.hasAbonament && car.subscriptionPrice > 0) {
                    result.append(String.format("%.2f", car.subscriptionPrice));
                } else if(car.distanceTierMark > 0 && car.priceTier2 > 0) {
                    result.append(String.format("%.2f (do %d), %.2f (od %d)",
                            car.priceTier1, car.distanceTierMark,
                            car.priceTier2, car.distanceTierMark + 1));
                } else {
                    result.append(String.format("%.2f", car.priceTier1));
                }
                break;
            }
        }

        if(!foundPrice) {
            result.append(", ceny brak");
        }

        return result.toString();
    }
}
