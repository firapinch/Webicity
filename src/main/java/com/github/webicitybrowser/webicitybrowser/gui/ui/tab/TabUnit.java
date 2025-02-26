package com.github.webicitybrowser.webicitybrowser.gui.ui.tab;

import com.github.webicitybrowser.thready.dimensions.AbsoluteSize;
import com.github.webicitybrowser.thready.drawing.core.text.Font2D;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.render.unit.RenderedUnit;

public record TabUnit(AbsoluteSize preferredSize, TabBox box, Font2D font) implements RenderedUnit {

}
