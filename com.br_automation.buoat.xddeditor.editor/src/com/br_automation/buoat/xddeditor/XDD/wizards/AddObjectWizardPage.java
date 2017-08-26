/**
 * @since 19.3.2013
 * @author Joris L�ckenga, Bernecker + Rainer Industrie Elektronik Ges.m.b.H.
 */

package com.br_automation.buoat.xddeditor.XDD.wizards;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.omg.CORBA.DATA_CONVERSION;

import com.br_automation.buoat.xddeditor.XDD.DocumentRoot;
import com.br_automation.buoat.xddeditor.XDD.TObject;
import com.br_automation.buoat.xddeditor.XDD.TObjectAccessType;
import com.br_automation.buoat.xddeditor.XDD.TObjectPDOMapping;
import com.br_automation.buoat.xddeditor.XDD.XDDPackage;
import com.br_automation.buoat.xddeditor.XDD.custom.DataType;
import com.br_automation.buoat.xddeditor.XDD.custom.Messages;
import com.br_automation.buoat.xddeditor.XDD.custom.XDDUtilities;
import com.br_automation.buoat.xddeditor.XDD.validation.NameVerifyListener;
import com.br_automation.buoat.xddeditor.editor.editors.DeviceDescriptionFileEditor;

/**
 * @brief Advanced configurationPage for a new XDD Model.
 *
 *        Provides different controls to set options and data in the
 *        "AdvancedWizard"-page.
 *
 * @author Joris L�ckenga
 */
public class AddObjectWizardPage extends WizardPage {
    private Text txtLowLimit;
    private Text txtHighLimit;
    private Text txtObjectNameText;
    private Text txtObjectIndexText;
    private Text txtDefaultValue;

    private DeviceDescriptionFileEditor editor;

    private DocumentRoot documentRoot;

    private static final String WINDOW_TITLE = "POWERLINK Object Wizard";

    public static final int MANUFACTURER_PROFILE_START_INDEX = 0x2000;
    public static final int MANUFACTURER_PROFILE_END_INDEX = 0x5FFF;

    public static final String DIALOG_DESCRIPTION = "Add object to controlled node or module.";
    public static final String DIALOG_PAGE_LABEL = "POWERLINK Object";
    private static final String FIRMWARE_FILE_LABEL = "Firmware File";
    private static final String DEFAULT_CONFIGURATION_LABEL = "Choose a firmware file";

    private static final String DIALOG_PAGE_NAME = "AddObjectWizardPage";

    /**
     * @param pageID
     *            ID of the page.
     * @param wizard
     *            the parent-wizard.
     */
    protected AddObjectWizardPage(String pageName, DocumentRoot documentRoot, DeviceDescriptionFileEditor editor) {
        super(pageName);
        this.documentRoot = documentRoot;
        this.editor = editor;
        setTitle(DIALOG_PAGE_LABEL);
        setDescription(DIALOG_DESCRIPTION);

    }

    /**
     * Name verify listener
     */
    private NameVerifyListener nameVerifyListener = new NameVerifyListener();

    private Combo comboAccessType;

    private Combo comboDataType;

    private Combo comboPdoMapping;

    private Combo comboObjectType;

    private static final String[] DATA_TYPE_LIST = new String[] { "Boolean", "Integer8", "Integer16", "Integer32", "Unsigned8",
            "Unsigned16", "Unsigned32", "Real32", "Visible_String", "Integer24", "Real64", "Integer40", "Integer48",
            "Integer56", "Integer64", "Octet_String", "Unicode_String", "Time_of_Day", "Time_Diff", "Domain",
            "Unsigned24", "Unsigned40", "Unsigned48", "Unsigned56", "Unsigned64", "MAC_ADDRESS", "IP_ADDRESS",
            "NETTIME" };

    private boolean isValueValid(String value) {
        if (!value.isEmpty()) {
        	try {
        	if(value.contains("0x")) {
        		value = value.substring(2);
            Integer val = Integer.valueOf(value);
            if (val < 0) {
                return false;
            }
        }
        	} catch (Exception ex) {
        		ex.printStackTrace();
        		setErrorMessage("Invalid value.");
        		return false;
        	}
        }
        return true;
    }

