/**
 * @since 19.3.2013
 * @author Joris L�ckenga, Bernecker + Rainer Industrie Elektronik Ges.m.b.H.
 */

package com.br_automation.buoat.xddeditor.XDD.custom;

/**
 * @brief Representation of CANopen device-profiles.
 * 
 * @author Joris L�ckenga
 * */
public enum CiADeviceProfile {

    CIA401(401, "CiA 401", "Generic I/O Modules"),
    CIA402(402, "CiA 402", "Drives and Motion Control"),
    CIA404(404, "CiA 404", "Measuring devices and Closed Loop Controllers"),
    CIA405(405, "CiA 405", "IEC 61131-3 Programmable Devices"),
    CIA406(406, "CiA 406", "Rotating and Linear Encoders"),
    CIA408(408, "CiA 408", "Hydraulic Drives and Proportional Valves"),
    CIA410(401, "CiA 410", "Inclinometers"),
    CIA412(412, "CiA 412", "Medical Devices"),
    CIA413(413, "CiA 413", "Truck Gateways"),
    CIA414(414, "CiA 414", "Yarn Feeding Units (Weaving Machines)"),
    CIA415(415, "CiA 415", "Road Construction Machinery"),
    CIA416(416, "CiA 416", "Building Door Control"),
    CIA417(417, "CiA 417", "Lift Control Systems"),
    CIA418(418, "CiA 418", "Battery Modules"),
    CIA419(419, "CiA 419", "Battery Chargers"),
    CIA420(420, "CiA 420", "Extruder Downstream Devices"),
    CIA422(422, "CiA 422", "Municipal Vehicles � CleANopen"),
    CIA423(423, "CiA 423", "Railway Diesel Control Systems"),
    CIA424(424, "CiA 424", "Rail Vehicle Door Control Systems"),
    CIA425(425, "CiA 425", "Medical Diagnostic Add-on Modules"),
    CIA445(445, "CiA 445", "RFID Devices");

    private String profileDescription;
    private String profileName;
    private int value;

    /**
     * @param value
     *            Value of the Profile e.g 401,402 etc.
     * @param profileName
     *            Name of Profile e.g CiA 401.
     * @param profileDescription
     *            Description of Profile e.g "Battery Charger".
     */
    private CiADeviceProfile(int value,
        String profileName,
        String profileDescription) {
        this.value = value;
        this.profileName = profileName;
        this.profileDescription = profileDescription;
    }

    public String getProfileDescription() {
        return this.profileDescription;
    }

    public String getProfileName() {
        return this.profileName;
    }

    public int getValue() {
        return this.value;
    }

} //CiADeviceProfile
