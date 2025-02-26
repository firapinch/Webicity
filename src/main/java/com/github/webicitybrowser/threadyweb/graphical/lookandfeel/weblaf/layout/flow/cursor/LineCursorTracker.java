package com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.layout.flow.cursor;

import com.github.webicitybrowser.thready.dimensions.AbsolutePosition;
import com.github.webicitybrowser.thready.dimensions.AbsoluteSize;

public class LineCursorTracker implements CursorTracker {
	
	private final LineDimensionConverter dimensionConverter;
	
	private LineDimension currentSize = new LineDimension(0, 0);
	private LineDimension currentPointer = new LineDimension(0, 0);

	public LineCursorTracker(LineDimensionConverter dimensionConverter) {
		this.dimensionConverter = dimensionConverter;
	}
	
	@Override
	public void add(AbsoluteSize unitSize) {
		LineDimension unitLineSize = dimensionConverter.getLineDimension(unitSize);
		this.currentSize = lineSizeCoveredAfterAdd(unitLineSize);
		float newPointerX = currentPointer.run() + unitLineSize.run();
		this.currentPointer = new LineDimension(newPointerX, currentPointer.depth());
	}

	@Override
	public boolean addWillOverflowLine(AbsoluteSize unitSize, AbsoluteSize bounds) {
		LineDimension unitLineSize = dimensionConverter.getLineDimension(unitSize);
		LineDimension lineBounds = dimensionConverter.getLineDimension(bounds);
		if (unitLineSize.run() == -1) {
			return false;
		}
		return lineSizeCoveredAfterAdd(unitLineSize).run() > lineBounds.run();
	}
	
	@Override
	public AbsoluteSize getSizeCovered() {
		return dimensionConverter.getAbsoluteSize(currentSize);
	}

	@Override
	public AbsoluteSize sizeCoveredAfterAdd(AbsoluteSize unitSize) {
		LineDimension unitLineSize = dimensionConverter.getLineDimension(unitSize);
		LineDimension linzeSizeAfterAdd = lineSizeCoveredAfterAdd(unitLineSize);
		return dimensionConverter.getAbsoluteSize(linzeSizeAfterAdd);
	}

	@Override
	public AbsolutePosition getNextPosition() {
		return dimensionConverter.getAbsolutePosition(currentPointer);
	}

	@Override
	public void nextLine() {
		currentPointer = new LineDimension(0, currentSize.depth());
	}
	
	private LineDimension lineSizeCoveredAfterAdd(LineDimension unitLineSize) {
		float runComponent = Math.max(currentSize.run(), currentPointer.run() + unitLineSize.run());
		float depthComponent = Math.max(currentSize.depth(), currentPointer.depth() + unitLineSize.depth());
		return new LineDimension(runComponent, depthComponent);
	}

}
