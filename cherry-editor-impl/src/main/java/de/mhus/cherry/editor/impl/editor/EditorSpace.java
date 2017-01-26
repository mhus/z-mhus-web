package de.mhus.cherry.editor.impl.editor;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.addon.borderlayout.BorderLayout;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.SliderPanelStyles;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderPanelListener;
import org.vaadin.sliderpanel.client.SliderTabPosition;

import com.vaadin.server.Page;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.ClientConnector.AttachEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.ResourceProvider;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorPanel;
import de.mhus.cherry.portal.api.control.EditorControl;
import de.mhus.cherry.portal.api.control.EditorControlFactory;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.control.GuiLifecycle;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.cherry.portal.api.control.Navigable;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.MProperties;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.logging.MLogUtil;
import de.mhus.lib.core.security.Account;
import de.mhus.lib.core.strategy.Operation;
import de.mhus.lib.core.util.Pair;
import de.mhus.lib.errors.MException;
import de.mhus.lib.karaf.MOsgi;
import de.mhus.lib.vaadin.DialogControl;
import de.mhus.lib.vaadin.MVaadin;
import de.mhus.lib.vaadin.ModalDialog;
import de.mhus.lib.vaadin.VWorkBar;
import de.mhus.lib.vaadin.ModalDialog.Action;
import de.mhus.lib.vaadin.operation.VaadinOperation;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AccessApi;
import de.mhus.osgi.sop.api.action.ActionApi;
import de.mhus.osgi.sop.api.action.ActionDescriptor;

public class EditorSpace extends VerticalLayout implements Navigable, GuiLifecycle {

	private static Log log = Log.getLog(EditorSpace.class);
	private static final long serialVersionUID = 1L;
	private Panel panel;
	private VerticalLayout contentLayout;
	private CaoNode[] resource;
	private EditorPanel editor;
	private Button bSave;
	private Button bCancel;
	private TabSheet tabs;
	private SliderPanel navigationSlider;
	private BorderLayout navigationContent;
	private VerticalLayout createContent;
	private SliderPanel createSlider;
	private NavigationView navigation;
	private VWorkBar navigationToolBar;
	private TextField breadcrumb;
	private HorizontalLayout actionButtons;

	@Override
	public String navigateTo(String selection, String filter) {
		
		return doShow(filter);
		
	}

	@Override
	public void doInitialize() {
		
		panel = new Panel();
		setMargin(true);
		addComponent(panel);
		panel.setCaption("Editor");
		panel.setSizeFull();
		tabs = new TabSheet();

		BorderLayout borders = new BorderLayout();
		borders.setSizeFull();
		
		breadcrumb = new TextField();
		breadcrumb.setSizeFull();
		MVaadin.handleEnter(breadcrumb, (sender, target) -> { doSelectFromBreadcrumb(); } );
		borders.addComponent(breadcrumb, BorderLayout.Constraint.NORTH);
		
		HorizontalLayout sliders = new HorizontalLayout();
		sliders.setSizeFull();
		
		borders.addComponent(tabs, BorderLayout.Constraint.CENTER);
		borders.addComponent(new Label("   "),BorderLayout.Constraint.EAST);
		sliders.addComponent(borders);
		sliders.setExpandRatio(borders, 1);
		
		panel.setContent(sliders);
		
		contentLayout = new VerticalLayout();
//		contentLayout.addComponent(l);

		navigationContent = new BorderLayout();
		navigationContent.setWidth( "100%" );
		navigationContent.setHeight("100%");
		
        navigationSlider =
                new SliderPanelBuilder(navigationContent, "Navigation")
	                .mode(SliderMode.RIGHT)
	                .tabPosition(SliderTabPosition.MIDDLE)
                    .flowInContent(true)
                    .autoCollapseSlider(true)
                    .zIndex(9980)
                    .animationDuration(200)
                    .style(SliderPanelStyles.COLOR_GRAY)
                    .listener(new SliderPanelListener() {
                        @Override
                        public void onToggle(final boolean expand) {
                       	 if (expand)
                    		 doResetNavigationContent();
                        }
                    }).build();
        sliders.addComponent(navigationSlider);
 		navigationSlider.setFixedContentSize(800);
      
 		createContent = new VerticalLayout();
 		createContent.setWidth("500px");
 		createContent.setHeight("100%");
 		
 		createSlider =
                 new SliderPanelBuilder(createContent, "Create")
 	                .mode(SliderMode.RIGHT)
 	                .tabPosition(SliderTabPosition.BEGINNING)
                     .flowInContent(true)
                     .autoCollapseSlider(true)
                     .zIndex(9980)
                     .animationDuration(500)
                     .style(SliderPanelStyles.COLOR_BLUE)
                     .listener(new SliderPanelListener() {
                         @Override
                         public void onToggle(final boolean expand) {
                        	 if (expand)
                        		 doResetCreateContent();
                         }
                     }).build();
 		sliders.addComponent(createSlider);
         
        this.addAttachListener(new ClientConnector.AttachListener() {
			
			@Override
			public void attach(AttachEvent event) {
				navigationContent.setWidth( "100%" );
				navigationSlider.setFixedContentSize(Page.getCurrent().getBrowserWindowWidth() - 200);
			}
		});
		Page.getCurrent().addBrowserWindowResizeListener(new Page.BrowserWindowResizeListener() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public void browserWindowResized(BrowserWindowResizeEvent event) {
				navigationContent.setWidth( "100%" );
				navigationSlider.setFixedContentSize(event.getWidth() - 200);
			}
		});
		
