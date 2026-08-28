package br.nom.rccrv.code.infrastructure.persistence.repository;

import br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento;
import br.nom.rccrv.code.infrastructure.persistence.entity.Pagamento_;
import jakarta.annotation.Generated;
import jakarta.annotation.Nonnull;
import jakarta.data.Order;
import jakarta.data.exceptions.DataException;
import jakarta.data.exceptions.EmptyResultException;
import jakarta.data.exceptions.EntityExistsException;
import jakarta.data.exceptions.OptimisticLockingFailureException;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.page.impl.PageRecord;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.List;
import static java.util.Objects.requireNonNull;
import java.util.Optional;
import static java.util.Optional.ofNullable;
import java.util.UUID;
import java.util.stream.Stream;
import org.hibernate.StaleStateException;
import org.hibernate.StatelessSession;
import org.hibernate.exception.ConstraintViolationException;
import static org.hibernate.query.Order.asc;
import static org.hibernate.query.SortDirection.*;
import org.hibernate.query.specification.SelectionSpecification;

/**
 * Implements Jakarta Data repository {@link br.nom.rccrv.code.infrastructure.persistence.repository.PagamentoRepository}
 **/
@Dependent
@Generated("org.hibernate.processor.HibernateProcessor")
public class PagamentoRepository_ implements PagamentoRepository {

	
	/**
	 * @see #findByCorrelationId(UUID)
	 **/
	static final String FIND_BY_CORRELATION_ID_UUID = "select pagamento from Pagamento pagamento where pagamento.correlationId = :correlationId";
	
	/**
	 * @see #findPendingExpired(LocalDateTime)
	 **/
	static final String FIND_PENDING_EXPIRED_LocalDateTime = "select pagamento from Pagamento pagamento where pagamento.status = 'PENDING' and pagamento.expiresAt <= :now";

	
	protected @Nonnull StatelessSession session;
	
	@Inject
	public PagamentoRepository_(@Nonnull StatelessSession session) {
		this.session = session;
	}
	
	public @Nonnull StatelessSession session() {
		return session;
	}
	
