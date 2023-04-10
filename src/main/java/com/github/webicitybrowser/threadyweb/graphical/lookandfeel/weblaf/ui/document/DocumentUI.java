package com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.ui.document;

import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.ComponentUI;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.box.Box;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.box.BoxContext;
import com.github.webicitybrowser.thready.gui.tree.core.Component;
import com.github.webicitybrowser.threadyweb.tree.DocumentComponent;
import com.github.webicitybrowser.threadyweb.tree.ElementComponent;

public class DocumentUI implements ComponentUI {
	
	private final DocumentComponent component;
	
	private ComponentUI childComponentUI;

	public DocumentUI(Component component, ComponentUI parent) {
		this.component = (DocumentComponent) component;
	}

	@Override
	public DocumentComponent getComponent() {
		return this.component;
	}

	@Override
	public Box[] generateBoxes(BoxContext context) {
		return component
			.getVisibleChild()
			.map(child -> getComponentUI(context, child))
			.map(ui -> ui.generateBoxes(context))
			.orElse(new Box[0]);
	}

	private ComponentUI getComponentUI(BoxContext context, ElementComponent child) {
		if (childComponentUI == null || childComponentUI.getComponent() != child) {
			this.childComponentUI = context.getLookAndFeel().createUIFor(child, this);
		}
		
		return this.childComponentUI;
	}

}
