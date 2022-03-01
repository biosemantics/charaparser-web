package edu.arizona.biosemantics.author.database.search;

import javax.persistence.*;

@Entity
@Table(name="disputes")

public class Dispute {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String label;

    private String definition;

    private String IRI;

    private String deprecated_reason;

    private String disputed_by;

    private String disputed_reason;
    
    private String new_definition;

    private String example_sentence;

    private String taxa;

    private String created_at;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getIRI() {
		return IRI;
	}

	public void setIRI(String iRI) {
		IRI = iRI;
	}

	public String getDeprecated_reason() {
		return deprecated_reason;
	}

	public void setDeprecated_reason(String deprecated_reason) {
		this.deprecated_reason = deprecated_reason;
	}

	public String getDisputed_by() {
		return disputed_by;
	}

	public void setDisputed_by(String disputed_by) {
		this.disputed_by = disputed_by;
	}

	public String getDisputed_reason() {
		return disputed_reason;
	}

	public void setDisputed_reason(String disputed_reason) {
		this.disputed_reason = disputed_reason;
	}

	public String getNew_definition() {
		return new_definition;
	}

	public void setNew_definition(String new_definition) {
		this.new_definition = new_definition;
	}

	public String getExample_sentence() {
		return example_sentence;
	}

	public void setExample_sentence(String example_sentence) {
		this.example_sentence = example_sentence;
	}

	public String getTaxa() {
		return taxa;
	}

	public void setTaxa(String taxa) {
		this.taxa = taxa;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
    
    
    
    
}
