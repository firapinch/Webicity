package com.github.webicitybrowser.spiderhtml.context;

import java.util.function.Function;

import com.github.webicitybrowser.spec.html.parse.ParseError;
import com.github.webicitybrowser.spiderhtml.insertion.InsertionMode;
import com.github.webicitybrowser.spiderhtml.misc.EmitterLogic;
import com.github.webicitybrowser.spiderhtml.token.EOFToken;
import com.github.webicitybrowser.spiderhtml.token.EndTagToken;
import com.github.webicitybrowser.spiderhtml.token.StartTagToken;
import com.github.webicitybrowser.spiderhtml.token.Token;
import com.github.webicitybrowser.spiderhtml.tokenize.TokenizeState;

public class SharedContext {
	
	private final InsertionContext insertionContext;
	
	private TokenizeState tokenizeState;
	private InsertionMode insertionMode;
	private TokenizeState returnState;
	
	private String lastStartTagTokenName;
	
	public SharedContext(Function<SharedContext, InsertionContext> insertionContextFactory) {
		this.insertionContext = insertionContextFactory.apply(this);
	}
	
	public void setTokenizeState(TokenizeState tokenizeState) {
		this.tokenizeState = tokenizeState;
	}

	public TokenizeState getTokenizeState() {
		return this.tokenizeState;
	}
	
	public void setReturnState(TokenizeState returnState) {
		this.returnState = returnState;
	}
	
	public TokenizeState getReturnState() {
		return this.returnState;
	}
	
	public void setInsertionMode(InsertionMode insertionMode) {
		this.insertionMode = insertionMode;
	}
	
	public InsertionMode getInsertionMode() {
		return this.insertionMode;
	}
	
	public void emit(Token token) {
		if (token instanceof EOFToken) {
			setTokenizeState(null); // Just in case stopParsing is never called
		} else if (token instanceof StartTagToken startTagToken) {
			lastStartTagTokenName = startTagToken.getName(insertionContext.getStringCache());
		}
		EmitterLogic.emit(this, insertionContext, token);
		if (
			token instanceof StartTagToken startTagToken &&
			startTagToken.isSelfClosingTag() &&
			!startTagToken.hasAcknowledgedSelfClosingTag()
		) {
			recordError(ParseError.NON_VOID_HTML_ELEMENT_START_TAG_WITH_TRAILING_SOLIDUS);
		}
	}

	public void recordError(ParseError unexpectedNullCharacter) {
		// TODO Auto-generated method stub
	}

	public void recordError() {
		// TODO Auto-generated method stub
	}

	public boolean isAppropriateEndTagToken(EndTagToken endTagToken) {
		return endTagToken.getName(insertionContext.getStringCache())
			.equals(lastStartTagTokenName);
	}
	
}
