package org.mrshoffen.weather.http.controller.authenticated;


import lombok.RequiredArgsConstructor;
import org.mrshoffen.weather.http.resolver.AuthorizedUser;
import org.mrshoffen.weather.model.dto.in.LocationSaveDto;
import org.mrshoffen.weather.model.dto.out.LocationDto;
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
public class UserSavedLocations {

    private final UserLocationService userLocationService;

    @PostMapping
    ResponseEntity<LocationDto> saveLocation(@AuthorizedUser UserResponseDto authorizedUser, @RequestBody LocationSaveDto locationSaveDto) throws URISyntaxException {

        LocationDto locationDto = userLocationService.saveLocationForUser(authorizedUser.getId(), locationSaveDto);

        return ResponseEntity
                .created(new URI("weather/api/user/locations/" + locationDto.getId()))
                .body(locationDto);

    }

    @GetMapping
    ResponseEntity<List<LocationDto>> getAllSavedLocations(@AuthorizedUser UserResponseDto authorizedUser) {

        List<LocationDto> savedLocations = userLocationService.getAllSavedLocations(authorizedUser.getId());

        return ResponseEntity.ok(savedLocations);
    }
}
