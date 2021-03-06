/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.modeler.core.adapters.AdapterUtil;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.core.utils.NamespaceUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.xsd.XSDElementDeclaration;

/**
 * @author Bob Brodt
 *
 */
public class ItemDefinitionPropertiesAdapter extends ExtendedPropertiesAdapter<ItemDefinition> {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public ItemDefinitionPropertiesAdapter(AdapterFactory adapterFactory, ItemDefinition object) {
		super(adapterFactory, object);
    	final EStructuralFeature ref = Bpmn2Package.eINSTANCE.getItemDefinition_StructureRef();
		setProperty(ref, UI_CAN_CREATE_NEW, Boolean.TRUE);
		setProperty(ref, UI_IS_MULTI_CHOICE, Boolean.TRUE);
		
		
    	setFeatureDescriptor(ref,
			new FeatureDescriptor<ItemDefinition>(adapterFactory,object,ref) {
				@Override
				public String getLabel(Object context) {
					return "Data Type";
				}

				@Override
				public String getDisplayName(Object context) {
					final ItemDefinition itemDefinition = adopt(context);
					Resource resource = ModelUtil.getResource(itemDefinition);
					String name = "";
					Object value = itemDefinition.getStructureRef();
					if (value!=null) {
						if (value instanceof XSDElementDeclaration) {
							XSDElementDeclaration elem = (XSDElementDeclaration)value;
							name = elem.getQName();
						}
						else if (value instanceof Message) {
							Message message = (Message)value;
							name = NamespaceUtil.normalizeQName(resource,message.getQName());
						}
						else if (ModelUtil.isStringWrapper(value))
							name = ModelUtil.getStringWrapperValue(value);
					}
					else {
						name = itemDefinition.getId() + ".type";
					}
					if (itemDefinition.isIsCollection())
						name += "[]";
					return name;
				}
				
	    		@Override
				public EObject createFeature(Resource resource, Object context, EClass eClass) {
					final ItemDefinition itemDefinition = adopt(context);
					EObject structureRef = ModelUtil.createStringWrapper("");
					itemDefinition.setStructureRef(ref);
					return structureRef;
	    		}

	    		@Override
	    		public Object getValue(Object context) {
					final ItemDefinition itemDefinition = adopt(context);
					Object value = itemDefinition.getStructureRef();
					if (value==null)
						value = ModelUtil.createStringWrapper("");
					return value;
	    		}

	    		@Override
	    		public void setValue(Object context, Object value) {
	    			if (value instanceof String) {
						value = ModelUtil.createStringWrapper((String)value);
	    			}
	    			else if (!ModelUtil.isStringWrapper(value)) {
	    				return;
	    			}
	    			super.setValue(object, value);
	    		}

				@Override
				public Hashtable<String, Object> getChoiceOfValues(Object context) {
					ItemDefinition object = adopt(context);
					// add all ItemDefinitions
					Hashtable<String,Object> choices = new Hashtable<String,Object>();
					String s;
					Definitions defs = ModelUtil.getDefinitions(object);
					List<ItemDefinition> itemDefs = ModelUtil.getAllRootElements(defs, ItemDefinition.class);
					for (ItemDefinition id : itemDefs) {
						s = ModelUtil.getStringWrapperValue(id.getStructureRef());
						if (s==null || s.isEmpty())
							s = id.getId();
						choices.put(s,id);
					}
					return choices;
				}

				@Override
				public String getChoiceString(Object value) {
					return super.getChoiceString(value);
				}
	    		
			}
    	);
    	
		setObjectDescriptor(new ObjectDescriptor<ItemDefinition>(adapterFactory, object) {
			@Override
			public String getDisplayName(Object context) {
				return getFeatureDescriptor(ref).getDisplayName(context);
			}
			
			@Override
			public String getLabel(Object context) {
				return getFeatureDescriptor(ref).getLabel(context);
			}
			
			@Override
			
			public ItemDefinition createObject(Resource resource, Object context) {
				ExtendedPropertiesAdapter adapter = (ExtendedPropertiesAdapter) AdapterUtil.adapt(
						Bpmn2Package.eINSTANCE.getRootElement(),
						ExtendedPropertiesAdapter.class);
				ItemDefinition itemDef = (ItemDefinition) adapter.getObjectDescriptor().createObject(resource, object);
				EObject value = ModelUtil.createStringWrapper(itemDef.getId());
				itemDef.setStructureRef(value);
				return itemDef;
			}
		});
	}

}
