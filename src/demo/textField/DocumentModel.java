package demo.textField;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.parser.ParserDelegator;

import cabocha.Cabocha;
import grammar.pattern.WordPattern;
import grammar.sentence.Sentence;
import grammar.word.Word;
import modules.syntacticParse.CabochaDecoder;

public class DocumentModel extends AbstractDocumentModel{

	private static final long serialVersionUID = 1L;

	private HTMLDocument htmlDoc;		// plainDocumentからHTMLに切り替える

	private Cabocha cabocha;

	private static WordPattern noun = new WordPattern("名詞");
	private static String defaultPlainText = "この文章はデフォルトテキストです。";
	private static String defaultHTMLTags =
			"<head>default head</head>"
			+ "<body id=\"body\">"
			+ "</body>";


	public DocumentModel() {
		super();
		htmlDoc = new HTMLDocument();
		cabocha = new Cabocha();
		htmlDoc.setParser(new ParserDelegator());

		try {
			insertString(0, defaultPlainText, new SimpleAttributeSet());
			htmlDoc.setInnerHTML(htmlDoc.getDefaultRootElement(), defaultHTMLTags);
		} catch (BadLocationException | IOException e) {
			e.printStackTrace();
		}
	}

	public List<Sentence> getSentences(String plainTexts) {
		List<String> texts = Stream.of(plainTexts.split("\n"))
				.collect(Collectors.toList());
		// PlainTextを改行を境に分解して解析
		return new CabochaDecoder().decodeProcessOutput(cabocha.parse(texts));
	}
	private void plain2html() {
		List<Sentence> sentenceList = new ArrayList<>();
		try {
			sentenceList = getSentences(this.getText(0, this.getLength()));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		for(final Sentence sentence : sentenceList) {
			String htmlText = "<p>";
			//htmlText += "<font size=\"+1\">";

			for(final Word word : sentence.words()) {	// 文の単語を走査
				htmlText += (noun.matches(word))	// 名詞ならアンカータグで囲む
						? "<a href=\"" + word.name() + "\">" + word.name() + "</a>"
						: word.name();
			}
			//htmlText += "</font>";
			htmlText += "</p><br>\n";				// 文末で改行
			try {
				htmlDoc.insertBeforeEnd(htmlDoc.getElement("body"), htmlText);
			} catch (BadLocationException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public HTMLDocument getHtmlDoc() {
		plain2html();
		return htmlDoc;
	}
	public void setHtmlDoc(HTMLDocument htmlDoc) {
		this.htmlDoc = htmlDoc;
	}


}
