package curso.api.rest.model;

import java.io.Serializable;

public class TelefoneDTO implements Serializable{

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String numero;
	
	
	public TelefoneDTO(Telefone telefone) {
		this.id = telefone.getId();
		this.numero = telefone.getNumero();		
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}
	

	
}
