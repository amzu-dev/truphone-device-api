package com.truphone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phone.device.DeviceManagerApplication;
import com.phone.device.entity.Device;
import com.phone.device.jpa.DeviceRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = DeviceManagerApplication.class)
@AutoConfigureMockMvc
public class DeviceControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceRepository deviceRepository;

    @Test
    public void create_device_empty_values_400() throws Exception {
        String device = "{}";

        mockMvc.perform(post("/api/device")
                    .content(device)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status",is(400)))
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors",hasItem("brand is required.")))
                    .andExpect(jsonPath("$.errors",hasItem("device is required.")));

        verify(deviceRepository,times(0)).saveAndFlush(any(Device.class));
    }

    @Test
    public void create_device_no_brand_400() throws Exception {
        String device = "{\n" +
                "    \"name\":\"3310\"\n" +
                "}";

        mockMvc.perform(post("/api/device")
                    .content(device)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status",is(400)))
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors",hasItem("brand is required.")));

        verify(deviceRepository,times(0)).saveAndFlush(any(Device.class));
    }

    @Test
    public void create_device_no_name_400() throws Exception {
        String device = "{\n" +
                "    \"brand\":\"nokia\"\n" +
                "}";

        mockMvc.perform(post("/api/device")
                .content(device)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status",is(400)))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors",hasItem("device is required.")));
        verify(deviceRepository,times(0)).saveAndFlush(any(Device.class));
    }

    @Test
    public void create_device_invalid_json_body() throws Exception {
        String device = "{\n" +
                "    \"name\":\"3310\",\n" +
                "    \"brand\":nokia\n" +
                "}";
        mockMvc.perform(post("/api/device")
                .content(device)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is4xxClientError());

        verify(deviceRepository,times(0)).saveAndFlush(any(Device.class));

    }

    @Test
    public void create_device_success_200() throws Exception {
        String device = "{\n" +
                "    \"name\":\"3310\",\n" +
                "    \"brand\":\"nokia\"\n" +
                "}";
        mockMvc.perform(post("/api/device")
                .content(device)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(deviceRepository,times(1)).saveAndFlush(any(Device.class));

    }

    @Test
    public void update_device_no_id_400() throws Exception {

        String device = "{\n" +
                "    \"name\":\"3310\",\n" +
                "    \"brand\":\"nokia\"\n" +
                "}";
        mockMvc.perform(patch("/api/device")
                .content(device)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Device Not found or Invalid Device Id.")));

        verify(deviceRepository,times(0)).saveAndFlush(any(Device.class));

    }

    @Test
    public void update_device_no_name_200() throws Exception {

        String device = "{\n" +
                "    \"id\":1," +
                "    \"brand\":\"nokia\"\n" +
                "}";
        Device oldDevice = new Device();
        oldDevice.setId(Long.getLong("1"));
        oldDevice.setName("Galaxy S5");
        oldDevice.setBrand("Samsung");
        Mockito.when(deviceRepository.getById(anyLong())).thenReturn(oldDevice);
        mockMvc.perform(patch("/api/device")
                .content(device)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deviceRepository,times(1)).getById(anyLong());
        verify(deviceRepository,times(1)).saveAndFlush(any(Device.class));

    }

    @Test
    public void update_device_no_brand_200() throws Exception {

        String device = "{\n" +
                "    \"id\":1," +
                "    \"name\":\"3310\"\n" +
                "}";
        Device oldDevice = new Device();
        oldDevice.setId(Long.getLong("1"));
        oldDevice.setName("Galaxy S5");
        oldDevice.setBrand("Samsung");
        Mockito.when(deviceRepository.getById(anyLong())).thenReturn(oldDevice);
        mockMvc.perform(patch("/api/device")
                .content(device)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deviceRepository,times(1)).getById(anyLong());
        verify(deviceRepository,times(1)).saveAndFlush(any(Device.class));

    }

    @Test
    public void update_device_no_values_400() throws Exception {

        String device = "{}";
        mockMvc.perform(patch("/api/device")
                .content(device)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("Device Not found or Invalid Device Id.")));

        verify(deviceRepository,times(0)).getById(anyLong());
        verify(deviceRepository,times(0)).saveAndFlush(any(Device.class));

    }

    @Test
    public void update_device_no_id_with_device_and_name_400() throws Exception {

        String device = "{\n" +
                "    \"name\":\"3310\",\n" +
                "    \"brand\":\"nokia\n" +
                "}";
        mockMvc.perform(patch("/api/device")
                .content(device)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(deviceRepository,times(0)).getById(anyLong());
        verify(deviceRepository,times(0)).saveAndFlush(any(Device.class));

    }

    @Test
    public void update_device_only_id_400() throws Exception {

        String device = "{\n" +
                "    \"id\":1\n" +
                "}";
        mockMvc.perform(patch("/api/device")
                .content(device)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message",is("At least one of name or device value are needed.")));

        verify(deviceRepository,times(0)).getById(anyLong());
        verify(deviceRepository,times(0)).saveAndFlush(any(Device.class));

    }

    @Test
    public void update_device_success_200() throws Exception {

        String device = "{\n" +
                "    \"id\":1," +
                "    \"name\":\"3310\",\n" +
                "    \"brand\":\"nokia\"\n" +
                "}";
        Device oldDevice = new Device();
        oldDevice.setId(1L);
        oldDevice.setBrand("nokia");
        oldDevice.setName("5510");
        Mockito.when(deviceRepository.getById(1L)).thenReturn(oldDevice);
        mockMvc.perform(patch("/api/device")
                .content(device)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deviceRepository,times(1)).getById(anyLong());
        verify(deviceRepository,times(1)).saveAndFlush(any(Device.class));
    }

    @Test
    public void get_device_no_id_400() throws Exception {

        mockMvc.perform(get("/api/device")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isMethodNotAllowed());
        verify(deviceRepository,times(0)).getById(anyLong());

    }

    @Test
    public void get_device_wrong_id_400() throws Exception {

        mockMvc.perform(get("/api/device/adfadsf")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(deviceRepository,times(0)).getById(anyLong());

    }

    @Test
    public void get_device_blank_or_space_id_500() throws Exception {

        mockMvc.perform(get("/api/device/  ")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isInternalServerError());
        verify(deviceRepository,times(0)).getById(anyLong());

    }

    @Test
    public void get_device_invalid_or_wrong_id_404() throws Exception {

        mockMvc.perform(get("/api/device/8888")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(deviceRepository,times(1)).findById(anyLong());

    }

    @Test
    public void get_device_valid_id_404() throws Exception {

        mockMvc.perform(get("/api/device/8888")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());
        verify(deviceRepository,times(1)).findById(anyLong());

    }

    @Test
    public void get_device_valid_200() throws Exception {
        Device device = new Device();
        device.setId(1L);
        device.setBrand("nokia");
        device.setName("5510");
        Mockito.when(deviceRepository.findById(1L)).thenReturn(java.util.Optional.of(device));
        mockMvc.perform(get("/api/device/1")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
        verify(deviceRepository,times(1)).findById(anyLong());

    }

    @Test
    public void delete_wrong_device_id_400() throws Exception {
        mockMvc.perform(delete("/api/device/11")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(deviceRepository,times(0)).deleteById(anyLong());

    }

    @Test
    public void delete_device_id_200() throws Exception {
        Mockito.when(deviceRepository.existsById(11L)).thenReturn(true);
        doNothing().when(deviceRepository).deleteById(11L);
        mockMvc.perform(delete("/api/device/11")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(deviceRepository,times(1)).deleteById(anyLong());
    }

    @Test
    public void list_device_400() throws Exception {
        mockMvc.perform(get("/api/device/list/1asdfasd")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(deviceRepository,times(0)).findAll();
    }

    @Test
    public void list_device_200() throws Exception {
        mockMvc.perform(get("/api/device/list")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        verify(deviceRepository,times(1)).findAll();
    }

    @Test
    public void search_device_200() throws Exception {
        Device device1 = new Device();
        Device device2 = new Device();
        device1.setId(1L);
        device1.setName("iPhone 15");
        device1.setBrand("Apple");

        device2.setId(2L);
        device2.setName("Galaxy S4");
        device2.setBrand("Samsung");

        Mockito.when(deviceRepository.findByBrandContainingIgnoreCase(anyString())).thenReturn(List.of(device1));
        mockMvc.perform(get("/api/device/search/sa")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$[0].brand",is("Apple")));

        verify(deviceRepository,times(1)).findByBrandContainingIgnoreCase(anyString());
    }

}
