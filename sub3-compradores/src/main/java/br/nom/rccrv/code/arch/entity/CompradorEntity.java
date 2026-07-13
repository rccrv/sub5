package br.nom.rccrv.code.arch.entity;

public class CompradorEntity {

    private CompradorId compradorId;
    private String cpf;
    private String primeiroNome;
    private String ultimoNome;
    private String email;
    private String telefone;
    private Boolean autorizado;

    public CompradorEntity(CompradorId compradorId) {
        this.compradorId = compradorId;
    }

    public CompradorEntity(
            CompradorId compradorId,
            String cpf,
            String primeiroNome,
            String ultimoNome,
            String email,
            String telefone,
            Boolean autorizado
    ) {
        this.compradorId = compradorId;
        this.cpf = cpf;
        this.primeiroNome = primeiroNome;
        this.ultimoNome = ultimoNome;
        this.email = email;
        this.telefone = telefone;
        this.autorizado = autorizado;
    }

    public CompradorId getCompradorId() {
        return compradorId;
    }

    public void setCompradorId(CompradorId compradorId) {
        this.compradorId = compradorId;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPrimeiroNome() {
        return primeiroNome;
    }

    public void setPrimeiroNome(String primeiroNome) {
        this.primeiroNome = primeiroNome;
    }

    public String getUltimoNome() {
        return ultimoNome;
    }

    public void setUltimoNome(String ultimoNome) {
        this.ultimoNome = ultimoNome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getAutorizado() {
        return autorizado;
    }

    public void setAutorizado(Boolean autorizado) {
        this.autorizado = autorizado;
    }
}
