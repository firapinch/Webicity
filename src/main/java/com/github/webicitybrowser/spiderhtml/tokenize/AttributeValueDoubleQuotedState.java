package com.github.webicitybrowser.spiderhtml.tokenize;

import java.util.function.Consumer;

import com.github.webicitybrowser.spiderhtml.context.ParsingContext;
import com.github.webicitybrowser.spiderhtml.context.ParsingInitializer;
import com.github.webicitybrowser.spiderhtml.context.SharedContext;
import com.github.webicitybrowser.spiderhtml.token.StartTagToken;

public class AttributeValueDoubleQuotedState implements TokenizeState {

	private final CharacterReferenceState characterReferenceState;
	private final AfterAttributeValueQuotedState afterAttributeValueQuotedState;

	public AttributeValueDoubleQuotedState(ParsingInitializer initializer, Consumer<TokenizeState> callback) {
		callback.accept(this);
		this.afterAttributeValueQuotedState = initializer.getTokenizeState(AfterAttributeValueQuotedState.class);
		this.characterReferenceState = initializer.getTokenizeState(CharacterReferenceState.class);
	}
	
	@Override
	public void process(SharedContext context, ParsingContext parsingContext, int ch) {
		// TODO
		switch (ch) {
		case '"':
			context.setTokenizeState(afterAttributeValueQuotedState);
			break;
		case '&':
			context.setReturnState(this);
			context.setTokenizeState(characterReferenceState);
			break;
		default:
			parsingContext.getCurrentToken(StartTagToken.class)
				.appendToAttributeValue(ch);
		}
	}

}
