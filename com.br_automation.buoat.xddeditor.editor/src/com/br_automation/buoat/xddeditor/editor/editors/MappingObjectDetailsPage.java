/*******************************************************************************
 * @file   MappingObjectDetailsPage.java
 *
 * @author Sree Hari Vignesh B, Kalycito Infotech Private Limited.
 *
 * @copyright (c) 2017, Kalycito Infotech Private Limited
 *                    All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the copyright holders nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package com.br_automation.buoat.xddeditor.editor.editors;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.eef.runtime.ui.properties.sections.EEFAdvancedPropertySection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;

import com.br_automation.buoat.xddeditor.XDD.DocumentRoot;
import com.br_automation.buoat.xddeditor.XDD.SubObjectType;
import com.br_automation.buoat.xddeditor.XDD.TObject;
import com.br_automation.buoat.xddeditor.XDD.TObjectPDOMapping;
import com.br_automation.buoat.xddeditor.XDD.custom.Messages;
import com.br_automation.buoat.xddeditor.XDD.custom.XDDUtilities;
import com.br_automation.buoat.xddeditor.XDD.custom.propertypages.AdvancedMappingObjectPropertySection;
import com.br_automation.buoat.xddeditor.XDD.custom.propertypages.TObjectComposite;
import com.br_automation.buoat.xddeditor.XDD.impl.SubObjectTypeImpl;

/**
 * Class to populate the details page of mapping subobjects
 *
 * @author Sree Hari Vignesh
 *
 */
public class MappingObjectDetailsPage extends EEFAdvancedPropertySection implements IDetailsPage {

    private IManagedForm managedForm;

    private Text accessTypeText;

    private Text actualValueText;

    private Text dataTypeText;

    private Text defaultValueText;
    private Text denotationText;
    private Text highLimitText;
    // private Text indexText;
    private Text lowLimitText;
    private Text nameText;
    private Text objTypeText;
    private Text objFlagsText;
    private Text pdoMappingText;
    private Text subIndexText;
    private Text uniqueIdRefText;
    private SubObjectTypeImpl subObject;
    private DocumentRoot docRoot;
    private DeviceDescriptionFileEditor editor;

    public MappingObjectDetailsPage(SubObjectTypeImpl subObject, DocumentRoot docRoot,
            DeviceDescriptionFileEditor editor) {
        this.subObject = subObject;
        this.docRoot = docRoot;
        this.editor = editor;
    }

    public boolean updateDocument(DocumentRoot documentRoot) {
        // Create a resource set
        ResourceSet resourceSet = new ResourceSetImpl();

        // Get the URI of the model file.
        URI fileURI = URI.createPlatformResourceURI(editor.getModelFile().getFullPath().toString(), true);

        // Create a resource for this file.
        Resource resource = resourceSet.createResource(fileURI);

        // Add the initial model object to the contents.
        EObject rootObject = documentRoot;
        if (rootObject != null)
            resource.getContents().add(rootObject);

        // Save the contents of the resource to the file system.
        Map<Object, Object> options = new HashMap<Object, Object>();
        options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        try {
            resource.save(options);
            return true;
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return false;
    }

    @Override
    public void initialize(IManagedForm form) {
        managedForm = form;

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void commit(boolean onSave) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean setFormInput(Object input) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isStale() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    private TObjectComposite tobjectComposite;
    private SubObjectType tsubObject;

    private Map<Integer, SubObjectType> validSubObjectTypes;
    private Set<TObjectPDOMapping> validTObjectMapping;
    private Map<Integer, TObject> validTObjects;

    /**
     * @brief Sets the different types of error-texts, depending on given value.
     * @param errorNr
     *            Stands for a specific error.
     */
    private void setError(int errorNr) {
        this.lblError.setVisible(true);

        switch (errorNr) {
        case 0:
            this.lblError.setVisible(false);
            break;
        case 1:
            this.lblError.setText(Messages.advancedMappingObjectPropertySection_err_subobject_not_found);
            break;
        case 2:
            this.lblError.setText(Messages.advancedMappingObjectPropertySection_err_mappingObject_not_found);
            break;
        case 3:
            this.lblError.setText(Messages.advancedMappingObjectPropertySection_err_offset);
            break;
        case 4:
            this.lblError.setText(Messages.advancedMappingObjectPropertySection_err_index_not_found);
            break;
        case 5:
            this.lblError.setText(Messages.advancedMappingObjectPropertySection_err_length);
            break;
        case 6:
            this.lblError.setText(Messages.advancedMappingObjectPropertySection_err_length_and_offset);
            break;
        case 7:
            this.lblError.setText(NLS.bind(Messages.advancedMappingObjectPropertySection_err_offset_not_dividable,
                    this.offsetValue, (this.offsetValue % 8)));
            break;
        case 8:
            this.lblError.setText(NLS.bind(Messages.advancedMappingObjectPropertySection_err_length_not_dividable,
                    this.lengthValue, (this.lengthValue % 8)));
            break;
        case 9:
            this.lblError.setText(Messages.general_error_defaultValueInvalid);
            break;
        default:
            break;
        } // switchcase
    } // setError

    private long lengthValue;
    // When Offset-Texbox looses focus -> Check values,calculate & set the new
    // defaultvalues
    private final FocusAdapter offsetListener = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent arg0) {
            Integer offset = XDDUtilities.parseInt(txtOffset.getText());
            if (offset != null && (offset >= 0 && offset <= AdvancedMappingObjectPropertySection.MAX_OFFSET_VALUE)) {
                setError(0);
                offsetValue = offset.intValue() * 8;
                setNewDefaultValue();
            } else {
                txtOffset.forceFocus();
                txtOffset.selectAll();
                setError(3);

            }
        } // focusLost
    }; // FocusAdapter

