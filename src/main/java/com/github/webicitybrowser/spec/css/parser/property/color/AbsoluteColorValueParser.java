package com.github.webicitybrowser.spec.css.parser.property.color;

import com.github.webicitybrowser.spec.css.parser.TokenLike;
import com.github.webicitybrowser.spec.css.parser.property.PropertyValueParseResult;
import com.github.webicitybrowser.spec.css.parser.property.PropertyValueParser;
import com.github.webicitybrowser.spec.css.parser.property.imp.PropertyValueParseResultImp;
import com.github.webicitybrowser.spec.css.property.color.ColorValue;

public class AbsoluteColorValueParser implements PropertyValueParser<ColorValue> {

	@Override
	public PropertyValueParseResult<ColorValue> parse(TokenLike[] tokens, int offset, int length) {
		// TODO Auto-generated method stub
		return PropertyValueParseResultImp.empty();
	}

}
