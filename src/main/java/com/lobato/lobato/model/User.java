package com.lobato.lobato.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Document(collection = "users")
public class User implements UserDetails {
    
    @Id
    private String id;
    
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 20, message = "Username precisa ter mais de 3 caracteres")
    @Indexed(unique = true)
    private String username;
    
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "o e-mail precisa ser válido")
    @Indexed(unique = true)
    private String email;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;
    
    // Campos pessoais
    private String nome;
    
    private String telefone;
    
    private String cpf;
    
    private String rg;
    
    // Campos de endereço
    private String logradouro;
    private String cep;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    
    // Campos para documentos (armazenados como dados binários)
    private byte[] comprovanteResidencia;
    private String comprovanteResidenciaNome;
    private String comprovanteResidenciaContentType;
    
    private byte[] comprovanteDocumento;
    private String comprovanteDocumentoNome;
    private String comprovanteDocumentoContentType;
    
    private Set<String> roles;
    
    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    
    // Constructors
    public User() {
        this.roles = Collections.singleton("ROLE_USER");
    }
    
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = Collections.singleton("ROLE_USER");
    }
    
    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }
    
    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }
    
    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }
    
    // Getters and Setters for new fields
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getTelefone() {
        return telefone;
    }
    
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public String getCpf() {
        return cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    
    public String getRg() {
        return rg;
    }
    
    public void setRg(String rg) {
        this.rg = rg;
    }
    
    public String getLogradouro() {
        return logradouro;
    }
    
    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }
    
    public String getCep() {
        return cep;
    }
    
    public void setCep(String cep) {
        this.cep = cep;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public String getComplemento() {
        return complemento;
    }
    
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    
    public String getBairro() {
        return bairro;
    }
    
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
    
    public String getCidade() {
        return cidade;
    }
    
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public byte[] getComprovanteResidencia() {
        return comprovanteResidencia;
    }
    
    public void setComprovanteResidencia(byte[] comprovanteResidencia) {
        this.comprovanteResidencia = comprovanteResidencia;
    }
    
    public String getComprovanteResidenciaNome() {
        return comprovanteResidenciaNome;
    }
    
    public void setComprovanteResidenciaNome(String comprovanteResidenciaNome) {
        this.comprovanteResidenciaNome = comprovanteResidenciaNome;
    }
    
    public String getComprovanteResidenciaContentType() {
        return comprovanteResidenciaContentType;
    }
    
    public void setComprovanteResidenciaContentType(String comprovanteResidenciaContentType) {
        this.comprovanteResidenciaContentType = comprovanteResidenciaContentType;
    }
    
    public byte[] getComprovanteDocumento() {
        return comprovanteDocumento;
    }
    
    public void setComprovanteDocumento(byte[] comprovanteDocumento) {
        this.comprovanteDocumento = comprovanteDocumento;
    }
    
    public String getComprovanteDocumentoNome() {
        return comprovanteDocumentoNome;
    }
    
    public void setComprovanteDocumentoNome(String comprovanteDocumentoNome) {
        this.comprovanteDocumentoNome = comprovanteDocumentoNome;
    }
    
    public String getComprovanteDocumentoContentType() {
        return comprovanteDocumentoContentType;
    }
    
    public void setComprovanteDocumentoContentType(String comprovanteDocumentoContentType) {
        this.comprovanteDocumentoContentType = comprovanteDocumentoContentType;
    }
}