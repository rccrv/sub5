package br.nom.rccrv.code.infrastructure.persistence.entity;

import br.nom.rccrv.code.domain.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(name = "correlation_id", nullable = false, unique = true)
  public UUID correlationId;

  @Column(nullable = false)
  public String cpf;

  @Column(nullable = false)
  public String placa;

  @Column(nullable = false)
  public String endereco;

  @Column(nullable = false)
  public String cep;

  @Column(name = "pix_code", nullable = false, unique = true)
  public String pixCode;

  @Column(name = "quoted_amount", nullable = false, precision = 12, scale = 2)
  public BigDecimal quotedAmount;

  @Column(name = "settled_amount", precision = 12, scale = 2)
  public BigDecimal settledAmount;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  public PaymentStatus status;

  @Column(name = "created_at", nullable = false)
  public LocalDateTime createdAt;

  @Column(name = "expires_at", nullable = false)
  public LocalDateTime expiresAt;

  @Column(name = "updated_at")
  public LocalDateTime updatedAt;

  @Version
  @Column(nullable = false)
  public long version;
}
