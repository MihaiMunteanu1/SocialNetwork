package org.example.llab67.ui;

import org.example.llab67.domain.Prietenie;
import org.example.llab67.domain.Utilizator;
import org.example.llab67.exceptions.ServiceException;
import org.example.llab67.service.Service;
import org.example.llab67.service.SocialCommunities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class UI {

    private final Service srv;
    private final SocialCommunities socialCommunities;

    public UI(Service srv, SocialCommunities socialCommunities) {
        this.srv = srv;
        this.socialCommunities = socialCommunities;
    }

    private void printMenu() {
        System.out.println("1. Adauga utilizaor");
        System.out.println("2. Sterge utilizator");
        System.out.println("3. Adauga prietenie");
        System.out.println("4. Sterge prietenie");
        System.out.println("5. Afiseaza utilizatori");
        System.out.println("6. Afiseaza prietenii");
        System.out.println("7. Numarul de comunitati");
        System.out.println("8. Cea mai sociabila comunitate");
        System.out.println("9. Afiseaza prietenii unui utilizator");
        System.out.println("10. Afiseaza data prieteniei a 2 utilizatori");
        System.out.println("0. EXIT");
    }

    public void run() {
        Scanner scan = new Scanner(System.in);
        boolean ok = true;
        while (ok) {
            printMenu();
            String input = scan.nextLine();
            switch (input) {
                case "1":
                    addUtilizator();
                    break;
                case "2":
                    deleteUtilizator();
                    break;
                case "3":
                    addPrietenie();
                    break;
                case "4":
                    deletePrietenie();
                    break;
                case "5":
                    printUtilizatori();
                    break;
                case "6":
                    printPrieteni();
                    break;
                case "7":
                    printNumarDeComunitati();
                    break;
                case "8":
                    printCeaMaiSociabilaComunitate();
                    break;
                case "9":
                    printPrieteniiUnuiUtilizator();
                    break;
                case "10":
                    printDataPrietenieiADoiUitilizatori();
                    break;
                case "0":
                    ok = false;
                    break;
                default:
                    System.out.println("Comanda invalida");
                    break;
            }
        }
    }

    private void printDataPrietenieiADoiUitilizatori() {
        System.out.println("ID-ul utilizatorului 1: ");
        Scanner scanner = new Scanner(System.in);
        Long id1 = scanner.nextLong();

        System.out.println("ID-ul utilizatorului 2: ");
        Long id2 = scanner.nextLong();

        try {
            LocalDateTime date = srv.getDataPrieteniei(id1, id2);
            if (date != null)
                System.out.println("Data prieteniei: " + date);
            else
                System.out.println("Nu sunt prieteni");
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printPrieteniiUnuiUtilizator() {
        System.out.print("ID-ul utilizatorului: ");
        Scanner scanner = new Scanner(System.in);
        Long id = scanner.nextLong();
        List<Utilizator> prieteni = srv.getPrieteniiUnuiUtilizator(id);
        if (prieteni.isEmpty())
            System.out.println("Utilizatorul nu are prieteni");
        else
            System.out.println("Prieteni:");
        prieteni.forEach(u ->
                System.out.println("ID: " + u.getId() + " " + u.getFirstName() + " " + u.getLastName())
        );
    }


    private void printCeaMaiSociabilaComunitate() {
        List<Utilizator> community = socialCommunities.getCeaMaiSociabilaComunitate();
        System.out.println("Cea mai sociabila comunitate:");

        community.forEach(u -> System.out.println("ID: " + u.getId() + " " + u.getFirstName() + " " + u.getLastName()));
    }

    private void printNumarDeComunitati() {
        int numarDeComunitati = socialCommunities.getNumarDeComunitati();
        System.out.println("Numarul de comunitati: " + numarDeComunitati);
    }

    private void addPrietenie() {
        System.out.println("Introduceti id-ul utilizatorului 1: ");
        Scanner scan = new Scanner(System.in);
        Long id1 = scan.nextLong();
        System.out.println("Introduceti id-ul utilizatorului 2: ");
        Long id2 = scan.nextLong();
        try {
            srv.addPrietenie(new Prietenie(id1, id2, LocalDateTime.now()));
            System.out.println("Prietenie adaugata cu succes");
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        }

    }

    private void deletePrietenie() {
        System.out.println("Introduceti id-ul utilizatorului 1: ");
        Scanner scan = new Scanner(System.in);
        Long id1 = scan.nextLong();
        System.out.println("Introduceti id-ul utilizatorului 2: ");
        Long id2 = scan.nextLong();
        try {
            srv.removePrietenie(id1, id2);
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        }

    }

    private void deleteUtilizator() {
        System.out.println("Introduceti id-ul utilizatorului: ");
        Scanner scan = new Scanner(System.in);
        Long id = scan.nextLong();
        try {
            srv.removeUtilizator(id);
            //System.out.println("Utilizator sters cu succes");
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addUtilizator() {
        System.out.println("Introduceti numele: ");
        Scanner scan = new Scanner(System.in);
        String nume = scan.nextLine();
        System.out.println("Introduceti prenumele: ");
        String prenume = scan.nextLine();
        System.out.println("Introduceti emailu: ");
        String email = scan.nextLine();
        System.out.println("Introduceti parola: ");
        String password = scan.nextLine();
        Utilizator u = new Utilizator(nume, prenume, email, password);
        try {
            srv.addUtilizator(u);
            System.out.println("Utilizator adaugat cu succes");
        } catch (ServiceException e) {
            System.out.println(e.getMessage());
        }

    }

    private void printUtilizatori() {
        srv.getUtilizatori().forEach(u ->
                System.out.println("ID: " + u.getId() + " " + u.getFirstName() + " " + u.getLastName())
        );
    }

    private void printPrieteni() {
        srv.getPrietenii().forEach(p ->
                System.out.println("ID: " + p.getId() + ", idUser1: " + p.getId1() + " idUser2: " + p.getId2() + " " + p.getFriendsFrom())
        );
    }
}
