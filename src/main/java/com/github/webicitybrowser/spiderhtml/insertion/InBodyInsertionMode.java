package com.github.webicitybrowser.spiderhtml.insertion;

import java.util.function.Consumer;

import com.github.webicitybrowser.spec.dom.node.Element;
import com.github.webicitybrowser.spec.dom.node.Node;
import com.github.webicitybrowser.spec.infra.Namespace;
import com.github.webicitybrowser.spiderhtml.context.InsertionContext;
import com.github.webicitybrowser.spiderhtml.context.ParsingInitializer;
import com.github.webicitybrowser.spiderhtml.context.SharedContext;
import com.github.webicitybrowser.spiderhtml.misc.ElementStack;
import com.github.webicitybrowser.spiderhtml.misc.ElementUtil;
import com.github.webicitybrowser.spiderhtml.misc.InsertionLogic;
import com.github.webicitybrowser.spiderhtml.misc.StackLogic;
import com.github.webicitybrowser.spiderhtml.token.CharacterToken;
import com.github.webicitybrowser.spiderhtml.token.CommentToken;
import com.github.webicitybrowser.spiderhtml.token.EOFToken;
import com.github.webicitybrowser.spiderhtml.token.EndTagToken;
import com.github.webicitybrowser.spiderhtml.token.StartTagToken;
import com.github.webicitybrowser.spiderhtml.token.Token;

public class InBodyInsertionMode implements InsertionMode {
	
	private final AfterBodyInsertionMode afterBodyInsertionMode;
	private final InHeadInsertionMode inHeadInsertionMode;
	private final InTableInsertionMode inTableInsertionMode;

	public InBodyInsertionMode(ParsingInitializer initializer, Consumer<InsertionMode> callback) {
		callback.accept(this);
		this.afterBodyInsertionMode = initializer.getInsertionMode(AfterBodyInsertionMode.class);
		this.inHeadInsertionMode = initializer.getInsertionMode(InHeadInsertionMode.class);
		this.inTableInsertionMode = initializer.getInsertionMode(InTableInsertionMode.class);
	}

	@Override
	public void emit(SharedContext context, InsertionContext insertionContext, Token token) {
		// TODO
		if (token instanceof CharacterToken characterToken) {
			handleCharacter(context, insertionContext, characterToken);
		} else if (token instanceof CommentToken commentToken) {
			handleCommentToken(context, insertionContext, commentToken);
		} else if (token instanceof StartTagToken startTagToken) {
			handleStartTag(context, insertionContext, startTagToken);
		} else if (token instanceof EndTagToken endTagToken) {
			handleEndTag(context, insertionContext, endTagToken);
		} else if (token instanceof EOFToken) {
			insertionContext.stopParsing();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private void handleCharacter(SharedContext context, InsertionContext insertionContext, CharacterToken characterToken) {
		int ch = characterToken.getCharacter();
		if (characterToken.getCharacter() == 0) {
			context.recordError();
		}
		// TODO: Handle any active formatting elements, and unset frameset-ok
		InsertionLogic.insertCharacters(context, insertionContext, new int[] { ch });
	}
	
	private void handleCommentToken(SharedContext context, InsertionContext insertionContext, CommentToken commentToken) {
		InsertionLogic.insertComment(insertionContext, commentToken, null);
	}
	
	private void handleStartTag(SharedContext context, InsertionContext insertionContext, StartTagToken token) {
		// TODO
		switch (token.getName(insertionContext.getStringCache())) {
		case "meta":
		case "script":
		case "style":
		case "title":
			inHeadInsertionMode.emit(context, insertionContext, token);
			break;
		case "div":
			handleMiscNoPStartTag(context, insertionContext, token);
			break;
		case "table":
			handleTableStartTag(context, insertionContext, token);
			break;
		case "area":
		case "br":
		case"embed":
		case "img":
		case "keygen":
		case "wbr":
			handleSelfClosingStartTag(context, insertionContext, token);
			break;
		default:
			handleOrdinaryElementStartTag(context, insertionContext, token);
		}
	}

	private void handleMiscNoPStartTag(SharedContext context, InsertionContext insertionContext, StartTagToken token) {
		// TODO: Close p if in button scope
		InsertionLogic.insertHTMLElement(insertionContext, token);
	}
	
	private void handleSelfClosingStartTag(SharedContext context, InsertionContext insertionContext, StartTagToken token) {
		// TODO: Handle any active formatting elements
		InsertionLogic.insertHTMLElement(insertionContext, token);
		insertionContext.getOpenElementStack().pop();
		if (token.isSelfClosingTag()) {
			token.acknowledgeSelfClosingTag();
		}
		// TODO: Set frameset-ok flag
	}
	
	private void handleTableStartTag(SharedContext context, InsertionContext insertionContext, StartTagToken token) {
		// TODO: Close a p element if not quirks
		InsertionLogic.insertHTMLElement(insertionContext, token);
		// TODO: Set frameset-ok flag
		context.setInsertionMode(inTableInsertionMode);
	}
	
	private void handleOrdinaryElementStartTag(SharedContext context, InsertionContext insertionContext, StartTagToken token) {
		// TODO: Handle any active formatting elements
		InsertionLogic.insertHTMLElement(insertionContext, token);
	}

	private void handleEndTag(SharedContext context, InsertionContext insertionContext, EndTagToken token) {
		// TODO
		switch (token.getName(insertionContext.getStringCache())) {
		case "body":
			handleBodyEndTag(context, insertionContext, token);
			break;
		case "div":
			handleMiscNoPEndTag(context, insertionContext, token);
			break;
		default:
			handleOrdinaryElementEndTag(context, insertionContext, token);
		}
	}

	private void handleBodyEndTag(SharedContext context, InsertionContext insertionContext, EndTagToken token) {
		// TODO: Parse errors
		context.setInsertionMode(afterBodyInsertionMode);
	}
	
	private void handleMiscNoPEndTag(SharedContext context, InsertionContext insertionContext, EndTagToken token) {
		String tokenName = token.getName(insertionContext.getStringCache());
		ElementStack stack = insertionContext.getOpenElementStack();
		// TODO: Ensure element in scope
		StackLogic.generateImpliedEndTags(stack, tokenName);
		if (!ElementUtil.isHTMLElementWithName(stack.peek(), tokenName)) {
			context.recordError();
		}
		StackLogic.popUntil(
			insertionContext.getOpenElementStack(),
			Namespace.HTML_NAMESPACE,
			token.getName(insertionContext.getStringCache()));
	}
	
	private void handleOrdinaryElementEndTag(SharedContext context, InsertionContext insertionContext, EndTagToken token) {
		ElementStack stack = insertionContext.getOpenElementStack();
		String tokenName = token.getName(insertionContext.getStringCache());
		for (int pos = 0; pos < stack.size(); pos++) {
			Node node = stack.peek(pos);
			if (ElementUtil.isHTMLElementWithName(node, tokenName)) {
				StackLogic.generateImpliedEndTags(stack, tokenName);
				if (!stack.peek().equals(node)) {
					context.recordError();
				}
				while (!stack.pop().equals(node));
				break;
			} else if (node instanceof Element element && ElementUtil.isSpecial(element)) {
				context.recordError();
				break;
			}
		}
	}

}
