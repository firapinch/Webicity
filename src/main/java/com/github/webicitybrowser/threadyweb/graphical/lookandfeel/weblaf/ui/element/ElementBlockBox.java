package com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.ui.element;

import com.github.webicitybrowser.thready.gui.directive.core.pool.DirectivePool;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.base.stage.box.SolidBoxChildrenTracker;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.stage.box.BoundBoxChildrenTracker;
import com.github.webicitybrowser.thready.gui.tree.core.Component;
import com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.layout.InnerDisplayLayout;
import com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.util.WebDefaults;

public class ElementBlockBox implements ElementBox {
	
	private final Component owningComponent;
	private final DirectivePool styleDirectives;
	private final BoundBoxChildrenTracker childrenTracker;
	private final InnerDisplayLayout layout;

	public ElementBlockBox(Component owningComponent, DirectivePool styleDirectives, InnerDisplayLayout layout) {
		this.owningComponent = owningComponent;
		this.styleDirectives = styleDirectives;
		this.childrenTracker = new SolidBoxChildrenTracker(this, WebDefaults.INLINE_DISPLAY);
		this.layout = layout;
	}
	
	@Override
	public Component owningComponent() {
		return this.owningComponent;
	}

	@Override
	public DirectivePool styleDirectives() {
		return this.styleDirectives;
	}

	@Override
	public BoundBoxChildrenTracker getChildrenTracker() {
		return this.childrenTracker;
	}
	
	@Override
	public InnerDisplayLayout layout() {
		return this.layout;
	}

}
