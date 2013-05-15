/**
 * @since 19.3.2013
 * @author Joris L�ckenga, Bernecker + Rainer Industrie Elektronik Ges.m.b.H.
 */

package com.br_automation.buoat.xddeditor.XDD.custom;

import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;

import com.br_automation.buoat.xddeditor.XDD.DocumentRoot;
import com.br_automation.buoat.xddeditor.XDD.ISO15745ProfileContainerType;
import com.br_automation.buoat.xddeditor.XDD.ISO15745ProfileType;
import com.br_automation.buoat.xddeditor.XDD.IdentityType;
import com.br_automation.buoat.xddeditor.XDD.ObjectListType;
import com.br_automation.buoat.xddeditor.XDD.ProfileBodyDataType;
import com.br_automation.buoat.xddeditor.XDD.ProfileHeaderDataType;
import com.br_automation.buoat.xddeditor.XDD.TApplicationLayers;
import com.br_automation.buoat.xddeditor.XDD.TCNFeatures;
import com.br_automation.buoat.xddeditor.XDD.TDeviceIdentity;
import com.br_automation.buoat.xddeditor.XDD.TGeneralFeatures;
import com.br_automation.buoat.xddeditor.XDD.TNetworkManagement;
import com.br_automation.buoat.xddeditor.XDD.TObject;
import com.br_automation.buoat.xddeditor.XDD.TVersion;
import com.br_automation.buoat.xddeditor.XDD.VersionTypeType;
import com.br_automation.buoat.xddeditor.XDD.XDDFactory;
import com.br_automation.buoat.xddeditor.XDD.XDDPackage;
import com.br_automation.buoat.xddeditor.XDD.util.XDDResourceFactoryImpl;

/**
 * @brief Provides methods to create an Initial model based on data configured
 *        in the Wizard.
 * 
 *        Gets the user-input from the CustomXDDWizard-Pages and adds the needed
 *        objects to the root. Also adds the correct time,user etc.
 * 
 * @author Joris L�ckenga
 */
public class ModelLoader {

    /**
     * @brief Fetches the given resource by its name and searches for objects
     *        with matching indices.
     * @param resourceName
     *            Name String of the Resource without extension.
     * @param neededObjects
     *            Array of indices to search in the resource.
     * @return A {@link List} containing {@link TObject} elements with indices
     *         found in the specified resource.
     */
    public static List<TObject> getTObjectFromResource(String resourceName,
        List<Integer> neededObjects) {
        //Get RootObject of Template
        List<TObject> foundObjects = new ArrayList<TObject>();
        //create encoding Map
        Map<Object, Object> options = new HashMap<Object, Object>();
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        URL resourcepath = ModelLoader.class.getResource("/resources/" + resourceName + ".xdd");
        if (resourcepath == null)
            return null;
        //get new Resource
        ResourceSet resSet = new ResourceSetImpl();
        resSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("xdd", new XDDResourceFactoryImpl());

        //Get the File and root object
        URI fileuri = URI.createURI(resourcepath.toString());
        Resource resource = resSet.getResource(fileuri, true);
        DocumentRoot root = (DocumentRoot) resource.getContents().get(0);

        //Go through every element and check for Index...
        TreeIterator<EObject> treeIterator = root.eAllContents();
        EObject currentObject;

        while (treeIterator.hasNext()) {
            currentObject = treeIterator.next();
            if (currentObject instanceof TObject) {
                if (neededObjects != null) { //if no Inidices are given,extract all objects of the Resource
                    for (Integer index : neededObjects)
                        if (index.compareTo(new BigInteger(((TObject) currentObject).getIndex()) // NOPMD by lueckengaj on 18.04.13 09:23
                            .intValue()) == 0) {
                            foundObjects.add((TObject) currentObject);
                        } else
                            treeIterator.prune();
                } else {
                    foundObjects.add((TObject) currentObject);
                    treeIterator.prune();
                }
            }
        }
        return foundObjects;
    } //getResourceEObjects

