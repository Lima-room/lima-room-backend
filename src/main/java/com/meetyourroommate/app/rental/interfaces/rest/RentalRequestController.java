package com.meetyourroommate.app.rental.interfaces.rest;

import com.meetyourroommate.app.profile.application.services.ProfileService;
import com.meetyourroommate.app.profile.domain.aggregates.Profile;
import com.meetyourroommate.app.rental.application.services.RentalOfferingService;
import com.meetyourroommate.app.rental.application.services.RentalRequestService;
import com.meetyourroommate.app.rental.application.transform.resources.RentalRequestResource;
import com.meetyourroommate.app.rental.domain.entities.RentalOffering;
import com.meetyourroommate.app.rental.domain.entities.RentalRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.List;
import java.util.Optional;

@Tag(name = "Rental Request", description = "Create, read, update and delete rental request")
@RestController
@RequestMapping("/api/v1")
public class RentalRequestController {

    private RentalRequestService rentalRequestService;
    private RentalOfferingService rentalOfferingService;
    private ProfileService profileService;

    public RentalRequestController(RentalRequestService rentalRequestService, RentalOfferingService rentalOfferingService, ProfileService profileService) {
        this.rentalRequestService = rentalRequestService;
        this.rentalOfferingService = rentalOfferingService;
        this.profileService = profileService;
    }

    @Operation(summary = "Create new rental request", description = "Create new rental request to create rental object")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Created rental request", content = @Content(mediaType = "application/json"))
    })
    @PostMapping(path = "/rental/request", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody RentalRequestResource resource){
        try{
            Optional<Profile> profile = profileService.findByUserId(resource.getUserId());
            if(profile.isEmpty()){
                return new ResponseEntity<>("Profile not found.", HttpStatus.NOT_FOUND);
            }
            Optional<RentalOffering> rentalOffering = rentalOfferingService.findById(resource.getRentalOfferId());
            if(rentalOffering.isEmpty()){
                return new ResponseEntity<>("RentalOffering not found.", HttpStatus.NOT_FOUND);
            }
            Optional<RentalRequest> optionalRentalRequest = rentalRequestService.findByProfileAndOffer(profile.get(), rentalOffering.get());
            if(optionalRentalRequest.isPresent()){
                return new ResponseEntity<>("There is already a rental request.",HttpStatus.CONFLICT);
            }
            RentalRequest rentalRequest  = new RentalRequest(profile.get(), rentalOffering.get(), resource.getMessage());
            return new ResponseEntity<>(rentalRequestService.save(rentalRequest), HttpStatus.OK);

        }catch(Exception e){
           return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Find all rental request by offer", description = "Find all rental request by offer id")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Listed rental request", content = @Content(mediaType = "application/json"))
    })
    @GetMapping(path = "/rental/offer/{id}/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllByOffer(@PathVariable Long id){
        try{
            Optional<RentalOffering> rentalOffering = rentalOfferingService.findById(id);
            if(rentalOffering.isEmpty()){
                return new ResponseEntity<>("Rental Offer not found.", HttpStatus.NOT_FOUND);
            }
            List<RentalRequest> rentalOfferingList = rentalRequestService.findByRentalOffering(rentalOffering.get());
            return new ResponseEntity<>(rentalOfferingList, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Find all rental request by user id", description = "Find all rental request by user id")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Listed rental request", content = @Content(mediaType = "application/json"))
    })
    @GetMapping(path = "/users/{id}/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAllByUser(@PathVariable String id){
        try{
            Optional<Profile> profile = profileService.findByUserId(id);
            if(profile.isEmpty()){
                return new ResponseEntity<>("Profile not found.", HttpStatus.NOT_FOUND);
            }
            List<RentalRequest> rentalOfferingList = rentalRequestService.findByProfile(profile.get());
            return new ResponseEntity<>(rentalOfferingList, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
