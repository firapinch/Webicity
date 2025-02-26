package com.github.webicitybrowser.webicity.renderer.frontend.thready.html;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.github.webicitybrowser.spec.css.parser.CSSParser;
import com.github.webicitybrowser.spec.css.parser.tokenizer.CSSTokenizer;
import com.github.webicitybrowser.spec.css.parser.tokens.Token;
import com.github.webicitybrowser.spec.css.rule.CSSRule;
import com.github.webicitybrowser.spec.css.rule.CSSRuleList;
import com.github.webicitybrowser.spec.dom.node.Node;
import com.github.webicitybrowser.spec.html.node.HTMLDocument;
import com.github.webicitybrowser.thready.gui.directive.core.pool.DirectivePool;
import com.github.webicitybrowser.thready.gui.directive.core.style.StyleGeneratorRoot;
import com.github.webicitybrowser.thready.gui.graphical.base.GUIContent;
import com.github.webicitybrowser.thready.gui.graphical.base.imp.GUIContentImp;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.LookAndFeel;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.core.LookAndFeelBuilder;
import com.github.webicitybrowser.thready.gui.graphical.lookandfeel.simplelaf.SimpleLookAndFeel;
import com.github.webicitybrowser.thready.gui.tree.core.Component;
import com.github.webicitybrowser.thready.windowing.core.ScreenContent;
import com.github.webicitybrowser.threadyweb.graphical.lookandfeel.weblaf.WebLookAndFeel;
import com.github.webicitybrowser.threadyweb.tree.DocumentComponent;
import com.github.webicitybrowser.webicity.core.AssetLoader;
import com.github.webicitybrowser.webicity.core.renderer.RendererContext;
import com.github.webicitybrowser.webicity.renderer.backend.html.HTMLRendererBackend;
import com.github.webicitybrowser.webicity.renderer.backend.html.cssom.CSSOMTree;
import com.github.webicitybrowser.webicity.renderer.frontend.thready.core.ThreadyRendererFrontend;
import com.github.webicitybrowser.webicity.renderer.frontend.thready.html.style.cssbinding.CSSOMBinder;
import com.github.webicitybrowser.webicity.renderer.frontend.thready.html.style.generator.DocumentStyleGeneratorRoot;
import com.github.webicitybrowser.webicity.renderer.frontend.thready.html.style.generator.DocumentStyleSheetSet;

public class ThreadyHTMLRendererFrontend implements ThreadyRendererFrontend {

	private final HTMLRendererBackend backend;
	private final RendererContext rendererContext;
	private final ScreenContent content;

	public ThreadyHTMLRendererFrontend(HTMLRendererBackend backend, RendererContext rendererContext) {
		this.backend = backend;
		this.rendererContext = rendererContext;
		this.content = createContent();
	}

	@Override
	public ScreenContent getContent() {
		return content;
	}
	
	private ScreenContent createContent() {
		Component documentComponent = DocumentComponent.create(backend.getDocument());
		LookAndFeelBuilder lookAndFeelBuilder = LookAndFeelBuilder.create();
		SimpleLookAndFeel.installTo(lookAndFeelBuilder);
		WebLookAndFeel.installTo(lookAndFeelBuilder);
		LookAndFeel lookAndFeel = lookAndFeelBuilder.build();
		
		GUIContent content = new GUIContentImp();
		content.setRoot(documentComponent, lookAndFeel, createStyleGenerator());
		
		return content;
	}
	
	private StyleGeneratorRoot createStyleGenerator() {
		HTMLDocument document = backend.getDocument();
		DocumentStyleSheetSet styleSheetSet = new DocumentStyleSheetSet(document.getStyleSheets());
		styleSheetSet.addUARules(loadUAStylesheet());
		
		return new DocumentStyleGeneratorRoot(backend.getDocument(), () -> createCSSOMTrees(styleSheetSet));
	}

	private CSSRuleList loadUAStylesheet() {
		// TODO: Better error handling
		AssetLoader assetLoader = rendererContext.getAssetLoader();
		try (Reader reader = assetLoader.streamAsset("static", "renderer/html/ua.css")) {
			Token[] tokens = CSSTokenizer.create().tokenize(reader);
			CSSRule[] rules = CSSParser.create().parseAListOfRules(tokens);
			return CSSRuleList.create(rules);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private CSSOMTree<Node, DirectivePool>[] createCSSOMTrees(DocumentStyleSheetSet styleSheetSet) {
		List<CSSOMTree<Node, DirectivePool>> cssomTrees = new ArrayList<>();
		CSSOMBinder binder = CSSOMBinder.create();
		for (CSSRuleList rules: styleSheetSet.getRuleLists()) {
			cssomTrees.add(binder.createCSSOMFor(rules));
		}
		
		return cssomTrees.toArray(new CSSOMTree[0]);
	}

}