    /**
     * @brief Loads a new model based on configuration in the Wizard.
     * @param wizardTemplatePage
     *            The first configuration page.
     * @param wizardConfigurationPage1
     *            The advanced wizard page with userdata.
     * @return DocumentRoot With appended data.
     */
    public static DocumentRoot loadXDD(WizardTemplatePage wizardTemplatePage,
        WizardConfigurationPage1 wizardConfigurationPage1) {
        URL resourcepath;

        //create encoding Map
        Map<Object, Object> options = new HashMap<Object, Object>();
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");

        //get new Resource
        ResourceSet resSet = new ResourceSetImpl();
        resSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("xdd", new XDDResourceFactoryImpl());

        //check which Template is used (static etc.)
        if (wizardTemplatePage.getLoadEmpty())
            return ModelLoader.getEmptyModel();
        else {
            String choice = wizardTemplatePage.getTemplateCombo().getText();

            if (choice.contentEquals("Default device"))
                resourcepath = wizardConfigurationPage1.getClass().getResource(
                    "/resources/default_template.xdd");

            else if (choice.contentEquals("Default extended device"))
                resourcepath = wizardConfigurationPage1.getClass().getResource(
                    "/resources/default_template_extended.xdd");

            else
                resourcepath = wizardConfigurationPage1.getClass().getResource(
                    "/resources/default_template_static.xdd");
        }

        URI fileuri = URI.createURI(resourcepath.toString());
        Resource resource = resSet.getResource(fileuri, true);
        DocumentRoot root = (DocumentRoot) resource.getContents().get(0);

        if (wizardTemplatePage.isConfigurationWizardStatus())
            ModelLoader.appendUserData(root, wizardConfigurationPage1);
        else
            ModelLoader.appendMetaData(root, wizardConfigurationPage1);
        return root;
    } //loadXDD

    /**
     * @brief Add/Remove objects required for IP-Support.
     * 
     * @param status
     *            <code>True</code> to add objects, <code>false</code>
     *            otherwise. <code>False</code> is not implemented yet.
     * 
     * @param root
     *            The DocumentRoot where IP-support objects should be set.
     */
    public static void setIPSupportObjects(boolean status, DocumentRoot root) {

        //Get all iPSupportIndices
        List<Integer> iPSupportObjects = new ArrayList<Integer>();
        iPSupportObjects.add(ObjectDictionaryEntry.NWL_HOSTNAME_VSTR);
        iPSupportObjects.add(ObjectDictionaryEntry.NWL_IPGROUP_TYPE);
        int i = ObjectDictionaryEntry.NWL_IPADDRTABLE_REC_MIN;
        while (i <= ObjectDictionaryEntry.NWL_IPADDRTABLE_REC_MAX) {
            iPSupportObjects.add(i);
            i++;
        }
        if (status) {
            List<TObject> objectsToAdd = ModelLoader.getTObjectFromResource(
                "ipSupportTemplate", iPSupportObjects);
            //Get Current Objects of the Resource
            List<ObjectListType> objectList = XDDUtilities.findEObjects(
                root, XDDPackage.eINSTANCE.getObjectListType());
            EList<TObject> resourceObjects;
            if (!objectList.isEmpty())
                resourceObjects = objectList.get(0).getObject();
            else
                return;
            //Set Objects to Resource
            XDDUtilities.addTObjects(resourceObjects, objectsToAdd);
        }

        /*Only for this commit*/
        TreeIterator<EObject> iterator = root.eAllContents();
        while (iterator.hasNext()) {
            Object currentObject = iterator.next();
            if (currentObject instanceof TGeneralFeatures) {
                ((TGeneralFeatures) currentObject).setNWLIPSupport(status);
                break;
            }
        }

    }

    /**
     * @brief Add/Remove objects required for Multi-ASnd
     * 
     * @param status
     *            <code>True</code> to add objects, <code>false</code>
     *            otherwise. <code>False</code> is not implemented yet.
     * @param root
     *            The DocumentRoot where Multi-ASnd objects should be set.
     */
    public static void setMultipleASndObjects(boolean status, DocumentRoot root) {

        List<Integer> multipleASndObjects = new ArrayList<Integer>();
        multipleASndObjects.add(ObjectDictionaryEntry.NMT_MNCYCLETIMING_REC);
        multipleASndObjects.add(ObjectDictionaryEntry.NMT_NODEASSIGNMENT_AU32);
        multipleASndObjects.add(ObjectDictionaryEntry.NMT_FEATUREFLAGS_U32);

        if (status) {
            List<TObject> objectsToAdd = ModelLoader.getTObjectFromResource(
                "MultiASndTemplate", multipleASndObjects);
            //Get Current Objects of the Resource
            EList<TObject> resourceObjects = XDDUtilities.getTObjectList(root);
            //Set Objects to Resource
            XDDUtilities.addTObjects(resourceObjects, objectsToAdd);
            //SET Properties in FeatureFlags & TCNFeatures/GeneralFeatures
            XDDUtilities.setFeatureFlag(status, 16, root);
        }
    }

