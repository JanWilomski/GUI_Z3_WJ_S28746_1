import static VehicleType.VehicleType.*;

public class Car extends Vehicle {

    public Car(String name, int distance) {
        super(name, distance);
        vehicleType=CAR;
    }
}
