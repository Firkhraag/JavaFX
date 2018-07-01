package org.spbu.histology.space.editor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.spbu.histology.shape.information.CellInformationInitialization;
import javafx.collections.MapChangeListener;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Point3D;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javax.swing.filechooser.FileSystemView;
import org.openide.LifecycleManager;
import org.openide.util.Lookup;
import org.spbu.histology.model.Cell;
import org.spbu.histology.model.Histion;
import org.spbu.histology.model.HistionManager;
import org.spbu.histology.model.HistologyObject;
import org.spbu.histology.model.Line;
import org.spbu.histology.model.LineEquations;
import org.spbu.histology.model.Names;
import org.spbu.histology.model.Node;
import org.spbu.histology.model.Part;
import org.spbu.histology.model.RecurrenceShifts;
import org.spbu.histology.model.TetgenPoint;
import org.spbu.histology.model.TwoIntegers;
import org.spbu.histology.shape.information.PartInformationInitialization;

public class HomeController implements Initializable {

    private HistionManager hm = null;

    @FXML
    private TreeView<HistologyObject<?>> shapeTreeView;

    private Integer cellId;
    private Integer partId;
    private final BooleanProperty pasteCellDisabledProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty pastePartDisabledProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty disableEverything = new SimpleBooleanProperty(false);

    int dataSize;
    Point3D nodeAvg;

    HistionTreeItem histion;
    private final ObservableMap<Integer, CellTreeItem> cellTreeItemMap
            = FXCollections.observableMap(new ConcurrentHashMap());
    private final ObservableMap<Integer, PartTreeItem> partTreeItemMap
            = FXCollections.observableMap(new ConcurrentHashMap());

    private final MapChangeListener<Integer, Part> partListener
            = (change) -> {
                if (change.wasRemoved() && change.wasAdded()) {
                    Part p = (Part) change.getValueAdded();
                    Part removedPart = (Part) change.getValueRemoved();
                    if (!(p.getName().equals(removedPart.getName()))) {
                        partTreeItemMap.get(p.getId()).getValue().setName(p.getName());
                        shapeTreeView.setCellFactory(new Callback<TreeView<HistologyObject<?>>, TreeCell<HistologyObject<?>>>() {
                            @Override
                            public TreeCell<HistologyObject<?>> call(TreeView<HistologyObject<?>> p) {
                                return new TreeCellImpl();
                            }
                        });
                    }
                } else if (change.wasAdded()) {
                    Part addedPart = (Part) change.getValueAdded();
                    PartTreeItem pti = new PartTreeItem(addedPart);
                    histion.getChildren().forEach(c -> {
                        if (c.getValue().getId() == addedPart.getCellId()) {
                            c.getChildren().add(pti);
                        }
                    });
                    partTreeItemMap.put(addedPart.getId(), pti);
                } else if (change.wasRemoved()) {
                    Part removedPart = (Part) change.getValueRemoved();
                    histion.getChildren().forEach(c -> {
                        if (c.getValue().getId() == removedPart.getCellId()) {
                            c.getChildren().remove(partTreeItemMap.get(removedPart.getId()));
                        }
                    });
                    partTreeItemMap.remove(removedPart.getId());
                }
            };

    private final MapChangeListener<Integer, Cell> cellListener
            = (change) -> {

                if (change.wasRemoved() && change.wasAdded()) {
                    Cell c = (Cell) change.getValueAdded();
                    Cell removedShape = (Cell) change.getValueRemoved();
                    if (!(c.getName().equals(removedShape.getName()))) {
                        cellTreeItemMap.get(c.getId()).getValue().setName(c.getName());
                        shapeTreeView.setCellFactory(new Callback<TreeView<HistologyObject<?>>, TreeCell<HistologyObject<?>>>() {
                            @Override
                            public TreeCell<HistologyObject<?>> call(TreeView<HistologyObject<?>> p) {
                                return new TreeCellImpl();
                            }
                        });
                    }
                    removedShape.getItemMap().removeListener(partListener);
                    c.getItemMap().addListener(partListener);

                } else if (change.wasAdded()) {
                    Cell addedCell = (Cell) change.getValueAdded();
                    CellTreeItem cti = new CellTreeItem(addedCell);
                    histion.getChildren().add(cti);
                    cellTreeItemMap.put(addedCell.getId(), cti);
                    addedCell.getItemMap().addListener(partListener);
                    addedCell.getItems().forEach(p -> {
                        PartTreeItem pti = new PartTreeItem(p);
                        histion.getChildren().forEach(c -> {
                            if (c.getValue().getId() == addedCell.getId()) {
                                c.getChildren().add(pti);
                            }
                        });
                        partTreeItemMap.put(p.getId(), pti);
                    });
                } else if (change.wasRemoved()) {
                    Cell removedShape = (Cell) change.getValueRemoved();
                    histion.getChildren().remove(cellTreeItemMap.get(removedShape.getId()));
                    cellTreeItemMap.remove(removedShape.getId());
                    removedShape.getItemMap().removeListener(partListener);
                }
            };

    public abstract class AbstractTreeItem extends TreeItem<HistologyObject<?>> {

        public abstract ContextMenu getMenu();
    }

