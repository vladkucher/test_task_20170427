package integration.helper;

import com.opinta.dto.ParcelDto;
import com.opinta.entity.*;
import com.opinta.mapper.ParcelMapper;
import com.opinta.service.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TestHelper {
    @Autowired
    private ClientService clientService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private CounterpartyService counterpartyService;
    @Autowired
    private PostcodePoolService postcodePoolService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private PostOfficeService postOfficeService;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private ParcelMapper parcelMapper;
    @Autowired
    private ParcelItemService parcelItemService;

    public PostOffice createPostOffice() {
        PostOffice postOffice = new PostOffice("Lviv post office", createAddress(), createPostcodePool());
        return postOfficeService.saveEntity(postOffice);
    }

    public void deletePostOffice(PostOffice postOffice) {
        postOfficeService.delete(postOffice.getId());
        postcodePoolService.delete(postOffice.getPostcodePool().getId());
    }

    private void createParcelItem(long parcelId){
        ParcelItem parcelItem = new ParcelItem("name",3,0.5f,1.1f);
        parcelItemService.save(parcelId,parcelItem);
    }

    private void createParcel(long shipmentId){
        Parcel parcel = new Parcel(4,5,12.5f,33);
        ParcelDto parcelDto = parcelService.save(shipmentId, parcelMapper.toDto(parcel));
        createParcelItem(parcelDto.getId());
    }

    public void deleteParcel(long shipmentId, ParcelDto parcelDto){
        parcelService.delete(shipmentId, parcelDto.getId());
    }

    public Shipment createShipment() {
        Shipment shipment = new Shipment(createClient(), createClient(),
                DeliveryType.D2D, new BigDecimal(30), new BigDecimal(35.2));
        shipment = shipmentService.saveEntity(shipment);
        createParcel(shipment.getId());
        return shipmentService.getEntityById(shipment.getId());
    }

    public void deleteShipment(Shipment shipment) {
        shipmentService.delete(shipment.getId());
        clientService.delete(shipment.getSender().getId());
        clientService.delete(shipment.getRecipient().getId());
    }

    public Client createClient() {
        Client newClient = new Client("FOP Ivanov", "001", createAddress(), createCounterparty());
        return clientService.saveEntity(newClient);
    }

    public void deleteClient(Client client) {
        clientService.delete(client.getId());
        addressService.delete(client.getAddress().getId());
        deleteCounterpartyWithPostcodePool(client.getCounterparty());
    }

    public Address createAddress() {
        Address address = new Address("00001", "Ternopil", "Monastiriska",
                "Monastiriska", "Sadova", "51", "");
        return addressService.saveEntity(address);
    }

    public Counterparty createCounterparty() {
        Counterparty counterparty = new Counterparty("Modna kasta", createPostcodePool());
        return counterpartyService.saveEntity(counterparty);
    }

    public PostcodePool createPostcodePool() {
        return postcodePoolService.saveEntity(new PostcodePool("12345", false));
    }

    public void deleteCounterpartyWithPostcodePool(Counterparty counterparty) {
        counterpartyService.delete(counterparty.getId());
        postcodePoolService.delete(counterparty.getPostcodePool().getId());
    }

    public JSONObject getJsonObjectFromFile(String filePath) throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(new FileReader(getFileFromResources(filePath)));
    }

    public String getJsonFromFile(String filePath) throws IOException, ParseException {
        return getJsonObjectFromFile(filePath).toString();
    }

    private File getFileFromResources(String path) {
        return new File(getClass().getClassLoader().getResource(path).getFile());
    }
}
