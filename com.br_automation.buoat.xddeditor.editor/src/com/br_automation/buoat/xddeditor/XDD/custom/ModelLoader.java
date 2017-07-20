/**
 * @since 19.3.2013
 * @author Joris L�ckenga, Bernecker + Rainer Industrie Elektronik Ges.m.b.H.
 */

package com.br_automation.buoat.xddeditor.XDD.custom;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

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

/**
 * @brief Provides methods to create an Initial model based on data configured
 *        in the Wizard.
 *
 *        Gets the user-input from the CustomXDDWizard-Pages and adds the needed
 *        objects to the root. Also adds the correct time,user etc.
 *
 * @author Joris L�ckenga
 */
public final class ModelLoader {

	/**
	 * @brief ModelLoader provides only static methods.
	 */
	private ModelLoader() {
	}

	/**
	 * @brief Loads a new model based on the configuration in the Wizard.
	 * @param wizardTemplatePage
	 *            The first configuration page.
	 * @param wizardConfigurationPage1
	 *            The advanced wizard page with userdata.
	 * @return DocumentRoot with appended data.
	 */
	public static DocumentRoot createXDDFromWizardData(WizardTemplatePage wizardTemplatePage,
			WizardConfigurationPage1 wizardConfigurationPage1) {
		// check which Template is used (static etc.)
		String resourceName;
		if (wizardTemplatePage.getLoadEmpty())
			return ModelLoader.getEmptyModel();
		else {
			String choice = wizardTemplatePage.getTemplateCombo().getText();
			if (choice.contentEquals("Default device")) //$NON-NLS-1$
				resourceName = Messages.modelLoader_resourceTemplate_XDDdefault;
			else if (choice.contentEquals("Default extended device")) //$NON-NLS-1$
				resourceName = Messages.modelLoader_resourceTemplate_XDDextended;
			else
				resourceName = Messages.modelLoader_resourceTemplate_XDDstatic;
		}
		DocumentRoot root = XDDUtilities.loadXDD(ModelLoader.class.getResource(resourceName));

		if (wizardTemplatePage.isConfigurationWizardStatus())
			ModelLoader.appendUserData(root, wizardConfigurationPage1);
		else
			ModelLoader.appendMetaData(root, wizardConfigurationPage1);
		return root;
	} // createXDDFromWizardData

