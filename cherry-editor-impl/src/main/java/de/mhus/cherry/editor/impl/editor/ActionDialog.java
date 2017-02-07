package de.mhus.cherry.editor.impl.editor;

import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.strategy.DefaultTaskContext;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.vaadin.DialogControl;
import de.mhus.lib.vaadin.ModalDialog;
import de.mhus.lib.vaadin.operation.VaadinOperation;
import de.mhus.osgi.sop.api.action.ActionDescriptor;

public class ActionDialog extends ModalDialog implements DialogControl {

	private VaadinOperation operation;
	private MProperties editorProperties;
	private Action confirm;
	private Action cancel;
	private CaoNode[] node;
	private Component editor;

	public ActionDialog(VaadinOperation operation, CaoNode[] node) throws Exception {
		this.operation = operation;
		editorProperties = new MProperties();
		editorProperties.put(CherryUtil.NODE, node);
		confirm = new Action("confirm", operation.nls("btn.execute=Execute"));
		confirm.setDefaultAction(true);
		cancel = new Action("cancel", operation.nls("btn.cancel=Cancel"));
		actions = new Action[] {cancel,confirm};
		this.node = node;
		
		setWidth("500px");
		setHeight("90%");
		initUI();
		
	}

	@Override
	protected void initContent(VerticalLayout layout) throws Exception {
		setCaption(operation.getDescription().getCaption());
		{
			TextField label = new TextField();
			label.setEnabled(false);
			label.setValue(nodePaths());
			label.setWidth("100%");
			layout.addComponent(label);
		}
		editor = operation.createEditor(editorProperties, this);
		if (editor != null)	{
			layout.addComponent(editor);
			layout.setExpandRatio(editor, 1);
		}
	}

	private String nodePaths() {
		StringBuilder out = new StringBuilder();
		for (CaoNode n : node) {
			if (out.length() != 0)
				out.append("; ");
			out.append(n.getPath());
		}
		return out.toString();
	}

	@Override
	protected boolean doAction(Action action) {
		if (action == cancel) return true;
		if (action == confirm) {
			try {
				OperationResult result = operation.doExecute(editorProperties, editor);
				showResult(operation.getDescription().getCaption(), result);
			} catch (Exception e) {
				MLogUtil.log().i(this,e);
				Notification.show(e.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
		}
		return true;
	}

	private static void showResult(String caption, OperationResult result) {
		if (result == null) {
			Notification.show(caption, "No result", Notification.TYPE_ERROR_MESSAGE);
		} else
		if (result.isSuccessful()) {
			Notification.show(caption, result.getMsg(), Notification.TYPE_HUMANIZED_MESSAGE);
		} else {
			Notification.show(caption, result.getMsg(), Notification.TYPE_ERROR_MESSAGE);
		}
	}

	@Override
	public void canSave(boolean saveable) {
		confirm.setEnabled(saveable);
	}

	public static void doExecuteAction(ActionDescriptor action, CaoNode[] node) {
		Operation oper = action.getAction().adaptTo(Operation.class);
		
		
		if (oper != null && oper instanceof VaadinOperation) {
			final VaadinOperation o = (VaadinOperation)oper;
			try {
				ActionDialog dialog = new ActionDialog(o, node);
				dialog.show(UI.getCurrent());
			} catch (Exception e) {
				MLogUtil.log().i(e);
			}
			
		} else
		if (oper != null) {
			DefaultTaskContext context = new DefaultTaskContext(oper.getClass());
			MProperties parameters = new MProperties();
			parameters.put(CherryUtil.NODE, node);
			context.setParameters(parameters);
			try {
				OperationResult result = oper.doExecute(context);
				showResult(oper.getDescription().getCaption(), result);
			} catch (Exception e) {
				MLogUtil.log().d(e);
				Notification.show(e.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
		} else {
			MProperties parameters = new MProperties();
			parameters.put(CherryUtil.NODE, node);
			try {
				OperationResult result = action.getAction().doExecute(parameters, null);
				showResult(action.getCaption(), result);
			} catch (Exception e) {
				MLogUtil.log().d(e);
				Notification.show(e.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void show(UI ui) throws Exception {
		if (editor == null) {
			doAction(confirm);
			return;
		}
		super.show(ui);
	}

}
