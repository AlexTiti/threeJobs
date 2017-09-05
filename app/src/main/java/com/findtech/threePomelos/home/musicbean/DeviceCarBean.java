package com.findtech.threePomelos.home.musicbean;

/**
 * <pre>
 *
 *   author   :   Alex
 *   e_mail   :   18238818283@sina.cn
 *   timr     :   2017/06/22
 *   desc     :
 *   version  :   V 1.0.5
 */
public class DeviceCarBean  {

    private String deviceName;
    private String deviceaAddress;
    private String deviceType;
    private String functionType;
    private String company;

    public String getCompany() {
        return company;
    }

    public String getFunctionType() {
        return functionType;
    }

    public DeviceCarBean(String deviceName, String deviceaAddress, String deviceType , String functionType ,String company) {
        this.deviceName = deviceName;
        this.deviceaAddress = deviceaAddress;
        this.deviceType = deviceType;
        this.functionType = functionType;
        this.company = company;

    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceaAddress() {
        return deviceaAddress;
    }

    public void setDeviceaAddress(String deviceaAddress) {
        this.deviceaAddress = deviceaAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }


}