    /**
     * @brief Add/Remove objects required for Multiplexing-feature
     * 
     * @param status
     *            <code>True</code> to add objects, <code>false</code>
     *            otherwise. <code>False</code> is not implemented yet.
     * @param root
     *            The DocumentRoot where Multiplexing-feature objects should be
     *            set.
     */
    public static void setMultiplexFeatureObjects(boolean status, DocumentRoot root) {
        List<TCNFeatures> tCNfeaturesList = XDDUtilities.findEObjects(
            root, XDDPackage.eINSTANCE.getTCNFeatures());
        List<ObjectListType> objectsList = XDDUtilities.findEObjects(
            root, XDDPackage.eINSTANCE.getObjectListType());

        if (objectsList.size() == 0 || tCNfeaturesList.size() == 0)
            return;

        TCNFeatures cnFeatures = tCNfeaturesList.get(0);
        cnFeatures.setDLLCNFeatureMultiplex(status);
        EList<TObject> objects = objectsList.get(0).getObject();

        for (TObject tObject : objects)
            if (ObjectDictionaryEntry.NMT_FEATUREFLAGS_U32 == new BigInteger(tObject.getIndex()) // NOPMD by lueckengaj on 18.04.13 09:23
                .intValue())
                if (status)
                    tObject.setDefaultValue("0x"
                        + Long.toHexString((Long.decode(tObject.getDefaultValue()) | 512)));
                else
                    tObject.setDefaultValue("0x"
                        + Long.toHexString((Long.decode(tObject.getDefaultValue()) & ~512)));

        List<Integer> multiplexFeatureObjects = new ArrayList<Integer>(3);
        multiplexFeatureObjects.add(ObjectDictionaryEntry.NMT_ISOCHSLOTASSIGN_AU8);
        multiplexFeatureObjects.add(ObjectDictionaryEntry.NMT_MULTIPLCYCLEASSIGN_AU8);
        multiplexFeatureObjects.add(ObjectDictionaryEntry.NMT_CYCLETIMING_REC);
        multiplexFeatureObjects.add(ObjectDictionaryEntry.NMT_FEATUREFLAGS_U32);

        if (status) {
            //Get Needed Objects for Multiplex Support
            List<TObject> objectsToAdd = ModelLoader.getTObjectFromResource(
                "multiplexFeatureTemplate", multiplexFeatureObjects);
            //Set Objects to Resource
            XDDUtilities.addTObjects(objectsList.get(0).getObject(), objectsToAdd);
        }

    }

    /**
     * @brief Add/Remove objects required for PRes-Chaining feature
     * 
     * @param status
     *            <code>True</code> to add objects, <code>false</code>
     *            otherwise. <code>False</code> is not implemented yet.
     * @param root
     *            The DocumentRoot where PRes-Chaining objects should be set.
     */
    public static void setPRespChainingObjects(boolean status, DocumentRoot root) {

        List<Integer> prespChainingObjects = new ArrayList<Integer>();
        prespChainingObjects.add(ObjectDictionaryEntry.NMT_RELATIVELATENCYDIFF_AU32);
        prespChainingObjects.add(ObjectDictionaryEntry.NMT_CYCLETIMING_REC);
        prespChainingObjects.add(ObjectDictionaryEntry.NMT_NODEASSIGNMENT_AU32);
        prespChainingObjects.add(ObjectDictionaryEntry.NMT_FEATUREFLAGS_U32);

        if (status) {
            List<TObject> objectsToAdd = ModelLoader.getTObjectFromResource(
                "PrespChainingTemplate", prespChainingObjects);
            //Get Current Objects of the Resource
            EList<TObject> resourceObjects = XDDUtilities.getTObjectList(root);
            //Set Objects to Resource
            XDDUtilities.addTObjects(resourceObjects, objectsToAdd);
            //Set Properties in FeatureFlags, TCN- / General Features
            XDDUtilities.setFeatureFlag(status, 18, root);
        }
    }

