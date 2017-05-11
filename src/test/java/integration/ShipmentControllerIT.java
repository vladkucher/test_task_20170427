package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opinta.dto.ParcelDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.DeliveryType;
import com.opinta.entity.Parcel;
import com.opinta.entity.ParcelItem;
import com.opinta.entity.Shipment;
import com.opinta.mapper.ShipmentMapper;
import com.opinta.service.ParcelItemService;
import com.opinta.service.ParcelService;
import com.opinta.service.ShipmentService;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import integration.helper.TestHelper;

import java.math.BigDecimal;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.when;
import static java.lang.Integer.MIN_VALUE;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.Matchers.equalTo;

public class ShipmentControllerIT extends BaseControllerIT {
    private Shipment shipment;
    private int shipmentId = MIN_VALUE;
    private Parcel parcel;
    private int parcelId = MIN_VALUE;
    private ParcelItem parcelItem;
    private int parcelItemId = MIN_VALUE;
    @Autowired
    private ShipmentMapper shipmentMapper;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private TestHelper testHelper;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private ParcelItemService parcelItemService;

    @Before
    public void setUp() throws Exception {
        shipment = testHelper.createShipment();
        shipmentId = (int) shipment.getId();
        parcel = shipment.getParcels().get(0);
        parcelId = (int) parcel.getId();
        parcelItem = parcel.getParcelItems().get(0);
        parcelItemId = (int) parcelItem.getId();
    }

    @After
    public void tearDown() throws Exception {
        testHelper.deleteShipment(shipment);
    }

