package br.nom.rccrv.code.arch.entity;

import java.math.BigDecimal;

public class VeiculoEntity {

    private VeiculoId veiculoId;
    private String marca;
    private String modelo;
    private Integer ano;
    private String placa;
    private String cor;
    private BigDecimal valor;
    private String compradorCpf;
    private Boolean vendido;

    public VeiculoEntity(VeiculoId veiculoId) {
        this.veiculoId = veiculoId;
    }

    public VeiculoEntity(
            VeiculoId veiculoId,
            String marca,
            String modelo,
            Integer ano,
            String placa,
            String cor,
            BigDecimal valor,
            String compradorCpf,
            Boolean vendido
    ) {
        this.veiculoId = veiculoId;
        this.marca = marca;
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
        this.cor = cor;
        this.valor = valor;
        this.compradorCpf = compradorCpf;
        this.vendido = vendido;
    }

    public VeiculoId getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(VeiculoId veiculoId) {
        this.veiculoId = veiculoId;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCompradorCpf() {
        return compradorCpf;
    }

    public void setCompradorCpf(String compradorCpf) {
        this.compradorCpf = compradorCpf;
    }

    public Boolean getVendido() {
        return vendido;
    }

    public void setVendido(Boolean vendido) {
        this.vendido = vendido;
    }
}
