package br.nom.rccrv.code.infrastructure.persistence.entity;

import jakarta.annotation.Generated;
import jakarta.data.metamodel.SortableAttribute;
import jakarta.data.metamodel.StaticMetamodel;
import jakarta.data.metamodel.TextAttribute;
import jakarta.data.metamodel.impl.SortableAttributeRecord;
import jakarta.data.metamodel.impl.TextAttributeRecord;

/**
 * Jakarta Data static metamodel for {@link br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento}
 **/
@StaticMetamodel(Pagamento.class)
@Generated("org.hibernate.processor.HibernateProcessor")
public interface _Pagamento {

	
	/**
	 * @see #id
	 **/
	String ID = "id";
	
	/**
	 * @see #correlationId
	 **/
	String CORRELATION_ID = "correlationId";
	
	/**
	 * @see #cpf
	 **/
	String CPF = "cpf";
	
	/**
	 * @see #placa
	 **/
	String PLACA = "placa";
	
	/**
	 * @see #endereco
	 **/
	String ENDERECO = "endereco";
	
	/**
	 * @see #cep
	 **/
	String CEP = "cep";
	
	/**
	 * @see #pixCode
	 **/
	String PIX_CODE = "pixCode";
	
	/**
	 * @see #quotedAmount
	 **/
	String QUOTED_AMOUNT = "quotedAmount";
	
	/**
	 * @see #settledAmount
	 **/
	String SETTLED_AMOUNT = "settledAmount";
	
	/**
	 * @see #status
	 **/
	String STATUS = "status";
	
	/**
	 * @see #createdAt
	 **/
	String CREATED_AT = "createdAt";
	
	/**
	 * @see #expiresAt
	 **/
	String EXPIRES_AT = "expiresAt";
	
	/**
	 * @see #updatedAt
	 **/
	String UPDATED_AT = "updatedAt";
	
	/**
	 * @see #version
	 **/
	String VERSION = "version";

	
	/**
	 * Static metamodel for attribute {@link Pagamento#id}
	 **/
	SortableAttribute<Pagamento> id = new SortableAttributeRecord<>(ID);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#correlationId}
	 **/
	SortableAttribute<Pagamento> correlationId = new SortableAttributeRecord<>(CORRELATION_ID);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#cpf}
	 **/
	TextAttribute<Pagamento> cpf = new TextAttributeRecord<>(CPF);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#placa}
	 **/
	TextAttribute<Pagamento> placa = new TextAttributeRecord<>(PLACA);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#endereco}
	 **/
	TextAttribute<Pagamento> endereco = new TextAttributeRecord<>(ENDERECO);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#cep}
	 **/
	TextAttribute<Pagamento> cep = new TextAttributeRecord<>(CEP);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#pixCode}
	 **/
	TextAttribute<Pagamento> pixCode = new TextAttributeRecord<>(PIX_CODE);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#quotedAmount}
	 **/
	SortableAttribute<Pagamento> quotedAmount = new SortableAttributeRecord<>(QUOTED_AMOUNT);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#settledAmount}
	 **/
	SortableAttribute<Pagamento> settledAmount = new SortableAttributeRecord<>(SETTLED_AMOUNT);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#status}
	 **/
	SortableAttribute<Pagamento> status = new SortableAttributeRecord<>(STATUS);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#createdAt}
	 **/
	SortableAttribute<Pagamento> createdAt = new SortableAttributeRecord<>(CREATED_AT);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#expiresAt}
	 **/
	SortableAttribute<Pagamento> expiresAt = new SortableAttributeRecord<>(EXPIRES_AT);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#updatedAt}
	 **/
	SortableAttribute<Pagamento> updatedAt = new SortableAttributeRecord<>(UPDATED_AT);
	
	/**
	 * Static metamodel for attribute {@link Pagamento#version}
	 **/
	SortableAttribute<Pagamento> version = new SortableAttributeRecord<>(VERSION);

}