    @Test
    public void getShipments() throws Exception {
        when().
                get("/shipments").
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getShipment() throws Exception {
        when().
                get("shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK).
                body("id", equalTo(shipmentId));
    }

    @Test
    public void getShipment_notFound() throws Exception {
        when().
                get("/shipments/{id}", shipmentId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createShipment() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("senderId", (int) testHelper.createClient().getId());
        jsonObject.put("recipientId", (int) testHelper.createClient().getId());
        String expectedJson = jsonObject.toString();

        int newShipmentId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                        when().
                        post("/shipments").
                        then().
                        extract().
                        path("id");

        // check created data
        Shipment createdShipment = shipmentService.getEntityById(newShipmentId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentMapper.toDto(createdShipment));

        JSONAssert.assertEquals(expectedJson, actualJson, false);

        // delete
        testHelper.deleteShipment(createdShipment);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateShipment() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/shipment.json");
        jsonObject.put("senderId", (int) testHelper.createClient().getId());
        jsonObject.put("recipientId", (int) testHelper.createClient().getId());
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
                when().
                put("/shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK);

        // check updated data
        ShipmentDto shipmentDto = shipmentMapper.toDto(shipmentService.getEntityById(shipmentId));
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(shipmentDto);

        expectedJson = jsonObject.toString();

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteShipment() throws Exception {
        when().
                delete("/shipments/{id}", shipmentId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteShipment_notFound() throws Exception {
        when().
                delete("/shipments/{id}", shipmentId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    public void getParcels() throws Exception {
        when().
                get("/shipments/{shipmentId}/parcels", shipmentId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getParcel() throws Exception {
        when().
                get("shipments/parcels/{id}", parcelId).
                then().
                statusCode(SC_OK).
                body("id", equalTo(parcelId));
    }

    @Test
    public void getParcel_notFound() throws Exception {
        when().
                get("/shipments/parcels/{id}", parcelId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createParcel() throws Exception {
        //create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/parcel.json");
        String expectedJsonParcel = jsonObject.toString();

        int newParcelId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJsonParcel).
                        when().
                        post("/shipments/{id}/parcels",shipmentId).
                        then().
                        extract().
                        path("id");

        // check created data
        ParcelDto createdParcelDto = parcelService.getById(newParcelId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJsonParcel = mapper.writeValueAsString(createdParcelDto);

        JSONAssert.assertEquals(expectedJsonParcel, actualJsonParcel, false);

        // check shipment data after add parcel
        ShipmentDto actualShipmentDto = shipmentService.getById(shipmentId);
        ShipmentDto expectedShipmentDto = new ShipmentDto(shipmentId, shipment.getSender().getId(), shipment.getRecipient().getId(),
                DeliveryType.D2D, new BigDecimal("126.00"), new BigDecimal("35.20"));

        Assert.assertEquals(expectedShipmentDto,actualShipmentDto);

        testHelper.deleteParcel(shipmentId, createdParcelDto);

        // check shipment data after delete parcel
        actualShipmentDto = shipmentService.getById(shipmentId);
        expectedShipmentDto.setPrice(new BigDecimal("69.00"));
        Assert.assertEquals(expectedShipmentDto,actualShipmentDto);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateParcel() throws Exception {
        //create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/parcel.json");
        jsonObject.put("price", 45);
        String expectedJsonParcel = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJsonParcel).
                when().
                put("/shipments/{shipmentId}/parcels/{id}", shipmentId, parcelId).
                then().
                statusCode(SC_OK);

        // check created data
        ParcelDto createdParcelDto = parcelService.getById(parcelId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJsonParcel = mapper.writeValueAsString(createdParcelDto);

        JSONAssert.assertEquals(expectedJsonParcel, actualJsonParcel, false);
    }

    @Test
    public void deleteParcel() throws Exception {
        when().
                delete("/shipments/{shipmentId}/parcels/{id}", shipmentId , parcelId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteParcel_notFound() throws Exception {
        when().
                delete("/shipments/{shipmentId}/parcels/{id}", shipmentId, parcelId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    public void getParcelItems() throws Exception {
        when().
                get("/shipments/parcels/{parcelId}/parcelItems", parcelId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void getParcelItem() throws Exception {
        when().
                get("shipments/parcels/parcelItems/{id}", parcelItemId).
                then().
                statusCode(SC_OK).
                body("id", equalTo(parcelItemId));
    }

    @Test
    public void getParcelItem_notFound() throws Exception {
        when().
                get("/shipments/parcels/parcelItems/{id}", parcelItemId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createParcelItem() throws Exception {
        // create
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/parcelItem.json");
        String expectedJson = jsonObject.toString();

        int newParcelItemId =
                given().
                        contentType("application/json;charset=UTF-8").
                        body(expectedJson).
                        when().
                        post("/shipments/parcels/{parcelId}/parcelItems",parcelId).
                        then().
                        extract().
                        path("id");

        // check created data
        ParcelItem createdParcelItem = parcelItemService.getById(newParcelItemId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(createdParcelItem);

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateParcelItem() throws Exception {
        // update
        JSONObject jsonObject = testHelper.getJsonObjectFromFile("json/parcelItem.json");
        jsonObject.put("price", 15);
        String expectedJson = jsonObject.toString();

        given().
                contentType("application/json;charset=UTF-8").
                body(expectedJson).
                when().
                put("/shipments/parcels/parcelItems/{id}", parcelItemId).
                then().
                statusCode(SC_OK);

        // check updated data
        ParcelItem parcelItem = parcelItemService.getById(parcelItemId);
        ObjectMapper mapper = new ObjectMapper();
        String actualJson = mapper.writeValueAsString(parcelItem);

        expectedJson = jsonObject.toString();

        JSONAssert.assertEquals(expectedJson, actualJson, false);
    }

    @Test
    public void deleteParcelItem() throws Exception {
        when().
                delete("/shipments/parcels/parcelItems/{id}", parcelItemId).
                then().
                statusCode(SC_OK);
    }

    @Test
    public void deleteParcelItem_notFound() throws Exception {
        when().
                delete("/shipments/parcels/parcelItems/{id}", parcelItemId + 1).
                then().
                statusCode(SC_NOT_FOUND);
    }
}