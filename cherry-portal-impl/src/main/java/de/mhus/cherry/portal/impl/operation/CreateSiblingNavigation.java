package de.mhus.cherry.portal.impl.operation;

import java.util.Collection;
import java.util.LinkedList;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CallContext;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.aspect.StructureControl;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.definition.DefRoot;
import de.mhus.lib.core.strategy.NotSuccessful;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationDescription;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.Successful;
import de.mhus.lib.core.strategy.TaskContext;
import de.mhus.lib.form.DataSource;
import de.mhus.lib.form.Item;
import de.mhus.lib.form.PropertiesDataSource;
import de.mhus.lib.form.definition.FmCheckbox;
import de.mhus.lib.form.definition.FmCombobox;
import de.mhus.lib.form.definition.FmText;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperation;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationEditor;
import de.mhus.lib.vaadin.operation.AbstractVaadinOperationForm;
import de.mhus.osgi.sop.api.Sop;

@Component(properties="tags=control|caonode|create",provide=Operation.class)
public class CreateSiblingNavigation extends CreateChildNavigation {

	@Override
	protected StructureControl getParentControl(CaoNode nav) {
		return  nav.getParent().adaptTo(StructureControl.class);
	}

	@Override
	protected String getCaption() {
		return "Create Sibling Navigation";
	}

}
