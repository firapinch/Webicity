package com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.display.wrapper;

import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.paint.GlobalPaintContext;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.paint.LocalPaintContext;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.unit.RenderedUnit;

public final class WebWrapperPainter {

	private WebWrapperPainter() {}

	public static <V extends RenderedUnit> void paint(WebWrapperUnit<V> unit, GlobalPaintContext globalPaintContext, LocalPaintContext localPaintContext) {
		WebWrapperBackgroundPainter.paint(unit, localPaintContext);
		unit.innerUnit().paint(globalPaintContext, localPaintContext);
	}
	
}
