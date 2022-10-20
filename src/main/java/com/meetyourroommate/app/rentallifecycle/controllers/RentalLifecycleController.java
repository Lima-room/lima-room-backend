package com.meetyourroommate.app.rentallifecycle.controllers;

import com.meetyourroommate.app.propertymanagement.domain.aggregates.Property;
import com.meetyourroommate.app.propertymanagement.service.PropertyService;
import com.meetyourroommate.app.rentallifecycle.domain.entities.RentalOffering;
import com.meetyourroommate.app.rentallifecycle.resources.RentalOfferingResource;
import com.meetyourroommate.app.rentallifecycle.services.RentalOfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RentalLifecycleController {
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private RentalOfferingService rentalOfferingService;

    @Operation(summary = "Create new property asset", description = "Create propertyasset to property", tags = {"Rental Lifecycle"})
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Created property assets", content = @Content(mediaType = "application/json"))
    })
    @PostMapping(path = "/property/{id}/rentaloffering", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@PathParam("id") Long property_id, @RequestBody RentalOfferingResource rentalOfferingResource){
        try{
            Optional<Property> property = propertyService.findById(property_id);
            if(property.isPresent()){
                RentalOffering rentalOffering = new RentalOffering(rentalOfferingResource.getLifecycle(),rentalOfferingResource.getAmount(),rentalOfferingResource.getConditions());
                rentalOffering.setProperty(property.get());
                return new ResponseEntity<RentalOffering>(rentalOfferingService.save(rentalOffering), HttpStatus.OK);
            }else{
                return new ResponseEntity<String>("Property Not Found.",HttpStatus.NOT_FOUND);
            }
        }catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "List rentaloffering", description = "List all rental offering without pagination", tags = {"Rental Lifecycle"})
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Listed all rental offering", content = @Content(mediaType = "application/json"))
    })
    @GetMapping(path = "/rentaloffering" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll(){
        try{
            List<RentalOffering> rentalOfferingList = rentalOfferingService.findAll();
            return new ResponseEntity<List<RentalOffering>>(rentalOfferingList, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
