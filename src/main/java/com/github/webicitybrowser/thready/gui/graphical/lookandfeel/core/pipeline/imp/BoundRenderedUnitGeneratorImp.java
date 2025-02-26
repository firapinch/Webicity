package com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.pipeline.imp;

import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.UIDisplay;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.pipeline.BoundRenderedUnit;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.pipeline.BoundRenderedUnitGenerator;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.unit.RenderedUnit;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.unit.RenderedUnitGenerator;

public class BoundRenderedUnitGeneratorImp<V extends RenderedUnit> implements BoundRenderedUnitGenerator<V> {

	private final RenderedUnitGenerator<V> unitGenerator;
	private final UIDisplay<?, ?, V> display;

	public BoundRenderedUnitGeneratorImp(RenderedUnitGenerator<V> unitGenerator, UIDisplay<?, ?, V> display) {
		this.unitGenerator = unitGenerator;
		this.display = display;
	}
	
	@Override
	public RenderedUnitGenerator<V> getRaw() {
		return this.unitGenerator;
	}

	@Override
	public BoundRenderedUnit<V> getLastGeneratedUnit() {
		return BoundRenderedUnit.create(
			unitGenerator.getLastGeneratedUnit(),
			display);
	}

}
