package org.mrshoffen.weather.http.controller.authenticated;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.http.controller.authenticated.resolver.AuthorizedUser;
import org.mrshoffen.weather.model.dto.in.LocationSaveDto;
import org.mrshoffen.weather.model.dto.out.LocationResponseDto;
import org.mrshoffen.weather.model.dto.out.UserResponseDto;
import org.mrshoffen.weather.service.UserLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/weather/api/user/locations")
public class SavedLocationsController {

    private final UserLocationService userLocationService;

    @PostMapping
    ResponseEntity<LocationResponseDto> saveLocation(@AuthorizedUser UserResponseDto authorizedUser, @Valid @RequestBody LocationSaveDto locationSaveDto) throws URISyntaxException {

        LocationResponseDto locationDto = userLocationService.saveLocationForUser(authorizedUser.getId(), locationSaveDto);

        return ResponseEntity
                .created(new URI("weather/api/user/locations/" + locationDto.getId()))
                .body(locationDto);

    }

    @GetMapping
    ResponseEntity<List<LocationResponseDto>> getAllSavedLocations(@AuthorizedUser UserResponseDto authorizedUser) {

        List<LocationResponseDto> savedLocations = userLocationService.getAllSavedLocations(authorizedUser.getId());

        return ResponseEntity.ok(savedLocations);
    }

    @GetMapping("{locationId:\\d+}")
    ResponseEntity<LocationResponseDto> getSavedLocation(@AuthorizedUser UserResponseDto authorizedUser, @PathVariable("locationId") Integer locationId) {

        LocationResponseDto locationDto = userLocationService.getLocationByLocationId(authorizedUser.getId(), locationId);

        return ResponseEntity.ok(locationDto);
    }

    @DeleteMapping("{locationId:\\d+}")
    public ResponseEntity<Void> deleteSavedLocation(@AuthorizedUser UserResponseDto authorizedUser, @PathVariable("locationId") Integer locationId) {

        userLocationService.deleteSavedLocation(authorizedUser.getId(), locationId);

        return ResponseEntity.noContent().build();
    }
}
