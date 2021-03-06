package demo.ontology;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import data.RDF.RDFTriple;

public class OntologyModel extends DefaultTableModel {
	private static final long serialVersionUID = 1L;
	
	private static final String[] columnNames = {"Subject", "Predicate", "Object"};

	/*********************/
	/**** コンストラクタ ****/
	/*********************/
	public OntologyModel() {
		super(columnNames, 0);
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void addTriple(String subject, String predicate, String object) {
		addRow(new String[]{subject, predicate, object});
	}
	
	public void addOntology(Model ontology) {
		StmtIterator itr = ontology.listStatements();
		while (itr.hasNext()) {
			Statement stmt = itr.next();
			String subject_sf = ontology.shortForm(stmt.getSubject().toString());
			String predicate_sf = ontology.shortForm(stmt.getPredicate().toString());
			String object_sf = ontology.shortForm(stmt.getObject().toString());
			addTriple(subject_sf, predicate_sf, object_sf);
		}
	}

	public String[] getRow(int rowNum) {
		return new String[]{
				(String) getValueAt(rowNum, RDFTriple.S),
				(String) getValueAt(rowNum, RDFTriple.P),
				(String) getValueAt(rowNum, RDFTriple.O)
				};
	}

	public List<String[]> getAllTable() {
		List<String[]> table = new LinkedList<String[]>();
		for(int r = 0; r<getRowCount(); r++) {
			table.add(getRow(r));
		}
		return table;
	}

	public void clear() {
		setRowCount(0);
	}

	/**
	 * トリプルの主語，述語，目的語のいずれか(s_p_oで指定)が入力文字列と一致するトリプルを集める.
	 * @param label 検索文字列
	 * @param s_p_o 主語，述語，目的語に対応する整数(0,1,2)
	 * @return
	 */
	private List<String[]> getCommonConcepts(String label, int s_p_o) {
		List<String[]> commonRowList = new LinkedList<String[]>();
		for(final String[] row: getAllTable()) {
			if(row[s_p_o].contains(label)) {	// 共通のsまたはpまたはoを含む行を集める
				commonRowList.add(row);
			}
		}
		return commonRowList;
	}
	public List<String[]> getPO(String subject) {
		return getCommonConcepts(subject, RDFTriple.S);
	}
	public List<String[]> getSO(String predicate) {
		return getCommonConcepts(predicate, RDFTriple.P);
	}
	public List<String[]> getSP(String object) {
		return getCommonConcepts(object, RDFTriple.O);
	}
}