    /**
     * @brief Appends reduced data when advanced wizard is not used.
     * @param root
     *            DocumentRoot of the new Resource.
     * @param wizardConfigurationPage1
     *            Instance of the WizardConfigurationPage fetch user-input.
     * @return DocumentRoot with appended meta-data gathered from the system.
     */
    private static DocumentRoot appendMetaData(DocumentRoot root,
        WizardConfigurationPage1 wizardConfigurationPage1) {

        EList<ISO15745ProfileType> profiles = root.getISO15745ProfileContainer()
            .getISO15745Profile();
        ISO15745ProfileType profile1 = profiles.get(0);
        ISO15745ProfileType profile2 = profiles.get(1);
        ProfileHeaderDataType header1 = profile1.getProfileHeader();
        header1.setProfileIdentification("Powerlink_"
            + wizardConfigurationPage1.getDeviceNameString() + "_Profile");
        header1.setProfileName(wizardConfigurationPage1.getDeviceNameString() + " Profile");

        //Setzen der Body-Werte im Profil 1
        ProfileBodyDataType body1 = profile1.getProfileBody();
        body1.setFileCreationDate(wizardConfigurationPage1.getCreationDateXML());
        body1.setFileCreationTime(wizardConfigurationPage1.getCreationTimeXML());
        body1.setFileCreator(wizardConfigurationPage1.getCreatorString());
        body1.setFileName(wizardConfigurationPage1.getFileNameString());
        body1.setFileModificationDate(wizardConfigurationPage1.getCreationDateXML());
        body1.setFileModificationTime(wizardConfigurationPage1.getCreationTimeXML());
        body1.setFileVersion(wizardConfigurationPage1.getFileVersionString());

        //Setzen der Body-Werte im Profil 2
        ProfileBodyDataType body2 = profile2.getProfileBody();
        body2.setFileCreationDate(wizardConfigurationPage1.getCreationDateXML());
        body2.setFileCreationTime(wizardConfigurationPage1.getCreationTimeXML());
        body2.setFileCreator(wizardConfigurationPage1.getCreatorString());
        body2.setFileName(wizardConfigurationPage1.getFileNameString());
        body2.setFileModificationDate(wizardConfigurationPage1.getCreationDateXML());
        body2.setFileModificationTime(wizardConfigurationPage1.getCreationTimeXML());
        body2.setFileVersion(wizardConfigurationPage1.getFileNameString());

        //For further Saves -> Give Utilites the creatorname
        XDDUtilities.setCreator(wizardConfigurationPage1.getCreatorString());

        return root;
    }

