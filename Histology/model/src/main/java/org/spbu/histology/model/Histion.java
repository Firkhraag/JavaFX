package org.spbu.histology.model;

public class Histion extends HistologyObject<Cell> {
    
    private static long count = 0;

    public Histion(String name, double xRot, double yRot, double xPos, 
            double yPos, double zPos) {
        super(count++, name, xRot, yRot, xPos, yPos, zPos);
    }
    
    public Histion(Long id, String name, double xRot, double yRot, double xPos,
            double yPos, double zPos) {
        super(id, name, xRot, yRot, xPos, yPos, zPos);
    }
    
    public Histion(Histion h) {
        super(count++, h.getName(), h.getXRotate(), h.getYRotate(), 
                h.getXCoordinate(), h.getYCoordinate(), h.getZCoordinate());
    }
    
    public Histion(Long id, Histion h) {
        super(id, h.getName(), h.getXRotate(), h.getYRotate(), 
                h.getXCoordinate(), h.getYCoordinate(), h.getZCoordinate());
    }

    @Override
    public void addChild(Cell c) {
        getItemMap().put(c.getId(), new Cell(c.getId(), c));
    }
    
    @Override
    public void deleteChild(long id) {
        getItemMap().remove(id);
    }
}
