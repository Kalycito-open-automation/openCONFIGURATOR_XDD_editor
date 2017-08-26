/*******************************************************************************
 * @file   NewFirmwareWizard.java
 *
 * @author Sree Hari Vignesh, Kalycito Infotech Private Limited.
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

package com.br_automation.buoat.xddeditor.XDD.wizards;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import com.br_automation.buoat.xddeditor.XDD.DocumentRoot;
import com.br_automation.buoat.xddeditor.XDD.ISO15745ProfileType;
import com.br_automation.buoat.xddeditor.XDD.ProfileBodyDataType;
import com.br_automation.buoat.xddeditor.XDD.ProfileHeaderDataType;
import com.br_automation.buoat.xddeditor.XDD.TDeviceFunction;
import com.br_automation.buoat.xddeditor.XDD.TFirmwareList;
import com.br_automation.buoat.xddeditor.XDD.TGeneralFeatures;
import com.br_automation.buoat.xddeditor.XDD.TObject;
import com.br_automation.buoat.xddeditor.XDD.XDDFactory;
import com.br_automation.buoat.xddeditor.XDD.impl.ProfileBodyCommunicationNetworkPowerlinkImpl;
import com.br_automation.buoat.xddeditor.XDD.impl.TApplicationLayersImpl;
import com.br_automation.buoat.xddeditor.XDD.impl.TNetworkManagementImpl;
import com.br_automation.buoat.xddeditor.XDD.impl.TObjectImpl;
import com.br_automation.buoat.xddeditor.editor.editors.DeviceDescriptionFileEditor;

import FwSchema.util.FwSchemaResourceFactoryImpl;

/**
 * Wizard page to add new firmware.
 *
 * @author Sree Hari Vignesh
 *
 */
public class NewObjectWizard extends Wizard {

    private static final String WINDOW_TITLE = "POWERLINK firmware wizard";

    /**
     * Add validateFirmwareWizardPage
     */
    private final AddObjectWizardPage addObjectWizardPage;

    private DocumentRoot documentRoot;

    private DeviceDescriptionFileEditor editor;

    public NewObjectWizard(

            DocumentRoot selectedObj, DeviceDescriptionFileEditor editor) {
        if (selectedObj == null) {
            System.err.println("Invalid node selection");
        }
        documentRoot = selectedObj;
        this.editor = editor;
        setWindowTitle(WINDOW_TITLE);
        addObjectWizardPage = new AddObjectWizardPage(WINDOW_TITLE, documentRoot, editor);
    }

    /**
     * Add wizard page
     */
    @Override
    public void addPages() {
        super.addPages();
        addPage(addObjectWizardPage);
    }

    @Override
    public boolean canFinish() {

        if (!addObjectWizardPage.isPageComplete()) {
            return false;
        }

        return true;
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
        // options.put(XMLResource.OPTION_ENCODING, "UTF-8");
        try {
            resource.save(options);
            return true;
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return false;
    }

    public TDeviceFunction getDeviceFunction() {
        EList<ISO15745ProfileType> profiles = documentRoot.getISO15745ProfileContainer().getISO15745Profile();
        ISO15745ProfileType profile1 = profiles.get(0);
        ISO15745ProfileType profile2 = profiles.get(1);

        ProfileHeaderDataType header1 = profile1.getProfileHeader();

        ProfileBodyDataType body1 = profile1.getProfileBody();
        EList<EObject> bodyContents = body1.eContents();
        for (EObject object : bodyContents) {
            if (object instanceof TDeviceFunction) {
                TDeviceFunction deviceFunction = (TDeviceFunction) object;
                return deviceFunction;
            }
        }
        return null;
    }

    private TApplicationLayersImpl getApplicationLayer() {
        EList<ISO15745ProfileType> profiles = documentRoot.getISO15745ProfileContainer().getISO15745Profile();
        for (ISO15745ProfileType profile : profiles) {
            ProfileBodyDataType profileBody = profile.getProfileBody();
            if (profileBody instanceof ProfileBodyCommunicationNetworkPowerlinkImpl) {
                EList<EObject> bodyContents = profileBody.eContents();
                for (EObject object : bodyContents) {
                	System.err.println("Object instance list.."+object);
                    if (object instanceof TApplicationLayersImpl) {
                    	TApplicationLayersImpl applicationLayer = (TApplicationLayersImpl) object;
                       return applicationLayer;
                    }
                }
            }
        }
       return null;

    }

    @Override
    public boolean performFinish() {

    	if(getApplicationLayer().getObjectList() != null) {
    		TObject obj = XDDFactory.eINSTANCE.createTObject();

    		obj.setAccessType(addObjectWizardPage.getAccessType());

    		if(!addObjectWizardPage.getActualValue().isEmpty()) {
    		obj.setActualValue(addObjectWizardPage.getActualValue());
    		}

    		if(addObjectWizardPage.getDataType() != null){
    		obj.setDataType(addObjectWizardPage.getDataType());
    		}

    		if(!addObjectWizardPage.getDefaultValue().isEmpty()) {
    		obj.setDefaultValue(addObjectWizardPage.getDefaultValue());
    		}

    		//obj.setDenotation(addObjectWizardPage.getDenotation());

    		if(!addObjectWizardPage.getHighLimit().isEmpty()) {
    		obj.setHighLimit(addObjectWizardPage.getHighLimit());
    		}

    		if(!addObjectWizardPage.getLowLimit().isEmpty()) {
    			obj.setLowLimit(addObjectWizardPage.getLowLimit());
    		}


    		if(addObjectWizardPage.getIndex() != null) {
    		obj.setIndex(addObjectWizardPage.getIndex());
    		}

    		obj.setObjectType(addObjectWizardPage.getObjectType());
    		obj.setPDOmapping(addObjectWizardPage.getPdoMapping());

    		obj.setName(addObjectWizardPage.getObjectName());

    		getApplicationLayer().getObjectList().getObject().add(obj);
    	}

               updateDocument(documentRoot);

        return true;
    }

}
