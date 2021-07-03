package com.phone.device.controller;

import com.phone.device.entity.Device;
import com.phone.device.entity.OnCreate;
import com.phone.device.entity.OnUpdate;
import com.phone.device.jpa.DeviceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class provides the REST endpoints for managing Devices.
 *
 * Constructor Injection is used to help unit testing, otherwise we could use field level injection too. Also
 * construction injection provides immutable objects which is thread safe in a multi-threaded environment. Even if we
 * do not have a multi-threaded environment, I would prefer to use constructor injection which is more of a best practice
 * that I learnt over the years.
 */
@RestController
@RequestMapping("/api/device")
public class DeviceController {

    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * Fetches a single Device by Id.
     * @param id The id of the Device as Long
     * @return Device
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Device> getDevice(@PathVariable @Min(1) Long id) {
        return deviceRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Creates a New Device.
     * @param device The Device to be created
     *
     */
    @PostMapping()
    public ResponseEntity<?> addDevice(@RequestBody @Validated(OnCreate.class) Device device) {
        device.setId(null);
        deviceRepository.saveAndFlush(device);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



    /**
     * List all devices
     * @return List<Device> List of Device as Json
     */
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Device>> listAll() {
        return ResponseEntity.ok(deviceRepository.findAll());
    }

    @GetMapping(value = "/paged-list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Device>> pagedList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
        return ResponseEntity.ok(deviceRepository.findAll(PageRequest.of(page, size)));
    }

    /**
     * Search by Brand Name.
     * @param searchTerm Search Term
     * @return List<Device>
     */
    @GetMapping(value = "/search/{searchTerm}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Device>> search(@PathVariable @NotBlank @Min(1) String searchTerm) {
        return ResponseEntity.ok(deviceRepository.findByBrandContainingIgnoreCase(searchTerm));
    }

    /**
     * Update Device information, partial as well as full.
     * @param device The Device Data, must contain id to be updated.
     * @return Http Status
     */
    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDevice(@Validated(OnUpdate.class) @RequestBody Device device) {
        if(device.getId() == null ) {
            return ResponseEntity.badRequest().body("{\n\"message\":\"Device Not found or Invalid Device Id.\"\n}");
        }
        if(device.getName() == null && device.getBrand() == null) {
            return ResponseEntity.badRequest().body("{\n\"message\":\"At least one of name or device value are needed.\"\n}");
        }
        Device oldDevice = deviceRepository.getById(device.getId());
        BeanUtils.copyProperties(device, oldDevice,getNullPropertyNames(device));
        System.out.println(oldDevice);
        deviceRepository.saveAndFlush(oldDevice);
        return ResponseEntity.noContent().build();
    }

    /**
     * A helper method for extracting field names with null value.
     * @param source The object from which the null field names will be extracted.
     * @return Array of String with properties that are null in the object.
     */
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null)
                emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * Deletes a Device with the given id
     * @param id Device Id
     * @return HTTP Status
     */

    @DeleteMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteDevice(@PathVariable @Min(1) @NotBlank Long id) {
        if(deviceRepository.existsById(id)) {
            deviceRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

}
