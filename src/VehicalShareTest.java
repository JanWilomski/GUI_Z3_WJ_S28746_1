

import static Payment.Payment.*;
import static VehicleType.VehicleType.*;

public class VehicalShareTest {

    // cena pojazdów danej marki z koszyka
    static double price(String vehicalName, Basket k) {
        double totalPrice = 0;
        Pricelist pricelist = Pricelist.getPricelist();

        for(Vehicle v : k.getVehicleList()) {
            if(v.getName().equals(vehicalName)) {
                // Szukamy ceny w cenniku
                for(Pricelist.EnteredCar car : pricelist.enteredCars) {
                    if(car.getType() == v.vehicleType && car.getModelName().equals(v.getName())) {
                        // Znaleziono pojazd w cenniku, obliczamy cenę
                        if(v.vehicleType == FREE && k.owner.hasAbonament) {
                            totalPrice += 0; // Darmowy przejazd dla abonentów
                        } else if(k.owner.hasAbonament && car.subscriptionPrice > 0) {
                            totalPrice += car.subscriptionPrice * v.getDistance();
                        } else if(car.distanceTierMark > 0 && v.getDistance() > car.distanceTierMark) {
                            // Cena podzielona na dwa przedziały
                            totalPrice += car.priceTier1 * car.distanceTierMark +
                                    car.priceTier2 * (v.getDistance() - car.distanceTierMark);
                        } else {
                            // Cena stała lub tylko pierwszy przedział
                            totalPrice += car.priceTier1 * v.getDistance();
                        }
                        break;
                    }
                }
            }
        }

        return totalPrice;
    }

    public static void main(String[] args) {

        // cennik
        Pricelist cennik = Pricelist.getPricelist();

        // dodawanie nowych cen do cennika
        cennik.add(CAR, "Syrena", 1.5, 2.5, 1.85, 100);  	// metoda przyjmująca 6 parametrów:
        // 1.5 zł za 1 km jeśli klient posiada abonament
        // próg odległości (km): 100
        // w przeciwnym przypadku: 2.5 zł za 1 km (do 100 km), 1.85 zł za 1 km (od 101-ego kilometra)

        cennik.add(DELIVERY, "Żuk", 4, 3, 150);			// metoda przyjmująca 5 parametrów:
        // próg odległości (km): 150
        // 4 zł za 1 km (do 150 km),
        // 3 zł za 1 km (od 151-tego kilometra)

        cennik.add(VINTAGE, "Ford T", 10);			// metoda przyjmująca 3 parametry (z których drugi jest typu String):
        // 10 zł za 1 km


        cennik.add(FREE, 50, "Tuk-Tuk");			// metoda przyjmująca 3 parametry (z których trzeci jest typu String):
        // darmowy przejazd tylko dla abonentów (do 50 km)


        // Klient f1 deklaruje kwotę 900 zł na zamównienia; true oznacza, że klient posiada abonament
        Client f1 = new Client("f1", 900, true);

        // Klient f1 dodaje do listy życzeń różne pojazdy:
        // "Syrena" typu osobowego na maks. 80 km
        // "Żuk" typu dostawczego na maks. 200 km,
        // "Lublin" typu zabytkowego na maks. 30 km,
        // "Tuk-Tuk" typu darmowego na maks. 60 km (ale może tylko do 50 km).
        f1.add(new Car("Syrena", 80));
        f1.add(new Delivery("Żuk", 200));
        f1.add(new Vintage("Lublin", 30));
        f1.add(new Free("Tuk-Tuk", 60));

        // Lista życzeń klienta f1
        Wishlist listaF1 = f1.getWishlist();

        System.out.println("Lista życzeń klienta " + listaF1);

        // Przed płaceniem, klient przepakuje pojazdy z listy życzeń do koszyka (po uprzednim wyczyszczeniu).
        // Możliwe, że na liście życzeń są pojazdy niemające ceny w cenniku,
        // w takim przypadku nie trafiłyby do koszyka
        Basket koszykF1 = f1.getBasket();
        f1.pack();

        // Co jest na liście życzeń klienta f1
        System.out.println("Po przepakowaniu, lista życzeń klienta " + f1.getWishlist());

        // Co jest w koszyku klienta f1
        System.out.println("Po przepakowaniu, koszyk klienta " + koszykF1);

        // Ile wynosi cena wszystkich pojazdów "Syrena" w koszyku klienta f1
        System.out.println("Pojazdy Syrena w koszyku klienta f1 kosztowały:  " + price("Syrena", koszykF1));
//
        // Klient zapłaci...
        f1.pay(CARD, false);		// płaci kartą płatniczą, prowizja 2%
        // true oznacza, że w przypadku braku środków aplikacja sam odłoży nadmiarowe kilometry/pojazdy,
        // false oznacza rezygnację z płacenia razem z wyczyszczeniem koszyka i listy życzeń

        // Ile klientowi f1 zostało pieniędzy?
        System.out.println("Po zapłaceniu, klientowi f1 zostało: " + f1.getWallet() + " zł");

        // Mogło klientowi zabraknąć srodków, wtedy, opcjonalnie, pojazdy/kilometry mogą być odkładane,
        // w przeciwnym przypadku, koszyk jest pusty po zapłaceniu
        System.out.println("Po zapłaceniu, koszyk klienta " + f1.getBasket());
        System.out.println("Po zapłaceniu, koszyk klienta " + koszykF1);

        // Teraz przychodzi klient dakar,
        // deklaruje 850 zł na zamówienia
        Client dakar = new Client("dakar", 850, false);

        // Zamówił za dużo jak na tę kwotę
        dakar.add(new Delivery("Żuk", 100));
        dakar.add(new Vintage("Ford T", 50));

        // Co klient dakar ma na swojej liście życzeń
        System.out.println("Lista życzeń klienta " + dakar.getWishlist());

        Basket koszykDakar = dakar.getBasket();
        dakar.pack();

        // Co jest na liście życzeń klienta dakar
        System.out.println("Po przepakowaniu, lista życzeń klienta " + dakar.getWishlist());

        // A co jest w koszyku klienta dakar
        System.out.println("Po przepakowaniu, koszyk klienta " + dakar.getBasket());

        // klient dakar płaci
        dakar.pay(TRANSFER, true);	// płaci przelewem, bez prowizji

        // Ile klientowi dakar zostało pieniędzy?
        System.out.println("Po zapłaceniu, klientowi dakar zostało: " + dakar.getWallet() + " zł");

        // Co zostało w koszyku klienta dakar (za mało pieniędzy miał)
        System.out.println("Po zapłaceniu, koszyk klienta " + koszykDakar);

        dakar.returnVehicle(DELIVERY, "Żuk", 50);	// zwrot (do koszyka) 50 km dostawczego "Żuka" z ostatniej transakcji

        // Ile klientowi dakar zostało pieniędzy?
        System.out.println("Po zwrocie, klientowi dakar zostało: " + dakar.getWallet() + " zł");

        // Co zostało w koszyku klienta dakar
        System.out.println("Po zwrocie, koszyk klienta " + koszykDakar);
//
    }
}