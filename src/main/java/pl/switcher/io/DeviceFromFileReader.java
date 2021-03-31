package pl.switcher.io;

import lombok.extern.log4j.Log4j2;
import pl.switcher.exceptions.WrongFilePropertiesError;
import pl.switcher.model.DeviceDto;
import pl.switcher.model.DeviceType;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@Log4j2
public class DeviceFromFileReader {
    private File pathToFile;
    private Scanner scanner;


    public List<DeviceDto> readPropertiesFromFile(String regex, String fileType){
        List<DeviceDto> devicePropertiesList = new ArrayList<>();
        pathToFile= new File(System.getProperty("user.dir") + "/ip." + fileType);
        try {
            scanner = new Scanner(pathToFile);
            while (scanner.hasNextLine()){
                String[] properties = scanner.nextLine().split(regex);
                DeviceDto deviceProperties = validateAndCreateDevice(properties);
                log.info("Create new devices from file properties : " + deviceProperties);
                devicePropertiesList.add(deviceProperties);
            }

        } catch (FileNotFoundException e) {
            log.error("File " + pathToFile + " doesnt exist !");
            e.getMessage();
        }
        return devicePropertiesList;
    }

    private DeviceDto validateAndCreateDevice(String[] properties) {
        String deviceName = properties[0];
        String deviceIpAddress = properties[1];
        String deviceId= properties[2];

        if(properties.length <3 || !isIPv4Address(deviceIpAddress) || !isNumeric(deviceId) || isSupportDevice( deviceName)==null) {
            log.error("Wrong parameters in file " + pathToFile);
            throw new WrongFilePropertiesError("Wrong parameters in file " + pathToFile);
        }

        return new DeviceDto(deviceName,deviceIpAddress,deviceId);
    }

    private boolean isIPv4Address(String address) {
        if (address.isEmpty()) {
            return false;
        }
        try {
            Object res = InetAddress.getByName(address);
            return res instanceof Inet4Address || res instanceof Inet6Address;
        } catch (final UnknownHostException exception) {
            log.error("Wrong Screen IP Address !");
            return false;
        }
    }

    private boolean isNumeric(String screenId){
        try {
            int Value = Integer.parseInt(screenId);
            return true;
        } catch (NumberFormatException e) {
            log.error("Wrong Screen ID !");
            return false;
        }
    }

    private static DeviceType isSupportDevice(String deviceName) {
        DeviceType[] values = DeviceType.values();
        return Arrays.stream(values).filter(d->deviceName.equalsIgnoreCase(d.getName())).findAny().orElse(null);
    }


}
