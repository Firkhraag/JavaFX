package org.spbu.histology.model;

public class Cell extends HistologyObject<Part> {
    
    private static long count = 0;

    public Cell(String name, double xRot, double yRot, double xPos, double yPos,
            double zPos) {
        super(count++, name, xRot, yRot, xPos, yPos, zPos);
    }
    
    public Cell(Long id, String name, double xRot, double yRot, double xPos,
            double yPos, double zPos) {
        super(id, name, xRot, yRot, xPos, yPos, zPos);
    }
    
    public Cell(Cell c) {
        super(count++, c.getName(), c.getXRotate(), c.getYRotate(), 
                c.getXCoordinate(), c.getYCoordinate(), c.getZCoordinate());
    }
    
    public Cell(Long id, Cell c) {
        super(id, c.getName(), c.getXRotate(), c.getYRotate(), 
                c.getXCoordinate(), c.getYCoordinate(), c.getZCoordinate());
    }
    
    @Override
    public void addChild(Part p) {
        getItemMap().put(p.getId(), new Part(p.getId(), p));
    }
    
    @Override
    public void deleteChild(long id) {
        getItemMap().remove(id);
    }
}