    Node intersect(Line line1, Line line2) {
        double x, y, z;
        x = 10000;
        y = 10000;
        z = -1;
        if ((!line1.vert) && (line2.vert)) {
            x = line2.b;
            y = line1.k * x + line1.b;
            if ((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                    || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))) {
                z = 0;
            }
        } else if ((!line1.vert) && (!line2.vert)) {
            if (Math.abs(line1.k - line2.k) > 0.001) {
                x = (line1.b - line2.b) / (line2.k - line1.k);
                y = line1.k * x + line1.b;
                if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                        && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                        || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                        && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                        && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                        && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                        || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                        && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                    z = 0;
                }
            }
        } else if ((line1.vert) && (!line2.vert)) {
            x = line1.b;
            y = line2.k * x + line2.b;
            if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                    || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                    && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                    && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                    || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                    && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                z = 0;
            }
        }
        return new Node(x, y, z);
    }

    Node intersect2(Line line1, Line line2) {
        double x, y, z;
        x = 10000;
        y = 10000;
        z = -1;
        if ((!line1.vert) && (line2.vert)) {
            x = line2.b;
            y = line1.k * x + line1.b;
            if ((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                    || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))) {
                if ((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                    z = 0;
                }
            }
        } else if ((!line1.vert) && (!line2.vert)) {
            if (Math.abs(line1.k - line2.k) > 0.001) {
                x = (line1.b - line2.b) / (line2.k - line1.k);
                y = line1.k * x + line1.b;
                if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                        && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                        || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                        && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                        && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                        && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                        || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                        && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                    if ((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                        z = 0;
                    }
                }
            }
        } else if ((line1.vert) && (!line2.vert)) {
            x = line1.b;
            y = line2.k * x + line2.b;
            if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                    || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                    && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                    && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                    || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                    && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                if ((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                    z = 0;
                }
            }
        }
        return new Node(x, y, z);
    }

    Node intersect3(Line line1, Line line2) {
        double x, y, z;
        x = 10000;
        y = 10000;
        z = -1;
        if ((!line1.vert) && (line2.vert)) {
            x = line2.b;
            y = line1.k * x + line1.b;
            if ((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                    || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))) {
                if ((x > line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                    z = 0;
                }
            }
        } else if ((!line1.vert) && (!line2.vert)) {
            if (Math.abs(line1.k - line2.k) > 0.001) {
                x = (line1.b - line2.b) / (line2.k - line1.k);
                y = line1.k * x + line1.b;
                if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                        && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                        || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                        && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                        && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                        && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                        || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                        && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                    if ((x < line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                        z = 0;
                    }
                }
            }
        } else if ((line1.vert) && (!line2.vert)) {
            x = line1.b;
            y = line2.k * x + line2.b;
            if (((((y < line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y > line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001)))
                    || (((y > line2.p1.y) || (Math.abs(y - line2.p1.y) < 0.000001))
                    && ((y < line2.p2.y) || (Math.abs(y - line2.p2.y) < 0.000001))))
                    && ((((x < line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                    && ((x > line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001)))
                    || (((x > line2.p1.x) || (Math.abs(x - line2.p1.x) < 0.000001))
                    && ((x < line2.p2.x) || (Math.abs(x - line2.p2.x) < 0.000001))))) {
                if ((x < line1.p1.x) || (Math.abs(x - line1.p1.x) < 0.000001)) {
                    z = 0;
                }
            }
        }
        return new Node(x, y, z);
    }

    public class HistionTreeItem extends AbstractTreeItem {

        ContextMenu histionMenu = new ContextMenu();

        public HistionTreeItem(HistologyObject<?> object) {
            this.setValue(object);
            this.setExpanded(true);
        }

        @Override
        public ContextMenu getMenu() {

            MenuItem saveHistion = new MenuItem();
            saveHistion.setText("Сохранить гистион");
            saveHistion.setOnAction(event -> {
                histionMenu.hide();
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                String userDirectoryString = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
                userDirectoryString += "\\HistologyApp" + System.getProperty("sun.arch.data.model") + "\\Histions";
                File userDirectory = new File(userDirectoryString);
                if (!userDirectory.exists()) {
                    userDirectory.mkdirs();
                }
                fileChooser.setInitialDirectory(userDirectory);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(null);

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(userDirectoryString
                            + "\\" + file.getName()));

                    writer.write(0 + " "
                            + 0 + " "
                            + hm.getHistionMap().get(0).getXCoordinate() + " "
                            + hm.getHistionMap().get(0).getYCoordinate() + " "
                            + hm.getHistionMap().get(0).getZCoordinate());
                    writer.newLine();

                    writer.write(RecurrenceShifts.getXShift() + "");
                    writer.newLine();
                    writer.write(RecurrenceShifts.getZShift() + "");
                    writer.newLine();
                    
                    writer.write(hm.getHistionMap().get(0).getItems().size() + "");
                    writer.newLine();

                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        try {
                            writer.write(c.getName());
                            writer.newLine();
                            writer.write(c.getXRotate() + " " + c.getYRotate() + " "
                                    + c.getXCoordinate() + " " + c.getYCoordinate() + " "
                                    + c.getZCoordinate());
                            writer.newLine();
                            writer.write(c.getDiffuseColor().getRed() + " "
                                    + c.getDiffuseColor().getGreen() + " "
                                    + c.getDiffuseColor().getBlue());
                            writer.newLine();
                            writer.write(c.getSpecularColor().getRed() + " "
                                    + c.getSpecularColor().getGreen() + " "
                                    + c.getSpecularColor().getBlue());
                            writer.newLine();
                            writer.write(c.getShow() + "");
                            writer.newLine();
                            writer.write(c.getItems().size() + "");
                            writer.newLine();

                            c.getItems().forEach(p -> {
                                try {
                                    writer.write(p.getName());
                                    writer.newLine();
                                    writer.write(p.getPointData().size() + "");
                                    writer.newLine();
                                    for (int i = 0; i < p.getPointData().size(); i++) {
                                        writer.write(p.getPointData().get(i).getX() + " "
                                                + p.getPointData().get(i).getY() + " "
                                                + p.getPointData().get(i).getZ());
                                        writer.newLine();
                                    }
                                } catch (Exception ex) {

                                }
                            });

                            writer.write(c.getFacetData().size() + "");
                            writer.newLine();

                            c.getFacetData().forEach(list -> {
                                try {
                                    for (int i = 0; i < list.size(); i++) {
                                        if (i == list.size() - 1) {
                                            writer.write(list.get(i) + "");
                                        } else {
                                            writer.write(list.get(i) + " ");
                                        }
                                    }
                                    writer.newLine();
                                } catch (Exception ex) {

                                }
                            });
                        } catch (Exception ex) {

                        }
                    });
                    writer.close();
                } catch (Exception ex) {

                }

            });
            /*MenuItem deleteHistion = new MenuItem();
            deleteHistion.setText("Очистить гистион");
            deleteHistion.setOnAction(event -> {
                hm.getHistionMap().get(0).getItems().forEach(c -> {
                    String name = c.getName();
                    Names.removeCellName(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
                    hm.getHistionMap().get(0).deleteChild(c.getId());
                });
            });*/
            MenuItem loadHistion = new MenuItem();
            loadHistion.setText("Загрузить гистион");
            loadHistion.setOnAction(event -> {
                histionMenu.hide();
                /*FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                String userDirectoryString = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
                //System.out.println(userDirectoryString);
                userDirectoryString += "\\HistologyApp\\Histions";
                File userDirectory = new File(userDirectoryString);
                if (!userDirectory.exists()) {
                    userDirectory.mkdirs();
                }
                fileChooser.setInitialDirectory(userDirectory);
                File selectedFile = fileChooser.showOpenDialog(null);

                try {
                    Histion main = hm.getHistionMap().get(0);
                    BufferedReader br = new BufferedReader(new FileReader(userDirectoryString + "\\" + selectedFile.getName()));
                    String line = br.readLine();

                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    main.setXCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    main.setYCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    main.setZCoordinate(Double.parseDouble(line));
                    
                    line = br.readLine();
                    RecurrenceShifts.setXShift(Double.parseDouble(line));
                    line = br.readLine();
                    RecurrenceShifts.setZShift(Double.parseDouble(line));
                    
                    line = br.readLine();
                    int cellNum = Integer.parseInt(line);
                    for (int i = 0; i < cellNum; i++) {
                        ObservableList<ArrayList<Integer>> facetData = FXCollections.observableArrayList();
                        Cell c = new Cell("Name", 0, 0, 0, 0, 0, FXCollections.observableArrayList(),
                                Color.BLUE, Color.LIGHTBLUE, 0, true);
                        double r, g, b;
                        line = br.readLine();

                        String name = line;
                        name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                        int count = 1;
                        while (Names.containsCellName(name)) {
                            name = line;
                            name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                            name += "(" + count + ")";
                            count++;
                        }
                        c.setName("Клетка <" + name + ">");

                        line = br.readLine();
                        c.setXRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setYRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setXCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setYCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setZCoordinate(Double.parseDouble(line));

                        line = br.readLine();
                        r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        b = Double.parseDouble(line);
                        c.setDiffuseColor(Color.color(r, g, b));

                        line = br.readLine();
                        r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        b = Double.parseDouble(line);
                        c.setSpecularColor(Color.color(r, g, b));

                        line = br.readLine();
                        c.setShow(Boolean.parseBoolean(line));

                        line = br.readLine();
                        int partNum = Integer.parseInt(line);

                        ArrayList<TetgenPoint> pd = new ArrayList<>();
                        int num = 1;
                        for (int j = 0; j < partNum; j++) {
                            ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
                            Part p = new Part("Слой", FXCollections.observableArrayList(), c.getId());
                            line = br.readLine();
                            //p.setName(line);

                            name = line;
                            name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                            p.setName("Слой <" + name + ">");

                            line = br.readLine();
                            int pointNum = Integer.parseInt(line);
                            for (int q = 0; q < pointNum; q++) {
                                line = br.readLine();
                                r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                                g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                                b = Double.parseDouble(line);
                                pointData.add(new TetgenPoint(q + 1, r, g, b));
                                pd.add(new TetgenPoint(num, r, g, b));
                                num++;
                            }
                            p.setPointData(pointData);
                            p.setAvgNode();
                            c.addChild(p);
                        }
                        line = br.readLine();
                        int facetNum = Integer.parseInt(line);
                        for (int j = 0; j < facetNum; j++) {
                            ArrayList<Integer> list = new ArrayList<>();
                            line = br.readLine();
                            while (line.contains(" ")) {
                                list.add(Integer.parseInt(line.substring(0, line.indexOf(" "))));
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                            }
                            list.add(Integer.parseInt(line));
                            facetData.add(list);
                        }
                        c.setFacetData(facetData);

                        ArrayList<TwoIntegers> lineList = new ArrayList<>();
                        for (ArrayList<Integer> f : facetData) {
                            for (int j = 1; j < f.size(); j++) {
                                TwoIntegers ti = new TwoIntegers(j, f.get(j - 1), f.get(j));
                                if (!lineList.contains(ti)) {
                                    lineList.add(ti);
                                }
                            }
                            TwoIntegers ti = new TwoIntegers(f.size(), f.get(f.size() - 1), f.get(0));
                            if (!lineList.contains(ti)) {
                                lineList.add(ti);
                            }
                        }

                        ArrayList<Line> lines = new ArrayList<>();
                        for (int j = 0; j < lineList.size(); j++) {

                            TetgenPoint point1 = pd.get(lineList.get(j).getPoint1() - 1);
                            TetgenPoint point2 = pd.get(lineList.get(j).getPoint2() - 1);
                            if (Math.abs(point1.getY() - point2.getY()) < 0.0001) {
                                lines.add(new Line(new org.spbu.histology.model.Node(point1.getX(), point1.getZ(), point1.getY()),
                                        new org.spbu.histology.model.Node(point2.getX(), point2.getZ(), point2.getY())));
                            }
                        }
                        LineEquations.addLine(c.getId(), lines);

                        main.addChild(c);
                        name = c.getName();
                        Names.addCellName(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
                    }
                    br.close();
                } catch (Exception ex) {
                    System.out.println("error");
                }*/

                LoadHistionBox.display();
            });
            MenuItem loadCell = new MenuItem();
            loadCell.setText("Загрузить клетку");
            loadCell.setOnAction(event -> {
                histionMenu.hide();
                //LoadCellBox.display();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                String userDirectoryString = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
                //System.out.println(userDirectoryString);
                userDirectoryString += "\\HistologyApp" + System.getProperty("sun.arch.data.model") + "\\Cells";
                File userDirectory = new File(userDirectoryString);
                if (!userDirectory.exists()) {
                    userDirectory.mkdirs();
                }
                fileChooser.setInitialDirectory(userDirectory);
                File selectedFile = fileChooser.showOpenDialog(null);

                try {
                    Histion main = hm.getHistionMap().get(0);
                    BufferedReader br = new BufferedReader(new FileReader(userDirectoryString + "\\" + selectedFile.getName()));
                    String line = br.readLine();

                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    main.setXCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    main.setYCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                    line = line.substring(line.indexOf(" ") + 1, line.length());
                    main.setZCoordinate(Double.parseDouble(line));
                    line = br.readLine();
                    int cellNum = Integer.parseInt(line);
                    for (int i = 0; i < cellNum; i++) {
                        ObservableList<ArrayList<Integer>> facetData = FXCollections.observableArrayList();
                        Cell c = new Cell("Name", 0, 0, 0, 0, 0, FXCollections.observableArrayList(),
                                Color.BLUE, Color.LIGHTBLUE, 0, true);
                        double r, g, b;
                        line = br.readLine();

                        String name = line;
                        name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                        int count = 1;
                        while (Names.containsCellName(name)) {
                            name = line;
                            name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                            name += "(" + count + ")";
                            count++;
                        }
                        c.setName("Клетка <" + name + ">");

                        line = br.readLine();
                        c.setXRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setYRotate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setXCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setYCoordinate(Double.parseDouble(line.substring(0, line.indexOf(" "))));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        c.setZCoordinate(Double.parseDouble(line));

                        line = br.readLine();
                        r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        b = Double.parseDouble(line);
                        c.setDiffuseColor(Color.color(r, g, b));

                        line = br.readLine();
                        r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        b = Double.parseDouble(line);
                        c.setSpecularColor(Color.color(r, g, b));

                        line = br.readLine();
                        c.setShow(Boolean.parseBoolean(line));

                        line = br.readLine();
                        int partNum = Integer.parseInt(line);

                        ArrayList<TetgenPoint> pd = new ArrayList<>();
                        int num = 1;
                        for (int j = 0; j < partNum; j++) {
                            ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
                            Part p = new Part("Слой", FXCollections.observableArrayList(), c.getId());
                            line = br.readLine();

                            name = line;
                            name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                            p.setName("Слой <" + name + ">");

                            line = br.readLine();
                            int pointNum = Integer.parseInt(line);
                            for (int q = 0; q < pointNum; q++) {
                                line = br.readLine();
                                r = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                                g = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                                b = Double.parseDouble(line);
                                pointData.add(new TetgenPoint(q + 1, r, g, b));
                                pd.add(new TetgenPoint(num, r, g, b));
                                num++;
                            }
                            p.setPointData(pointData);
                            p.setAvgNode();
                            c.addChild(p);
                        }
                        line = br.readLine();
                        int facetNum = Integer.parseInt(line);
                        for (int j = 0; j < facetNum; j++) {
                            ArrayList<Integer> list = new ArrayList<>();
                            line = br.readLine();
                            while (line.contains(" ")) {
                                list.add(Integer.parseInt(line.substring(0, line.indexOf(" "))));
                                line = line.substring(line.indexOf(" ") + 1, line.length());
                            }
                            list.add(Integer.parseInt(line));
                            facetData.add(list);
                        }
                        c.setFacetData(facetData);

                        ArrayList<TwoIntegers> lineList = new ArrayList<>();
                        for (ArrayList<Integer> f : facetData) {
                            for (int j = 1; j < f.size(); j++) {
                                TwoIntegers ti = new TwoIntegers(j, f.get(j - 1), f.get(j));
                                if (!lineList.contains(ti)) {
                                    lineList.add(ti);
                                }
                            }
                            TwoIntegers ti = new TwoIntegers(f.size(), f.get(f.size() - 1), f.get(0));
                            if (!lineList.contains(ti)) {
                                lineList.add(ti);
                            }
                        }

                        ArrayList<Line> lines = new ArrayList<>();
                        for (int j = 0; j < lineList.size(); j++) {

                            TetgenPoint point1 = pd.get(lineList.get(j).getPoint1() - 1);
                            TetgenPoint point2 = pd.get(lineList.get(j).getPoint2() - 1);
                            if (Math.abs(point1.getY() - point2.getY()) < 0.0001) {
                                lines.add(new Line(new org.spbu.histology.model.Node(point1.getX(), point1.getZ(), point1.getY()),
                                        new org.spbu.histology.model.Node(point2.getX(), point2.getZ(), point2.getY())));
                            }
                        }
                        LineEquations.addLine(c.getId(), lines);

                        main.addChild(c);
                        name = c.getName();
                        Names.addCellName(name.substring(name.indexOf("<") + 1, name.lastIndexOf(">")));
                    }
                    br.close();
                } catch (Exception ex) {
                    System.out.println("error");
                }
            });
            MenuItem addCell = new MenuItem();
            addCell.setText("Добавить клетку");
            addCell.setOnAction(event -> {
                AddBox.display("Добавить клетку", "Название клетки", this.getValue().getId());
            });
            CheckMenuItem fillModel = new CheckMenuItem();
            fillModel.setText("Распространить гистион");
            fillModel.setOnAction(event -> {
                histionMenu.hide();
                if (fillModel.isSelected()) {

                    final DoubleProperty leftX = new SimpleDoubleProperty(10000);
                    final DoubleProperty rightX = new SimpleDoubleProperty(-10000);
                    final DoubleProperty upperZ = new SimpleDoubleProperty(-10000);
                    final DoubleProperty bottomZ = new SimpleDoubleProperty(10000);
                    final DoubleProperty upperY = new SimpleDoubleProperty(-10000);
                    final DoubleProperty bottomY = new SimpleDoubleProperty(10000);

                    ArrayList<TetgenPoint> pl = new ArrayList<>();

                    hm.getHistionMap().get(0).getItems().forEach(c -> {
                        if (c.getShow()) {
                            c.getTransformedPointData().forEach(point -> {
                                pl.add(point);
                                if (point.getX() < leftX.get()) {
                                    leftX.set(point.getX());
                                }
                                if (point.getX() > rightX.get()) {
                                    rightX.set(point.getX());
                                }
                                if (point.getZ() < bottomZ.get()) {
                                    bottomZ.set(point.getZ());
                                }
                                if (point.getZ() > upperZ.get()) {
                                    upperZ.set(point.getZ());
                                }
                                if (point.getY() < bottomY.get()) {
                                    bottomY.set(point.getY());
                                }
                                if (point.getY() > upperY.get()) {
                                    upperY.set(point.getY());
                                }
                            });
                        }
                    });

                    DoubleProperty xSpace = new SimpleDoubleProperty(0);
                    DoubleProperty ySpace = new SimpleDoubleProperty(0);
                    DoubleProperty xzSpace = new SimpleDoubleProperty(0);
                    DoubleProperty zSpace = new SimpleDoubleProperty(0);

                    for (TetgenPoint point : pl) {
                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                            if (c.getShow()) {
                                double minY = 10000;
                                double maxY = -10000;
                                for (TetgenPoint p : c.getTransformedPointData()) {
                                    if (p.getY() < minY) {
                                        minY = p.getY();
                                    }
                                    if (p.getY() > maxY) {
                                        maxY = p.getY();
                                    }
                                }
                                if (maxY - minY > ySpace.get()) {
                                    ySpace.set(maxY - minY);
                                }
                                for (Line line : LineEquations.getLineMap().get(c.getId())) {
                                    if (Math.abs(point.getY() - line.p1.z) < 0.000001) {
                                        Node p1 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() + 1, point.getZ(), point.getY())), line);
                                        Node p2 = intersect(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX(), point.getZ() + 1, point.getY())), line);

                                        if (p1.z == 0) {
                                            if (Math.abs(point.getX() - p1.x) > xSpace.get()) {
                                                xSpace.set(Math.abs(point.getX() - p1.x));
                                            }
                                        }
                                        if (p2.z == 0) {
                                            if (Math.abs(point.getZ() - p2.y) > zSpace.get()) {
                                                zSpace.set(Math.abs(point.getZ() - p2.y));
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }

                    ArrayList<Double> yArr = new ArrayList<>();
                    for (TetgenPoint point : pl) {
                        if (!yArr.contains(point.getY())) {
                            yArr.add(point.getY());
                        }
                    }
                    final IntegerProperty count = new SimpleIntegerProperty(0);
                    final IntegerProperty count2 = new SimpleIntegerProperty(0);
                    for (TetgenPoint point : pl) {
                        for (Double y : yArr) {
                            count.set(0);
                            count2.set(0);
                            if (Math.abs(point.getY() - y) < 0.000001) {
                                continue;
                            }
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                if (c.getShow()) {
                                    for (Line line : LineEquations.getLineMap().get(c.getId())) {
                                        if (Math.abs(y - line.p1.z) < 0.000001) {
                                            {
                                                Node p1 = intersect2(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() + 1, point.getZ(), point.getY())), line);
                                                Node p2 = intersect3(new Line(new Node(point.getX(), point.getZ(), point.getY()), new Node(point.getX() - 1, point.getZ(), point.getY())), line);
                                                if (p1.z == 0) {
                                                    count.set(count.get() + 1);
                                                }
                                                if (p2.z == 0) {
                                                    count2.set(count2.get() + 1);
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                            if ((count.get() % 2 == 1) || (count2.get() % 2 == 1)) {
                                if (Math.abs(point.getY() - y) > ySpace.get()) {
                                    ySpace.set(Math.abs(point.getY() - y));
                                }
                            }
                        }
                    }

                    IntegerProperty xUpperLimit = new SimpleIntegerProperty(1);
                    IntegerProperty xLowerLimit = new SimpleIntegerProperty(1);
                    IntegerProperty yUpperLimit = new SimpleIntegerProperty(1);
                    IntegerProperty yLowerLimit = new SimpleIntegerProperty(1);
                    IntegerProperty zUpperLimit = new SimpleIntegerProperty(1);
                    IntegerProperty zLowerLimit = new SimpleIntegerProperty(1);

                    DoubleProperty xShift = new SimpleDoubleProperty(0);
                    DoubleProperty zShift = new SimpleDoubleProperty(0);

                    BooleanProperty buttonPressed = new SimpleBooleanProperty(false);

                    HistionRecurrence.display("Spacing", xUpperLimit, xLowerLimit,
                            yUpperLimit, yLowerLimit, zUpperLimit, zLowerLimit,
                            buttonPressed, xShift, zShift);

                    if (!buttonPressed.get()) {
                        fillModel.setSelected(false);
                        return;
                    }

                    /*if (hm.getHistionMap().get(0).getItems().size() > 0) {
                        Cell cell = hm.getHistionMap().get(0).getItems().get(hm.getHistionMap().get(0).getItems().size() - 1);
                        Cell c = new Cell(cell.getId(), cell);

                        hm.getHistionMap().get(0).getItemMap().get(c.getId()).getItems().forEach(p -> {
                            c.addChild(p);
                        });
                        hm.getHistionMap().get(0).addChild(c);
                    }*/
                    
                    double deltaX = xSpace.get();
                    double deltaY = ySpace.get();
                    double deltaZ = zSpace.get();

                    double hZ = hm.getHistionMap().get(0).getZCoordinate();
                    double hY = hm.getHistionMap().get(0).getYCoordinate();
                    double hX = hm.getHistionMap().get(0).getXCoordinate();

                    double xUpperBoundary = deltaX * xUpperLimit.get();
                    double xLowerBoundary = deltaX * xLowerLimit.get();
                    double yUpperBoundary = deltaY * yUpperLimit.get();
                    double yLowerBoundary = deltaY * yLowerLimit.get();
                    double zUpperBoundary = deltaZ * zUpperLimit.get();
                    double zLowerBoundary = deltaZ * zLowerLimit.get();

                    if ((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0)) {
                        while (hX < xUpperBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(0));
                            newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setXCoordinate(hX + deltaX + xShift.get());
                            newHistion.setZCoordinate(hZ + zShift.get());
                            hm.addHistion(newHistion);
                            hX += deltaX + xShift.get();
                            hZ += zShift.get();
                        }
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        hZ = hm.getHistionMap().get(0).getZCoordinate();
                        while (hX > -xLowerBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(0));
                            newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setXCoordinate(hX - deltaX - xShift.get());
                            newHistion.setZCoordinate(hZ - zShift.get());
                            hm.addHistion(newHistion);
                            hX -= deltaX + xShift.get();
                            hZ -= zShift.get();
                        }
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        hZ = hm.getHistionMap().get(0).getZCoordinate();
                    }

                    if ((yUpperLimit.get() >= 0) && (yLowerLimit.get() >= 0)) {
                        while (hY < yUpperBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(0));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Y: "
                                    + String.valueOf(hY + deltaY) + " ; Z: "
                                    + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setYCoordinate(hY + deltaY);
                            hm.addHistion(newHistion);
                            hY += deltaY;
                        }
                        hY = hm.getHistionMap().get(0).getYCoordinate();
                        while (hY > -yLowerBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(0));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Y: "
                                    + String.valueOf(hY - deltaY) + " ; Z: "
                                    + String.valueOf(hZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setYCoordinate(hY - deltaY);
                            hm.addHistion(newHistion);
                            hY -= deltaY;
                        }
                        hY = hm.getHistionMap().get(0).getYCoordinate();
                    }

                    if ((zUpperLimit.get() >= 0) && (zLowerLimit.get() >= 0)) {
                        while (hZ < zUpperBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(0));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setZCoordinate(hZ + deltaZ);
                            hm.addHistion(newHistion);
                            hZ += deltaZ;
                        }
                        hZ = hm.getHistionMap().get(0).getZCoordinate();
                        while (hZ > -zLowerBoundary) {
                            Histion newHistion = new Histion(hm.getHistionMap().get(0));
                            newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                            hm.getHistionMap().get(0).getItems().forEach(c -> {
                                Cell newCell = new Cell(c, newHistion.getId());
                                c.getItems().forEach(p -> {
                                    newCell.addChild(p);
                                });
                                newHistion.addChild(newCell);
                            });
                            newHistion.setZCoordinate(hZ - deltaZ);
                            hm.addHistion(newHistion);
                            hZ -= deltaZ;
                        }
                        hZ = hm.getHistionMap().get(0).getZCoordinate();
                    }

                    int num = 0;
                    if (((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0))
                            && ((zUpperLimit.get() >= 0) && (zLowerLimit.get() >= 0))) {
                        while (hX < xUpperBoundary) {
                            hZ = hm.getHistionMap().get(0).getZCoordinate() + num * zShift.get();
                            while (hZ - num * zShift.get() < zUpperBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ + zShift.get());
                                newHistion.setXCoordinate(hX + deltaX + xShift.get());
                                hm.addHistion(newHistion);
                                hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(0).getZCoordinate() + num * zShift.get();
                            while (hZ - num * zShift.get() > -zLowerBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ + zShift.get());
                                newHistion.setXCoordinate(hX + deltaX + xShift.get());
                                hm.addHistion(newHistion);
                                hZ -= deltaZ;
                            }
                            hX += deltaX + xShift.get();
                            num++;
                        }
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        num = 0;
                        while (hX > -xLowerBoundary) {
                            hZ = hm.getHistionMap().get(0).getZCoordinate() - num * zShift.get();
                            while (hZ + num * zShift.get() < zUpperBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ - zShift.get());
                                newHistion.setXCoordinate(hX - deltaX - xShift.get());
                                hm.addHistion(newHistion);
                                hZ += deltaZ;
                            }
                            hZ = hm.getHistionMap().get(0).getZCoordinate() - num * zShift.get();
                            while (hZ + num * zShift.get() > -zLowerBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ - zShift.get());
                                newHistion.setXCoordinate(hX - deltaX - xShift.get());
                                hm.addHistion(newHistion);
                                hZ -= deltaZ;
                            }
                            hX -= deltaX + xShift.get();
                            num++;
                        }
                    }

                    if (((yUpperLimit.get() >= 0) && (yLowerLimit.get() >= 0))
                            && ((zUpperLimit.get() >= 0) && (zLowerLimit.get() >= 0))) {
                        num = 0;
                        while (hY < yUpperBoundary) {
                            num = 0;
                            hZ = hm.getHistionMap().get(0).getZCoordinate();
                            while (hZ < zUpperBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ);
                                newHistion.setYCoordinate(hY + deltaY);
                                hm.addHistion(newHistion);

                                if ((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0)) {
                                    hX = hm.getHistionMap().get(0).getXCoordinate();
                                    while (hX < xUpperBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(0));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX + xShift.get());
                                        newHist.setZCoordinate(hZ + deltaZ + zShift.get());
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX + xShift.get();
                                        hZ += zShift.get();
                                    }
                                    hX = hm.getHistionMap().get(0).getXCoordinate();
                                    hZ = hm.getHistionMap().get(0).getZCoordinate() + deltaZ * num;
                                    while (hX > -xLowerBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(0));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX - xShift.get());
                                        newHist.setZCoordinate(hZ + deltaZ - zShift.get());
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX + xShift.get();
                                        hZ -= zShift.get();
                                    }
                                }
                                num++;
                                hZ = hm.getHistionMap().get(0).getZCoordinate() + deltaZ * num;
                            }
                            hZ = hm.getHistionMap().get(0).getZCoordinate();
                            num = 0;
                            while (hZ > -zLowerBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ);
                                newHistion.setYCoordinate(hY + deltaY);
                                hm.addHistion(newHistion);
                                if ((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0)) {
                                    hX = hm.getHistionMap().get(0).getXCoordinate();
                                    while (hX < xUpperBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(0));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX + xShift.get());
                                        newHist.setZCoordinate(hZ - deltaZ + zShift.get());
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX + xShift.get();
                                        hZ += zShift.get();
                                    }
                                    hX = hm.getHistionMap().get(0).getXCoordinate();
                                    hZ = hm.getHistionMap().get(0).getZCoordinate() - deltaZ * num;
                                    while (hX > -xLowerBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(0));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX - xShift.get());
                                        newHist.setZCoordinate(hZ - deltaZ - zShift.get());
                                        newHist.setYCoordinate(hY + deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX + xShift.get();
                                        hZ -= zShift.get();
                                    }
                                }
                                num++;
                                hZ = hm.getHistionMap().get(0).getZCoordinate() - deltaZ * num;
                            }
                            hY += deltaY;
                        }
                        hY = hm.getHistionMap().get(0).getYCoordinate();
                        while (hY > -yLowerBoundary) {
                            num = 0;
                            hZ = hm.getHistionMap().get(0).getZCoordinate();
                            while (hZ < zUpperBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ + deltaZ);
                                newHistion.setYCoordinate(hY - deltaY);
                                hm.addHistion(newHistion);

                                if ((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0)) {
                                    hX = hm.getHistionMap().get(0).getXCoordinate();
                                    while (hX < xUpperBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(0));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX + xShift.get());
                                        newHist.setZCoordinate(hZ + deltaZ + zShift.get());
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX + xShift.get();
                                        hZ += zShift.get();
                                    }
                                    hX = hm.getHistionMap().get(0).getXCoordinate();
                                    hZ = hm.getHistionMap().get(0).getZCoordinate() + deltaZ * num;
                                    while (hX > -xLowerBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(0));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ + deltaZ) + ">");
                                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX - xShift.get());
                                        newHist.setZCoordinate(hZ + deltaZ - zShift.get());
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX + xShift.get();
                                        hZ -= zShift.get();
                                    }
                                }

                                num++;
                                hZ = hm.getHistionMap().get(0).getZCoordinate() + deltaZ * num;

                            }
                            hZ = hm.getHistionMap().get(0).getZCoordinate();
                            num = 0;
                            while (hZ > -zLowerBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setZCoordinate(hZ - deltaZ);
                                newHistion.setYCoordinate(hY - deltaY);
                                hm.addHistion(newHistion);

                                if ((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0)) {
                                    hX = hm.getHistionMap().get(0).getXCoordinate();
                                    while (hX < xUpperBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(0));
                                        newHist.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX + deltaX + xShift.get());
                                        newHist.setZCoordinate(hZ - deltaZ + zShift.get());
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX += deltaX + xShift.get();
                                        hZ += zShift.get();
                                    }
                                    hX = hm.getHistionMap().get(0).getXCoordinate();
                                    hZ = hm.getHistionMap().get(0).getZCoordinate() - deltaZ * num;
                                    while (hX > -xLowerBoundary) {
                                        Histion newHist = new Histion(hm.getHistionMap().get(0));
                                        newHist.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ - deltaZ) + ">");
                                        hm.getHistionMap().get(0).getItems().forEach(c -> {
                                            Cell newCell = new Cell(c, newHist.getId());
                                            c.getItems().forEach(p -> {
                                                newCell.addChild(p);
                                            });
                                            newHist.addChild(newCell);
                                        });
                                        newHist.setXCoordinate(hX - deltaX - xShift.get());
                                        newHist.setZCoordinate(hZ - deltaZ - zShift.get());
                                        newHist.setYCoordinate(hY - deltaY);
                                        hm.addHistion(newHist);
                                        hX -= deltaX + xShift.get();
                                        hZ -= zShift.get();
                                    }
                                }

                                num++;
                                hZ = hm.getHistionMap().get(0).getZCoordinate() - deltaZ * num;
                            }
                            hY -= deltaY;
                        }
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        hZ = hm.getHistionMap().get(0).getZCoordinate();
                    }

                    if (((xUpperLimit.get() >= 0) && (xLowerLimit.get() >= 0))
                            && ((yUpperLimit.get() >= 0) && (yLowerLimit.get() >= 0))) {
                        while (hX < xUpperBoundary) {
                            hY = hm.getHistionMap().get(0).getYCoordinate();
                            while (hY < yUpperBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY + deltaY);
                                newHistion.setXCoordinate(hX + deltaX + xShift.get());
                                newHistion.setZCoordinate(hZ + zShift.get());
                                hm.addHistion(newHistion);
                                hY += deltaY;
                            }
                            hY = hm.getHistionMap().get(0).getYCoordinate();
                            while (hY > -yLowerBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX + deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY - deltaY);
                                newHistion.setXCoordinate(hX + deltaX + xShift.get());
                                newHistion.setZCoordinate(hZ + zShift.get());
                                hm.addHistion(newHistion);
                                hY -= deltaY;
                            }
                            hX += deltaX + xShift.get();
                            hZ += zShift.get();
                        }
                        hX = hm.getHistionMap().get(0).getXCoordinate();
                        hZ = hm.getHistionMap().get(0).getXCoordinate();
                        while (hX > -xLowerBoundary) {

                            hY = hm.getHistionMap().get(0).getYCoordinate();
                            while (hY < yUpperBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY + deltaY);
                                newHistion.setXCoordinate(hX - deltaX - xShift.get());
                                newHistion.setZCoordinate(hZ - zShift.get());
                                hm.addHistion(newHistion);
                                hY += deltaY;
                            }
                            hY = hm.getHistionMap().get(0).getYCoordinate();
                            while (hY > -yLowerBoundary) {
                                Histion newHistion = new Histion(hm.getHistionMap().get(0));
                                newHistion.setName("Histion <X: " + String.valueOf(hX - deltaX) + " ; Z: " + String.valueOf(hZ) + ">");
                                hm.getHistionMap().get(0).getItems().forEach(c -> {
                                    Cell newCell = new Cell(c, newHistion.getId());
                                    c.getItems().forEach(p -> {
                                        newCell.addChild(p);
                                    });
                                    newHistion.addChild(newCell);
                                });
                                newHistion.setYCoordinate(hY - deltaY);
                                newHistion.setXCoordinate(hX - deltaX - xShift.get());
                                newHistion.setZCoordinate(hZ - zShift.get());
                                hm.addHistion(newHistion);
                                hY -= deltaY;
                            }
                            hX -= deltaX + xShift.get();
                            hZ -= zShift.get();
                        }
                    }
                    disableEverything.set(true);
                    pastePartDisabledProperty.set(true);
                    pasteCellDisabledProperty.set(true);
                } else {
                    hm.getAllHistions().forEach(h -> {
                        if (h.getId() != 0) {
                            h.getItems().forEach(c -> {
                                hm.getHistionMap().get(h.getId()).deleteChild(c.getId());
                            });
                            hm.deleteHistion(h.getId());
                        }
                    });
                    disableEverything.set(false);
                }
            });
            MenuItem pasteCell = new MenuItem();
            pasteCell.setText("Вставить клетку");
            pasteCell.setOnAction(event -> {
                Cell newCell = new Cell(hm.getHistionMap().get(0).getItemMap().get(cellId), 0);
                String name = newCell.getName();
                name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                int count = 1;
                while (Names.containsCellName(name)) {
                    name = newCell.getName();
                    name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                    name += "(" + count + ")";
                    count++;
                }
                newCell.setName("Клетка <" + name + ">");
                Names.addCellName(name);
                hm.getHistionMap().get(0).getItemMap().get(cellId).getItems().forEach(p -> {
                    newCell.addChild(new Part(p, newCell.getId()));
                });
                hm.getHistionMap().get(this.getValue().getId()).addChild(newCell);
            });
            pasteCell.disableProperty().bind(pasteCellDisabledProperty);

            loadHistion.disableProperty().bind(disableEverything);
            loadCell.disableProperty().bind(disableEverything);
            addCell.disableProperty().bind(disableEverything);
            if (hm.getAllHistions().size() > 1) {
                fillModel.setSelected(true);
            }
            histionMenu = new ContextMenu(loadHistion, saveHistion, loadCell, addCell, pasteCell, fillModel);
            return histionMenu;
        }
    }

    public class CellTreeItem extends AbstractTreeItem {
        
        ContextMenu cellMenu = new ContextMenu();

        public CellTreeItem(HistologyObject<?> object) {
            this.setValue(object);
            this.setExpanded(true);
        }

        @Override
        public ContextMenu getMenu() {
            Cell c = (Cell) this.getValue();
            MenuItem saveCell = new MenuItem();
            saveCell.setText("Сохранить клетку");
            saveCell.setOnAction(event -> {
                cellMenu.hide();
                /*FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                String userDirectoryString = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
                userDirectoryString += "\\HistologyApp\\Cells";
                File userDirectory = new File(userDirectoryString);
                if (!userDirectory.exists()) {
                    userDirectory.mkdirs();
                }
                fileChooser.setInitialDirectory(userDirectory);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(null);

                try {

                    BufferedWriter writer = new BufferedWriter(new FileWriter(userDirectoryString
                            + "\\" + file.getName()));

                    writer.write(0 + " "
                            + 0 + " "
                            + hm.getHistionMap().get(0).getXCoordinate() + " "
                            + hm.getHistionMap().get(0).getYCoordinate() + " "
                            + hm.getHistionMap().get(0).getZCoordinate());
                    writer.newLine();

                    writer.write("1");
                    writer.newLine();
                    ArrayList<Integer> pointIds = new ArrayList<>();
                    try {
                        writer.write(c.getName());
                        writer.newLine();
                        writer.write(c.getXRotate() + " " + c.getYRotate() + " "
                                + c.getXCoordinate() + " " + c.getYCoordinate() + " "
                                + c.getZCoordinate());
                        writer.newLine();
                        writer.write(c.getDiffuseColor().getRed() + " "
                                + c.getDiffuseColor().getGreen() + " "
                                + c.getDiffuseColor().getBlue());
                        writer.newLine();
                        writer.write(c.getSpecularColor().getRed() + " "
                                + c.getSpecularColor().getGreen() + " "
                                + c.getSpecularColor().getBlue());
                        writer.newLine();
                        writer.write(c.getShow() + "");
                        writer.newLine();

                        IntegerProperty num = new SimpleIntegerProperty(1);
                        ArrayList<Integer> partIds = new ArrayList<>();
                        writer.write(c.getItems().size() + "");
                        writer.newLine();

                        IntegerProperty num2 = new SimpleIntegerProperty(1);
                        num.set(1);
                        c.getItems().forEach(p -> {
                            try {
                                writer.write(p.getName());
                                writer.newLine();
                                writer.write(p.getPointData().size() + "");
                                writer.newLine();
                                for (int i = 0; i < p.getPointData().size(); i++) {
                                    writer.write(p.getPointData().get(i).getX() + " "
                                            + p.getPointData().get(i).getY() + " "
                                            + p.getPointData().get(i).getZ());
                                    writer.newLine();
                                }
                            } catch (Exception ex) {

                            }
                        });

                        writer.write(c.getFacetData().size() + "");
                        writer.newLine();

                        c.getFacetData().forEach(list -> {
                            try {

                                for (int i = 0; i < list.size(); i++) {
                                    if (i == list.size() - 1) {
                                        writer.write(list.get(i) + "");
                                    } else {
                                        writer.write(list.get(i) + " ");
                                    }
                                }
                                writer.newLine();
                            } catch (Exception ex) {

                            }
                        });
                    } catch (Exception ex) {

                    }
                    writer.close();
                } catch (Exception ex) {

                }*/
                cellMenu.hide();
                SaveCellBox.display(c.getId());
            });
            MenuItem editCell = new MenuItem();
            editCell.setText("Изменить клетку");
            editCell.setOnAction(event -> {
                CellInformationInitialization.createScene(hm.getHistionMap().
                        get(c.getHistionId()).getItemMap().
                        get(c.getId()));
            });
            MenuItem loadPart = new MenuItem();
            loadPart.setText("Загрузить слой");
            loadPart.setOnAction(event -> {
                cellMenu.hide();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                String userDirectoryString = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
                userDirectoryString += "\\HistologyApp" + System.getProperty("sun.arch.data.model") + "\\Parts";
                File userDirectory = new File(userDirectoryString);
                if (!userDirectory.exists()) {
                    userDirectory.mkdirs();
                }
                fileChooser.setInitialDirectory(userDirectory);
                File selectedFile = fileChooser.showOpenDialog(null);

                try {
                    Histion main = hm.getHistionMap().get(0);
                    BufferedReader br = new BufferedReader(new FileReader(userDirectory + "\\" + selectedFile.getName()));

                    double x, y, z;

                    String line = br.readLine();

                    ObservableList<TetgenPoint> pointData = FXCollections.observableArrayList();
                    Part p = new Part("Слой", FXCollections.observableArrayList(), c.getId());
                    String name = line;
                    name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                    p.setName("Слой <" + name + ">");

                    line = br.readLine();
                    int pointNum = Integer.parseInt(line);
                    for (int q = 0; q < pointNum; q++) {
                        line = br.readLine();
                        x = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        y = Double.parseDouble(line.substring(0, line.indexOf(" ")));
                        line = line.substring(line.indexOf(" ") + 1, line.length());
                        z = Double.parseDouble(line);
                        pointData.add(new TetgenPoint(q + 1, x, y, z));
                    }
                    p.setPointData(pointData);
                    p.setAvgNode();
                    hm.getHistionMap().get(0).getItemMap().get(c.getId()).addChild(p);
                    br.close();
                } catch (Exception ex) {
                    System.out.println("error");
                }
            });
            MenuItem addPart = new MenuItem();
            addPart.setText("Добавить слой");
            addPart.setOnAction(event -> {
                PartInformationInitialization.show(c.getId(), -1);
            });
            MenuItem copyCell = new MenuItem();
            copyCell.setText("Копировать клетку");
            copyCell.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(false);
                cellId = c.getId();
                partId = -1;
            });
            MenuItem pastePart = new MenuItem();
            pastePart.setText("Вставить слой");
            pastePart.setOnAction(event -> {
                Integer newHistionId = c.getHistionId();
                Integer newCellId = c.getId();
                Part newPart = new Part(hm.getHistionMap().get(0).
                        getItemMap().get(cellId).getItemMap().get(partId), newCellId);
                String name = newPart.getName();
                name = name.substring(name.indexOf("<") + 1, name.lastIndexOf(">"));
                name += "(Копия)";
                newPart.setName("Слой <" + name + ">");
                hm.getHistionMap().get(newHistionId).getItemMap().get(newCellId).
                        addChild(newPart);
            });
            pastePart.disableProperty().bind(pastePartDisabledProperty);
            MenuItem deleteCell = new MenuItem();
            deleteCell.setText("Удалить клетку");
            deleteCell.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                ConfirmBox.display("Подтверждение удаления", "Вы уверены, что хотите удалить "
                        + c.getName(), c.getId(), -1);
            });

            loadPart.disableProperty().bind(disableEverything);
            editCell.disableProperty().bind(disableEverything);
            addPart.disableProperty().bind(disableEverything);
            deleteCell.disableProperty().bind(disableEverything);
            copyCell.disableProperty().bind(disableEverything);
            
            cellMenu = new ContextMenu(editCell, saveCell, loadPart, addPart, copyCell, pastePart, deleteCell);
            return cellMenu;
            //return new ContextMenu(editCell, saveCell, loadPart, addPart, copyCell, pastePart, deleteCell);
        }
    }

    public class PartTreeItem extends AbstractTreeItem {
        
        ContextMenu partMenu = new ContextMenu();

        public PartTreeItem(HistologyObject<?> object) {
            this.setValue(object);
            this.setExpanded(true);
        }

        @Override
        public ContextMenu getMenu() {
            Part p = (Part) this.getValue();
            MenuItem savePart = new MenuItem();
            savePart.setText("Сохранить слой");
            savePart.setOnAction(event -> {
                partMenu.hide();
                FileChooser fileChooser = new FileChooser();

                //Set extension filter
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                String userDirectoryString = FileSystemView.getFileSystemView().getDefaultDirectory().getPath();
                userDirectoryString += "\\HistologyApp" + System.getProperty("sun.arch.data.model") + "\\Parts";
                File userDirectory = new File(userDirectoryString);
                if (!userDirectory.exists()) {
                    userDirectory.mkdirs();
                }
                fileChooser.setInitialDirectory(userDirectory);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(null);

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(userDirectoryString
                            + "\\" + file.getName()));

                    writer.write(p.getName());
                    writer.newLine();
                    writer.write(p.getPointData().size() + "");
                    writer.newLine();
                    for (int i = 0; i < p.getPointData().size(); i++) {
                        writer.write(p.getPointData().get(i).getX() + " "
                                + p.getPointData().get(i).getY() + " "
                                + p.getPointData().get(i).getZ());
                        writer.newLine();
                    }
                    writer.close();
                } catch (Exception ex) {

                }
            });
            MenuItem editPart = new MenuItem();
            editPart.setText("Изменить слой");
            editPart.setOnAction(event -> {
                PartInformationInitialization.show(p.getCellId(), p.getId());
            });
            MenuItem copyPart = new MenuItem();
            copyPart.setText("Копировать слой");
            copyPart.setOnAction(event -> {
                pastePartDisabledProperty.set(false);
                pasteCellDisabledProperty.set(true);
                cellId = p.getCellId();
                partId = p.getId();
            });
            MenuItem deletePart = new MenuItem();
            deletePart.setText("Удалить слой");
            deletePart.setOnAction(event -> {
                pastePartDisabledProperty.set(true);
                pasteCellDisabledProperty.set(true);
                ConfirmBox.display("Подтверждение удаления", "Вы уверены, что хотите удалить "
                        + p.getName(), p.getCellId(), p.getId());
            });

            editPart.disableProperty().bind(disableEverything);
            copyPart.disableProperty().bind(disableEverything);
            deletePart.disableProperty().bind(disableEverything);
            
            partMenu = new ContextMenu(editPart, savePart, copyPart, deletePart);
            return partMenu;
            //return new ContextMenu(editPart, savePart, copyPart, deletePart);
        }
    }

    private final class TreeCellImpl extends TreeCell<HistologyObject<?>> {

        @Override
        public void updateItem(HistologyObject<?> item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(getItem() == null ? "" : getItem().getName());
                setGraphic(getTreeItem().getGraphic());
                setContextMenu(((AbstractTreeItem) getTreeItem()).getMenu());
            }
        }
    }

    boolean SameSide(Point3D v1, Point3D v2, Point3D v3, Point3D v4, Point3D p) {
        Point3D p1 = v2.subtract(v1);
        Point3D p2 = v3.subtract(v1);
        Point3D normal = p1.crossProduct(p2);
        double dotV4 = normal.dotProduct(v4.subtract(v1));
        double dotP = normal.dotProduct(p.subtract(v1));
        if (Math.signum(dotV4) * Math.signum(dotP) < 0) {
            return false;
        }
        return true;
    }

    boolean PointInTetrahedron(Point3D v1, Point3D v2, Point3D v3, Point3D v4, Point3D p) {
        return SameSide(v1, v2, v3, v4, p)
                && SameSide(v2, v3, v4, v1, p)
                && SameSide(v3, v4, v1, v2, p)
                && SameSide(v4, v1, v2, v3, p);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        hm = Lookup.getDefault().lookup(HistionManager.class);
        if (hm == null) {
            LifecycleManager.getDefault().exit();
        }

        if (hm.getHistionMap().isEmpty()) {
            hm.addHistion(new Histion("Главный гистион", 0, 0, 0));
        }

        if (hm.getAllHistions().size() > 1) {
            disableEverything.set(true);
            pastePartDisabledProperty.set(true);
            pasteCellDisabledProperty.set(true);
        }

        histion = new HistionTreeItem(hm.getHistionMap().get(0));

        hm.getHistionMap().get(0).getItemMap().addListener(cellListener);
        hm.getHistionMap().get(0).getItems().forEach(c -> {
            CellTreeItem cti = new CellTreeItem(c);
            histion.getChildren().add(cti);
            cellTreeItemMap.put(c.getId(), cti);
            c.getItemMap().addListener(partListener);
            c.getItems().forEach(p -> {
                PartTreeItem pti = new PartTreeItem(p);
                histion.getChildren().forEach(cell -> {
                    if (cell.getValue().getId() == c.getId()) {
                        cell.getChildren().add(pti);
                    }
                });
                partTreeItemMap.put(p.getId(), pti);
            });
        });

        TreeItem treeRoot = new TreeItem();
        treeRoot.setExpanded(true);
        treeRoot.getChildren().addAll(histion);
        shapeTreeView.setRoot(treeRoot);
        shapeTreeView.setShowRoot(false);
        shapeTreeView.setCellFactory(new Callback<TreeView<HistologyObject<?>>, TreeCell<HistologyObject<?>>>() {
            @Override
            public TreeCell<HistologyObject<?>> call(TreeView<HistologyObject<?>> p) {
                return new TreeCellImpl();
            }
        });

    }

    public void setTreeViewSize(int width, int height) {
        shapeTreeView.setPrefSize(width, height);
    }

    public void removeListeners() {
        hm.getHistionMap().get(0).getItemMap().removeListener(cellListener);
        hm.getHistionMap().get(0).getItems().forEach(c -> {
            c.getItemMap().removeListener(partListener);
        });
    }

}
