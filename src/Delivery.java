import VehicleType.VehicleType;

public class Delivery extends Vehicle{

    public Delivery(String name, int distance) {
        super(name, distance);
        vehicleType= VehicleType.DELIVERY;
    }
}
