package com.github.webicitybrowser.thready.gui.graphical.lookandfeel.base.stage.box;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import com.github.webicitybrowser.thready.gui.directive.core.DirectivePool;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.base.stage.box.imp.FluidRendererAdapter;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.box.Box;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.box.FluidBox;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.FluidRenderer;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.Renderer;

public class BasicFluidBox implements FluidBox {

	private final DirectivePool directives;
	private final BiFunction<Box, Box[], Renderer> rendererGenerator;
	
	private final List<Box> children = new ArrayList<>();
	
	public BasicFluidBox(DirectivePool directives, BiFunction<Box, Box[], Renderer> rendererGenerator) {
		this.directives = directives;
		this.rendererGenerator = rendererGenerator;
	}
	
	@Override
	public void addChild(Box child) {
		children.add(child);
	}

	@Override
	public Box[] getAdjustedBoxTree() {
		return new Box[] { this };
	}

	@Override
	public DirectivePool getStyleDirectives() {
		return this.directives;
	}

	@Override
	public FluidRenderer createRenderer() {
		Renderer renderer = rendererGenerator.apply(this, children.toArray(Box[]::new));
		return FluidRendererAdapter.createFor(renderer);
	}

}