    /**
     * @brief Calculates new default value.
     */
    private void setNewDefaultValue() {
        long newSubObjectValue = (this.indexValue) | (this.subindexValue << 16) | (this.offsetValue << 32)
                | (this.lengthValue << 48);
        String newValue = "0x" + String.format("%016x", newSubObjectValue).toUpperCase(); //$NON-NLS-1$
        this.lblError.setText("");
        subObject.setDefaultValue(newValue);
        updateDocument(docRoot);
        // this.subObjectItemProvider.setPropertyValue(this.tsubObject,
        // "defaultValue", newValue); //$NON-NLS-1$
        this.lblDefaultValueValue.setText(newValue);

    } // setDefaultValue

    private long offsetValue;

    private long subindexValue;

    private long indexValue;

    /**
     * @brief Parses the current default value and sets contents of controls
     *        accordingly.
     * @param currentValue
     *            The current default value of subobject.
     */
    private void parseDefaultParameter(long currentValue) { // NOPMD by
                                                            // lueckengaj on
                                                            // 21.05.13 08:33

        // Extract Index,Subindex,Offset & Length from defaultvalue of
        // tsubObject
        this.lengthValue = (currentValue & AdvancedMappingObjectPropertySection.MASK_LENGTH_VALUE) >> 48;
        this.offsetValue = (currentValue & AdvancedMappingObjectPropertySection.MASK_OFFSET_VALUE) >> 32;
        this.subindexValue = (currentValue & AdvancedMappingObjectPropertySection.MASK_SUBINDEX_VALUE) >> 16;
        this.indexValue = (int) (currentValue & AdvancedMappingObjectPropertySection.MASK_INDEX_VALUE);
        boolean subObjectset = false;
        boolean tObjectset = false;
        TObject selectedObject = null;
        SubObjectType selectedSubObject = null;

        if (this.indexValue != 0) { // If IndexValue is set...find the index
            selectedObject = this.validTObjects.get(this.indexValue);
            if (selectedObject != null) {
                this.cmbIndex.select(this.cmbIndex.indexOf(selectedObject.getName()));
                this.setError(0);
                tObjectset = true;
                this.validSubObjectTypes = XDDUtilities.getMappingSubObjects(selectedObject, this.validTObjectMapping);
            } else {
                this.setError(2);
            }

            if (selectedObject != null && tObjectset && !this.validSubObjectTypes.isEmpty()) { // If
                                                                                                // there
                                                                                                // are
                                                                                                // valid
                                                                                                // Subobjects
                for (Entry<Integer, SubObjectType> entry : this.validSubObjectTypes.entrySet()) {
                    this.cmbSubindex.add(entry.getValue().getName());
                    this.cmbSubindex.setData(entry.getValue().getName(), entry.getValue());
                }
                selectedSubObject = this.validSubObjectTypes.get((int) this.subindexValue);
                if (selectedSubObject != null) {
                    this.cmbSubindex.select(this.cmbSubindex.indexOf(selectedSubObject.getName()));
                    this.cmbSubindex.setEnabled(true);
                    this.cmbSubindex.setBackground(XDDUtilities.getWhite(this.device));
                    subObjectset = true;
                }

            } else { // if not...
                this.cmbSubindex.setEnabled(false);
                this.cmbSubindex.setBackground(XDDUtilities.getGrey(this.device));
            }

            if (!tObjectset) // if TObject not set -> Index not found
                this.setError(4);
            else if (this.subindexValue != 0 && !subObjectset)
                this.setError(1);
            else {
                if (this.offsetValue >= 0
                        && this.offsetValue <= AdvancedMappingObjectPropertySection.MAX_OFFSET_VALUE * 8
                        && this.offsetValue % 8 == 0)
                    this.txtOffset.setText(Long.toString(this.offsetValue / 8));
                else {
                    this.setError(3);
                    if (this.offsetValue % 8 != 0) {
                        this.setError(7);
                        this.offsetValue = 0;
                    }
                }
                if (this.lengthValue >= 1 && this.lengthValue <= 1490 * 8 && this.lengthValue % 8 == 0)
                    this.txtLength.setText(Long.toString(this.lengthValue / 8));
                else {
                    this.setError(5);
                    if (this.lengthValue % 8 != 0)
                        this.setError(8);
                    this.lengthValue = 8;
                }
                if (this.lengthValue == 0 && !(this.offsetValue >= 0
                        && this.offsetValue <= AdvancedMappingObjectPropertySection.MAX_OFFSET_VALUE * 8))
                    this.setError(6);
            }
        } else
            this.setError(4); // If IndexValue is 0, even if other Values are
                                // set!

    } // getDefaultParameter

