package curso.api.rest.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UsuarioDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String login;
	private String nome;
	private String senha;
	private String cpf;
	private Date dataNascimento;
	private List<Telefone> telefones = new ArrayList<Telefone>();

	public UsuarioDTO(Usuario usuario) {
		this.id = usuario.getId();
		this.login = usuario.getLogin();
		this.nome = usuario.getNome();
		this.senha = usuario.getSenha();
		this.cpf = usuario.getCpf();
		this.telefones = usuario.getTelefones();
		this.dataNascimento = usuario.getDataNascimento();
	}
	
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getSenha() {
		return senha;
	}


	public void setSenha(String senha) {
		this.senha = senha;
	}


	public String getCpf() {
		return cpf;
	}


	public void setCpf(String cpf) {
		this.cpf = cpf;
	}


	
	public Date getDataNascimento() {
		return dataNascimento;
	}


	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}


	public List<Telefone> getTelefones() {
		return telefones;
	}


	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}
	
	


	
}
