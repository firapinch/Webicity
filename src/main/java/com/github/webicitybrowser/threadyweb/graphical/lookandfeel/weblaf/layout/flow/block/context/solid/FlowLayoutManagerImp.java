package com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.layout.flow.block.context.solid;

import java.util.List;

import com.github.webicitybrowser.thready.dimensions.AbsolutePosition;
import com.github.webicitybrowser.thready.dimensions.AbsoluteSize;
import com.github.webicitybrowser.thready.dimensions.Rectangle;
import com.github.webicitybrowser.thready.dimensions.RelativeDimension;
import com.github.webicitybrowser.thready.gui.graphical.directive.PositionDirective;
import com.github.webicitybrowser.thready.gui.graphical.directive.SizeDirective;
import com.github.webicitybrowser.thready.gui.graphical.layout.core.ChildLayoutResult;
import com.github.webicitybrowser.thready.gui.graphical.layout.core.LayoutResult;
import com.github.webicitybrowser.thready.gui.graphical.layout.core.SolidLayoutManager;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.base.stage.box.BasicAnonymousFluidBox;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.UIDisplay;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.pipeline.BoundBox;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.pipeline.BoundRenderedUnit;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.pipeline.BoundRenderedUnitGenerator;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.box.ChildrenBox;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.GlobalRenderContext;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.LocalRenderContext;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.unit.RenderedUnitGenerator;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.unit.RenderedUnitGenerator.GenerationResult;
import com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.layout.InnerDisplayUnit;
import com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.layout.flow.block.context.inline.FlowFluidRenderer;
import com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.ui.element.ElementDisplay;

public class FlowLayoutManagerImp implements SolidLayoutManager {

	private static final UIDisplay<?, ?, InnerDisplayUnit> ELEMENT_DISPLAY = new ElementDisplay();

	@Override
	public LayoutResult render(ChildrenBox box, GlobalRenderContext globalRenderContext, LocalRenderContext localRenderContext) {
		RenderCursorTracker renderCursor = new RenderCursorTracker();
		ChildLayoutResult[] childrenResults = renderChildren(box, globalRenderContext, localRenderContext, renderCursor);
		return LayoutResult.create(childrenResults, renderCursor.getCoveredSize());
	}

	private ChildLayoutResult[] renderChildren(
		ChildrenBox box, GlobalRenderContext globalRenderContext, LocalRenderContext localRenderContext, RenderCursorTracker renderCursor
	) {
		List<BoundBox<?, ?>> children = box.getChildrenTracker().getChildren();
		ChildLayoutResult[] results = new ChildLayoutResult[children.size()];
		for (int i = 0; i < children.size(); i++) {
			// TODO: Do we need to get the adjusted box tree?
			results[i] = renderChild(children.get(i), globalRenderContext, localRenderContext, renderCursor);
		}
		
		return results;
	}

	private ChildLayoutResult renderChild(
		BoundBox<?, ?> childBox, GlobalRenderContext globalRenderContext, LocalRenderContext localRenderContext, RenderCursorTracker renderCursor
	) {
		AbsoluteSize parentSize = localRenderContext.getPreferredSize();
		
		AbsoluteSize precomputedSize = precomputeChildSize(childBox, parentSize);
		LocalRenderContext childRenderContext = LocalRenderContext.create(precomputedSize, localRenderContext.getContextSwitches());
		
		BoundRenderedUnitGenerator<?> childUnitGenerator = childBox.getRaw() instanceof BasicAnonymousFluidBox ?
			renderAnonBox(childBox, globalRenderContext, childRenderContext) :
			childBox.render(globalRenderContext, childRenderContext);
		
		GenerationResult generationResult = childUnitGenerator.getRaw().generateNextUnit(precomputedSize, true);
		assert generationResult == GenerationResult.NORMAL && childUnitGenerator.getRaw().completed();
		BoundRenderedUnit<?> childUnit = childUnitGenerator.getLastGeneratedUnit();

		AbsoluteSize renderedSize = childUnit.getRaw().preferredSize();
		AbsoluteSize finalSize = computeFinalChildSize(renderedSize, precomputedSize, parentSize);
		
		AbsolutePosition computedPosition = computeNormalChildPosition(childBox, parentSize, finalSize, renderCursor);
		AbsolutePosition finalPosition = computedPosition;
		
		Rectangle renderedRectangle = new Rectangle(finalPosition, finalSize);
		
		return new ChildLayoutResult(renderedRectangle, childUnit);
	}

	private BoundRenderedUnitGenerator<?> renderAnonBox(BoundBox<?, ?> childBox, GlobalRenderContext globalRenderContext, LocalRenderContext localRenderContext) {
		BasicAnonymousFluidBox inlineBox = (BasicAnonymousFluidBox) childBox.getRaw();
		RenderedUnitGenerator<InnerDisplayUnit> renderedUnitGenerator = FlowFluidRenderer.render(inlineBox, globalRenderContext, localRenderContext);
		return BoundRenderedUnitGenerator.create(renderedUnitGenerator, ELEMENT_DISPLAY);
	}

	private AbsoluteSize precomputeChildSize(BoundBox<?, ?> childBox, AbsoluteSize parentSize) {
		return childBox
			.getRaw()
			.styleDirectives()
			.getDirectiveOrEmpty(SizeDirective.class)
			.map(directive -> directive.getSize())
			.map(relativeSize -> relativeSize.resolveAbsoluteSize(parentSize))
			.orElse(new AbsoluteSize(parentSize.width(), RelativeDimension.UNBOUNDED));
	}
	
	private AbsoluteSize computeFinalChildSize(AbsoluteSize renderedSize, AbsoluteSize precomputedSize, AbsoluteSize parentSize) {
		float widthComponent = precomputedSize.width() != RelativeDimension.UNBOUNDED ?
			precomputedSize.width() :
			Math.max(renderedSize.width(), parentSize.width());
		
		float heightComponent = precomputedSize.height() != RelativeDimension.UNBOUNDED ?
			precomputedSize.height() :
			renderedSize.height();
				
		return new AbsoluteSize(widthComponent, heightComponent);
	}
	
	private AbsolutePosition computeNormalChildPosition(
		BoundBox<?, ?> child, AbsoluteSize parentSize, AbsoluteSize finalSize, RenderCursorTracker renderCursor
	) {
		return child
			.getRaw()
			.styleDirectives()
			.getDirectiveOrEmpty(PositionDirective.class)
			.map(directive -> directive.getPosition())
			.map(relativePosition -> relativePosition.resolveAbsolutePosition(parentSize))
			.orElse(selectNextPosition(finalSize, renderCursor));
	}

	private AbsolutePosition selectNextPosition(AbsoluteSize finalSize, RenderCursorTracker renderCursor) {
		float yPos = renderCursor.getNextYPos();
		renderCursor.recordWidth(finalSize.width());
		renderCursor.increaseNextYPos(finalSize.height());
		
		return new AbsolutePosition(0, yPos);
	}

}
