import Payment.Payment;
import VehicleType.VehicleType;

import java.util.ArrayList;
import java.util.List;

import static VehicleType.VehicleType.*;

public class Client {

    boolean hasAbonament;
    double availableCredit;
    String name;
    private Wishlist wishlist;
    private final Basket basket;

    private List<Vehicle> lastTransaction = new ArrayList<>();


    public Client(String name,  double availableCredit, boolean hasAbonament) {
        this.name = name;
        this.hasAbonament = hasAbonament;
        this.availableCredit = availableCredit;
        this.wishlist = new Wishlist(this);
        this.basket = new Basket(this);
    }

    public Basket getBasket() {
        return basket;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void add(Vehicle v){
        wishlist.addVehicle(v);
    }

    public double getWallet() {

        return Math.round(availableCredit * 100.0) / 100.0;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public void pack(){
        Pricelist pricelist = Pricelist.getPricelist();
        basket.vehicleList.clear();

        Wishlist newWishlist = new Wishlist(this);
        for(Vehicle v : wishlist.vehicleList){

            boolean found = false;

            for(Pricelist.EnteredCar car : pricelist.enteredCars) {
                if(v.vehicleType == car.getType() && v.getName().equals(car.getModelName())) {
                    found = true;

                    if(v.vehicleType == FREE) {
                        if(hasAbonament) {
                            int distance = Math.min(v.getDistance(), car.distanceTierMark);
                            basket.addVehicle(new Free(v.getName(), distance));
                        }
                    } else {
                        basket.addVehicle(v);
                    }
                    break;
                }
            }

            if(!found) {
                newWishlist.addVehicle(v);
            }

        }
        this.wishlist = newWishlist;

    }


    public void pay(Payment method, boolean acceptPartial) {
        Pricelist pricelist = Pricelist.getPricelist();
        List<Vehicle> paidVehicles = new ArrayList<>();
        double totalCost = 0;

        List<Vehicle> sorted = new ArrayList<>(basket.vehicleList);
        lastTransaction.clear();

        for (Vehicle v : sorted) {
            for (Pricelist.EnteredCar car : pricelist.enteredCars) {
                if (car.getType() == v.vehicleType && car.getModelName().equals(v.getName())) {
                    double cost = 0;
                    int distance = v.getDistance();

                    if (v.vehicleType == VehicleType.FREE && hasAbonament) {
                        cost = 0;
                    } else if (hasAbonament && car.subscriptionPrice > 0) {
                        cost = car.subscriptionPrice * distance;
                    } else if (car.distanceTierMark > 0 && car.priceTier2 > 0 && distance > car.distanceTierMark) {
                        cost = car.priceTier1 * car.distanceTierMark + car.priceTier2 * (distance - car.distanceTierMark);
                    } else {
                        cost = car.priceTier1 * distance;
                    }

                    double fee = method == Payment.CARD ? cost * 0.02 : 0;
                    double totalWithFee = cost + fee;

                    if (totalWithFee <= availableCredit) {
                        availableCredit -= totalWithFee;
                        paidVehicles.add(v);
                        totalCost += totalWithFee;
                    } else if (acceptPartial) {
                        double unitPrice;
                        if (v.vehicleType == VehicleType.FREE && hasAbonament) {
                            unitPrice = 0;
                        } else if (hasAbonament && car.subscriptionPrice > 0) {
                            unitPrice = car.subscriptionPrice;
                        } else {
                            unitPrice = car.priceTier1;
                        }

                        int maxKm = (int) (availableCredit / (unitPrice + (method == Payment.CARD ? unitPrice * 0.02 : 0)));
                        if (maxKm > 0) {
                            Vehicle partial = createVehicleOfSameType(v, maxKm);
                            paidVehicles.add(partial);
                            availableCredit -= maxKm * unitPrice;
                            if (method == Payment.CARD) availableCredit -= maxKm * unitPrice * 0.02;
                        }
                    }

                    break;
                }
            }
        }

        basket.vehicleList.clear();
        basket.vehicleList.addAll(paidVehicles);
        lastTransaction.addAll(paidVehicles);

        if (!acceptPartial && basket.vehicleList.size() < sorted.size()) {
            basket.vehicleList.clear();
            wishlist.vehicleList.clear();
        }
    }

    private Vehicle createVehicleOfSameType(Vehicle v, int km) {
        switch (v.vehicleType) {
            case CAR: return new Car(v.getName(), km);
            case DELIVERY: return new Delivery(v.getName(), km);
            case VINTAGE: return new Vintage(v.getName(), km);
            case FREE: return new Free(v.getName(), km);
            default: throw new IllegalArgumentException("Zły model: " + v.vehicleType);
        }
    }



    public void returnVehicle(VehicleType type, String name, int km) {
        Pricelist pricelist = Pricelist.getPricelist();

        for (Vehicle v : lastTransaction) {
            if (v.vehicleType == type && v.getName().equals(name)) {
                int kmToReturn = Math.min(km, v.getDistance());
                double refund = 0;

                for (Pricelist.EnteredCar car : pricelist.enteredCars) {
                    if (car.getType() == type && car.getModelName().equals(name)) {
                        if (type == VehicleType.FREE && hasAbonament) {
                            refund = 0;
                        } else if (hasAbonament && car.subscriptionPrice > 0) {
                            refund = kmToReturn * car.subscriptionPrice;
                        } else if (car.distanceTierMark > 0 && car.priceTier2 > 0 && kmToReturn > car.distanceTierMark) {
                            refund = car.priceTier1 * car.distanceTierMark +
                                    car.priceTier2 * (kmToReturn - car.distanceTierMark);
                        } else {
                            refund = kmToReturn * car.priceTier1;
                        }
                        break;
                    }
                }

                // aktualizuj stan portfela
                availableCredit += refund;

                // zaktualizuj stan w koszyku
                basket.vehicleList.removeIf(bv -> bv.vehicleType == type && bv.getName().equals(name));
                if (v.getDistance() > kmToReturn) {
                    Vehicle updated = createVehicleOfSameType(v, v.getDistance() - kmToReturn);
                    basket.addVehicle(updated);
                }

                // zaktualizuj ostatnią transakcję
                lastTransaction.remove(v);
                if (v.getDistance() > kmToReturn) {
                    lastTransaction.add(createVehicleOfSameType(v, v.getDistance() - kmToReturn));
                }

                break;
            }
        }
    }










}
