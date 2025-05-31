

import static VehicleType.VehicleType.FREE;

public class Free extends Vehicle{

    public Free(String name, int distance) {
        super(name, distance);
        vehicleType = FREE;
    }
}
