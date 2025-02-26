package com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.ui.text;

import com.github.webicitybrowser.thready.gui.graphical.base.InvalidationLevel;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.ComponentUI;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.UIDisplay;
import com.github.webicitybrowser.thready.gui.tree.core.Component;
import com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.display.wrapper.WebWrapperDisplay;

public class TextUI implements ComponentUI {
	
	private static final UIDisplay<?, ?, ?> TEXT_DISPLAY = new WebWrapperDisplay<>(new TextDisplay());
	
	private final Component component;
	private final ComponentUI parent;
	
	public TextUI(Component component, ComponentUI parent) {
		this.component = component;
		this.parent = parent;
	}

	@Override
	public Component getComponent() {
		return this.component;
	}

	@Override
	public void invalidate(InvalidationLevel level) {
		parent.invalidate(level);
	}
	
	@Override
	public UIDisplay<?, ?, ?> getRootDisplay() {
		return TEXT_DISPLAY;
	}

}
