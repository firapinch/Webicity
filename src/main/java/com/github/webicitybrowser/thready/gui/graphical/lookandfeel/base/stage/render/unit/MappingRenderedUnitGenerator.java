package com.github.webicitybrowser.thready.gui.graphical.lookandfeel.base.stage.render.unit;

import java.util.function.Function;

import com.github.webicitybrowser.thready.dimensions.AbsoluteSize;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.unit.RenderedUnit;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.unit.RenderedUnitGenerator;

public class MappingRenderedUnitGenerator<T extends RenderedUnit, U extends RenderedUnit> implements RenderedUnitGenerator<U> {

	private final RenderedUnitGenerator<T> backingGenerator;
	private final Function<T, U> mapper;

	public MappingRenderedUnitGenerator(RenderedUnitGenerator<T> backingGenerator, Function<T, U> mapper) {
		if (backingGenerator == null) throw new RuntimeException();
		this.backingGenerator = backingGenerator;
		this.mapper = mapper;
	}
	
	@Override
	public U generateNextUnit(AbsoluteSize preferredBounds, boolean forceFit) {
		T unit = backingGenerator.generateNextUnit(preferredBounds, forceFit);
		if (unit == null) {
			return null;
		}
		return mapper.apply(unit);
	}

	@Override
	public boolean completed() {
		return backingGenerator.completed();
	}

}
