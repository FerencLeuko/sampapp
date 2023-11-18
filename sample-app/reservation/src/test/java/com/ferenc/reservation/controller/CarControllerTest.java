package com.ferenc.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferenc.reservation.controller.dto.CarDto;
import com.ferenc.reservation.mapper.CarMapper;
import com.ferenc.reservation.mapper.CarTypeMapper;
import com.ferenc.reservation.repository.model.Car;
import com.ferenc.reservation.repository.model.CarTypeEnum;
import com.ferenc.reservation.businessservice.CarBusinessService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CarController.class)
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc(addFilters = false)
public class CarControllerTest {

    private CarTypeMapper carTypeMapper = CarController.getCarTypeMapper();
	
	private CarMapper carMapper = CarController.getCarMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private CarBusinessService carBusinessService;

    @AfterEach
    private void verifyAll(){
        Mockito.verifyNoMoreInteractions(carBusinessService);
    }

    @Test
    void testGetAvailableCars_For200() throws Exception {
        MultiValueMap<String, String> params = getMultiValueMapForDateParams();
        Mockito.when(carBusinessService.getAvailableCars(LocalDate.now(),LocalDate.now())).thenReturn(List.of(getCar()));
        mockMvc.perform( get("/cars/available").params(params))
                .andExpect(status().isOk());
        Mockito.verify(carBusinessService).getAvailableCars(LocalDate.now(),LocalDate.now());
    }

    @Test
    void testGetAvailableCars_For400() throws Exception {
        MultiValueMap<String, String> params = getMultiValueMapForDateParams();
        params.set("startDate",null);
        mockMvc.perform( get("/cars/available").params(params))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCars_For200() throws Exception {
        Mockito.when(carBusinessService.getAllCars()).thenReturn(List.of(getCar()));
        mockMvc.perform( get("/cars").param("licencePlate","ABC111"))
                .andExpect(status().isOk());
        Mockito.verify(carBusinessService).getAllCars();
    }

    @Test
    void testPostCar_For201() throws Exception {
        CarDto actual = getCarDto();
        Mockito.when(carBusinessService.createCar(carMapper.fromDto(actual)))
                .thenReturn(getCar());
        mockMvc.perform(post("/cars")
                        .contentType("application/json").content(objectMapper.writeValueAsString(actual)))
                .andExpect(status().isCreated());
        Mockito.verify(carBusinessService).createCar(carMapper.fromDto(actual));
    }

    @Test
    void testPostCar_For400() throws Exception {
        CarDto actual = getCarDto();
        actual.setLicencePlate("");
        Mockito.when(carBusinessService
				.createCar(carMapper.fromDto(actual)))
                .thenReturn(getCar());
        mockMvc.perform(post("/cars")
                        .contentType("application/json").content(objectMapper.writeValueAsString(actual)))
                .andExpect(status().isBadRequest());
    }

    private static MultiValueMap<String, String> getMultiValueMapForDateParams() {
        String startDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.put("startDate", List.of(startDate));
        params.put("endDate", List.of(endDate));
        return params;
    }

    private static CarDto getCarDto(){
        CarDto carDto = new CarDto("ABC123","Opel","Astra", com.ferenc.reservation.controller.dto.CarTypeEnum.SEDAN,5);
        return carDto;
    }

    private static Car getCar(){
        Car car = new Car("ABC123","Opel","Astra", CarTypeEnum.SEDAN,5);
        return car;
    }

}
