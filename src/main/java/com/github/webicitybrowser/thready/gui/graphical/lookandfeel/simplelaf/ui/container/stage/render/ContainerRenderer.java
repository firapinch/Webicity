package com.github.webicitybrowser.thready.gui.graphical.lookandfeel.simplelaf.ui.container.stage.render;

import java.util.function.Function;

import com.github.webicitybrowser.thready.dimensions.AbsoluteSize;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.box.Box;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.box.FluidBox;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.RenderContext;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.Renderer;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.unit.Unit;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.simplelaf.stage.render.unit.BlockWrappingUnit;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.simplelaf.ui.container.stage.render.fluid.ContainerFluidRenderer;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.simplelaf.ui.container.stage.render.solid.ContainerSolidRenderer;

public class ContainerRenderer implements Renderer {

	private final Box box;
	private final Box[] children;

	public ContainerRenderer(Box box, Box[] children) {
		this.box = box;
		this.children = children;
	}

	@Override
	public Unit render(RenderContext renderContext, AbsoluteSize precomputedSize) {
		Function<AbsoluteSize, Unit> innerUnitGenerator =
				precomputedInnerSize -> renderChildren(renderContext, precomputedInnerSize);
		return BlockWrappingUnit.render(box, precomputedSize, innerUnitGenerator);
	}
	
	private Unit renderChildren(RenderContext renderContext, AbsoluteSize precomputedInnerSize) {
		if (children.length == 0 || !(children[0] instanceof FluidBox)) {
			return ContainerSolidRenderer.render(renderContext, precomputedInnerSize, children);
		} else {
			return ContainerFluidRenderer.render(renderContext, precomputedInnerSize, children);
		}
	}
}
