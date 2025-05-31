import VehicleType.VehicleType;

import java.util.ArrayList;

import java.util.List;

public class Pricelist {

    private static Pricelist instance;

    List<EnteredCar> enteredCars=new ArrayList<>();


    public static class EnteredCar{
        VehicleType type;
        String modelName;
        double priceTier1=-1;
        double priceTier2=-1;
        int distanceTierMark=-1;
        double subscriptionPrice=-1;

        public EnteredCar(VehicleType type, String modelName, double priceTier1, double priceTier2, int distanceTierMark, double subscriptionPrice) {
            this.type = type;
            this.modelName = modelName;
            this.priceTier1 = priceTier1;
            this.priceTier2 = priceTier2;
            this.distanceTierMark = distanceTierMark;
            this.subscriptionPrice = subscriptionPrice;
        }
        public EnteredCar(VehicleType type, String modelName, double priceTier1, double priceTier2, int distanceTierMark) {
            this.type = type;
            this.modelName = modelName;
            this.priceTier1 = priceTier1;
            this.priceTier2 = priceTier2;
            this.distanceTierMark = distanceTierMark;
        }
        public EnteredCar(VehicleType type, String modelName, double priceTier1) {
            this.type = type;
            this.modelName = modelName;
            this.priceTier1 = priceTier1;
        }
        public EnteredCar(VehicleType type, int distanceTierMark, String modelName) {
            this.type = type;
            this.modelName = modelName;
            this.distanceTierMark = distanceTierMark;
        }

        public VehicleType getType() {
            return type;
        }

        public String getModelName() {
            return modelName;
        }
    }

    // Metoda dostępowa do jedynego egzemplarza cennika
    public static Pricelist getPricelist() {
        if (instance == null) {
            instance = new Pricelist();
        }
        return instance;
    }

    // Metoda ułatwiająca tworzenie klucza
    private String makeKey(VehicleType type, String model) {
        return type.toString() + ":" + model;
    }

    public void add(VehicleType type, String model, double subscriptionPrice, double priceTier1, double priceTier2, int distanceTierMark){
        enteredCars.add(new EnteredCar(type, model, priceTier1, priceTier2, distanceTierMark, subscriptionPrice));
    }

    public void add(VehicleType type, String model, double priceTier1, double priceTier2, int distanceTierMark){
        enteredCars.add(new EnteredCar(type, model, priceTier1, priceTier2, distanceTierMark));
    }
    public void add(VehicleType type, String model, double priceTier1){
        enteredCars.add(new EnteredCar(type, model, priceTier1));
    }
    public void add(VehicleType type, int distanceTierMark, String model){
        enteredCars.add(new EnteredCar(type, distanceTierMark, model));
    }


}
