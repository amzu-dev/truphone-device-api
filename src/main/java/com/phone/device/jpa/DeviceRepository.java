package com.phone.device.jpa;

import com.phone.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * There will be a question on why use JpaRepository and n ot CrudRepository?
 * JpaRepository is a little more than CrudRepository as it extends PageAndSortingRepository which in turn extends CrudRepository and
 * is required for listing the Devices. This can be helpful when we want to implement Pageable or pagination.
 * JpaRepository is also helpful in case of batch operations like deleteInBatch().
 * JpaRepository returns a List<Device> instead of Iterable<Device> in findAll, which is a bit convenient.
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByBrandContainingIgnoreCase(String name);

}