    private static final String[] ACCESS_TYPE_LIST = new String[] { "Constant", "Read only", "Write only", "Read write" };

    /**
     * @see WizardPage#createControl(Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        this.setControl(container);

        SimpleDateFormat creationTime = new SimpleDateFormat("HH:mm:ssZ"); //$NON-NLS-1$
        String creationTimeStr = creationTime.format(new Date());
        creationTimeStr = creationTimeStr.substring(0, 11) + ":00";

        Group grpAddObjectAdvancedOptions = new Group(container, SWT.SHADOW_OUT);
        grpAddObjectAdvancedOptions.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL)); //$NON-NLS-1$
        grpAddObjectAdvancedOptions.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
        grpAddObjectAdvancedOptions.setText(Messages.addObjectWizardPage_advanced_options);
        grpAddObjectAdvancedOptions.setBounds(278, 10, 286, 221);

        Label lblLowLimit = new Label(grpAddObjectAdvancedOptions, SWT.NONE);
        lblLowLimit.setBounds(10, 136, 120, 21);
        lblLowLimit.setText(Messages.addObjectWizardPage_lblLow_limit); // $NON-NLS-1$

        Label lblHighLimit = new Label(grpAddObjectAdvancedOptions, SWT.NONE);
        lblHighLimit.setBounds(10, 164, 81, 15);
        lblHighLimit.setText(Messages.addObjectWizardPage_lblHigh_limit); // $NON-NLS-1$

        Label lblDataType = new Label(grpAddObjectAdvancedOptions, SWT.NONE);
        lblDataType.setBounds(10, 22, 120, 21);
        lblDataType.setText(Messages.addObjectWizardPage_lblDataType);

        txtLowLimit = new Text(grpAddObjectAdvancedOptions, SWT.BORDER);
        txtLowLimit.setText(Messages.addObjectWizardPage_txtLowLimit); // $NON-NLS-1$
        txtLowLimit.setBounds(136, 136, 140, 23);
        txtLowLimit.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setErrorMessage(null);
                setPageComplete(true);
                lowLimit = txtLowLimit.getText();
                if (!isValueValid(lowLimit)) {
                    setErrorMessage("Invalid value.");
                    setPageComplete(false);
                }
                getWizard().getContainer().updateButtons();
            }

        });
        txtLowLimit.addVerifyListener(nameVerifyListener);
        txtLowLimit.setFocus();

        txtHighLimit = new Text(grpAddObjectAdvancedOptions, SWT.BORDER);
        txtHighLimit.setText(Messages.addObjectWizardPage_txtHighLimit); // $NON-NLS-1$
        txtHighLimit.setBounds(136, 164, 140, 21);
        txtHighLimit.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setErrorMessage(null);
                setPageComplete(true);
                highLimit = txtHighLimit.getText();
                if (!isValueValid(highLimit)) {
                    setErrorMessage("Invalid value.");
                    setPageComplete(false);
                }
                getWizard().getContainer().updateButtons();
            }

        });
        txtHighLimit.addVerifyListener(nameVerifyListener);
        txtHighLimit.setFocus();

        Label lblAccessType = new Label(grpAddObjectAdvancedOptions, SWT.NONE);
        lblAccessType.setText(Messages.addObjectWizardPage_lblAccess_type);
        lblAccessType.setBounds(10, 51, 120, 21);

        comboAccessType = new Combo(grpAddObjectAdvancedOptions, SWT.NONE);
        comboAccessType.setItems(ACCESS_TYPE_LIST);
        comboAccessType.setBounds(136, 51, 140, 25);
        comboAccessType.select(0);
        comboAccessType.setText(ACCESS_TYPE_LIST[0]);
        comboAccessType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                accessType = comboAccessType.getText();
            }
        });

        comboDataType = new Combo(grpAddObjectAdvancedOptions, SWT.NONE);
        comboDataType.setItems(DATA_TYPE_LIST);
        comboDataType.setBounds(136, 22, 140, 25);
        comboDataType.select(0);
        comboDataType.setText(DATA_TYPE_LIST[0]);
        comboDataType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                dataType = comboDataType.getText();
            }
        });

        txtDefaultValue = new Text(grpAddObjectAdvancedOptions, SWT.BORDER);
        txtDefaultValue.setText(Messages.addObjectWizardPage_txtDefaultValue);
        txtDefaultValue.setBounds(136, 107, 140, 23);
        txtDefaultValue.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setErrorMessage(null);
                setPageComplete(true);
                defaultValue = txtDefaultValue.getText();
                if (!isValueValid(defaultValue)) {
                    setErrorMessage("Invalid value.");
                    setPageComplete(false);
                }
                getWizard().getContainer().updateButtons();
            }

        });
        txtDefaultValue.addVerifyListener(nameVerifyListener);
        txtDefaultValue.setFocus();

        Label lblDefaultValue = new Label(grpAddObjectAdvancedOptions, SWT.NONE);
        lblDefaultValue.setText(Messages.addObjectWizardPage_lblDefault_value);
        lblDefaultValue.setBounds(10, 107, 120, 21);

        Label lblPdoMapping = new Label(grpAddObjectAdvancedOptions, SWT.NONE);
        lblPdoMapping.setText(Messages.addObjectWizardPage_lblPdo_mapping);
        lblPdoMapping.setBounds(10, 80, 120, 21);

        Label lblUniqueIdRef = new Label(grpAddObjectAdvancedOptions, SWT.NONE);
        lblUniqueIdRef.setText(Messages.addObjectWizardPage_lblUnique_ref_id);
        lblUniqueIdRef.setBounds(10, 191, 120, 21);

        comboPdoMapping = new Combo(grpAddObjectAdvancedOptions, SWT.NONE);
        comboPdoMapping.setItems(PDO_MAPPING_TYPES);
        comboPdoMapping.setBounds(136, 78, 140, 23);
        comboPdoMapping.select(0);
        comboPdoMapping.setText(PDO_MAPPING_TYPES[0]);
        comboPdoMapping.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                pdoMapping = comboPdoMapping.getText();
            }
        });

        Combo comboUniqueRefId = new Combo(grpAddObjectAdvancedOptions, SWT.NONE);
        comboUniqueRefId.setItems(new String[] {});
        comboUniqueRefId.setBounds(136, 191, 140, 23);
        comboUniqueRefId.select(0);

        Group grpAddObjectBasicOptions = new Group(container, SWT.NONE);
        grpAddObjectBasicOptions.setBounds(10, 10, 262, 96);
        grpAddObjectBasicOptions.setText(Messages.addObjectWizardPage_basic_options);

        Label lblObjectIndex = new Label(grpAddObjectBasicOptions, SWT.NONE);
        lblObjectIndex.setBounds(10, 22, 91, 15);
        lblObjectIndex.setText(Messages.addObjectWizardPage_lblObject_index);

        Label lblObjectName = new Label(grpAddObjectBasicOptions, SWT.NONE);
        lblObjectName.setText(Messages.addObjectWizardPage_lblObject_name);
        lblObjectName.setBounds(10, 46, 78, 15);

        Label lblObjectType = new Label(grpAddObjectBasicOptions, SWT.NONE);
        lblObjectType.setText(Messages.addObjectWizardPage_lblObject_type);
        lblObjectType.setBounds(10, 70, 91, 23);

        txtObjectIndexText = new Text(grpAddObjectBasicOptions, SWT.BORDER);
        txtObjectIndexText.setText(Messages.addObjectWizardPage_txtObject_index); // $NON-NLS-1$
        txtObjectIndexText.setBounds(107, 19, 140, 21);
        txtObjectIndexText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setErrorMessage(null);
                setPageComplete(true);
                objIndex = txtObjectIndexText.getText();

                getWizard().getContainer().updateButtons();
            }

        });
        txtObjectIndexText.addVerifyListener(nameVerifyListener);
        txtObjectIndexText.setFocus();

        txtObjectNameText = new Text(grpAddObjectBasicOptions, SWT.BORDER);
        txtObjectNameText.setText(Messages.addObjectWizardPage_txtObject_name);
        txtObjectNameText.setBounds(107, 43, 140, 21);
        txtObjectNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setErrorMessage(null);
                setPageComplete(true);
                objName = txtObjectNameText.getText();
                if (!isValueValid(objName)) {
                    setErrorMessage("Invalid value.");
                    setPageComplete(false);
                }
                getWizard().getContainer().updateButtons();
            }

        });
        txtObjectNameText.addVerifyListener(nameVerifyListener);
        txtObjectNameText.setFocus();

        comboObjectType = new Combo(grpAddObjectBasicOptions, SWT.NONE);
        comboObjectType.setItems(OBJECT_TYPES);
        comboObjectType.setBounds(107, 67, 140, 21);
        comboObjectType.select(0);
        comboObjectType.setText(OBJECT_TYPES[0]);
        comboObjectType.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                objectTypeText = comboObjectType.getText();
            }
        });
    } // createControl

    // Metadata getters

    private static final String[] OBJECT_TYPES = new String[] { "Variable", "Array", "Record" };

    private static final String[] PDO_MAPPING_TYPES = new String[] { "Non-mappable", "Mapped by default", "Mapped optionally",
            "Trasmit process data objects", "Receive process data objects" };

    public void getDataTypeDetails() {

    }

    private String accessType;

    private String pdoMapping;

    private String dataType;

    private boolean isObjectIndexValid(String text) {
        if (!text.isEmpty()) {
        	try{
            Integer indexvalue = Integer.decode(text);
            if ((indexvalue < MANUFACTURER_PROFILE_START_INDEX) || (indexvalue > MANUFACTURER_PROFILE_END_INDEX)) {

                return false;
            }
        }
        catch (Exception e){
        	e.printStackTrace();
        	return false;
        }
        }
        return true;
    }

    public TObjectAccessType getAccessType() {
    	if(accessType == null) {
    		accessType = comboAccessType.getText();
    	}

        if (accessType.equalsIgnoreCase("Constant")) {
            return TObjectAccessType.CONST;
        }
        if (accessType.equalsIgnoreCase("Read only")) {
            return TObjectAccessType.RO;
        }
        if (accessType.equalsIgnoreCase("Write only")) {
            return TObjectAccessType.WO;
        }
        if (accessType.equalsIgnoreCase("Read write")) {
            return TObjectAccessType.RW;
        }
        return TObjectAccessType.CONST;
    }

    public String getTxtLowLimit() {
        return lowLimit;
    }

    private String lowLimit = StringUtils.EMPTY;

    private String highLimit = StringUtils.EMPTY;

    private String objIndex = StringUtils.EMPTY;

    private String defaultValue = StringUtils.EMPTY;

    private String objName = StringUtils.EMPTY;

    public String getTxtHighLimit() {
        return highLimit;
    }

    public String getTxtObjectNameText() {
        return objName;
    }

    public String getTxtObjectIndexText() {
        return objIndex;
    }

    public String getTxtDefaultValue() {
        return defaultValue;
    }

    private boolean validateObjectModel() {
        String index = getTxtObjectIndexText();
        setErrorMessage(null);
        if(index.isEmpty()){
        	setErrorMessage("Enter the hexadecimal object index value within the range (0x2000 to 0x5FFF).");
        	return false;
        }

        if (!isValueValid(objIndex)) {
            setErrorMessage("Invalid value.");
            return false;
        }
        if (!isObjectIndexValid(objIndex)) {
            setErrorMessage("The index does not match within the limit of user defined objects.");
            return false;
        }

        if(!isObjectIndexAvailable(objIndex)) {
        	setErrorMessage("The Object index '"+objIndex+"' already available.");
        	return false;
        }

        String objName = getTxtObjectNameText();
        if(objName.isEmpty()){
        	setErrorMessage("Enter the name of object.");
        	return false;
        }


        return true;
    }

    private boolean isObjectIndexAvailable(String objIndex) {
    	List<TObject> tObjects = XDDUtilities.findEObjects(documentRoot, XDDPackage.eINSTANCE.getTObject());

    	for(TObject object: tObjects) {
    		byte[] index = object.getIndex();
     		String indexValue = DatatypeConverter.printHexBinary(index);
if(objIndex.contains("0x")) {
	objIndex = objIndex.substring(2);
}
if(indexValue.equalsIgnoreCase(objIndex)) {
	return false;
}
    	}
		return true;
	}

	/**
     * Checks for error in the page
     *
     * @return page complete status.
     */
    @Override
    public boolean isPageComplete() {

        boolean pageComplete = (super.isPageComplete());



        if (validateObjectModel()) {
            pageComplete = true;
        } else {
            pageComplete = false;
        }

        return pageComplete;
    }

