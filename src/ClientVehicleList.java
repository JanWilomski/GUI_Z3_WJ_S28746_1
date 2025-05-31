import java.util.*;

public abstract class ClientVehicleList {

    List<Vehicle> vehicleList = new ArrayList<Vehicle>();

    Client owner;

    public void addVehicle(Vehicle v) {
        vehicleList.add(v);
    }

    public ClientVehicleList(Client owner) {
        this.owner = owner;


    }

    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }



    @Override
    public abstract String toString();


}
