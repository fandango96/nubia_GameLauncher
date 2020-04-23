package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour;

public class Optimizer {
    /* JADX WARNING: type inference failed for: r24v7 */
    /* JADX WARNING: type inference failed for: r24v8 */
    /* JADX WARNING: type inference failed for: r0v50, types: [android.support.constraint.solver.widgets.ConstraintWidget] */
    /* JADX WARNING: type inference failed for: r24v10 */
    /* JADX WARNING: type inference failed for: r24v16 */
    /* JADX WARNING: type inference failed for: r24v17 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r24v7
      assigns: []
      uses: []
      mth insns count: 324
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.util.ArrayList.forEach(ArrayList.java:1257)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.util.ArrayList.forEach(ArrayList.java:1257)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.util.ArrayList.forEach(ArrayList.java:1257)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void applyDirectResolutionHorizontalChain(android.support.constraint.solver.widgets.ConstraintWidgetContainer r21, android.support.constraint.solver.LinearSystem r22, int r23, android.support.constraint.solver.widgets.ConstraintWidget r24) {
        /*
            r7 = r24
            r18 = 0
            r6 = 0
            r12 = 0
            r3 = 0
            r17 = 0
        L_0x0009:
            if (r24 == 0) goto L_0x00d3
            int r19 = r24.getVisibility()
            r20 = 8
            r0 = r19
            r1 = r20
            if (r0 != r1) goto L_0x00be
            r8 = 1
        L_0x0018:
            if (r8 != 0) goto L_0x0064
            int r3 = r3 + 1
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r0 = r0.mHorizontalDimensionBehaviour
            r19 = r0
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r20 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r0 = r19
            r1 = r20
            if (r0 == r1) goto L_0x00c7
            int r19 = r24.getWidth()
            int r18 = r18 + r19
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x00c1
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            int r19 = r19.getMargin()
        L_0x0048:
            int r18 = r18 + r19
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x00c4
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mRight
            r19 = r0
            int r19 = r19.getMargin()
        L_0x0062:
            int r18 = r18 + r19
        L_0x0064:
            r12 = r24
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x00d0
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r0.mOwner
            r24 = r0
        L_0x0086:
            if (r24 == 0) goto L_0x0009
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x00ba
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x0009
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r0.mOwner
            r19 = r0
            r0 = r19
            if (r0 == r12) goto L_0x0009
        L_0x00ba:
            r24 = 0
            goto L_0x0009
        L_0x00be:
            r8 = 0
            goto L_0x0018
        L_0x00c1:
            r19 = 0
            goto L_0x0048
        L_0x00c4:
            r19 = 0
            goto L_0x0062
        L_0x00c7:
            r0 = r24
            float r0 = r0.mHorizontalWeight
            r19 = r0
            float r17 = r17 + r19
            goto L_0x0064
        L_0x00d0:
            r24 = 0
            goto L_0x0086
        L_0x00d3:
            r9 = 0
            if (r12 == 0) goto L_0x0118
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r12.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x0230
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r12.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r0.mOwner
            r19 = r0
            int r9 = r19.getX()
        L_0x00f6:
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r12.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x0118
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r12.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r5 = r0.mOwner
            r0 = r21
            if (r5 != r0) goto L_0x0118
            int r9 = r21.getRight()
        L_0x0118:
            int r19 = r9 - r6
            r0 = r19
            float r0 = (float) r0
            r16 = r0
            r0 = r18
            float r0 = (float) r0
            r19 = r0
            float r15 = r16 - r19
            int r19 = r3 + 1
            r0 = r19
            float r0 = (float) r0
            r19 = r0
            float r14 = r15 / r19
            r24 = r7
            r4 = 0
            if (r23 != 0) goto L_0x0233
            r4 = r14
        L_0x0135:
            if (r24 == 0) goto L_0x02af
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x023c
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            int r10 = r19.getMargin()
        L_0x014f:
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x023f
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mRight
            r19 = r0
            int r13 = r19.getMargin()
        L_0x0167:
            int r19 = r24.getVisibility()
            r20 = 8
            r0 = r19
            r1 = r20
            if (r0 == r1) goto L_0x0267
            float r0 = (float) r10
            r19 = r0
            float r4 = r4 + r19
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            r0 = r19
            android.support.constraint.solver.SolverVariable r0 = r0.mSolverVariable
            r19 = r0
            r20 = 1056964608(0x3f000000, float:0.5)
            float r20 = r20 + r4
            r0 = r20
            int r0 = (int) r0
            r20 = r0
            r0 = r22
            r1 = r19
            r2 = r20
            r0.addEquality(r1, r2)
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r0 = r0.mHorizontalDimensionBehaviour
            r19 = r0
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r20 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r0 = r19
            r1 = r20
            if (r0 != r1) goto L_0x025a
            r19 = 0
            int r19 = (r17 > r19 ? 1 : (r17 == r19 ? 0 : -1))
            if (r19 != 0) goto L_0x0242
            float r0 = (float) r10
            r19 = r0
            float r19 = r14 - r19
            float r0 = (float) r13
            r20 = r0
            float r19 = r19 - r20
            float r4 = r4 + r19
        L_0x01b6:
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.SolverVariable r0 = r0.mSolverVariable
            r19 = r0
            r20 = 1056964608(0x3f000000, float:0.5)
            float r20 = r20 + r4
            r0 = r20
            int r0 = (int) r0
            r20 = r0
            r0 = r22
            r1 = r19
            r2 = r20
            r0.addEquality(r1, r2)
            if (r23 != 0) goto L_0x01d7
            float r4 = r4 + r14
        L_0x01d7:
            float r0 = (float) r13
            r19 = r0
            float r4 = r4 + r19
        L_0x01dc:
            r12 = r24
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x02ab
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r0.mOwner
            r24 = r0
        L_0x01fe:
            if (r24 == 0) goto L_0x0226
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x0226
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r0.mOwner
            r19 = r0
            r0 = r19
            if (r0 == r12) goto L_0x0226
            r24 = 0
        L_0x0226:
            r0 = r24
            r1 = r21
            if (r0 != r1) goto L_0x0135
            r24 = 0
            goto L_0x0135
        L_0x0230:
            r9 = 0
            goto L_0x00f6
        L_0x0233:
            r0 = r23
            float r0 = (float) r0
            r19 = r0
            float r14 = r15 / r19
            goto L_0x0135
        L_0x023c:
            r10 = 0
            goto L_0x014f
        L_0x023f:
            r13 = 0
            goto L_0x0167
        L_0x0242:
            r0 = r24
            float r0 = r0.mHorizontalWeight
            r19 = r0
            float r19 = r19 * r15
            float r19 = r19 / r17
            float r0 = (float) r10
            r20 = r0
            float r19 = r19 - r20
            float r0 = (float) r13
            r20 = r0
            float r19 = r19 - r20
            float r4 = r4 + r19
            goto L_0x01b6
        L_0x025a:
            int r19 = r24.getWidth()
            r0 = r19
            float r0 = (float) r0
            r19 = r0
            float r4 = r4 + r19
            goto L_0x01b6
        L_0x0267:
            r19 = 1073741824(0x40000000, float:2.0)
            float r19 = r14 / r19
            float r11 = r4 - r19
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mLeft
            r19 = r0
            r0 = r19
            android.support.constraint.solver.SolverVariable r0 = r0.mSolverVariable
            r19 = r0
            r20 = 1056964608(0x3f000000, float:0.5)
            float r20 = r20 + r11
            r0 = r20
            int r0 = (int) r0
            r20 = r0
            r0 = r22
            r1 = r19
            r2 = r20
            r0.addEquality(r1, r2)
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mRight
            r19 = r0
            r0 = r19
            android.support.constraint.solver.SolverVariable r0 = r0.mSolverVariable
            r19 = r0
            r20 = 1056964608(0x3f000000, float:0.5)
            float r20 = r20 + r11
            r0 = r20
            int r0 = (int) r0
            r20 = r0
            r0 = r22
            r1 = r19
            r2 = r20
            r0.addEquality(r1, r2)
            goto L_0x01dc
        L_0x02ab:
            r24 = 0
            goto L_0x01fe
        L_0x02af:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.Optimizer.applyDirectResolutionHorizontalChain(android.support.constraint.solver.widgets.ConstraintWidgetContainer, android.support.constraint.solver.LinearSystem, int, android.support.constraint.solver.widgets.ConstraintWidget):void");
    }

    /* JADX WARNING: type inference failed for: r24v7 */
    /* JADX WARNING: type inference failed for: r24v8 */
    /* JADX WARNING: type inference failed for: r0v50, types: [android.support.constraint.solver.widgets.ConstraintWidget] */
    /* JADX WARNING: type inference failed for: r24v10 */
    /* JADX WARNING: type inference failed for: r24v16 */
    /* JADX WARNING: type inference failed for: r24v17 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r24v7
      assigns: []
      uses: []
      mth insns count: 324
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.util.ArrayList.forEach(ArrayList.java:1257)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.util.ArrayList.forEach(ArrayList.java:1257)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.util.ArrayList.forEach(ArrayList.java:1257)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void applyDirectResolutionVerticalChain(android.support.constraint.solver.widgets.ConstraintWidgetContainer r21, android.support.constraint.solver.LinearSystem r22, int r23, android.support.constraint.solver.widgets.ConstraintWidget r24) {
        /*
            r8 = r24
            r18 = 0
            r7 = 0
            r12 = 0
            r4 = 0
            r17 = 0
        L_0x0009:
            if (r24 == 0) goto L_0x00d3
            int r19 = r24.getVisibility()
            r20 = 8
            r0 = r19
            r1 = r20
            if (r0 != r1) goto L_0x00be
            r9 = 1
        L_0x0018:
            if (r9 != 0) goto L_0x0064
            int r4 = r4 + 1
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r0 = r0.mVerticalDimensionBehaviour
            r19 = r0
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r20 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r0 = r19
            r1 = r20
            if (r0 == r1) goto L_0x00c7
            int r19 = r24.getHeight()
            int r18 = r18 + r19
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x00c1
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            int r19 = r19.getMargin()
        L_0x0048:
            int r18 = r18 + r19
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x00c4
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            r19 = r0
            int r19 = r19.getMargin()
        L_0x0062:
            int r18 = r18 + r19
        L_0x0064:
            r12 = r24
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x00d0
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r0.mOwner
            r24 = r0
        L_0x0086:
            if (r24 == 0) goto L_0x0009
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x00ba
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x0009
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r0.mOwner
            r19 = r0
            r0 = r19
            if (r0 == r12) goto L_0x0009
        L_0x00ba:
            r24 = 0
            goto L_0x0009
        L_0x00be:
            r9 = 0
            goto L_0x0018
        L_0x00c1:
            r19 = 0
            goto L_0x0048
        L_0x00c4:
            r19 = 0
            goto L_0x0062
        L_0x00c7:
            r0 = r24
            float r0 = r0.mVerticalWeight
            r19 = r0
            float r17 = r17 + r19
            goto L_0x0064
        L_0x00d0:
            r24 = 0
            goto L_0x0086
        L_0x00d3:
            r10 = 0
            if (r12 == 0) goto L_0x0118
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r12.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x0230
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r12.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r0.mOwner
            r19 = r0
            int r10 = r19.getX()
        L_0x00f6:
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r12.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x0118
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r12.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r6 = r0.mOwner
            r0 = r21
            if (r6 != r0) goto L_0x0118
            int r10 = r21.getBottom()
        L_0x0118:
            int r19 = r10 - r7
            r0 = r19
            float r0 = (float) r0
            r16 = r0
            r0 = r18
            float r0 = (float) r0
            r19 = r0
            float r14 = r16 - r19
            int r19 = r4 + 1
            r0 = r19
            float r0 = (float) r0
            r19 = r0
            float r13 = r14 / r19
            r24 = r8
            r5 = 0
            if (r23 != 0) goto L_0x0233
            r5 = r13
        L_0x0135:
            if (r24 == 0) goto L_0x02af
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x023c
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            int r15 = r19.getMargin()
        L_0x014f:
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x023f
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            r19 = r0
            int r3 = r19.getMargin()
        L_0x0167:
            int r19 = r24.getVisibility()
            r20 = 8
            r0 = r19
            r1 = r20
            if (r0 == r1) goto L_0x0267
            float r0 = (float) r15
            r19 = r0
            float r5 = r5 + r19
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            r0 = r19
            android.support.constraint.solver.SolverVariable r0 = r0.mSolverVariable
            r19 = r0
            r20 = 1056964608(0x3f000000, float:0.5)
            float r20 = r20 + r5
            r0 = r20
            int r0 = (int) r0
            r20 = r0
            r0 = r22
            r1 = r19
            r2 = r20
            r0.addEquality(r1, r2)
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r0 = r0.mVerticalDimensionBehaviour
            r19 = r0
            android.support.constraint.solver.widgets.ConstraintWidget$DimensionBehaviour r20 = android.support.constraint.solver.widgets.ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT
            r0 = r19
            r1 = r20
            if (r0 != r1) goto L_0x025a
            r19 = 0
            int r19 = (r17 > r19 ? 1 : (r17 == r19 ? 0 : -1))
            if (r19 != 0) goto L_0x0242
            float r0 = (float) r15
            r19 = r0
            float r19 = r13 - r19
            float r0 = (float) r3
            r20 = r0
            float r19 = r19 - r20
            float r5 = r5 + r19
        L_0x01b6:
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.SolverVariable r0 = r0.mSolverVariable
            r19 = r0
            r20 = 1056964608(0x3f000000, float:0.5)
            float r20 = r20 + r5
            r0 = r20
            int r0 = (int) r0
            r20 = r0
            r0 = r22
            r1 = r19
            r2 = r20
            r0.addEquality(r1, r2)
            if (r23 != 0) goto L_0x01d7
            float r5 = r5 + r13
        L_0x01d7:
            float r0 = (float) r3
            r19 = r0
            float r5 = r5 + r19
        L_0x01dc:
            r12 = r24
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x02ab
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r0.mOwner
            r24 = r0
        L_0x01fe:
            if (r24 == 0) goto L_0x0226
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            if (r19 == 0) goto L_0x0226
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTarget
            r19 = r0
            r0 = r19
            android.support.constraint.solver.widgets.ConstraintWidget r0 = r0.mOwner
            r19 = r0
            r0 = r19
            if (r0 == r12) goto L_0x0226
            r24 = 0
        L_0x0226:
            r0 = r24
            r1 = r21
            if (r0 != r1) goto L_0x0135
            r24 = 0
            goto L_0x0135
        L_0x0230:
            r10 = 0
            goto L_0x00f6
        L_0x0233:
            r0 = r23
            float r0 = (float) r0
            r19 = r0
            float r13 = r14 / r19
            goto L_0x0135
        L_0x023c:
            r15 = 0
            goto L_0x014f
        L_0x023f:
            r3 = 0
            goto L_0x0167
        L_0x0242:
            r0 = r24
            float r0 = r0.mVerticalWeight
            r19 = r0
            float r19 = r19 * r14
            float r19 = r19 / r17
            float r0 = (float) r15
            r20 = r0
            float r19 = r19 - r20
            float r0 = (float) r3
            r20 = r0
            float r19 = r19 - r20
            float r5 = r5 + r19
            goto L_0x01b6
        L_0x025a:
            int r19 = r24.getHeight()
            r0 = r19
            float r0 = (float) r0
            r19 = r0
            float r5 = r5 + r19
            goto L_0x01b6
        L_0x0267:
            r19 = 1073741824(0x40000000, float:2.0)
            float r19 = r13 / r19
            float r11 = r5 - r19
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mTop
            r19 = r0
            r0 = r19
            android.support.constraint.solver.SolverVariable r0 = r0.mSolverVariable
            r19 = r0
            r20 = 1056964608(0x3f000000, float:0.5)
            float r20 = r20 + r11
            r0 = r20
            int r0 = (int) r0
            r20 = r0
            r0 = r22
            r1 = r19
            r2 = r20
            r0.addEquality(r1, r2)
            r0 = r24
            android.support.constraint.solver.widgets.ConstraintAnchor r0 = r0.mBottom
            r19 = r0
            r0 = r19
            android.support.constraint.solver.SolverVariable r0 = r0.mSolverVariable
            r19 = r0
            r20 = 1056964608(0x3f000000, float:0.5)
            float r20 = r20 + r11
            r0 = r20
            int r0 = (int) r0
            r20 = r0
            r0 = r22
            r1 = r19
            r2 = r20
            r0.addEquality(r1, r2)
            goto L_0x01dc
        L_0x02ab:
            r24 = 0
            goto L_0x01fe
        L_0x02af:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.Optimizer.applyDirectResolutionVerticalChain(android.support.constraint.solver.widgets.ConstraintWidgetContainer, android.support.constraint.solver.LinearSystem, int, android.support.constraint.solver.widgets.ConstraintWidget):void");
    }

    static void checkMatchParent(ConstraintWidgetContainer container, LinearSystem system, ConstraintWidget widget) {
        if (container.mHorizontalDimensionBehaviour != DimensionBehaviour.WRAP_CONTENT && widget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT) {
            widget.mLeft.mSolverVariable = system.createObjectVariable(widget.mLeft);
            widget.mRight.mSolverVariable = system.createObjectVariable(widget.mRight);
            int left = widget.mLeft.mMargin;
            int right = container.getWidth() - widget.mRight.mMargin;
            system.addEquality(widget.mLeft.mSolverVariable, left);
            system.addEquality(widget.mRight.mSolverVariable, right);
            widget.setHorizontalDimension(left, right);
            widget.mHorizontalResolution = 2;
        }
        if (container.mVerticalDimensionBehaviour != DimensionBehaviour.WRAP_CONTENT && widget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT) {
            widget.mTop.mSolverVariable = system.createObjectVariable(widget.mTop);
            widget.mBottom.mSolverVariable = system.createObjectVariable(widget.mBottom);
            int top = widget.mTop.mMargin;
            int bottom = container.getHeight() - widget.mBottom.mMargin;
            system.addEquality(widget.mTop.mSolverVariable, top);
            system.addEquality(widget.mBottom.mSolverVariable, bottom);
            if (widget.mBaselineDistance > 0 || widget.getVisibility() == 8) {
                widget.mBaseline.mSolverVariable = system.createObjectVariable(widget.mBaseline);
                system.addEquality(widget.mBaseline.mSolverVariable, widget.mBaselineDistance + top);
            }
            widget.setVerticalDimension(top, bottom);
            widget.mVerticalResolution = 2;
        }
    }

    static void checkHorizontalSimpleDependency(ConstraintWidgetContainer container, LinearSystem system, ConstraintWidget widget) {
        float position;
        int left;
        int right;
        if (widget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
            widget.mHorizontalResolution = 1;
        } else if (container.mHorizontalDimensionBehaviour != DimensionBehaviour.WRAP_CONTENT && widget.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT) {
            widget.mLeft.mSolverVariable = system.createObjectVariable(widget.mLeft);
            widget.mRight.mSolverVariable = system.createObjectVariable(widget.mRight);
            int left2 = widget.mLeft.mMargin;
            int right2 = container.getWidth() - widget.mRight.mMargin;
            system.addEquality(widget.mLeft.mSolverVariable, left2);
            system.addEquality(widget.mRight.mSolverVariable, right2);
            widget.setHorizontalDimension(left2, right2);
            widget.mHorizontalResolution = 2;
        } else if (widget.mLeft.mTarget == null || widget.mRight.mTarget == null) {
            if (widget.mLeft.mTarget != null && widget.mLeft.mTarget.mOwner == container) {
                int left3 = widget.mLeft.getMargin();
                int right3 = left3 + widget.getWidth();
                widget.mLeft.mSolverVariable = system.createObjectVariable(widget.mLeft);
                widget.mRight.mSolverVariable = system.createObjectVariable(widget.mRight);
                system.addEquality(widget.mLeft.mSolverVariable, left3);
                system.addEquality(widget.mRight.mSolverVariable, right3);
                widget.mHorizontalResolution = 2;
                widget.setHorizontalDimension(left3, right3);
            } else if (widget.mRight.mTarget != null && widget.mRight.mTarget.mOwner == container) {
                widget.mLeft.mSolverVariable = system.createObjectVariable(widget.mLeft);
                widget.mRight.mSolverVariable = system.createObjectVariable(widget.mRight);
                int right4 = container.getWidth() - widget.mRight.getMargin();
                int left4 = right4 - widget.getWidth();
                system.addEquality(widget.mLeft.mSolverVariable, left4);
                system.addEquality(widget.mRight.mSolverVariable, right4);
                widget.mHorizontalResolution = 2;
                widget.setHorizontalDimension(left4, right4);
            } else if (widget.mLeft.mTarget != null && widget.mLeft.mTarget.mOwner.mHorizontalResolution == 2) {
                SolverVariable target = widget.mLeft.mTarget.mSolverVariable;
                widget.mLeft.mSolverVariable = system.createObjectVariable(widget.mLeft);
                widget.mRight.mSolverVariable = system.createObjectVariable(widget.mRight);
                int left5 = (int) (target.computedValue + ((float) widget.mLeft.getMargin()) + 0.5f);
                int right5 = left5 + widget.getWidth();
                system.addEquality(widget.mLeft.mSolverVariable, left5);
                system.addEquality(widget.mRight.mSolverVariable, right5);
                widget.mHorizontalResolution = 2;
                widget.setHorizontalDimension(left5, right5);
            } else if (widget.mRight.mTarget == null || widget.mRight.mTarget.mOwner.mHorizontalResolution != 2) {
                boolean hasLeft = widget.mLeft.mTarget != null;
                boolean hasRight = widget.mRight.mTarget != null;
                if (!hasLeft && !hasRight) {
                    if (widget instanceof Guideline) {
                        Guideline guideline = (Guideline) widget;
                        if (guideline.getOrientation() == 1) {
                            widget.mLeft.mSolverVariable = system.createObjectVariable(widget.mLeft);
                            widget.mRight.mSolverVariable = system.createObjectVariable(widget.mRight);
                            if (guideline.getRelativeBegin() != -1) {
                                position = (float) guideline.getRelativeBegin();
                            } else if (guideline.getRelativeEnd() != -1) {
                                position = (float) (container.getWidth() - guideline.getRelativeEnd());
                            } else {
                                position = ((float) container.getWidth()) * guideline.getRelativePercent();
                            }
                            int value = (int) (0.5f + position);
                            system.addEquality(widget.mLeft.mSolverVariable, value);
                            system.addEquality(widget.mRight.mSolverVariable, value);
                            widget.mHorizontalResolution = 2;
                            widget.mVerticalResolution = 2;
                            widget.setHorizontalDimension(value, value);
                            widget.setVerticalDimension(0, container.getHeight());
                            return;
                        }
                        return;
                    }
                    widget.mLeft.mSolverVariable = system.createObjectVariable(widget.mLeft);
                    widget.mRight.mSolverVariable = system.createObjectVariable(widget.mRight);
                    int left6 = widget.getX();
                    int right6 = left6 + widget.getWidth();
                    system.addEquality(widget.mLeft.mSolverVariable, left6);
                    system.addEquality(widget.mRight.mSolverVariable, right6);
                    widget.mHorizontalResolution = 2;
                }
            } else {
                SolverVariable target2 = widget.mRight.mTarget.mSolverVariable;
                widget.mLeft.mSolverVariable = system.createObjectVariable(widget.mLeft);
                widget.mRight.mSolverVariable = system.createObjectVariable(widget.mRight);
                int right7 = (int) ((target2.computedValue - ((float) widget.mRight.getMargin())) + 0.5f);
                int left7 = right7 - widget.getWidth();
                system.addEquality(widget.mLeft.mSolverVariable, left7);
                system.addEquality(widget.mRight.mSolverVariable, right7);
                widget.mHorizontalResolution = 2;
                widget.setHorizontalDimension(left7, right7);
            }
        } else if (widget.mLeft.mTarget.mOwner == container && widget.mRight.mTarget.mOwner == container) {
            int leftMargin = widget.mLeft.getMargin();
            int rightMargin = widget.mRight.getMargin();
            if (container.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                left = leftMargin;
                right = container.getWidth() - rightMargin;
            } else {
                left = leftMargin + ((int) ((((float) (((container.getWidth() - leftMargin) - rightMargin) - widget.getWidth())) * widget.mHorizontalBiasPercent) + 0.5f));
                right = left + widget.getWidth();
            }
            widget.mLeft.mSolverVariable = system.createObjectVariable(widget.mLeft);
            widget.mRight.mSolverVariable = system.createObjectVariable(widget.mRight);
            system.addEquality(widget.mLeft.mSolverVariable, left);
            system.addEquality(widget.mRight.mSolverVariable, right);
            widget.mHorizontalResolution = 2;
            widget.setHorizontalDimension(left, right);
        } else {
            widget.mHorizontalResolution = 1;
        }
    }

    static void checkVerticalSimpleDependency(ConstraintWidgetContainer container, LinearSystem system, ConstraintWidget widget) {
        float position;
        int top;
        int bottom;
        if (widget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
            widget.mVerticalResolution = 1;
        } else if (container.mVerticalDimensionBehaviour != DimensionBehaviour.WRAP_CONTENT && widget.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_PARENT) {
            widget.mTop.mSolverVariable = system.createObjectVariable(widget.mTop);
            widget.mBottom.mSolverVariable = system.createObjectVariable(widget.mBottom);
            int top2 = widget.mTop.mMargin;
            int bottom2 = container.getHeight() - widget.mBottom.mMargin;
            system.addEquality(widget.mTop.mSolverVariable, top2);
            system.addEquality(widget.mBottom.mSolverVariable, bottom2);
            if (widget.mBaselineDistance > 0 || widget.getVisibility() == 8) {
                widget.mBaseline.mSolverVariable = system.createObjectVariable(widget.mBaseline);
                system.addEquality(widget.mBaseline.mSolverVariable, widget.mBaselineDistance + top2);
            }
            widget.setVerticalDimension(top2, bottom2);
            widget.mVerticalResolution = 2;
        } else if (widget.mTop.mTarget == null || widget.mBottom.mTarget == null) {
            if (widget.mTop.mTarget != null && widget.mTop.mTarget.mOwner == container) {
                int top3 = widget.mTop.getMargin();
                int bottom3 = top3 + widget.getHeight();
                widget.mTop.mSolverVariable = system.createObjectVariable(widget.mTop);
                widget.mBottom.mSolverVariable = system.createObjectVariable(widget.mBottom);
                system.addEquality(widget.mTop.mSolverVariable, top3);
                system.addEquality(widget.mBottom.mSolverVariable, bottom3);
                if (widget.mBaselineDistance > 0 || widget.getVisibility() == 8) {
                    widget.mBaseline.mSolverVariable = system.createObjectVariable(widget.mBaseline);
                    system.addEquality(widget.mBaseline.mSolverVariable, widget.mBaselineDistance + top3);
                }
                widget.mVerticalResolution = 2;
                widget.setVerticalDimension(top3, bottom3);
            } else if (widget.mBottom.mTarget != null && widget.mBottom.mTarget.mOwner == container) {
                widget.mTop.mSolverVariable = system.createObjectVariable(widget.mTop);
                widget.mBottom.mSolverVariable = system.createObjectVariable(widget.mBottom);
                int bottom4 = container.getHeight() - widget.mBottom.getMargin();
                int top4 = bottom4 - widget.getHeight();
                system.addEquality(widget.mTop.mSolverVariable, top4);
                system.addEquality(widget.mBottom.mSolverVariable, bottom4);
                if (widget.mBaselineDistance > 0 || widget.getVisibility() == 8) {
                    widget.mBaseline.mSolverVariable = system.createObjectVariable(widget.mBaseline);
                    system.addEquality(widget.mBaseline.mSolverVariable, widget.mBaselineDistance + top4);
                }
                widget.mVerticalResolution = 2;
                widget.setVerticalDimension(top4, bottom4);
            } else if (widget.mTop.mTarget != null && widget.mTop.mTarget.mOwner.mVerticalResolution == 2) {
                SolverVariable target = widget.mTop.mTarget.mSolverVariable;
                widget.mTop.mSolverVariable = system.createObjectVariable(widget.mTop);
                widget.mBottom.mSolverVariable = system.createObjectVariable(widget.mBottom);
                int top5 = (int) (target.computedValue + ((float) widget.mTop.getMargin()) + 0.5f);
                int bottom5 = top5 + widget.getHeight();
                system.addEquality(widget.mTop.mSolverVariable, top5);
                system.addEquality(widget.mBottom.mSolverVariable, bottom5);
                if (widget.mBaselineDistance > 0 || widget.getVisibility() == 8) {
                    widget.mBaseline.mSolverVariable = system.createObjectVariable(widget.mBaseline);
                    system.addEquality(widget.mBaseline.mSolverVariable, widget.mBaselineDistance + top5);
                }
                widget.mVerticalResolution = 2;
                widget.setVerticalDimension(top5, bottom5);
            } else if (widget.mBottom.mTarget != null && widget.mBottom.mTarget.mOwner.mVerticalResolution == 2) {
                SolverVariable target2 = widget.mBottom.mTarget.mSolverVariable;
                widget.mTop.mSolverVariable = system.createObjectVariable(widget.mTop);
                widget.mBottom.mSolverVariable = system.createObjectVariable(widget.mBottom);
                int bottom6 = (int) ((target2.computedValue - ((float) widget.mBottom.getMargin())) + 0.5f);
                int top6 = bottom6 - widget.getHeight();
                system.addEquality(widget.mTop.mSolverVariable, top6);
                system.addEquality(widget.mBottom.mSolverVariable, bottom6);
                if (widget.mBaselineDistance > 0 || widget.getVisibility() == 8) {
                    widget.mBaseline.mSolverVariable = system.createObjectVariable(widget.mBaseline);
                    system.addEquality(widget.mBaseline.mSolverVariable, widget.mBaselineDistance + top6);
                }
                widget.mVerticalResolution = 2;
                widget.setVerticalDimension(top6, bottom6);
            } else if (widget.mBaseline.mTarget == null || widget.mBaseline.mTarget.mOwner.mVerticalResolution != 2) {
                boolean hasBaseline = widget.mBaseline.mTarget != null;
                boolean hasTop = widget.mTop.mTarget != null;
                boolean hasBottom = widget.mBottom.mTarget != null;
                if (!hasBaseline && !hasTop && !hasBottom) {
                    if (widget instanceof Guideline) {
                        Guideline guideline = (Guideline) widget;
                        if (guideline.getOrientation() == 0) {
                            widget.mTop.mSolverVariable = system.createObjectVariable(widget.mTop);
                            widget.mBottom.mSolverVariable = system.createObjectVariable(widget.mBottom);
                            if (guideline.getRelativeBegin() != -1) {
                                position = (float) guideline.getRelativeBegin();
                            } else if (guideline.getRelativeEnd() != -1) {
                                position = (float) (container.getHeight() - guideline.getRelativeEnd());
                            } else {
                                position = ((float) container.getHeight()) * guideline.getRelativePercent();
                            }
                            int value = (int) (0.5f + position);
                            system.addEquality(widget.mTop.mSolverVariable, value);
                            system.addEquality(widget.mBottom.mSolverVariable, value);
                            widget.mVerticalResolution = 2;
                            widget.mHorizontalResolution = 2;
                            widget.setVerticalDimension(value, value);
                            widget.setHorizontalDimension(0, container.getWidth());
                            return;
                        }
                        return;
                    }
                    widget.mTop.mSolverVariable = system.createObjectVariable(widget.mTop);
                    widget.mBottom.mSolverVariable = system.createObjectVariable(widget.mBottom);
                    int top7 = widget.getY();
                    int bottom7 = top7 + widget.getHeight();
                    system.addEquality(widget.mTop.mSolverVariable, top7);
                    system.addEquality(widget.mBottom.mSolverVariable, bottom7);
                    if (widget.mBaselineDistance > 0 || widget.getVisibility() == 8) {
                        widget.mBaseline.mSolverVariable = system.createObjectVariable(widget.mBaseline);
                        system.addEquality(widget.mBaseline.mSolverVariable, widget.mBaselineDistance + top7);
                    }
                    widget.mVerticalResolution = 2;
                }
            } else {
                SolverVariable target3 = widget.mBaseline.mTarget.mSolverVariable;
                widget.mTop.mSolverVariable = system.createObjectVariable(widget.mTop);
                widget.mBottom.mSolverVariable = system.createObjectVariable(widget.mBottom);
                int top8 = (int) ((target3.computedValue - ((float) widget.mBaselineDistance)) + 0.5f);
                int bottom8 = top8 + widget.getHeight();
                system.addEquality(widget.mTop.mSolverVariable, top8);
                system.addEquality(widget.mBottom.mSolverVariable, bottom8);
                widget.mBaseline.mSolverVariable = system.createObjectVariable(widget.mBaseline);
                system.addEquality(widget.mBaseline.mSolverVariable, widget.mBaselineDistance + top8);
                widget.mVerticalResolution = 2;
                widget.setVerticalDimension(top8, bottom8);
            }
        } else if (widget.mTop.mTarget.mOwner == container && widget.mBottom.mTarget.mOwner == container) {
            int topMargin = widget.mTop.getMargin();
            int bottomMargin = widget.mBottom.getMargin();
            if (container.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
                top = topMargin;
                bottom = top + widget.getHeight();
            } else {
                top = (int) (((float) topMargin) + (((float) (((container.getHeight() - topMargin) - bottomMargin) - widget.getHeight())) * widget.mVerticalBiasPercent) + 0.5f);
                bottom = top + widget.getHeight();
            }
            widget.mTop.mSolverVariable = system.createObjectVariable(widget.mTop);
            widget.mBottom.mSolverVariable = system.createObjectVariable(widget.mBottom);
            system.addEquality(widget.mTop.mSolverVariable, top);
            system.addEquality(widget.mBottom.mSolverVariable, bottom);
            if (widget.mBaselineDistance > 0 || widget.getVisibility() == 8) {
                widget.mBaseline.mSolverVariable = system.createObjectVariable(widget.mBaseline);
                system.addEquality(widget.mBaseline.mSolverVariable, widget.mBaselineDistance + top);
            }
            widget.mVerticalResolution = 2;
            widget.setVerticalDimension(top, bottom);
        } else {
            widget.mVerticalResolution = 1;
        }
    }
}
