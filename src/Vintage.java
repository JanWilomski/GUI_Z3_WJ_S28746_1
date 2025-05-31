import static VehicleType.VehicleType.VINTAGE;

public class Vintage extends Vehicle{

    public Vintage(String name, int distance) {
        super(name, distance);
        vehicleType=VINTAGE;
    }
}
