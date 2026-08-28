package br.nom.rccrv.code.infrastructure.persistence.entity;

import br.nom.rccrv.code.domain.enums.PaymentStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Static metamodel for {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento}
 **/
@StaticMetamodel(Pagamento.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public abstract class Pagamento_ {

	
	/**
	 * @see #id
	 **/
	public static final String ID = "id";
	
	/**
	 * @see #correlationId
	 **/
	public static final String CORRELATION_ID = "correlationId";
	
	/**
	 * @see #cpf
	 **/
	public static final String CPF = "cpf";
	
	/**
	 * @see #placa
	 **/
	public static final String PLACA = "placa";
	
	/**
	 * @see #endereco
	 **/
	public static final String ENDERECO = "endereco";
	
	/**
	 * @see #cep
	 **/
	public static final String CEP = "cep";
	
	/**
	 * @see #pixCode
	 **/
	public static final String PIX_CODE = "pixCode";
	
	/**
	 * @see #quotedAmount
	 **/
	public static final String QUOTED_AMOUNT = "quotedAmount";
	
	/**
	 * @see #settledAmount
	 **/
	public static final String SETTLED_AMOUNT = "settledAmount";
	
	/**
	 * @see #status
	 **/
	public static final String STATUS = "status";
	
	/**
	 * @see #createdAt
	 **/
	public static final String CREATED_AT = "createdAt";
	
	/**
	 * @see #expiresAt
	 **/
	public static final String EXPIRES_AT = "expiresAt";
	
	/**
	 * @see #updatedAt
	 **/
	public static final String UPDATED_AT = "updatedAt";
	
	/**
	 * @see #version
	 **/
	public static final String VERSION = "version";

	
	/**
	 * Static metamodel type for {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento}
	 **/
	public static volatile EntityType<Pagamento> class_;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#id}
	 **/
	public static volatile SingularAttribute<Pagamento, Long> id;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#correlationId}
	 **/
	public static volatile SingularAttribute<Pagamento, UUID> correlationId;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#cpf}
	 **/
	public static volatile SingularAttribute<Pagamento, String> cpf;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#placa}
	 **/
	public static volatile SingularAttribute<Pagamento, String> placa;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#endereco}
	 **/
	public static volatile SingularAttribute<Pagamento, String> endereco;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#cep}
	 **/
	public static volatile SingularAttribute<Pagamento, String> cep;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#pixCode}
	 **/
	public static volatile SingularAttribute<Pagamento, String> pixCode;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#quotedAmount}
	 **/
	public static volatile SingularAttribute<Pagamento, BigDecimal> quotedAmount;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#settledAmount}
	 **/
	public static volatile SingularAttribute<Pagamento, BigDecimal> settledAmount;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#status}
	 **/
	public static volatile SingularAttribute<Pagamento, PaymentStatus> status;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#createdAt}
	 **/
	public static volatile SingularAttribute<Pagamento, LocalDateTime> createdAt;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#expiresAt}
	 **/
	public static volatile SingularAttribute<Pagamento, LocalDateTime> expiresAt;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#updatedAt}
	 **/
	public static volatile SingularAttribute<Pagamento, LocalDateTime> updatedAt;
	
	/**
	 * Static metamodel for attribute {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento#version}
	 **/
	public static volatile SingularAttribute<Pagamento, Long> version;

}

