package de.mhus.cherry.editor.impl.pages;

import java.util.LinkedList;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import aQute.bnd.annotation.component.Component;
import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.ControlParent;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.control.PageControl;
import de.mhus.cherry.portal.api.control.PageControlFactory;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.MFile;
import de.mhus.lib.core.MLog;
import de.mhus.lib.core.MString;
import de.mhus.lib.vaadin.ConfirmDialog;
import de.mhus.lib.vaadin.TextInputDialog;
import de.mhus.osgi.sop.api.Sop;

@Component
public class PageControlControl extends MLog implements PageControlFactory {
	
	@Override
	public String getName() {
		return "Control";
	}

	@Override
	public PageControl createPageControl() {
		
		return new Control();
	}

	private static class Control extends PageControl {

		private LinkedList<Button> createButtons = new LinkedList<>();
		private Button bDeletePage;
		private CaoNode nav;
		private CaoNode res;
		private ControlParent controlParent;

		public Control() {

			bDeletePage = new Button("Delete");
			bDeletePage.setIcon(FontAwesome.MINUS);
			bDeletePage.setEnabled(false);
			bDeletePage.addClickListener(new ClickListener() {
				
				@Override
				public void buttonClick(ClickEvent event) {
					doDelete();
				}
			});
			bDeletePage.setWidth("100%");
			addComponent(bDeletePage);
			
			{
				Label label = new Label( "Create Page" );
				addComponent( label);
			}
			for (EditorFactory editor : CherryUtil.orderServices(PageControlControl.class, EditorFactory.class) ) {
				if (editor.isPage()) {
					Button bCreatePage = new Button(editor.getName());
					// bCreatePage.setIcon(FontAwesome.PLUS);
					bCreatePage.setEnabled(false);
					bCreatePage.setIcon(FontAwesome.PLUS);
					bCreatePage.addClickListener(new ClickListener() {
						
						@Override
						public void buttonClick(ClickEvent event) {
							doCreatePage(editor);
						}
					});
					bCreatePage.setWidth("100%");
					addComponent(bCreatePage);
					createButtons.add(bCreatePage);
				}
			}

		}
		
		protected void doDelete() {
			if (nav == null || res == null) return;
			
			try {
				CaoNode parent = nav.getParent();
				if (parent == null) {
					UI.getCurrent().showNotification("Can't delete root");
					return;
				}
				VirtualHost vHost = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost();
			
//				CaoNode page = res.getParent();
				CaoNode page = res;
				EditorFactory editor = Sop.getApi(WidgetApi.class).getControlEditorFactory(vHost, page);
			
				ConfirmDialog dialog = new ConfirmDialog("Delete Page", "Really delete page and all sub pages", "Delete", "Cancel", new ConfirmDialog.Listener() {
					
					@Override
					public void onClose(ConfirmDialog dialog) {
						try {
							if (dialog.isCancel()) return;
							if (editor.deletePage(nav))
								UI.getCurrent().showNotification("Page deleted");
							else
								UI.getCurrent().showNotification("Error deleting page");
							controlParent.doRefreshNode(parent);
						} catch (Throwable t) {
							t.printStackTrace();
							UI.getCurrent().showNotification("Error deleting page");
						}
					}
				});
				dialog.show(getUI());
				
			} catch (Throwable t) {
				t.printStackTrace();
				UI.getCurrent().showNotification("Error deleting page");
			}
		}

		protected void doCreatePage(EditorFactory editor) {
			if (nav == null) return;
			try {
				TextInputDialog dialog = new TextInputDialog("Create New Page", "Insert title of the new page", "", "Create", "Cancel", new TextInputDialog.Listener() {
					
					@Override
					public boolean validate(String txtInput) {
						return MString.isSet(txtInput);
					}
					
					@Override
					public void onClose(TextInputDialog dialog) {
						if (dialog.isCancel()) return;
						try {
							String title = dialog.getInputText();
							CaoNode newNode = editor.createPage(nav, title);
							if (newNode == null)
								UI.getCurrent().showNotification("Error creating page!");
							else
								UI.getCurrent().showNotification("Page Created");
							
							// TODO Update Tree
							controlParent.doRefreshNode(nav);
						} catch (Throwable t) {
							t.printStackTrace();
							UI.getCurrent().showNotification("Error creating page!");
						}
					}
				});
				dialog.show(getUI());
				
			} catch (Throwable t) {
				t.printStackTrace();
				UI.getCurrent().showNotification("Error creating page!");
			}
		}
		
		
		@Override
		public void doClean() {
			bDeletePage.setEnabled(false);
			createButtons.forEach(this::doDisable);
			this.nav = null;
			this.res = null;
		}
		
		public void doDisable(Button b) {
			b.setEnabled(false);
		}

		public void doEnable(Button b) {
			b.setEnabled(true);
		}
		
		@Override
		public void doUpdate(CaoNode nav, CaoNode res) {
			bDeletePage.setEnabled(true);
			createButtons.forEach(this::doEnable);
			this.nav = nav;
			this.res = res;
		}

		@Override
		public void doInit(ControlParent controlParent) {
			this.controlParent = controlParent;
		}
		
	}
}
