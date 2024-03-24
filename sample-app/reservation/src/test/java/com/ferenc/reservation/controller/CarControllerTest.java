package com.ferenc.reservation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.format.DateTimeFormatter;
import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ferenc.reservation.AbstractTest;
import com.ferenc.reservation.businessservice.CarBusinessService;
import com.ferenc.reservation.controller.dto.CarDto;
import com.ferenc.reservation.mapper.CarMapper;
import com.ferenc.reservation.mapper.CarTypeMapper;
import com.ferenc.reservation.repository.model.Car;
import com.ferenc.reservation.repository.model.CarTypeEnum;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = CarController.class)
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc(addFilters = false)
class CarControllerTest extends AbstractTest {

    private CarTypeMapper carTypeMapper = CarController.getCarTypeMapper();

    private CarMapper carMapper = CarController.getCarMapper();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarBusinessService carBusinessService;

    private static MultiValueMap<String, String> getMultiValueMapForDateParams() {
        String startDate = START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String endDate = END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.put("startDate", List.of(startDate));
        params.put("endDate", List.of(endDate));
        return params;
    }

    private static CarDto getCarDto() {
        CarDto carDto = new CarDto(LICENCE_PLATE, "Opel", "Astra", com.ferenc.reservation.controller.dto.CarTypeEnum.SEDAN, 5);
        return carDto;
    }

    private static Car getCar() {
        Car car = new Car(LICENCE_PLATE, "Opel", "Astra", CarTypeEnum.SEDAN, 5);
        return car;
    }

    @AfterEach
    public void verifyAll() {
        Mockito.verifyNoMoreInteractions(carBusinessService);
    }

    @Test
    void testGetAvailableCars_For200() throws Exception {
        MultiValueMap<String, String> params = getMultiValueMapForDateParams();
        Mockito.when(carBusinessService.getAvailableCars(START_DATE, END_DATE)).thenReturn(List.of(getCar()));
        mockMvc.perform(get("/cars/available").params(params))
                .andExpect(status().isOk());
        Mockito.verify(carBusinessService).getAvailableCars(START_DATE, END_DATE);
    }

    @Test
    void testGetAvailableCars_For400() throws Exception {
        MultiValueMap<String, String> params = getMultiValueMapForDateParams();
        params.set("startDate", null);
        mockMvc.perform(get("/cars/available").params(params))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCar_For200() throws Exception {
        Mockito.when(carBusinessService.getCar(LICENCE_PLATE)).thenReturn(getCar());
        mockMvc.perform(get("/cars/{licencePlate}", LICENCE_PLATE))
                .andExpect(status().isOk());
        Mockito.verify(carBusinessService).getCar(LICENCE_PLATE);
    }

    @Test
    void testGetCars_For200() throws Exception {
        Mockito.when(carBusinessService.getAllCars()).thenReturn(List.of(getCar()));
        mockMvc.perform(get("/cars"))
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
        mockMvc.perform(post("/cars")
                        .contentType("application/json").content(objectMapper.writeValueAsString(actual)))
                .andExpect(status().isBadRequest());
    }

}