	@Override
	public  <S extends Pagamento> S insert(@Nonnull S entity) {
		requireNonNull(entity, "Null entity");
		try {
			session.insert(entity);
		}
		catch (ConstraintViolationException _ex) {
			throw new EntityExistsException(_ex.getMessage(), _ex);
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
		return entity;
	}
	
	@Override
	public  <S extends Pagamento> List<S> insertAll(@Nonnull List<S> entities) {
		requireNonNull(entities, "Null entities");
		try {
			session.insertMultiple(entities);
		}
		catch (ConstraintViolationException _ex) {
			throw new EntityExistsException(_ex.getMessage(), _ex);
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
		return entities;
	}
	
	@Override
	public  <S extends Pagamento> S update(@Nonnull S entity) {
		requireNonNull(entity, "Null entity");
		try {
			session.update(entity);
		}
		catch (StaleStateException _ex) {
			throw new OptimisticLockingFailureException(_ex.getMessage(), _ex);
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
		return entity;
	}
	
	@Override
	public  <S extends Pagamento> List<S> updateAll(@Nonnull List<S> entities) {
		requireNonNull(entities, "Null entities");
		try {
			session.updateMultiple(entities);
		}
		catch (StaleStateException _ex) {
			throw new OptimisticLockingFailureException(_ex.getMessage(), _ex);
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
		return entities;
	}
	
	@Override
	public  <S extends Pagamento> S save(@Nonnull S entity) {
		requireNonNull(entity, "Null entity");
		try {
			if (session.getIdentifier(entity) == null)
				session.insert(entity);
			else
				session.upsert(entity);
		}
		catch (StaleStateException _ex) {
			throw new OptimisticLockingFailureException(_ex.getMessage(), _ex);
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
		return entity;
	}
	
	@Override
	public  <S extends Pagamento> List<S> saveAll(@Nonnull List<S> entities) {
		requireNonNull(entities, "Null entities");
		try {
			session.upsertMultiple(entities);
		}
		catch (StaleStateException _ex) {
			throw new OptimisticLockingFailureException(_ex.getMessage(), _ex);
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
		return entities;
	}
	
	@Override
	public void delete(@Nonnull Pagamento entity) {
		requireNonNull(entity, "Null entity");
		try {
			session.delete(entity);
		}
		catch (StaleStateException _ex) {
			throw new OptimisticLockingFailureException(_ex.getMessage(), _ex);
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
	}
	
	@Override
	public void deleteAll(@Nonnull List<? extends Pagamento> entities) {
		requireNonNull(entities, "Null entities");
		try {
			session.deleteMultiple(entities);
		}
		catch (StaleStateException _ex) {
			throw new OptimisticLockingFailureException(_ex.getMessage(), _ex);
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
	}
	
	/**
	 * Execute the query {@value #FIND_BY_CORRELATION_ID_UUID}.
	 *
	 * @see br.nom.rccrv.code.infrastructure.persistence.repository.PagamentoRepository#findByCorrelationId(UUID)
	 **/
	@Override
	public Optional<Pagamento> findByCorrelationId(UUID correlationId) {
		try {
			var _select = session.createSelectionQuery(FIND_BY_CORRELATION_ID_UUID, Pagamento.class)
				.setParameter("correlationId", correlationId);
			return _select
				.uniqueResultOptional();
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
	}
	
	/**
	 * Execute the query {@value #FIND_PENDING_EXPIRED_LocalDateTime}.
	 *
	 * @see br.nom.rccrv.code.infrastructure.persistence.repository.PagamentoRepository#findPendingExpired(LocalDateTime)
	 **/
	@Override
	public List<Pagamento> findPendingExpired(LocalDateTime now) {
		try {
			var _select = session.createSelectionQuery(FIND_PENDING_EXPIRED_LocalDateTime, Pagamento.class)
				.setParameter("now", now);
			return _select
				.getResultList();
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
	}
	
	/**
	 * Find {@link Pagamento} by {@link Pagamento#id id}.
	 *
	 * @see br.nom.rccrv.code.infrastructure.persistence.repository.PagamentoRepository#findById(Long)
	 **/
	@Override
	public Optional<Pagamento> findById(@Nonnull Long id) {
		requireNonNull(id, "Null id");
		try {
			return ofNullable(session.get(Pagamento.class, id));
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
	}
	
	/**
	 * Find {@link Pagamento}.
	 *
	 * @see br.nom.rccrv.code.infrastructure.persistence.repository.PagamentoRepository#findAll()
	 **/
	@Override
	public Stream<Pagamento> findAll() {
		var _builder = session.getCriteriaBuilder();
		var _query = _builder.createQuery(Pagamento.class);
		var _entity = _query.from(Pagamento.class);
		_query.where(
		);
		try {
			var _select = session.createSelectionQuery(_query);
			return _select
				.getResultStream();
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
	}
	
	/**
	 * Find {@link Pagamento}.
	 *
	 * @see br.nom.rccrv.code.infrastructure.persistence.repository.PagamentoRepository#findAll(PageRequest,Order)
	 **/
	@Override
	public Page<Pagamento> findAll(@Nonnull PageRequest pageRequest, @Nonnull Order<Pagamento> sortBy) {
		requireNonNull(pageRequest, "Null pageRequest");
		requireNonNull(sortBy, "Null sortBy");
		var _builder = session.getCriteriaBuilder();
		var _query = _builder.createQuery(Pagamento.class);
		var _entity = _query.from(Pagamento.class);
		_query.where(
		);
		var _spec = SelectionSpecification.create(_query);
		for (var _sort : sortBy.sorts()) {
			_spec.sort(asc(Pagamento.class, _sort.property())
						.reversedIf(_sort.isDescending())
						.ignoringCaseIf(_sort.ignoreCase()));
		}
		try {
			var _select = _spec.createQuery(session);
			long _totalResults = 
					pageRequest.requestTotal()
							? _select
									.getResultCount()
							: -1;
			var _results = _select
				.setFirstResult((int) (pageRequest.page()-1) * pageRequest.size())
				.setMaxResults(pageRequest.size())
				.getResultList();
			return new PageRecord<>(pageRequest, _results, _totalResults);
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
	}
	
	/**
	 * Delete {@link Pagamento} by {@link Pagamento#id id}.
	 *
	 * @see br.nom.rccrv.code.infrastructure.persistence.repository.PagamentoRepository#deleteById(Long)
	 **/
	@Override
	public void deleteById(@Nonnull Long id) {
		requireNonNull(id, "Null id");
		var _builder = session.getCriteriaBuilder();
		var _query = _builder.createCriteriaDelete(Pagamento.class);
		var _entity = _query.from(Pagamento.class);
		_query.where(
				_builder.equal(_entity.get(Pagamento_.id), id)
		);
		try {
			session.createMutationQuery(_query).executeUpdate();
		}
		catch (NoResultException _ex) {
			throw new EmptyResultException(_ex.getMessage(), _ex);
		}
		catch (NonUniqueResultException _ex) {
			throw new jakarta.data.exceptions.NonUniqueResultException(_ex.getMessage(), _ex);
		}
		catch (PersistenceException _ex) {
			throw new DataException(_ex.getMessage(), _ex);
		}
	}

}