    public String getActualValue() {

        return StringUtils.EMPTY;
    }

    public String getDatTypeValue(String dataType) {
    	switch(dataType) {
    	case "Boolean":
    		return "0001";
    	case "Integer8":
    		return "0002";
    	case "Integer16":
    		return "0003";
    	case "Integer32":
    		return "0004";
    	case "Unsigned8":
    		return "0005";
    	case "Unsigned16":
    		return "0006";
    	case "Unsigned32":
    		return "0007";
    	case "Real32":
    		return "0008";
    	case "Visible_String":
    		return "0009";
    	case "Integer24":
    		return "0010";
    	case "Real64":
    		return "0011";
    	case "Integer40":
    		return "0012";
    	case "Integer48":
    		return "0013";
    	case "Integer56":
    		return "0014";
    	case "Integer64":
    		return "0015";
    	case "Octet_String":
    		return "000A";
    	case "Unicode_String":
    		return "000B";
    	case "Time_of_Day":
    		return "000C";
    	case "Time_Diff":
    		return "000D";
    	case "Domain":
    		return "000F";
    	case "Unsigned24":
    		return "0016";
    	case "Unsigned40":
    		return "0018";
    	case "Unsigned48":
    		return "0019";
    	case "Unsigned56":
    		return "001A";
    	case "Unsigned64":
    		return "001B";
    	case "MAC_ADDRESS":
    		return "0401";
    	case "IP_ADDRESS":
    		return "0402";
    	case "NETTIME":
    		return "0403";

    	default:
    		return "0000";
    	}


    }

