package br.nom.rccrv.code.arch.port.service;

public interface CreateUserAuthServicePort {

    String criarComprador(String username);
    void rollbackCriarComprador(String username);
}