    // Shows Mappable Objects and adds subindexListener when object with
    // subobjects is selected
    // also sets lengthfield depending on object,triggers new
    // defaultvalue-calculation
    private final SelectionAdapter subIndexListener = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            String selection = cmbSubindex.getItem(cmbSubindex.getSelectionIndex());
            // If selection is made...
            if (!selection.isEmpty()) {
                SubObjectType subobject = (SubObjectType) cmbSubindex.getData(selection);

                String lengthResultString;
                // Check if Empty DataType exists...
                if (subobject.getDataType() != null)
                    lengthResultString = Long.toString((Long.parseLong(
                            Integer.toString(XDDUtilities.getDataType(subobject.getDataType()).getBitSize()))) / 8);
                else
                    lengthResultString = Messages.advancedMappingObjectPropertySection_No_DataType;
                // When there is a valid value
                if (!lengthResultString.contentEquals("0") //$NON-NLS-1$
                        && !lengthResultString
                                .contentEquals(Messages.advancedMappingObjectPropertySection_No_DataType)) {
                    txtLength.setText(lengthResultString);
                    txtLength.setEnabled(false);
                    lengthValue = Long.parseLong(lengthResultString) * 8;
                } else {
                    if (!lengthResultString.equals(Messages.advancedMappingObjectPropertySection_No_DataType))
                        txtLength.setText(Messages.advancedMappingObjectPropertySection_Set_Length);
                    else
                        txtLength.setText(lengthResultString);
                    txtLength.setEnabled(true);
                    txtLength.setFocus();
                    txtLength.selectAll();
                }
                subindexValue = new BigInteger(1, subobject.getSubIndex()).intValue();
                setNewDefaultValue();

                lblDefaultValueValue.setText(tsubObject.getDefaultValue());
            }
        } // widgetSelected
    }; // SelectionAdapter

    private final SelectionAdapter indexListener = new SelectionAdapter() {

        @Override
        public void widgetSelected(SelectionEvent e) {
            Combo cmbSelected = ((Combo) e.getSource());
            String indexSelection = cmbSelected.getText();
            TObject object;
            setError(0);
            String dataTypeSize;
            cmbSubindex.removeAll();

            if (!indexSelection.isEmpty()) {
                // choose the object selected
                object = (TObject) cmbSelected.getData(indexSelection);
                // AdvancedMappingObjectPropertySection.this.subObjectItemProvider.setPropertyValue(
                // AdvancedMappingObjectPropertySection.this.tsubObject,
                // "defaultValue", "0x0");
                // If Subobjects NOT empty...modify the Subindex-Combobox
                if (!object.getSubObject().isEmpty()) {
                    // Get Subobjects & Enable Combobox
                    validSubObjectTypes = XDDUtilities.getMappingSubObjects(object, validTObjectMapping);
                    cmbSubindex.setEnabled(true);
                    cmbSubindex.setBackground(XDDUtilities.getWhite(device));

                    // Fill Comobobox
                    for (Entry<Integer, SubObjectType> entry : validSubObjectTypes.entrySet()) {
                        cmbSubindex.add(entry.getValue().getName());
                        cmbSubindex.setData(entry.getValue().getName(), entry.getValue());
                    }

                    // Set Text-Instructions
                    txtLength.setText(Messages.advancedMappingObjectPropertySection_Set_Subindex);
                    txtLength.setEnabled(false);
                    // Add Listener to subObjectCombobox
                    cmbSubindex.addSelectionListener(subIndexListener);
                } else {
                    cmbSubindex.setEnabled(false);
                    cmbSubindex.setBackground(XDDUtilities.getGrey(device));
                    subindexValue = 0;
                    // Find Bitlength of DataType
                    dataTypeSize = Integer.toString(XDDUtilities.getDataType(object.getDataType()).getBitSize());
                    if (!dataTypeSize.contentEquals("0")) { //$NON-NLS-1$
                        dataTypeSize = Long.toString((Long.parseLong(dataTypeSize)) / 8);
                        txtLength.setText(dataTypeSize);
                        lengthValue = Long.parseLong(dataTypeSize) * 8;
                        txtLength.setEnabled(false);
                    } else {
                        txtLength.setText(Messages.advancedMappingObjectPropertySection_Set_Length);
                        txtLength.setEnabled(true);
                        txtLength.setFocus();
                        txtLength.selectAll();
                    }
                }
                indexValue = new BigInteger(1, object.getIndex()).intValue();
                setNewDefaultValue();
                lblDefaultValueValue.setText(tsubObject.getDefaultValue());
            } // If selection is not empty!
        } // widgetSelected
    }; // SelectionAdapter

    @Override
    public void selectionChanged(IFormPart part, ISelection selection) {
        IStructuredSelection sel = (IStructuredSelection) selection;

        SubObjectTypeImpl obj = (SubObjectTypeImpl) sel.getFirstElement();

        this.tsubObject = (SubObjectType) obj;
        TObject tobject = (TObject) this.tsubObject.eContainer();
        // this.tobjectComposite.setObject(this.tsubObject);
        this.indexValue = 0;
        this.subindexValue = 0;
        this.offsetValue = 0;
        this.lengthValue = 0;
        this.setError(0);

        if (XDDUtilities.isRPDO(tobject)) {
            this.validTObjectMapping = XDDUtilities.getValidMappingTypes(TObjectPDOMapping.RPDO);

        } else {
            this.validTObjectMapping = XDDUtilities.getValidMappingTypes(TObjectPDOMapping.TPDO);

        }

        this.cmbIndex.removeAll();
        this.cmbSubindex.removeAll();
        this.txtLength.setText(""); //$NON-NLS-1$
        this.txtOffset.setText("0"); //$NON-NLS-1$
        this.validTObjects = XDDUtilities.getMappingObjects(
                (DocumentRoot) EcoreUtil.getRootContainer((EObject) tobject), this.validTObjectMapping);

        for (Entry<Integer, TObject> entry : this.validTObjects.entrySet()) {
            this.cmbIndex.setData(entry.getValue().getName(), entry.getValue());
            this.cmbIndex.add(entry.getValue().getName());
        }

        if (this.cmbIndex.getItemCount() > 0)
            this.cmbIndex.addSelectionListener(this.indexListener);

        if (this.tsubObject.getDefaultValue() != null)
            try {
                this.lblDefaultValueValue.setText(this.tsubObject.getDefaultValue());
                this.defaultValue = Long.decode(this.tsubObject.getDefaultValue());
                if (this.defaultValue != 0)
                    this.parseDefaultParameter(this.defaultValue);
            } catch (NumberFormatException e) {
                this.setError(9);
            }

        // if (obj.getAccessType() != null) {
        // accessTypeText.setText(obj.getAccessType().getName());
        // }
        //
        // if (obj.getActualValue() != null) {
        // actualValueText.setText(obj.getActualValue());
        // }
        //
        // if (obj.getDataType() != null) {
        // String dataType =
        // DatatypeConverter.printHexBinary(obj.getDataType());
        // dataTypeText.setText(dataType);
        // }
        //
        // if (obj.getDefaultValue() != null) {
        // defaultValueText.setText(obj.getDefaultValue());
        // }
        //
        // if (obj.getDenotation() != null) {
        // denotationText.setText(obj.getDenotation());
        // }
        //
        // if (obj.getHighLimit() != null) {
        // highLimitText.setText(obj.getHighLimit());
        // }

        if (obj.getSubIndex() != null) {
            String index = DatatypeConverter.printHexBinary(obj.getSubIndex());
            index = "0x" + index;
            subIndexText.setText(index);
        }

        // if (obj.getLowLimit() != null) {
        // lowLimitText.setText(obj.getLowLimit());
        // }

        if (obj.getName() != null) {
            nameText.setText(obj.getName());
        }

        if (obj.getObjectType() != 0) {
            objTypeText.setText(String.valueOf(obj.getObjectType()));
        }

        // if (obj.getObjFlags() != null) {
        // objFlagsText.setText(obj.getObjFlags().toString());
        // }
        //
        // if (obj.getPDOmapping() != null) {
        // pdoMappingText.setText(obj.getPDOmapping().getName());
        // }
        //
        // if (obj.getUniqueIDRef() != null) {
        // uniqueIdRefText.setText(obj.getUniqueIDRef());
        // }

    }

    private Label lblDefaultValue;

    private Label lblDefaultValueValue;
    private Label lblError;

    private Combo cmbIndex;
    private Combo cmbSubindex;
    private long defaultValue;
    private final Device device = Display.getCurrent();

    private Text txtLength;
    private Text txtOffset;

    @Override
    public void createContents(Composite parent) {

        GridLayout layout = new GridLayout(1, true);

        parent.setLayout(layout);
        layout.marginWidth = 20;
        Section deviceFirmwareSection = managedForm.getToolkit().createSection(parent, ExpandableComposite.EXPANDED
                | Section.DESCRIPTION | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
        managedForm.getToolkit().paintBordersFor(deviceFirmwareSection);
        deviceFirmwareSection.setText(ObjectDictionaryEditorPage.SUB_OBJECT_DICTIONARY_DETAILS_HEADING);
        deviceFirmwareSection.setDescription(ObjectDictionaryEditorPage.OBJECT_DICTIONARY_HEADING_DESCRIPTION);

        Composite clientComposite = managedForm.getToolkit().createComposite(deviceFirmwareSection, SWT.WRAP);
        GridLayout layouts = new GridLayout(1, true);
        layouts.marginWidth = 2;
        layouts.marginHeight = 2;
        clientComposite.setLayout(layouts);
        managedForm.getToolkit().paintBordersFor(clientComposite);

        deviceFirmwareSection.setClient(clientComposite);

        Composite groupComposite = new Composite(clientComposite, SWT.NONE);
        groupComposite.setLayout(new GridLayout(1, false));

        Group grpMandatoryData = new Group(groupComposite, SWT.NONE);
        GridData gd_grpConfigurationFile = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gd_grpConfigurationFile.widthHint = 558;
        grpMandatoryData.setLayoutData(gd_grpConfigurationFile);
        grpMandatoryData.setText("Mandatory Data");
        grpMandatoryData.setLayout(new GridLayout(6, false));

        Label nameLabel = new Label(grpMandatoryData, SWT.NONE);
        nameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        nameLabel.setText("Name:");
        managedForm.getToolkit().adapt(nameLabel, true, true);
        nameLabel.setForeground(managedForm.getToolkit().getColors().getColor(IFormColors.TITLE));

        nameText = new Text(grpMandatoryData, SWT.BORDER | SWT.WRAP);
        nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        managedForm.getToolkit().adapt(nameText, true, true);
        nameText.setEditable(false);

        Label objTypelabel = new Label(grpMandatoryData, SWT.NONE);
        objTypelabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        objTypelabel.setText("Object Type:");
        managedForm.getToolkit().adapt(objTypelabel, true, true);
        objTypelabel.setForeground(managedForm.getToolkit().getColors().getColor(IFormColors.TITLE));

        objTypeText = new Text(grpMandatoryData, SWT.BORDER | SWT.WRAP);
        objTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        managedForm.getToolkit().adapt(objTypeText, true, true);
        objTypeText.setEditable(false);

        Label indexLabel = new Label(grpMandatoryData, SWT.NONE);
        indexLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        indexLabel.setText("Sub Index:");
        managedForm.getToolkit().adapt(indexLabel, true, true);
        indexLabel.setForeground(managedForm.getToolkit().getColors().getColor(IFormColors.TITLE));

        subIndexText = new Text(grpMandatoryData, SWT.BORDER | SWT.WRAP);
        subIndexText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 5, 1));
        managedForm.getToolkit().adapt(subIndexText, true, true);
        subIndexText.setEditable(false);

        Composite groupComposite2 = new Composite(clientComposite, SWT.NONE);
        groupComposite2.setLayout(new GridLayout(2, false));

        Group grpOptionalData = new Group(groupComposite2, SWT.NONE);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gridData.widthHint = 558;
        grpOptionalData.setLayoutData(gridData);
        grpOptionalData.setText("Optional Data");
        grpOptionalData.setLayout(new GridLayout(2, false));

        FormData data;

        // lblDefaultvalue
        this.lblDefaultValue = managedForm.getToolkit().createLabel(grpOptionalData,
                Messages.advancedMappingObjectPropertySection_lbl_Default_Value);
        data = new FormData();
        this.lblDefaultValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        // lblDefaultValueValue Label (Actual value)
        this.lblDefaultValueValue = managedForm.getToolkit().createLabel(grpOptionalData,
                Messages.advancedMappingObjectPropertySection_No_Value_Set);
        data = new FormData();

        this.lblDefaultValueValue.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        // MappingobjectIndex label
        Label lblMOIndex = managedForm.getToolkit().createLabel(grpOptionalData,
                Messages.advancedMappingObjectPropertySection_lbl_MO_Index);
        lblMOIndex.setToolTipText("Index of Mapping Object"); //$NON-NLS-1$

        lblMOIndex.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        this.cmbIndex = new Combo(grpOptionalData, SWT.READ_ONLY);
        this.cmbIndex.setToolTipText(Messages.advancedMappingObjectPropertySection_help_Index_combobox);

        this.cmbIndex.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        this.cmbIndex.removeAll();

        // SubindexLabel
        Label lblSubindex = managedForm.getToolkit().createLabel(grpOptionalData,
                Messages.advancedMappingObjectPropertySection_lbl_MO_Subindex);
        lblSubindex.setToolTipText("Subindex of Subobject"); //$NON-NLS-1$

        lblSubindex.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        this.cmbSubindex = new Combo(grpOptionalData, SWT.READ_ONLY);
        this.cmbSubindex.setToolTipText(Messages.advancedMappingObjectPropertySection_help_subindex_combobox);

        this.cmbSubindex.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        this.cmbSubindex.removeAll();
        this.cmbSubindex.setEnabled(false);
        this.cmbSubindex.setBackground(XDDUtilities.getGrey(this.device));

        // OffsetLabel
        Label lbloffset = managedForm.getToolkit().createLabel(grpOptionalData,
                Messages.advancedMappingObjectPropertySection_lbl_Offset);

        lbloffset.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        this.txtOffset = managedForm.getToolkit().createText(grpOptionalData, ""); //$NON-NLS-1$

        this.txtOffset.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        this.txtOffset.addFocusListener(this.offsetListener);

        // Lengthlabel
        Label lbllength = managedForm.getToolkit().createLabel(grpOptionalData,
                Messages.advancedMappingObjectPropertySection_lbl_Length);

        lbllength.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        // Combobox / Textboxen

        // Textbox Lenght
        this.txtLength = managedForm.getToolkit().createText(grpOptionalData, ""); //$NON-NLS-1$

        this.txtLength.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        this.txtLength.addFocusListener(this.lengthListener);

        // Errorlabel
        this.lblError = managedForm.getToolkit().createLabel(grpOptionalData,
                "This is the initial Error text. This string reserves Space in the composite " //$NON-NLS-1$
                        + "\n for further use and implementation of Errors.Must not be translated."); //$NON-NLS-1$

        this.lblError.setForeground(XDDUtilities.getRed(this.device));
        this.lblError.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        this.lblError.setVisible(false);

    }

    private final FocusAdapter lengthListener = new FocusAdapter() {
        // On lost focus -> check entered Value and set it / show error
        @Override
        public void focusLost(FocusEvent arg0) {
            Integer length = XDDUtilities.parseInt(txtLength.getText());
            if (length != null && (length >= 0 && length <= 1490)) {
                lengthValue = length.intValue() * 8;
                setNewDefaultValue();
            } else {
                setError(5);
                txtLength.forceFocus();
                txtLength.selectAll();
            }
        } // focusLost
    };

}