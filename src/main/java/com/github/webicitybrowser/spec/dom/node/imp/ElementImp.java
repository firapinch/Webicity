package com.github.webicitybrowser.spec.dom.node.imp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.webicitybrowser.spec.dom.node.Document;
import com.github.webicitybrowser.spec.dom.node.Element;
import com.github.webicitybrowser.spec.dom.node.Node;
import com.github.webicitybrowser.spec.dom.node.imp.util.InsertUtil;
import com.github.webicitybrowser.spec.dom.node.imp.util.StringifyUtil;
import com.github.webicitybrowser.spec.dom.node.support.NodeList;
import com.github.webicitybrowser.spec.dom.node.support.imp.NodeListImp;

public class ElementImp extends NodeImp implements Element {
	
	private final String namespace;
	private final String localTag;
	
	private final List<Node> childNodes = new ArrayList<>(1);
	private final Map<String, String> attributes = new LinkedHashMap<>(1);

	public ElementImp(Document nodeDocument, String namespace, String localTag) {
		super(nodeDocument);
		this.namespace = namespace;
		this.localTag = localTag;
	}
	
	@Override
	public String getNamespace() {
		return this.namespace;
	}

	@Override
	public String getLocalName() {
		return this.localTag;
	};
	
	@Override
	public String[] getAttributeNames() {
		return attributes.keySet().toArray(String[]::new);
	}
	
	@Override
	public void setAttribute(String name, String value) {
		attributes.put(name, value);
	}
	
	@Override
	public String getAttribute(String name) {
		return attributes.get(name);
	}
	
	@Override
	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}
	
	@Override
	public NodeList getChildNodes() {
		return new NodeListImp(childNodes);
	}
	
	@Override
	public void insertBefore(Node node, Node child) {
		// TODO: Run prechecks
		InsertUtil.insertBefore(childNodes, node, child);
	}

	@Override
	public void appendChild(Node node) {
		// TODO: Run prechecks
		childNodes.add(node);
	}
	
	// toString is useful for debugging. Not guaranteed to produce valid or correct HTML
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<");
		stringBuilder.append(localTag);
		appendAttributes(stringBuilder);
		stringBuilder.append(">\n");
		stringBuilder.append(StringifyUtil.serializeChildren(childNodes, "\t"));
		stringBuilder.append("\n</");
		stringBuilder.append(localTag);
		stringBuilder.append(">");
		return stringBuilder.toString();
	}

	private void appendAttributes(StringBuilder stringBuilder) {
		for (Entry<String, String> attribute: attributes.entrySet()) {
			stringBuilder.append(" ");
			stringBuilder.append(attribute.getKey());
			stringBuilder.append("=\"");
			stringBuilder.append(attribute.getValue());
			stringBuilder.append("\"");
		}
	}
	
}
