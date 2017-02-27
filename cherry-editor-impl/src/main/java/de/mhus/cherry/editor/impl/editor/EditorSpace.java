package de.mhus.cherry.editor.impl.editor;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.vaadin.addon.borderlayout.BorderLayout;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;
import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelBuilder;
import org.vaadin.sliderpanel.SliderPanelStyles;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderPanelListener;
import org.vaadin.sliderpanel.client.SliderTabPosition;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ClientConnector;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Page.BrowserWindowResizeEvent;
import com.vaadin.shared.MouseEventDetails.MouseButton;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.mhus.cherry.portal.api.CherryApi;
import de.mhus.cherry.portal.api.NavNode;
import de.mhus.cherry.portal.api.StructureChangesListener;
import de.mhus.cherry.portal.api.VirtualHost;
import de.mhus.cherry.portal.api.WidgetApi;
import de.mhus.cherry.portal.api.control.EditorControl;
import de.mhus.cherry.portal.api.control.EditorControlFactory;
import de.mhus.cherry.portal.api.control.EditorFactory;
import de.mhus.cherry.portal.api.control.EditorPanel;
import de.mhus.cherry.portal.api.control.GuiUtil;
import de.mhus.cherry.portal.api.control.LayoutPanel;
import de.mhus.cherry.portal.api.util.CherryUtil;
import de.mhus.lib.cao.CaoConst;
import de.mhus.lib.cao.CaoCore;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoUtil;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.cao.aspect.StructureControl;
import de.mhus.lib.cao.util.DefaultChangesQueue.Change;
import de.mhus.lib.cao.util.DefaultChangesQueue.EVENT;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.security.Account;
import de.mhus.lib.core.util.Pair;
import de.mhus.lib.errors.MException;
import de.mhus.lib.vaadin.ConfirmDialog;
import de.mhus.lib.vaadin.MVaadin;
import de.mhus.lib.vaadin.VWorkBar;
import de.mhus.lib.vaadin.desktop.GuiLifecycle;
import de.mhus.lib.vaadin.desktop.Navigable;
import de.mhus.osgi.sop.api.Sop;
import de.mhus.osgi.sop.api.aaa.AccessApi;
import de.mhus.osgi.sop.api.action.ActionDescriptor;

public class EditorSpace extends VerticalLayout implements Navigable, GuiLifecycle, StructureChangesListener {

	private static Log log = Log.getLog(EditorSpace.class);
	private static final long serialVersionUID = 1L;
	private Panel panel;
	private VerticalLayout contentLayout;
	private CaoNode[] resource;
	private EditorPanel editor;
	private Button bSave;
	private Button bCancel;
	private Button bReload;
	private Button bApply;
	private TabSheet tabs;
	private SliderPanel navigationSlider;
	private BorderLayout navigationContent;
	private VerticalLayout createContent;
	private SliderPanel createSlider;
	private NavigationView navigation;
	private VWorkBar navigationToolBar;
	private TextField breadcrumb;
	private HorizontalLayout actionButtons;
	private CaoNode[] tempResource;
	private LayoutPanel layout;
	private VirtualHost vHost;

	@Override
	public String navigateTo(String selection, String filter) {
		
		return doShow(filter);
		
	}

