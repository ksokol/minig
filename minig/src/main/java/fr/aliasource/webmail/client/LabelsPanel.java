package fr.aliasource.webmail.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HandlesAllKeyEvents;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CustomButton;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.IFolder;

public class LabelsPanel extends FlexTable implements IFolderSelectionListener {

    private List<IFolder> allFolders;
    private Map<IFolder, LabelPresenter> widgets;
    private IFolderClickHandlerFactory clickFactory;
    private TextBox filterField;
    private FlexTable main;

    public LabelsPanel() {
        this(true, new IFolderClickHandlerFactory() {

            @Override
            public ClickHandler createHandler(final IFolder f) {
                ClickHandler ret = new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        WebmailController.get().getSelector().select(f);
                    }
                };
                return ret;
            }
        });
    }

    public LabelsPanel(boolean editable, IFolderClickHandlerFactory clickFactory) {
        this.clickFactory = clickFactory;
        widgets = new HashMap<IFolder, LabelPresenter>();
        addStyleName("whiteBackground");
        setWidth("100%");

        HorizontalPanel hp = new HorizontalPanel();
        hp.setStyleName("labelsPanel");
        filterField = new TextBox();
        filterField.setWidth("100%");
        createFilterHandlers();
        hp.add(filterField);
        hp.setCellWidth(filterField, "100%");

        class ClearButton extends CustomButton {
            public ClearButton() {
                super(new Image("minig/images/x.gif"));
                DOM.setStyleAttribute(getElement(), "padding", "2px");
            }
        }

        ClearButton clear = new ClearButton();
        clear.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                filterField.setText("");
                applyFilter();
            }
        });
        hp.add(clear);
        hp.setCellVerticalAlignment(clear, HasVerticalAlignment.ALIGN_MIDDLE);
        try {
            DOM.setStyleAttribute(hp.getElement(), "tableLayout", "auto");
        } catch (Throwable t) {
            GWT.log("tableLayout inherit fails on ie :/", null);
        }

        setWidget(0, 0, hp);
        getFlexCellFormatter().setColSpan(0, 0, 2);

        main = new FlexTable();
        ScrollPanel sp = new ScrollPanel(main);
        sp.setStyleName("labelsList");
        DOM.setStyleAttribute(sp.getElement(), "overflowY", "scroll");
        DOM.setStyleAttribute(sp.getElement(), "overflowX", "hidden");
        setWidget(1, 0, sp);
        getFlexCellFormatter().setColSpan(1, 0, 2);

        HorizontalPanel bottomActions = new HorizontalPanel();
        bottomActions.setStyleName("bottomActions");

        if (editable) {
            Anchor edit = new Anchor(I18N.strings.editFolders());
            edit.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    WebmailController.get().getView().showFolderSettings();
                }
            });
            bottomActions.add(edit);
            bottomActions.setCellHorizontalAlignment(edit, HorizontalPanel.ALIGN_RIGHT);
        }

        // TODO breaks IE
        try {
            DOM.setStyleAttribute(bottomActions.getElement(), "tableLayout", "auto");
        } catch (Throwable t) {
            GWT.log("fails on ie", null);
        }
        setWidget(2, 0, bottomActions);
        getFlexCellFormatter().setColSpan(2, 0, 2);

        WebmailController.get().getSelector().addListener(this);
    }

    private void applyFilter() {
        for (IFolder f : widgets.keySet()) {
            LabelPresenter lp = widgets.get(f);
            lp.applyFilter(filterField.getText());
        }
    }

    private void createFilterHandlers() {
        class FilterFieldHandlers extends HandlesAllKeyEvents implements ValueChangeHandler<String> {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                applyFilter();
            }

            @Override
            public void onKeyDown(KeyDownEvent event) {
                applyFilter();
            }

            @Override
            public void onKeyUp(KeyUpEvent event) {
                applyFilter();
            }

            @Override
            public void onKeyPress(KeyPressEvent event) {
                applyFilter();
            }

        }
        ;
        FilterFieldHandlers ffh = new FilterFieldHandlers();
        ffh.addKeyHandlersTo(filterField);
        filterField.addValueChangeHandler(ffh);
    }

    public void destroy() {
        WebmailController.get().getSelector().removeListener(this);
        for (LabelPresenter lp : widgets.values()) {
            lp.destroy();
        }
        widgets.clear();
        main.clear();
    }

    @Override
    public void folderSelected(IFolder f) {
    }

    @Override
    public void foldersChanged(List<IFolder> folders) {

        // TODO
        // Arrays.sort(folders, new FolderComparator());
        this.allFolders = new LinkedList<IFolder>();

        for (IFolder f : folders) {
            // if (!withDnD) {
            allFolders.add(f);
            // } else if (!WebmailController.get().isSystemFolder(f.getName()))
            // {
            // allFolders.add(f);
            // }
        }

        for (LabelPresenter lp : widgets.values()) {
            lp.destroy();
        }
        main.clear();
        widgets.clear();

        int idx = 0;
        for (IFolder f : allFolders) {
            createWidget(f, idx++);
        }
        HTML spacer = new HTML("&nbsp;");
        main.setWidget(idx, 0, spacer);
        main.getCellFormatter().setHeight(idx, 0, "100%");
    }

    private void createWidget(IFolder f, int idx) {
        LabelPresenter lw = new LabelPresenter(f, idx, main.getRowFormatter());
        widgets.put(f, lw);
        main.setWidget(idx, 0, lw.getLink());
        main.getColumnFormatter().setWidth(0, "20px");
        if (clickFactory != null) {
            lw.registerClickHandler(clickFactory.createHandler(f));
        }
    }

}
