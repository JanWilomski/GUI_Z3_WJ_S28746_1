import VehicleType.VehicleType;

public abstract class Vehicle {
    private String name;
    private int distance;
    protected VehicleType vehicleType;
    public Vehicle(String name, int distance) {
        this.name = name;
        this.distance = distance;
    }
    public String getName() {
        return name;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(", typ: ").append(vehicleType).append(", ile: ").append(distance);

        if (vehicleType != VehicleType.FREE) {
            sb.append(" km, cena ");
        } else {
            sb.append(", ceny ");
        }

        // Sprawdzenie cennika
        Pricelist pricelist = Pricelist.getPricelist();
        boolean priceFound = false;

        for (Pricelist.EnteredCar car : pricelist.enteredCars) {
            if (car.getModelName().equals(name) && car.getType() == vehicleType) {
                priceFound = true;

                if (vehicleType == VehicleType.FREE) {
                    sb.append("0.00");
                } else if (car.distanceTierMark > 0 && car.priceTier2 > 0) {
                    sb.append(String.format("%.2f (do %d), %.2f (od %d)",
                            car.priceTier1, car.distanceTierMark, car.priceTier2, car.distanceTierMark + 1));
                } else {
                    sb.append(String.format("%.2f", car.priceTier1));
                }

                break;
            }
        }

        if (!priceFound) {
            sb.append("brak");
        }

        return sb.toString();
    }
}