	/**
	 * @brief Add/Remove objects required for IP-Support.
	 *
	 * @param status
	 *            <code>True</code> to add objects, <code>false</code>
	 *            otherwise. <code>False</code> is not implemented yet.
	 * @param root
	 *            The DocumentRoot where IP-Support objects should be set.
	 */
	public static void setIPSupportObjects(boolean status, DocumentRoot root) {
		// Get all iPSupportIndices
		List<Integer> ipSupportObjects = new ArrayList<Integer>();
		ipSupportObjects.add(EPLGeneralConstants.NWL_HOSTNAME_VSTR);
		ipSupportObjects.add(EPLGeneralConstants.NWL_IPGROUP_TYPE);
		for (int i = EPLGeneralConstants.NWL_IPADDRTABLE_REC_MIN; i <= EPLGeneralConstants.NWL_IPADDRTABLE_REC_MAX; i++)
			ipSupportObjects.add(i);
		if (status) {
			List<TObject> objectsToAdd = XDDUtilities.getTObjectsFromResource(
					ModelLoader.class.getResource(Messages.modelLoader_resourceTemplate_ipSupportObjects),
					ipSupportObjects);
			// Add Objects to Resource
			XDDUtilities.addTObjects(objectsToAdd, root);
		}

		List<TGeneralFeatures> generalFeatures = XDDUtilities.findEObjects(root,
				XDDPackage.eINSTANCE.getTGeneralFeatures());
		if (!generalFeatures.isEmpty()) {
			generalFeatures.get(0).setNWLIPSupport(status);
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

		List<Integer> multipleASndObjectIndices = new ArrayList<Integer>();
		multipleASndObjectIndices.add(EPLGeneralConstants.NMT_MNCYCLETIMING_REC);
		multipleASndObjectIndices.add(EPLGeneralConstants.NMT_NODEASSIGNMENT_AU32);
		multipleASndObjectIndices.add(EPLGeneralConstants.NMT_FEATUREFLAGS_U32);

		if (status) {
			List<TObject> objectsToAdd = XDDUtilities.getTObjectsFromResource(
					ModelLoader.class.getResource(Messages.modelLoader_resourceTemplate_multiASndObjects),
					multipleASndObjectIndices);
			// Set Objects to Resource
			XDDUtilities.addTObjects(objectsToAdd, root);
			// SET Properties in FeatureFlags & TCNFeatures/GeneralFeatures
			XDDUtilities.setFeatureFlag(status, EPLGeneralConstants.FF_OFFSET_MULTIPLE_ASND, root);
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
		List<TCNFeatures> tCNfeaturesList = XDDUtilities.findEObjects(root, XDDPackage.eINSTANCE.getTCNFeatures());
		List<ObjectListType> objectsList = XDDUtilities.findEObjects(root, XDDPackage.eINSTANCE.getObjectListType());

		if (objectsList.isEmpty() || tCNfeaturesList.isEmpty())
			return;

		TCNFeatures cnFeatures = tCNfeaturesList.get(0);
		cnFeatures.setDLLCNFeatureMultiplex(status);
		EList<TObject> objects = objectsList.get(0).getObject();

		for (TObject tObject : objects)
			if (EPLGeneralConstants.NMT_FEATUREFLAGS_U32 == new BigInteger(tObject.getIndex()).intValue())
				if (status)
					tObject.setDefaultValue("0x" //$NON-NLS-1$
							+ Long.toHexString((Long.decode(tObject.getDefaultValue()) | 512)));
				else
					tObject.setDefaultValue("0x" //$NON-NLS-1$
							+ Long.toHexString((Long.decode(tObject.getDefaultValue()) & ~512)));

		List<Integer> multiplexFeatureObjects = new ArrayList<Integer>(3);
		multiplexFeatureObjects.add(EPLGeneralConstants.NMT_ISOCHSLOTASSIGN_AU8);
		multiplexFeatureObjects.add(EPLGeneralConstants.NMT_MULTIPLCYCLEASSIGN_AU8);
		multiplexFeatureObjects.add(EPLGeneralConstants.NMT_CYCLETIMING_REC);
		multiplexFeatureObjects.add(EPLGeneralConstants.NMT_FEATUREFLAGS_U32);

		if (status) {
			// Get Needed Objects for Multiplex Support
			List<TObject> objectsToAdd = XDDUtilities.getTObjectsFromResource(
					ModelLoader.class.getResource(Messages.modelLoader_resourceTemplate_multiplexFeatureObjects),
					multiplexFeatureObjects);
			// Set Objects to Resource
			XDDUtilities.addTObjects(objectsToAdd, root);
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
	public static void setPResChainingObjects(boolean status, DocumentRoot root) {

		List<Integer> prespChainingObjects = new ArrayList<Integer>();
		prespChainingObjects.add(EPLGeneralConstants.NMT_RELATIVELATENCYDIFF_AU32);
		prespChainingObjects.add(EPLGeneralConstants.NMT_CYCLETIMING_REC);
		prespChainingObjects.add(EPLGeneralConstants.NMT_NODEASSIGNMENT_AU32);
		prespChainingObjects.add(EPLGeneralConstants.NMT_FEATUREFLAGS_U32);

		if (status) {
			List<TObject> objectsToAdd = XDDUtilities.getTObjectsFromResource(
					ModelLoader.class.getResource(Messages.modelLoader_resourceTemplate_prespChainingObjects),
					prespChainingObjects);
			// Set Objects to Resource
			XDDUtilities.addTObjects(objectsToAdd, root);
			// Set Properties in FeatureFlags, TCN- / General Features
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
	private static DocumentRoot appendMetaData(DocumentRoot root, WizardConfigurationPage1 wizardConfigurationPage1) {

		EList<ISO15745ProfileType> profiles = root.getISO15745ProfileContainer().getISO15745Profile();
		ISO15745ProfileType profile1 = profiles.get(0);
		ISO15745ProfileType profile2 = profiles.get(1);
		ProfileHeaderDataType header1 = profile1.getProfileHeader();
		header1.setProfileIdentification("Powerlink_" //$NON-NLS-1$
				+ wizardConfigurationPage1.getDeviceNameString() + "_Profile"); //$NON-NLS-1$
		header1.setProfileName(wizardConfigurationPage1.getDeviceNameString() + " Profile"); //$NON-NLS-1$

		// Setzen der Body-Werte im Profil 1
		ProfileBodyDataType body1 = profile1.getProfileBody();
		body1.setFileCreationDate(wizardConfigurationPage1.getCreationDateXML());
		body1.setFileCreationTime(wizardConfigurationPage1.getCreationTimeXML());
		body1.setFileCreator(wizardConfigurationPage1.getCreatorString());
		body1.setFileName(wizardConfigurationPage1.getFileNameString());
		body1.setFileModificationDate(wizardConfigurationPage1.getCreationDateXML());
		body1.setFileModificationTime(wizardConfigurationPage1.getCreationTimeXML());
		body1.setFileVersion(wizardConfigurationPage1.getFileVersionString());

		// Setzen der Body-Werte im Profil 2
		ProfileBodyDataType body2 = profile2.getProfileBody();
		body2.setFileCreationDate(wizardConfigurationPage1.getCreationDateXML());
		body2.setFileCreationTime(wizardConfigurationPage1.getCreationTimeXML());
		body2.setFileCreator(wizardConfigurationPage1.getCreatorString());
		body2.setFileName(wizardConfigurationPage1.getFileNameString());
		body2.setFileModificationDate(wizardConfigurationPage1.getCreationDateXML());
		body2.setFileModificationTime(wizardConfigurationPage1.getCreationTimeXML());
		body2.setFileVersion(wizardConfigurationPage1.getFileNameString());

		// For further Saves -> Give Utilites the creator name
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
	private static DocumentRoot appendUserData(DocumentRoot root, WizardConfigurationPage1 wizardConfigurationPage1) {

		EList<ISO15745ProfileType> profiles = root.getISO15745ProfileContainer().getISO15745Profile();
		ISO15745ProfileType profile1 = profiles.get(0);
		ISO15745ProfileType profile2 = profiles.get(1);

		ProfileHeaderDataType header1 = profile1.getProfileHeader();
		header1.setProfileIdentification("Powerlink_" //$NON-NLS-1$
				+ wizardConfigurationPage1.getDeviceNameString() + "_Profile"); //$NON-NLS-1$
		header1.setProfileName(wizardConfigurationPage1.getDeviceNameString() + " Profile"); //$NON-NLS-1$
		XDDUtilities.setCreator(wizardConfigurationPage1.getCreatorString());
		// Setzen der Body-Werte im Profil 1
		ProfileBodyDataType body1 = profile1.getProfileBody();
		body1.setFileCreationDate(wizardConfigurationPage1.getCreationDateXML());
		body1.setFileCreationTime(wizardConfigurationPage1.getCreationTimeXML());
		body1.setFileCreator(wizardConfigurationPage1.getCreatorString());
		body1.setFileName(wizardConfigurationPage1.getFileNameString());
		body1.setFileModificationDate(wizardConfigurationPage1.getCreationDateXML());
		body1.setFileModificationTime(wizardConfigurationPage1.getCreationTimeXML());
		body1.setFileModifiedBy(wizardConfigurationPage1.getCreatorString());
		body1.setFileVersion(wizardConfigurationPage1.getFileVersionString());

		// Setzen der Vendor-Werte im Body 1
		EList<EObject> bodyContents = body1.eContents();
		EObject identity = bodyContents.get(0);
		TDeviceIdentity tDeviceIdentity = (TDeviceIdentity) identity;
		tDeviceIdentity.getVendorName().setValue(wizardConfigurationPage1.getVendorNameString());
		tDeviceIdentity.getVendorID().setValue(wizardConfigurationPage1.getVendorIDString());
		tDeviceIdentity.getProductName().setValue(wizardConfigurationPage1.getProductNameString());
		tDeviceIdentity.getVersion().get(0).setValue(wizardConfigurationPage1.getHardwareversString());
		tDeviceIdentity.getVersion().get(1).setValue(wizardConfigurationPage1.getSoftwareversString());
		tDeviceIdentity.getVersion().add(XDDFactory.eINSTANCE.createTVersion());
		TVersion fwVersion = tDeviceIdentity.getVersion().get(2);
		fwVersion.setVersionType(VersionTypeType.HW);
		fwVersion.setReadOnly(true);
		fwVersion.setValue(wizardConfigurationPage1.getFirmwareversString());

		// Set unsetted Value of ProductID of template
		tDeviceIdentity.setProductID(XDDFactory.eINSTANCE.createTProductID());
		tDeviceIdentity.getProductID().setValue(wizardConfigurationPage1.getProductIDString());

		// Setzen der Body-Werte im Profil 2
		ProfileBodyDataType body2 = profile2.getProfileBody();
		body2.setFileCreationDate(wizardConfigurationPage1.getCreationDateXML());
		body2.setFileCreationTime(wizardConfigurationPage1.getCreationTimeXML());
		body2.setFileCreator(wizardConfigurationPage1.getCreatorString());
		body2.setFileName(wizardConfigurationPage1.getFileNameString());
		body2.setFileModificationDate(wizardConfigurationPage1.getCreationDateXML());
		body2.setFileModificationTime(wizardConfigurationPage1.getCreationTimeXML());
		body2.setFileModifiedBy(wizardConfigurationPage1.getCreatorString());
		body2.setFileVersion(wizardConfigurationPage1.getFileVersionString());

		// Setzen der Vendor.ID im Communication Body
		EList<EObject> bodyContents2 = body2.eContents();
		TApplicationLayers apllayers = (TApplicationLayers) bodyContents2.get(0);
		IdentityType identity2 = apllayers.getIdentity();
		identity2.getVendorID().setValue(wizardConfigurationPage1.getVendorIDString());
		identity2.setProductID(XDDFactory.eINSTANCE.createTProductID());
		identity2.getProductID().setValue(wizardConfigurationPage1.getProductIDString());

		// Setzen der General Features aus dem Wizard
		TNetworkManagement tnmg = (TNetworkManagement) body2.eContents().get(2);
		TGeneralFeatures generalFeatures = tnmg.getGeneralFeatures();
		generalFeatures.setNMTBootTimeNotActive(wizardConfigurationPage1.getNMTBootTimeNotActive());
		generalFeatures.setNMTCycleTimeMax(wizardConfigurationPage1.getNMTCycleTimeMax());
		generalFeatures.setNMTCycleTimeMin(wizardConfigurationPage1.getNMTCycleTimeMin());
		generalFeatures.setNMTErrorEntries(wizardConfigurationPage1.getNMTErrorEntries());
		generalFeatures.setDLLFeatureMN(false);

		// Setzen der CN Features aus dem Wizard
		TCNFeatures cnFeatures = tnmg.getCNFeatures();
		cnFeatures.setDLLCNPResChaining(wizardConfigurationPage1.isResponseChaining());
		cnFeatures.setNMTCNSoC2PReq(wizardConfigurationPage1.getNMTCNSoC2PReq());

		cnFeatures.setDLLCNFeatureMultiplex(wizardConfigurationPage1.isCnMultiplexFeature());
		if (wizardConfigurationPage1.isCnMultiplexFeature())
			ModelLoader.setMultiplexFeatureObjects(true, root);
		if (wizardConfigurationPage1.isNWLIPSupport())
			ModelLoader.setIPSupportObjects(true, root);
		if (wizardConfigurationPage1.isResponseChaining())
			ModelLoader.setPResChainingObjects(true, root);
		if (wizardConfigurationPage1.isMultipleASnd())
			ModelLoader.setPResChainingObjects(true, root);
		return root;
	}

	/**
	 * @brief Creates empty Standard-Model.
	 * @return An empty model of an XDD-File.
	 */
	private static DocumentRoot getEmptyModel() {
		DocumentRoot root = XDDFactory.eINSTANCE.createDocumentRoot();
		ISO15745ProfileContainerType container = XDDFactory.eINSTANCE.createISO15745ProfileContainerType();
		ISO15745ProfileType profile = XDDFactory.eINSTANCE.createISO15745ProfileType();
		root.setISO15745ProfileContainer(container);
		root.getISO15745ProfileContainer().getISO15745Profile().add(profile);
		profile.setProfileBody(XDDFactory.eINSTANCE.createProfileBodyCommunicationNetworkPowerlink());
		profile.setProfileHeader(XDDFactory.eINSTANCE.createProfileHeaderDataType());
		return root;
	}

} // InitialModelLoader