	@Override
	public void doInitialize() {
		
		vHost = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost();
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
 	                .tabStyle("top: 70%; width: 100px; left: -30px; overflow: hidden")
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
 	                .tabStyle("top: 25%; width: 100px; left: -30px; overflow: hidden")
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
                        	 else
                        		 tempResource = null;
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
		
		bReload = new Button("Reload", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				doReload();
			}
		});
		actionButtons.addComponent(bReload);
		
		actionButtons.addComponent(new Label(" "));

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

		actionButtons.addComponent(new Label(" "));
		
		bApply = new Button("Apply", new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				doApply();
			}
		});
		actionButtons.addComponent(bApply);
		
		Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().getStructureRegistry().registerWeak(this);
		
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
		if (tempResource != null) parent = tempResource;
		tempResource = null;
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
			navigation = new NavigationView() {

				@Override
				public void move(NavNode source, NavNode target, VerticalDropLocation location) {
					CaoNode s = source.getCurrent();
					CaoNode t = target.getCurrent();
					StructureControl control = s.adaptTo(StructureControl.class);
					
					switch (location) {
					case MIDDLE:
						control.moveTo(t);
						break;
					case TOP:
						control.moveBefore(t);
						break;
					case BOTTOM:
						control.moveAfter(t);
						break;
					default:
						break;
					}
					
					Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().doUpdates();

				}
				
			};
			navigationContent.addComponent(navigation, BorderLayout.Constraint.CENTER);
			navigationContent.setWidth( "100%" );
			navigationSlider.setFixedContentSize(Page.getCurrent().getBrowserWindowWidth() - 200);
			
			navigationToolBar = new VWorkBar() {

				@Override
				public List<Pair<String, Object>> getAddOptions() {
					tempResource = CherryUtil.getCurrent(navigation.getSelectedNode()); 
					EditorSpace.this.openCreateSlider();
					return null;
				}

				@Override
				public List<Pair<String, Object>> getModifyOptions() {
					return getModifyOptionsList();
				}

				@Override
				public List<Pair<String, Object>> getDeleteOptions() {
					List<Pair<String, Object>> ret = getDeleteOptionsList();
					if (ret != null && ret.size() == 1) {
						EditorSpace.this.doDelete(ret.get(0).getValue());
						return null;
					}
					return ret;
				}

				@Override
				protected void doModify(Object action) {
					EditorSpace.this.doModify(action);
				}

				@Override
				protected void doDelete(Object action) {
					EditorSpace.this.doDelete(action);
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
					} else
					if (event.getButton() == MouseButton.RIGHT) {
						
						ContextMenu cmMenu = new ContextMenu();
						ContextMenuItem cmRefresh = cmMenu.addItem("Refresh");
						cmRefresh.setSeparatorVisible(true);
						cmRefresh.addItemClickListener(p -> { navigation.doRefreshSelection(); });
						ContextMenuItem cmEdit = cmMenu.addItem("Edit");
						cmEdit.setSeparatorVisible(true);
						
						NavNode[] selectedNode = navigation.getSelectedNode();
						if (selectedNode != null && selectedNode.length == 1) {
							ContextMenuItem cmMove = cmMenu.addItem("Move");
							ContextMenuItem cmMoveUp = cmMove.addItem("Up");
							cmMoveUp.addItemClickListener(p -> { moveUp(); });
							ContextMenuItem cmMoveDown = cmMove.addItem("Down");
							cmMoveDown.addItemClickListener(p -> { moveDown(); });
							ContextMenuItem cmMoveTop = cmMove.addItem("To top");
							cmMoveTop.addItemClickListener(p -> { moveTop(); });
							ContextMenuItem cmMoveBottom = cmMove.addItem("To bottom");
							cmMoveBottom.addItemClickListener(p -> { moveBottom(); });
						}
						
						ContextMenuItem cmCreate = cmMenu.addItem("New");
						cmCreate.addItemClickListener(p -> { tempResource = CherryUtil.getCurrent(navigation.getSelectedNode()); openCreateSlider(); });
						ContextMenuItem cmDelete = cmMenu.addItem("Delete");
						cmDelete.addItemClickListener(p -> { doDelete(null);});
						cmMenu.setAsContextMenuOf(navigation.getTree());

						List<Pair<String, Object>> list = getModifyOptionsList();
						for (Pair<String, Object> p : list) {
							ContextMenuItem cmItem = cmEdit.addItem(p.getKey());
							cmItem.setData(p.getValue());
							cmItem.addItemClickListener(x -> { doModify( ((ContextMenuItem)x.getSource()).getData() ); } );
							if (".".equals(p.getValue()) )
								cmItem.setSeparatorVisible(true);
						}
						
						
						cmMenu.open(event.getClientX(),event.getClientY());
//						cmMenu.open(0,0);
					}
				}
			} );

			if (resource != null && resource.length > 0) {
				navigationToolBar.setEnabled(false);
				navigation.setSelected(resource[0]);
			}
		}
		
	}

	protected void moveBottom() {
		NavNode[] selectedNode = navigation.getSelectedNode();
		if (selectedNode == null || selectedNode.length != 1) return;
		StructureControl control = selectedNode[0].getCurrent().adaptTo(StructureControl.class);
		if (control == null) return;
		control.moveToBottom();
		Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().doUpdates();
	}

	protected void moveTop() {
		NavNode[] selectedNode = navigation.getSelectedNode();
		if (selectedNode == null || selectedNode.length != 1) return;
		StructureControl control = selectedNode[0].getCurrent().adaptTo(StructureControl.class);
		if (control == null) return;
		control.moveToTop();
		Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().doUpdates();
	}

	protected void moveDown() {
		NavNode[] selectedNode = navigation.getSelectedNode();
		if (selectedNode == null || selectedNode.length != 1) return;
		StructureControl control = selectedNode[0].getCurrent().adaptTo(StructureControl.class);
		if (control == null) return;
		control.moveDown();
		Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().doUpdates();
	}

	protected void moveUp() {
		NavNode[] selectedNode = navigation.getSelectedNode();
		if (selectedNode == null || selectedNode.length != 1) return;
		StructureControl control = selectedNode[0].getCurrent().adaptTo(StructureControl.class);
		if (control == null) return;
		control.moveUp();
		Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().doUpdates();
	}

	protected void openCreateSlider() {
		NavNode[] selectedNode = navigation.getSelectedNode();
		if (selectedNode != null || tempResource != null) {
			createSlider.expand();
			navigationSlider.collapse();
		}
	}

	protected void doDelete(Object action) {
		System.out.println("Delete: " + action);
		NavNode[] selectedNode = navigation.getSelectedNode();
		if (selectedNode == null) return;
		final CaoNode[] parentFinal = CherryUtil.getCurrent(selectedNode);
		System.out.println("Node: " + parentFinal);
		
		if (action == null) {
			List<Pair<String, Object>> list = getDeleteOptionsList();
			if (list.size() < 1) return;
			action = list.get(0).getValue();
		}
		
		ActionDialog.doExecuteAction((ActionDescriptor) action, parentFinal);
	}

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

	protected List<Pair<String, Object>> getDeleteOptionsList() {
		NavNode[] selectedNode = navigation.getSelectedNode();
		if (selectedNode == null) return null;
		LinkedList<Pair<String,Object>> list = new LinkedList<>();
		Collection<ActionDescriptor> actions = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().getActions(CherryApi.ACTION_DELETE, CherryUtil.getCurrent(selectedNode));
		for (ActionDescriptor action :actions) {
			list.add(new Pair<String,Object>(action.getCaption(), action ) );
		}
		return list;
	}

	protected List<Pair<String, Object>> getModifyOptionsList() {
		NavNode[] selectedNode = navigation.getSelectedNode();
		if (selectedNode == null) return null;
		
		LinkedList<Pair<String,Object>> list = new LinkedList<>();
		list.add(new Pair<String, Object>("Open", "."));
		
		Collection<ActionDescriptor> actions = Sop.getApi(CherryApi.class).getCurrentCall().getVirtualHost().getActions(CherryApi.ACTION_MODIFY, CherryUtil.getCurrent(selectedNode));
		for (ActionDescriptor action :actions) {
			list.add(new Pair<String,Object>(action.getCaption(), action ) );
		}
		
		return list;
	}

	private synchronized String doShow(String resId) {
		log.d("show resource", resId);
		
		resource = null;
		editor = null;
		layout = null;
		tabs.removeAllComponents();
		tabs.addTab(contentLayout, "Content");
		
		contentLayout.removeAllComponents();
		
		if (resId == null) return null;
		
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
		this.resource = new CaoNode[] {resource};
		breadcrumb.setValue( resource.getConnection().getName() + ":" + resource.getPath() );
		if (factory == null)
			factory = vHost.getDefaultEditorFactory(resource);
		
		if (factory != null) {
			doFillTabs(resource, factory);
		
			Panel editorPanel = new Panel();
			editorPanel.setSizeFull();
			contentLayout.addComponent(editorPanel);
	
			try {
				CaoWritableElement writable = resource.getWritableNode();
				editor = factory.createEditor(writable);
				editor.setSizeFull();
				editor.setMargin(true);
				editorPanel.setContent(editor);
				editor.initUi();
				panel.setCaption( editor.getTitle() );
				
				layout = factory.createLayoutPanel(writable);
				if (layout != null) {
					layout.setSizeFull();
					tabs.addTab(layout, "Layout");
					layout.doReload();
				}
				
			} catch (MException e) {
				log.e(e);
				return null;
			}
			contentLayout.setExpandRatio(editorPanel, 1f);
			contentLayout.setMargin(true);
			contentLayout.setSpacing(true);
			//contentLayout.setSizeFull();		
		}		
		
		
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

	protected void doReload() {
		doShow(resource[0].getConnection().getName() + ":" + resource[0].getId());
	}
	
	protected void doCancel() {
		doBack();
	}

	protected boolean doApply() {
		String error = editor.doSave();
		if (error != null) {
			UI.getCurrent().showNotification(error);
			return false;
		}
		if (layout != null) {
			error = layout.doSave();
			if (error != null) {
				UI.getCurrent().showNotification(error);
			}
		}
		return true;
	}
	
	protected void doSave() {
		if (!doApply()) return;
		doBack();
	}

	private void doBack() {
		GuiUtil.getApi().navigateBack();
	}

	@Override
	public void doDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShowSpace(boolean firstTime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void navigationChanges(Change[] changes) {
		if (resource == null || resource.length < 1) return;
		for (Change change : changes)
			if (change.getEvent() == EVENT.BIG_CHANGE) {
				
			} else
			if (change.getNode().equals(resource[0].getId())) {
				if (change.getEvent() == EVENT.DELETED) {
					doShow(null);
					return;
				} else
				if (change.getEvent() == EVENT.MODIFIED ) {
					
					CaoNode otherNode = vHost.getNavigationProvider().getResource(change.getNode());
					if (otherNode == null || MSystem.equals(resource[0].getDate(CaoConst.MODIFIED), otherNode.getDate(CaoConst.MODIFIED))) 
						continue;
					
					ConfirmDialog.show(getUI(), "Concurrent Modification", "Current node was modified by remote", "Reload", "Ignore", new ConfirmDialog.Listener() {
						
						@Override
						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								try {
									resource[0].reload();
									doShow(resource[0].getConnection().getName() + ":" + resource[0].getId());
								} catch (Throwable e) {
									log.e(e);
								}
							}
						}
					});
				}
			}
	}	

}
