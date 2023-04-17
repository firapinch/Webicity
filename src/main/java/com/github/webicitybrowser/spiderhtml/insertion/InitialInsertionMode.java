package com.github.webicitybrowser.spiderhtml.insertion;

import java.util.function.Consumer;

import com.github.webicitybrowser.spec.dom.node.DocumentType;
import com.github.webicitybrowser.spec.html.parse.HTMLTreeBuilder;
import com.github.webicitybrowser.spiderhtml.context.InsertionContext;
import com.github.webicitybrowser.spiderhtml.context.ParsingInitializer;
import com.github.webicitybrowser.spiderhtml.context.SharedContext;
import com.github.webicitybrowser.spiderhtml.token.CharacterToken;
import com.github.webicitybrowser.spiderhtml.token.DoctypeToken;
import com.github.webicitybrowser.spiderhtml.token.Token;
import com.github.webicitybrowser.spiderhtml.util.ASCIIUtil;

public class InitialInsertionMode implements InsertionMode {

	private final BeforeHTMLInsertionMode beforeHTMLInsertionMode;

	public InitialInsertionMode(ParsingInitializer initializer, Consumer<InsertionMode> callback) {
		callback.accept(this);
		this.beforeHTMLInsertionMode = initializer.getInsertionMode(BeforeHTMLInsertionMode.class);
	}
	
	@Override
	public void emit(SharedContext context, InsertionContext insertionContext, Token token) {
		if (token instanceof CharacterToken characterToken) {
			handleCharacterToken(characterToken);
		} else if (token instanceof DoctypeToken doctypeToken) {
			handleDoctypeToken(context, insertionContext, doctypeToken);
		} else {
			// TODO
			context.setInsertionMode(beforeHTMLInsertionMode);
			context.emit(token);
		}
	}

	private void handleCharacterToken(CharacterToken characterToken) {
		if (ASCIIUtil.isASCIIWhiteSpace(characterToken.getCharacter())) {
			return;
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private void handleDoctypeToken(SharedContext context, InsertionContext insertionContext, DoctypeToken token) {
		// TODO
		HTMLTreeBuilder treeBuilder = insertionContext.getTreeBuilder();
		DocumentType doctypeNode = treeBuilder.createDocumentType(token.getName(), "", "");
		treeBuilder.getDocument().appendChild(doctypeNode);
		
		context.setInsertionMode(beforeHTMLInsertionMode);
	}

}
