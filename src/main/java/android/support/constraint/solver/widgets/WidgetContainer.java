package android.support.constraint.solver.widgets;

import android.support.constraint.solver.Cache;
import java.util.ArrayList;

public class WidgetContainer extends ConstraintWidget {
    protected ArrayList<ConstraintWidget> mChildren = new ArrayList<>();

    public WidgetContainer() {
    }

    public WidgetContainer(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public WidgetContainer(int width, int height) {
        super(width, height);
    }

    public void reset() {
        this.mChildren.clear();
        super.reset();
    }

    public void add(ConstraintWidget widget) {
        this.mChildren.add(widget);
        if (widget.getParent() != null) {
            ((WidgetContainer) widget.getParent()).remove(widget);
        }
        widget.setParent(this);
    }

    public void remove(ConstraintWidget widget) {
        this.mChildren.remove(widget);
        widget.setParent(null);
    }

    public ArrayList<ConstraintWidget> getChildren() {
        return this.mChildren;
    }

    public ConstraintWidgetContainer getRootConstraintContainer() {
        ConstraintWidget parent = getParent();
        ConstraintWidgetContainer container = null;
        if (this instanceof ConstraintWidgetContainer) {
            container = (ConstraintWidgetContainer) this;
        }
        while (parent != null) {
            ConstraintWidget item = parent;
            parent = item.getParent();
            if (item instanceof ConstraintWidgetContainer) {
                container = (ConstraintWidgetContainer) item;
            }
        }
        return container;
    }

    /* JADX WARNING: type inference failed for: r2v3 */
    /* JADX WARNING: type inference failed for: r1v0, types: [android.support.constraint.solver.widgets.ConstraintWidget] */
    /* JADX WARNING: type inference failed for: r2v5 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.support.constraint.solver.widgets.ConstraintWidget findWidget(float r11, float r12) {
        /*
            r10 = this;
            r2 = 0
            int r4 = r10.getDrawX()
            int r7 = r10.getDrawY()
            int r9 = r10.getWidth()
            int r6 = r4 + r9
            int r9 = r10.getHeight()
            int r0 = r7 + r9
            float r9 = (float) r4
            int r9 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x002a
            float r9 = (float) r6
            int r9 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r9 > 0) goto L_0x002a
            float r9 = (float) r7
            int r9 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x002a
            float r9 = (float) r0
            int r9 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
            if (r9 > 0) goto L_0x002a
            r2 = r10
        L_0x002a:
            r3 = 0
            java.util.ArrayList<android.support.constraint.solver.widgets.ConstraintWidget> r9 = r10.mChildren
            int r5 = r9.size()
        L_0x0031:
            if (r3 >= r5) goto L_0x0075
            java.util.ArrayList<android.support.constraint.solver.widgets.ConstraintWidget> r9 = r10.mChildren
            java.lang.Object r8 = r9.get(r3)
            android.support.constraint.solver.widgets.ConstraintWidget r8 = (android.support.constraint.solver.widgets.ConstraintWidget) r8
            boolean r9 = r8 instanceof android.support.constraint.solver.widgets.WidgetContainer
            if (r9 == 0) goto L_0x004b
            android.support.constraint.solver.widgets.WidgetContainer r8 = (android.support.constraint.solver.widgets.WidgetContainer) r8
            android.support.constraint.solver.widgets.ConstraintWidget r1 = r8.findWidget(r11, r12)
            if (r1 == 0) goto L_0x0048
            r2 = r1
        L_0x0048:
            int r3 = r3 + 1
            goto L_0x0031
        L_0x004b:
            int r4 = r8.getDrawX()
            int r7 = r8.getDrawY()
            int r9 = r8.getWidth()
            int r6 = r4 + r9
            int r9 = r8.getHeight()
            int r0 = r7 + r9
            float r9 = (float) r4
            int r9 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x0048
            float r9 = (float) r6
            int r9 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r9 > 0) goto L_0x0048
            float r9 = (float) r7
            int r9 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x0048
            float r9 = (float) r0
            int r9 = (r12 > r9 ? 1 : (r12 == r9 ? 0 : -1))
            if (r9 > 0) goto L_0x0048
            r2 = r8
            goto L_0x0048
        L_0x0075:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.WidgetContainer.findWidget(float, float):android.support.constraint.solver.widgets.ConstraintWidget");
    }

    public ArrayList<ConstraintWidget> findWidgets(int x, int y, int width, int height) {
        ArrayList<ConstraintWidget> found = new ArrayList<>();
        Rectangle area = new Rectangle();
        area.setBounds(x, y, width, height);
        int mChildrenSize = this.mChildren.size();
        for (int i = 0; i < mChildrenSize; i++) {
            ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
            Rectangle bounds = new Rectangle();
            bounds.setBounds(widget.getDrawX(), widget.getDrawY(), widget.getWidth(), widget.getHeight());
            if (area.intersects(bounds)) {
                found.add(widget);
            }
        }
        return found;
    }

    public static Rectangle getBounds(ArrayList<ConstraintWidget> widgets) {
        Rectangle bounds = new Rectangle();
        if (widgets.size() != 0) {
            int minX = ConstraintAnchor.ANY_GROUP;
            int maxX = 0;
            int minY = ConstraintAnchor.ANY_GROUP;
            int maxY = 0;
            int widgetsSize = widgets.size();
            for (int i = 0; i < widgetsSize; i++) {
                ConstraintWidget widget = (ConstraintWidget) widgets.get(i);
                if (widget.getX() < minX) {
                    minX = widget.getX();
                }
                if (widget.getY() < minY) {
                    minY = widget.getY();
                }
                if (widget.getRight() > maxX) {
                    maxX = widget.getRight();
                }
                if (widget.getBottom() > maxY) {
                    maxY = widget.getBottom();
                }
            }
            bounds.setBounds(minX, minY, maxX - minX, maxY - minY);
        }
        return bounds;
    }

    public void setOffset(int x, int y) {
        super.setOffset(x, y);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ((ConstraintWidget) this.mChildren.get(i)).setOffset(getRootX(), getRootY());
        }
    }

    public void updateDrawPosition() {
        super.updateDrawPosition();
        if (this.mChildren != null) {
            int count = this.mChildren.size();
            for (int i = 0; i < count; i++) {
                ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
                widget.setOffset(getDrawX(), getDrawY());
                if (!(widget instanceof ConstraintWidgetContainer)) {
                    widget.updateDrawPosition();
                }
            }
        }
    }

    public void layout() {
        updateDrawPosition();
        if (this.mChildren != null) {
            int count = this.mChildren.size();
            for (int i = 0; i < count; i++) {
                ConstraintWidget widget = (ConstraintWidget) this.mChildren.get(i);
                if (widget instanceof WidgetContainer) {
                    ((WidgetContainer) widget).layout();
                }
            }
        }
    }

    public void resetSolverVariables(Cache cache) {
        super.resetSolverVariables(cache);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ((ConstraintWidget) this.mChildren.get(i)).resetSolverVariables(cache);
        }
    }

    public void resetGroups() {
        super.resetGroups();
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ((ConstraintWidget) this.mChildren.get(i)).resetGroups();
        }
    }

    public void removeAllChildren() {
        this.mChildren.clear();
    }
}