		actionButtons = new HorizontalLayout();
		actionButtons.setSizeFull();
		borders.addComponent(actionButtons, BorderLayout.Constraint.SOUTH);
		
		Label dummy = new Label(" ");
		actionButtons.addComponent(dummy);
		
		bCancel = new Button("Cancel", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				doCancel();
			}
		});
		actionButtons.addComponent(bCancel);
		actionButtons.setExpandRatio(dummy, 1f);
		bSave = new Button("Save", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				doSave();
			}
		});
		actionButtons.addComponent(bSave);

	}

	private void doSelectFromBreadcrumb() {
		VirtualHost vHost = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost();
		String path = breadcrumb.getValue();
		CaoNode res = vHost.getResourceResolver().getResource(vHost, path);
		doShow(vHost, res);
	}

	protected void doResetCreateContent() {
		createContent.removeAllComponents();
		
		CaoNode[] parent = resource;
		if (navigationSlider.isExpanded()) {
			NavNode[] p = navigation.getSelectedNode();
			if (p != null) parent = CherryUtil.getCurrent(p);
		}
		if (parent == null) {
			createSlider.collapse();
			return;
		}
		final CaoNode[] parentFinal = parent;
		VirtualHost vHost = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost();
		Collection<ActionDescriptor> actions = vHost.getActions(CherryApi.ACTION_CREATE, parent);
		for (ActionDescriptor action : actions) {
			Button b = new Button(action.getCaption());
			b.setWidth("100%");
			b.setStyleName("flatbutton");
			b.setIcon(FontAwesome.ARROW_RIGHT);
			b.setData(action);
			b.addClickListener((event) -> {
				ActionDescriptor a = (ActionDescriptor)event.getButton().getData();
				ActionDialog.doExecuteAction(a, parentFinal);
				createSlider.collapse();
			});
			createContent.addComponent(b);
		}
		Label dummy = new Label();
		createContent.addComponent(dummy);
		createContent.setExpandRatio(dummy, 1);
	}

	protected void doResetNavigationContent() {
		if (navigation == null) {
			navigation = new NavigationView();
			navigationContent.addComponent(navigation, BorderLayout.Constraint.CENTER);
			navigationContent.setWidth( "100%" );
			navigationSlider.setFixedContentSize(Page.getCurrent().getBrowserWindowWidth() - 200);
			
			navigationToolBar = new VWorkBar() {

				@Override
				public List<Pair<String, Object>> getAddOptions() {
					NavNode[] selectedNode = navigation.getSelectedNode();
					if (selectedNode != null) {
						createSlider.expand();
						navigationSlider.collapse();
					}
					return null;
				}

				@Override
				public List<Pair<String, Object>> getModifyOptions() {
					NavNode[] selectedNode = navigation.getSelectedNode();
					if (selectedNode == null) return null;
					
					LinkedList<Pair<String,Object>> list = new LinkedList<>();
					list.add(new Pair<String, Object>("Edit", "."));
					
					Collection<ActionDescriptor> actions = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().getActions(CherryApi.ACTION_MODIFY, CherryUtil.getCurrent(selectedNode));
					for (ActionDescriptor action :actions) {
						list.add(new Pair<String,Object>(action.getCaption(), action ) );
					}
					
					return list;
				}

				@Override
				public List<Pair<String, Object>> getDeleteOptions() {
					NavNode[] selectedNode = navigation.getSelectedNode();
					if (selectedNode == null) return null;
					LinkedList<Pair<String,Object>> list = new LinkedList<>();
					Collection<ActionDescriptor> actions = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().getActions(CherryApi.ACTION_DELETE, CherryUtil.getCurrent(selectedNode));
					for (ActionDescriptor action :actions) {
						list.add(new Pair<String,Object>(action.getCaption(), action ) );
					}
					return list;
				}

				@Override
				protected void doModify(Object action) {
					System.out.println("Modify: " + action);
					NavNode[] selectedNode = navigation.getSelectedNode();
					if (selectedNode == null) return;
					if (".".equals(action)) {
						if (selectedNode != null && selectedNode.length == 1) {
							GuiUtil.getApi().navigateToEditor(selectedNode[0].getCurrent());
							navigationSlider.collapse();
	//						doShow(vHost,selectedNode.getCurrent());
						}
					} else {
						final CaoNode[] parentFinal = CherryUtil.getCurrent(selectedNode);
						ActionDialog.doExecuteAction((ActionDescriptor) action, parentFinal);
					}
				}

				@Override
				protected void doDelete(Object action) {
					System.out.println("Delete: " + action);
					NavNode[] selectedNode = navigation.getSelectedNode();
					if (selectedNode == null) return;
					final CaoNode[] parentFinal = CherryUtil.getCurrent(selectedNode);
					System.out.println("Node: " + parentFinal);
					ActionDialog.doExecuteAction((ActionDescriptor) action, parentFinal);
				}

				@Override
				protected void doAdd(Object action) {
				}

			};
			navigationToolBar.setButtonStyleName("flatbutton");
			navigationContent.addComponent(navigationToolBar, BorderLayout.Constraint.SOUTH);

			navigation.addValueChangedListener(new Property.ValueChangeListener() {
				
				@Override
				public void valueChange(ValueChangeEvent event) {
					NavNode[] node = navigation.getSelectedNode();
					// System.out.println("Selected: " + node);
					if (node == null) {
						navigationToolBar.setEnabled(false);
					} else {
						navigationToolBar.setEnabled(true);
						
					}
				}
			});
			navigation.addItemClickListener( new ItemClickListener() {
				
				@Override
				public void itemClick(ItemClickEvent event) {
					if (event.isDoubleClick()) {
						NavNode[] selectedNode = navigation.getSelectedNode();
						if (selectedNode == null || selectedNode.length != 1) return;
						GuiUtil.getApi().navigateToEditor(selectedNode[0].getCurrent());
						navigationSlider.collapse();
					}
				}
			} );
			
			if (resource != null && resource.length > 0) {
				navigationToolBar.setEnabled(false);
				navigation.setSelected(resource[0]);
			}
		}
		
	}

	private synchronized String doShow(String resId) {
		log.d("show resource", resId);
		
		tabs.removeAllComponents();
		tabs.addTab(contentLayout, "Content");
		
		contentLayout.removeAllComponents();
		
		VirtualHost vHost = Sop.getApi(CherryApi.class).findVirtualHost( GuiUtil.getApi().getHost() );
		CaoNode resource = vHost.getResourceResolver().getResource(vHost, resId);
		if (resource == null) {
			// resource not found
			return null;
		}
		return doShow(vHost, resource);
	}
	
	private synchronized String doShow(VirtualHost vHost, CaoNode resource) {

		EditorFactory factory = Sop.getApi(WidgetApi.class).getControlEditorFactory(vHost,resource);
		if (factory == null) {
			// editor not found
			return null;
		}
		
		this.resource = new CaoNode[] {resource};
		breadcrumb.setValue( resource.getConnection().getName() + ":" + resource.getPath() );
		doFillTabs(resource, factory);
		
		
		Panel editorPanel = new Panel();
		editorPanel.setSizeFull();
		contentLayout.addComponent(editorPanel);

		try {
			editor = factory.createEditor(resource.getWritableNode());
			editor.setSizeFull();
			editor.setMargin(true);
			editorPanel.setContent(editor);
			editor.initUi();
			panel.setCaption( editor.getTitle() );
		} catch (MException e) {
			log.e(e);
			return null;
		}
		
		contentLayout.setExpandRatio(editorPanel, 1f);
		contentLayout.setMargin(true);
		contentLayout.setSpacing(true);
		//contentLayout.setSizeFull();		
		
		return "Edit " + resource.getString("title", resource.getName());
	}

	private void doFillTabs(CaoNode res, EditorFactory editorFactory) {
		AccessApi aaa = Sop.getApi(AccessApi.class);
		Account account = aaa.getCurrentOrGuest().getAccount();
		for (EditorControlFactory factory : CherryUtil.orderServices(EditorSpace.class, EditorControlFactory.class)) {
			if (aaa.hasGroupAccess(account, EditorSpace.class, factory.getName(), "create")) {
				EditorControl c = factory.createEditorControl(res, editorFactory);
				if (c != null) {
					tabs.addTab(c, factory.getName());
				}
			}
		}
		
		
	}

	protected void doCancel() {
		doBack();
	}

	protected void doSave() {
		String error = editor.doSave();
		if (error != null) {
			UI.getCurrent().showNotification(error);
			return;
		}
		doBack();
	}

	private void doBack() {
		GuiUtil.getApi().navigateBack();
	}

	@Override
	public void doDestroy() {
		// TODO Auto-generated method stub
		
	}	

}