    public byte[] getDataType() {
    	if(dataType == null) {
    		dataType = comboDataType.getText();
    	}


        if (!dataType.isEmpty()) {

            return DatatypeConverter.parseHexBinary(getDatTypeValue(dataType));
        }
        return null;

    }

    public String getDefaultValue() {
        String defaultValue = getTxtDefaultValue();
        if (!defaultValue.isEmpty()) {
            return defaultValue;
        }
        return StringUtils.EMPTY;
    }

    public String getDenotation() {

        return StringUtils.EMPTY;
    }

    public String getHighLimit() {
        String highLimit = getTxtHighLimit();
        if (!highLimit.isEmpty()) {
            return highLimit;
        }
        return StringUtils.EMPTY;
    }

    public String getLowLimit() {
        String lowLimit = getTxtLowLimit();
        if (!lowLimit.isEmpty()) {
            return lowLimit;
        }
        return StringUtils.EMPTY;
    }

    public byte[] getIndex() {
        String index = getTxtObjectIndexText();
        if (!index.isEmpty()) {
        	index = index.substring(2);
            return DatatypeConverter.parseHexBinary(index);
        }
        return null;
    }

    private String objectTypeText;

    public short getObjectType() {

    	if(objectTypeText == null) {
    		objectTypeText = comboObjectType.getText();
    	}

        if (objectTypeText.equalsIgnoreCase("Variable")) {
            return 7;
        }
        if (objectTypeText.equalsIgnoreCase("Array")) {
            return 8;
        }

        if (objectTypeText.equalsIgnoreCase("Record")) {
            return 9;
        }
        return 0;
    }

    public TObjectPDOMapping getPdoMapping() {

    	if(pdoMapping == null) {
    		pdoMapping = comboPdoMapping.getText();
    	}

        if (pdoMapping.equalsIgnoreCase("Non-mappable")) {
            return TObjectPDOMapping.NO;
        }
        if (pdoMapping.equalsIgnoreCase("Mapped by default")) {
            return TObjectPDOMapping.DEFAULT;
        }
        if (pdoMapping.equalsIgnoreCase("Mapped optionally")) {
            return TObjectPDOMapping.OPTIONAL;
        }
        if (pdoMapping.equalsIgnoreCase("Trasmit process data objects")) {
            return TObjectPDOMapping.TPDO;
        }
        if (pdoMapping.equalsIgnoreCase("Receive process data objects")) {
            return TObjectPDOMapping.RPDO;
        }
        return TObjectPDOMapping.DEFAULT;
    }

	public String getObjectName() {

		return objName;
	}
} // WizardConfigurationPage1