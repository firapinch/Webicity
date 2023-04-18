package com.github.webicitybrowser.thready.gui.graphical.base.imp;

import com.github.webicitybrowser.thready.dimensions.AbsolutePosition;
import com.github.webicitybrowser.thready.dimensions.AbsoluteSize;
import com.github.webicitybrowser.thready.dimensions.Rectangle;
import com.github.webicitybrowser.thready.drawing.core.Canvas2D;
import com.github.webicitybrowser.thready.drawing.core.ResourceLoader;
import com.github.webicitybrowser.thready.gui.directive.core.StyleGenerator;
import com.github.webicitybrowser.thready.gui.directive.core.StyleGeneratorRoot;
import com.github.webicitybrowser.thready.gui.graphical.base.GUIContent;
import com.github.webicitybrowser.thready.gui.graphical.base.InvalidationLevel;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.ComponentUI;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.LookAndFeel;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.box.Box;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.box.BoxContext;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.RenderContext;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.Renderer;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.unit.Unit;
import com.github.webicitybrowser.thready.gui.tree.core.Component;

public class GUIContentImp implements GUIContent {
	
	private final ResourceLoader resourceLoader;
	
	private InvalidationLevel invalidationLevel = InvalidationLevel.NONE;
	
	private ComponentUI rootUI;
	private LookAndFeel lookAndFeel;
	private StyleGeneratorRoot styleGeneratorRoot;
	
	private boolean redrawRequested = false;
	private Box rootBox;
	private Unit rootUnit;
	
	public GUIContentImp(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setRoot(Component component, LookAndFeel lookAndFeel, StyleGeneratorRoot styleGeneratorRoot) {
		this.redrawRequested = true;
		this.rootUI = createRootUI(component, lookAndFeel);
		this.lookAndFeel = lookAndFeel;
		this.styleGeneratorRoot = styleGeneratorRoot;
		this.invalidationLevel = InvalidationLevel.BOX;
	}

	@Override
	public boolean redrawRequested() {
		return this.redrawRequested;
	}

	@Override
	public void redraw(Canvas2D canvas, AbsoluteSize size) {
		this.redrawRequested = false;
		if (rootUI != null && lookAndFeel != null) {
			performRenderPipeline(canvas, size);
		}
	}
	
	private ComponentUI createRootUI(Component component, LookAndFeel lookAndFeel) {
		ComponentUI dummyUI = new RootUI() {};
		
		return lookAndFeel.createUIFor(component, dummyUI);
	}

	private void performRenderPipeline(Canvas2D canvas, AbsoluteSize contentSize) {
		if (invalidationLevel == InvalidationLevel.BOX) {
			performBoxCycle();
		}
		if (this.rootBox == null) {
			return;
		}
		
		switch (invalidationLevel) {
		case BOX:
		case RENDER:
			performRenderCycle(contentSize);
		case COMPOSITE:
		case PAINT:
		case NONE:
			// Even if the invalidation level is NONE, there is
			// probably a reason that redraw was called.
			// For example, if a buffer must be drawn twice.
			performPaintCycle(canvas, contentSize);
			invalidationLevel = InvalidationLevel.NONE;
			break;
		default:
			// TODO: Log when this case is reached
			invalidationLevel = InvalidationLevel.NONE;
		}
	}

	private void performBoxCycle() {
		BoxContext context = new BoxContextImp(lookAndFeel);
		StyleGenerator styleGenerator = styleGeneratorRoot.generateChildStyleGenerator(rootUI);
		Box[] generatedBoxes = rootUI.generateBoxes(context, null, styleGenerator);
		if (generatedBoxes.length == 0) {
			this.rootBox = null;
			return;
		}
		Box[] adjustedBoxes = generatedBoxes[0].getAdjustedBoxTree();
		if (adjustedBoxes.length == 0) {
			this.rootBox = null;
			return;
		}
		Box rootBox = adjustedBoxes[0];
		this.rootBox = rootBox;
	}

	private void performRenderCycle(AbsoluteSize contentSize) {
		RenderContext renderContext = new RenderContextImp(resourceLoader);
		Renderer rootRenderer = rootBox.createRenderer();
		this.rootUnit = rootRenderer.render(renderContext, contentSize);
	}
	
	private void performPaintCycle(Canvas2D canvas, AbsoluteSize contentSize) {
		clearPaint(canvas, contentSize);
		
		rootUnit.getPainter(createDocumentRect(contentSize))
			.paint(new PaintContextImp(), canvas);
	}

	private void clearPaint(Canvas2D canvas, AbsoluteSize contentSize) {
		// TODO: What if we have a transparent window?
		// The default paint is white
		canvas.drawRect(0, 0, contentSize.width(), contentSize.height());
	}
	
	private Rectangle createDocumentRect(AbsoluteSize windowSize) {
		return new Rectangle(new AbsolutePosition(0, 0), windowSize);
	}
	
}
