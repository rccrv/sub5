package br.nom.rccrv.code.arch.entity;

import br.nom.rccrv.code.domain.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PagamentoEntity {

  private Long id;
  private UUID correlationId;
  private String cpf;
  private String placa;
  private String endereco;
  private String cep;
  private String pixCode;
  private BigDecimal quotedAmount;
  private BigDecimal settledAmount;
  private PaymentStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime expiresAt;
  private LocalDateTime updatedAt;
  private long version;

  public PagamentoEntity(
      Long id,
      UUID correlationId,
      String cpf,
      String placa,
      String endereco,
      String cep,
      String pixCode,
      BigDecimal quotedAmount,
      PaymentStatus status,
      LocalDateTime createdAt,
      LocalDateTime expiresAt) {
    this.id = id;
    this.correlationId = correlationId;
    this.cpf = cpf;
    this.placa = placa;
    this.endereco = endereco;
    this.cep = cep;
    this.pixCode = pixCode;
    this.quotedAmount = quotedAmount;
    this.status = status;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
    this.updatedAt = createdAt;
  }

  public Long getId() {
    return id;
  }

  public UUID getCorrelationId() {
    return correlationId;
  }

  public String getCpf() {
    return cpf;
  }

  public String getPlaca() {
    return placa;
  }

  public String getEndereco() {
    return endereco;
  }

  public String getCep() {
    return cep;
  }

  public String getPixCode() {
    return pixCode;
  }

  public BigDecimal getQuotedAmount() {
    return quotedAmount;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getExpiresAt() {
    return expiresAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public BigDecimal getSettledAmount() {
    return settledAmount;
  }

  public void setSettledAmount(BigDecimal settledAmount) {
    this.settledAmount = settledAmount;
  }

  public void restorePersistenceState(
      BigDecimal settledAmount,
      LocalDateTime updatedAt,
      long version) {
    this.settledAmount = settledAmount;
    this.updatedAt = updatedAt;
    this.version = version;
  }

  public long getVersion() {
    return version;
  }

  public boolean belongsTo(String owner) {
    return cpf.equals(owner.replaceAll("\\D", ""));
  }

  public boolean expired(LocalDateTime now) {
    return status == PaymentStatus.PENDING && !expiresAt.isAfter(now);
  }

  public void expire(LocalDateTime now) {
    status = PaymentStatus.EXPIRED;
    updatedAt = now;
  }

  public boolean canProcess(String pixCode) {
    return status == PaymentStatus.PENDING && this.pixCode.equals(pixCode);
  }

  public void process(LocalDateTime now) {
    status = PaymentStatus.PROCESSING;
    updatedAt = now;
  }

  public boolean settle(BigDecimal amount, LocalDateTime now) {
    if (status != PaymentStatus.PROCESSING) {
      return false;
    }

    status = PaymentStatus.PAID;
    settledAmount = amount;
    updatedAt = now;
    return true;
  }

  public boolean cancel(LocalDateTime now) {
    if (status == PaymentStatus.CANCELLED) {
      return true;
    }

    status = PaymentStatus.CANCELLED;
    updatedAt = now;
    return true;
  }
}
