package de.mhus.cherry.editor.impl.editor;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.vaadin.DialogControl;
import de.mhus.lib.vaadin.ModalDialog;
import de.mhus.lib.vaadin.ModalDialog.Action;
import de.mhus.lib.vaadin.operation.VaadinOperation;

public class ActionDialog extends ModalDialog implements DialogControl {

	private VaadinOperation operation;
	private MProperties editorProperties;
	private Action confirm;
	private Action cancel;
	private CaoNode node;
	private Component editor;

	public ActionDialog(VaadinOperation operation, CaoNode node) throws Exception {
		this.operation = operation;
		editorProperties = new MProperties();
		editorProperties.put("caonode", node);
		confirm = new Action("confirm", operation.nls("btn.execute=Execute"));
		confirm.setDefaultAction(true);
		cancel = new Action("cancel", operation.nls("btn.cancel=Cancel"));
		actions = new Action[] {confirm,cancel};
		this.node = node;
		
		setWidth("500px");
		setHeight("90%");
		initUI();
	}

	@Override
	protected void initContent(VerticalLayout layout) throws Exception {
		setCaption(operation.getDescription().getCaption());
		{
			Label label = new Label(node.getPath());
			layout.addComponent(label);
		}
		editor = operation.createEditor(editorProperties, this);
		if (editor != null)	{
			layout.addComponent(editor);
			layout.setExpandRatio(editor, 1);
		}
	}

	@Override
	protected boolean doAction(Action action) {
		if (action == cancel) return true;
		if (action == confirm) {
			try {
				OperationResult result = operation.doExecute(editor);
				if (result.isSuccessful()) {
					Notification.show(result.getMsg(), Notification.TYPE_HUMANIZED_MESSAGE);
				} else {
					Notification.show(result.getMsg(), Notification.TYPE_ERROR_MESSAGE);
				}
			} catch (Exception e) {
				MLogUtil.log().d(this,e);
				Notification.show(e.toString(), Notification.TYPE_ERROR_MESSAGE);
			}
		}
		return true;
	}

	@Override
	public void canSave(boolean saveable) {
		confirm.setEnabled(saveable);
	}

}
