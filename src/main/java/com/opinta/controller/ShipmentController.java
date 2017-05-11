package com.opinta.controller;

import java.util.List;

import com.opinta.dto.ParcelDto;
import com.opinta.dto.ShipmentDto;
import com.opinta.entity.ParcelItem;
import com.opinta.service.PDFGeneratorService;
import com.opinta.service.ParcelItemService;
import com.opinta.service.ParcelService;
import com.opinta.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static java.lang.String.format;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/shipments")
public class ShipmentController {
    private ShipmentService shipmentService;
    private PDFGeneratorService pdfGeneratorService;
    private ParcelService parcelService;
    private ParcelItemService parcelItemService;

    @Autowired
    public ShipmentController(ShipmentService shipmentService, PDFGeneratorService pdfGeneratorService, ParcelService parcelService, ParcelItemService parcelItemService) {
        this.shipmentService = shipmentService;
        this.pdfGeneratorService = pdfGeneratorService;
        this.parcelService = parcelService;
        this.parcelItemService = parcelItemService;
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<ShipmentDto> getShipments() {
        return shipmentService.getAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getShipment(@PathVariable("id") long id) {
        ShipmentDto shipmentDto = shipmentService.getById(id);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format("No Shipment found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDto, OK);
    }

    @GetMapping("{id}/label-form")
    public ResponseEntity<?> getShipmentLabelForm(@PathVariable("id") long id) {
        byte[] data = pdfGeneratorService.generateLabel(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "labelform" + id + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(data, headers, OK);
    }

    @GetMapping("{id}/postpay-form")
    public ResponseEntity<?> getShipmentPostpayForm(@PathVariable("id") long id) {
        byte[] data = pdfGeneratorService.generatePostpay(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "postpayform" + id + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(data, headers, OK);
    }

    @PostMapping
    @ResponseStatus(OK)
    public ShipmentDto createShipment(@RequestBody ShipmentDto shipmentDto) {
        return shipmentService.save(shipmentDto);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateShipment(@PathVariable long id, @RequestBody ShipmentDto shipmentDto) {
        shipmentDto = shipmentService.update(id, shipmentDto);
        if (shipmentDto == null) {
            return new ResponseEntity<>(format("No Shipment found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(shipmentDto, OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteShipment(@PathVariable long id) {
        if (!shipmentService.delete(id)) {
            return new ResponseEntity<>(format("No Shipment found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }

    @GetMapping("{shipmentId}/parcels")
    public ResponseEntity<?> getParcels(@PathVariable long shipmentId) {
        List<ParcelDto> parcelDtos = parcelService.getAll(shipmentId);
        if(parcelDtos==null){
            return new ResponseEntity<>(format("Shipment %d doesn't exist", shipmentId), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDtos,OK);
    }

    @GetMapping("parcels/{id}")
    public ResponseEntity<?> getParcel(@PathVariable long id){
        ParcelDto parcelDto = parcelService.getById(id);
        if(parcelDto==null){
            return new ResponseEntity<>(format("No parcel found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDto,OK);
    }

    @PostMapping("{shipmentId}/parcels")
    @ResponseStatus(OK)
    public ResponseEntity<?> createParcel(@PathVariable long shipmentId, @RequestBody ParcelDto parcelDto){
        parcelDto = parcelService.save(shipmentId,parcelDto);
        if(parcelDto==null){
            return new ResponseEntity<>(format("Shipment %d doesn't exist", shipmentId), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDto,OK);
    }

    @PutMapping("{shipmentId}/parcels/{id}")
    public ResponseEntity<?> updateParcel(@PathVariable long shipmentId, @PathVariable long id, @RequestBody ParcelDto parcelDto) {
        parcelDto = parcelService.update(shipmentId, id, parcelDto);
        if (parcelDto == null) {
            return new ResponseEntity<>(format("No Parcel found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelDto, OK);
    }

    @DeleteMapping("{shipmentId}/parcels/{id}")
    public ResponseEntity<?> deleteParcel(@PathVariable long shipmentId, @PathVariable long id) {
        if (!parcelService.delete(shipmentId, id)) {
            return new ResponseEntity<>(format("No Parcel found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }

    @GetMapping("parcels/{parcelId}/parcelItems")
    public ResponseEntity<?> getParcelItems(@PathVariable long parcelId){
        List<ParcelItem> parcelItems = parcelItemService.getAll(parcelId);
        if(parcelItems==null){
            return new ResponseEntity<>(format("Parcel %d doesn't exist", parcelId), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelItems,OK);
    }

    @GetMapping("parcels/parcelItems/{id}")
    public ResponseEntity<?> getParcelItem(@PathVariable long id){
        ParcelItem parcelItem = parcelItemService.getById(id);
        if(parcelItem==null){
            return new ResponseEntity<>(format("No parcelItem found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelItem,OK);
    }

    @PostMapping("parcels/{parcelId}/parcelItems")
    @ResponseStatus(OK)
    public ResponseEntity<?> createParcelItem(@PathVariable long parcelId, @RequestBody ParcelItem parcelItem){
        parcelItem = parcelItemService.save(parcelId, parcelItem);
        if(parcelItem==null){
            return new ResponseEntity<>(format("Parcel %d doesn't exist", parcelId), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelItem,OK);
    }

    @PutMapping("parcels/parcelItems/{id}")
    public ResponseEntity<?> updateParcelItem(@PathVariable long id, @RequestBody ParcelItem parcelItem) {
        parcelItem = parcelItemService.update(id, parcelItem);
        if (parcelItem == null) {
            return new ResponseEntity<>(format("No parcelItem found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(parcelItem, OK);
    }

    @DeleteMapping("parcels/parcelItems/{id}")
    public ResponseEntity<?> deleteParcelItem(@PathVariable long id) {
        if (!parcelItemService.delete(id)) {
            return new ResponseEntity<>(format("No ParcelItem found for ID %d", id), NOT_FOUND);
        }
        return new ResponseEntity<>(OK);
    }
}