    /**
     * @brief Appends the userdata from WizardConfigurationPage1.
     * @param root
     *            DocumentRoot of the new Resource.
     * @param wizardConfigurationPage1
     *            Instance of the WizardConfigurationPage fetch user-input.
     * @return DocumentRoot with appended userdata from WizardConfigurationPage.
     */
    private static DocumentRoot appendUserData(DocumentRoot root,
        WizardConfigurationPage1 wizardConfigurationPage1) {

        EList<ISO15745ProfileType> profiles = root.getISO15745ProfileContainer()
            .getISO15745Profile();
        ISO15745ProfileType profile1 = profiles.get(0);
        ISO15745ProfileType profile2 = profiles.get(1);

        ProfileHeaderDataType header1 = profile1.getProfileHeader();
        header1.setProfileIdentification("Powerlink_"
            + wizardConfigurationPage1.getDeviceNameString() + "_Profile");
        header1.setProfileName(wizardConfigurationPage1.getDeviceNameString() + " Profile");
        XDDUtilities.setCreator(wizardConfigurationPage1.getCreatorString());
        //Setzen der Body-Werte im Profil 1
        ProfileBodyDataType body1 = profile1.getProfileBody();
        body1.setFileCreationDate(wizardConfigurationPage1.getCreationDateXML());
        body1.setFileCreationTime(wizardConfigurationPage1.getCreationTimeXML());
        body1.setFileCreator(wizardConfigurationPage1.getCreatorString());
        body1.setFileName(wizardConfigurationPage1.getFileNameString());
        body1.setFileModificationDate(wizardConfigurationPage1.getCreationDateXML());
        body1.setFileModificationTime(wizardConfigurationPage1.getCreationTimeXML());
        body1.setFileModifiedBy(wizardConfigurationPage1.getCreatorString());
        body1.setFileVersion(wizardConfigurationPage1.getFileVersionString());

        //Setzen der Vendor-Werte im Body 1
        EList<EObject> bodyContents = body1.eContents();
        EObject identity = bodyContents.get(0);
        TDeviceIdentity tDeviceIdentity = (TDeviceIdentity) identity;
        tDeviceIdentity.getVendorName().setValue(wizardConfigurationPage1.getVendorNameString());
        tDeviceIdentity.getVendorID().setValue(wizardConfigurationPage1.getVendorIDString());
        tDeviceIdentity.getProductName().setValue(wizardConfigurationPage1.getProductNameString());
        tDeviceIdentity.getVersion().get(0)
            .setValue(wizardConfigurationPage1.getHardwareversString());
        tDeviceIdentity.getVersion().get(1)
            .setValue(wizardConfigurationPage1.getSoftwareversString());
        tDeviceIdentity.getVersion().add(XDDFactory.eINSTANCE.createTVersion());
        TVersion fwVersion = tDeviceIdentity.getVersion().get(2);
        fwVersion.setVersionType(VersionTypeType.HW);
        fwVersion.setReadOnly(true);
        fwVersion.setValue(wizardConfigurationPage1.getFirmwareversString());

        //Set unsetted Value of ProductID of template
        tDeviceIdentity.setProductID(XDDFactory.eINSTANCE.createTProductID());
        tDeviceIdentity.getProductID().setValue(wizardConfigurationPage1.getProductIDString());

        //Setzen der Body-Werte im Profil 2
        ProfileBodyDataType body2 = profile2.getProfileBody();
        body2.setFileCreationDate(wizardConfigurationPage1.getCreationDateXML());
        body2.setFileCreationTime(wizardConfigurationPage1.getCreationTimeXML());
        body2.setFileCreator(wizardConfigurationPage1.getCreatorString());
        body2.setFileName(wizardConfigurationPage1.getFileNameString());
        body2.setFileModificationDate(wizardConfigurationPage1.getCreationDateXML());
        body2.setFileModificationTime(wizardConfigurationPage1.getCreationTimeXML());
        body2.setFileModifiedBy(wizardConfigurationPage1.getCreatorString());
        body2.setFileVersion(wizardConfigurationPage1.getFileVersionString());

        //Setzen der Vendor.ID im Communication Body
        EList<EObject> bodyContents2 = body2.eContents();
        TApplicationLayers apllayers = (TApplicationLayers) bodyContents2.get(0);
        IdentityType identity2 = apllayers.getIdentity();
        identity2.getVendorID().setValue(wizardConfigurationPage1.getVendorIDString());
        identity2.setProductID(XDDFactory.eINSTANCE.createTProductID());
        identity2.getProductID().setValue(wizardConfigurationPage1.getProductIDString());

        //Setzen der General Features aus dem Wizard
        TNetworkManagement tnmg = (TNetworkManagement) body2.eContents().get(2);
        TGeneralFeatures generalFeatures = tnmg.getGeneralFeatures();
        generalFeatures.setNMTBootTimeNotActive(wizardConfigurationPage1.getNMTBootTimeNotActive());
        generalFeatures.setNMTCycleTimeMax(wizardConfigurationPage1.getNMTCycleTimeMax());
        generalFeatures.setNMTCycleTimeMin(wizardConfigurationPage1.getNMTCycleTimeMin());
        generalFeatures.setNMTErrorEntries(wizardConfigurationPage1.getNMTErrorEntries());
        generalFeatures.setDLLFeatureMN(false);

        //Setzen der CN Features aus dem Wizard
        TCNFeatures cnFeatures = tnmg.getCNFeatures();

        cnFeatures.setDLLCNPResChaining(wizardConfigurationPage1.isResponseChaining());
        cnFeatures.setNMTCNSoC2PReq(wizardConfigurationPage1.getNMTCNSoC2PReq());

        cnFeatures.setDLLCNFeatureMultiplex(wizardConfigurationPage1.isCnMultiplexFeature());
        if (wizardConfigurationPage1.isCnMultiplexFeature()) {
            ModelLoader.setMultiplexFeatureObjects(true, root);
        }

        if (wizardConfigurationPage1.isNWLIPSupport())
            ModelLoader.setIPSupportObjects(true, root);

        if (wizardConfigurationPage1.isResponseChaining())
            ModelLoader.setPRespChainingObjects(true, root);

        if (wizardConfigurationPage1.isMultipleASnd())
            ModelLoader.setPRespChainingObjects(true, root);

        return root;
    }

    /**
     * @brief Creates empty Standard-Model.
     * @return An empty model of an XDD-File.
     */
    private static DocumentRoot getEmptyModel() {
        DocumentRoot root = XDDFactory.eINSTANCE.createDocumentRoot();
        ISO15745ProfileContainerType container = XDDFactory.eINSTANCE
            .createISO15745ProfileContainerType();
        ISO15745ProfileType profile = XDDFactory.eINSTANCE.createISO15745ProfileType();
        root.setISO15745ProfileContainer(container);
        root.getISO15745ProfileContainer().getISO15745Profile().add(profile);
        profile.setProfileBody(XDDFactory.eINSTANCE
            .createProfileBodyCommunicationNetworkPowerlink());
        profile.setProfileHeader(XDDFactory.eINSTANCE.createProfileHeaderDataType());
        return root;
    }
} //InitialModelLoader
