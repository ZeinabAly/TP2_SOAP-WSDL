package hai704i.hotel;


import jakarta.xml.ws.Endpoint;

public class Main {
    public static void main(String[] args) {
        Endpoint.publish("http://localhost:8081/hotel", new HotelPortImpl());
        System.out.println("Publié : http://localhost:8081/hotel?WSDL");
    }
}