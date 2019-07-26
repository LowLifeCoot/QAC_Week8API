package com.qa.persistence.repository;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.qa.persistence.domain.Account;
import com.qa.util.JSONUtil;

@Default
@Transactional(value = TxType.SUPPORTS)
public class AccountDBRepositry implements AccountRepository {

	@PersistenceContext(unitName = "primary")
	private EntityManager em;

	@Inject
	private JSONUtil json;

	public String getAllAccounts() {
		TypedQuery<Account> query = em.createQuery("SELECT a FROM Account a", Account.class);
		return json.getJSONForObject(query.getResultList());
	}

	@Transactional(value = TxType.REQUIRED)
	public String createAccount(String account) {
		Account toCreate = this.json.getObjectForJSON(account, Account.class);
		this.em.persist(toCreate);
		return SUCCESS;
	}

	@Transactional(value = TxType.REQUIRED)
	public String deleteAccount(int accountNumber) {
		Account accountTemp = em.find(Account.class, accountNumber);
		em.remove(accountTemp);
		return "Removed account: " + accountTemp.getName();
	}

	@Transactional(value = TxType.REQUIRED)
	public String updateAccount(int accountNumber, String account) {
		// if id is same then replace data at database

		Account current = this.em.find(Account.class, accountNumber);
		Account toChange = this.json.getObjectForJSON(account, Account.class);
		current.setName(toChange.getName());
		current.setPassword(toChange.getPassword());

		this.em.persist(toChange);
		return SUCCESS + toChange;
	}

	public Account findAccount(Long id) {
		return em.find(Account.class, id);
	}

	public String login(String account) {
		Account newAccount = this.json.getObjectForJSON(account, Account.class);
		String username = newAccount.getName();
		String password = newAccount.getPassword();

		TypedQuery<Account> query = this.em.createQuery("SELECT a FROM Account a WHERE name = '" + username + "'",
				Account.class);

		Account logAcc = (Account) query.getSingleResult();

		return this.json.getJSONForObject(logAcc) + "This";
	}

	public boolean checkUsername(String account) {
		Account user = this.json.getObjectForJSON(account, Account.class);
		String username = user.getName();
		TypedQuery<Account> query = this.em.createQuery("SELECT a FROM Account a WHERE name='" + username + "'",
				Account.class);
		if (query.getResultList().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
