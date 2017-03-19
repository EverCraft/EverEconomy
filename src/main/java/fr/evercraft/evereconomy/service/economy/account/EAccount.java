/*
 * This file is part of EverEconomy.
 *
 * EverEconomy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EverEconomy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EverEconomy.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.evercraft.evereconomy.service.economy.account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;
import org.spongepowered.api.text.Text;

import com.google.common.collect.ImmutableMap;

import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.evereconomy.EverEconomy;
import fr.evercraft.evereconomy.service.economy.ECurrency;
import fr.evercraft.evereconomy.service.economy.transaction.ETransactionResult;
import fr.evercraft.evereconomy.service.economy.transaction.ETransferResult;

public abstract class EAccount implements Account {
	
	protected final EverEconomy plugin;
	
	protected final String identifier;
	protected final Text name;
	
	protected final ConcurrentMap<Currency, BigDecimal> currencies;
	
	public EAccount(final EverEconomy plugin, final String identifier){
		this(plugin, identifier, Text.of(identifier));
	}
	
	public EAccount(final EverEconomy plugin, final String identifier, final Text name){
		this.plugin = plugin;
		this.identifier = identifier;
		this.name = name;
		
		this.currencies = new ConcurrentHashMap<Currency, BigDecimal>();
		this.select();
	}
	
	public void reload() {
		this.currencies.clear();
		this.select();
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}
	
	@Override
	public Text getDisplayName() {
		return this.name;
	}
	
	@Override
	public BigDecimal getDefaultBalance(final Currency currency) {
		if (currency instanceof ECurrency) {
            return ((ECurrency) currency).getBalanceDefault();
        }
		return BigDecimal.ZERO;
	}
	
	@Override
	public boolean hasBalance(final Currency currency, final Set<Context> contexts) {
		return this.currencies.containsKey(currency);
	}

	@Override
	public BigDecimal getBalance(final Currency currency, final Set<Context> contexts) {
		if (this.currencies.containsKey(currency)){
			return this.currencies.get(currency);
		}
		return getDefaultBalance(currency);
	}
	
	public void setBalance(final Currency currency, final BigDecimal amount) {
		if (!this.currencies.containsKey(currency)){
			this.currencies.put(currency, amount);
			this.plugin.getThreadAsync().execute(() -> this.insert(currency));
		}
		this.currencies.put(currency, amount);
		this.plugin.getThreadAsync().execute(() -> this.update(currency));
	}

	@Override
	public Map<Currency, BigDecimal> getBalances(final Set<Context> contexts) {
		return ImmutableMap.copyOf(this.currencies);
	}
	
	@Override
	public Set<Context> getActiveContexts() {
		return new HashSet<Context>();
	}

	@Override
	public TransactionResult setBalance(final Currency currency, final BigDecimal after, final Cause cause, final Set<Context> contexts) {
		TransactionType transaction = TransactionTypes.WITHDRAW;
		
		BigDecimal before = this.getBalance(currency);
		BigDecimal amount = before.subtract(after);
		
		// Si le changement est supérieur ou égal à 0 c'est que l'on dépose de l'argent
		if (amount.compareTo(BigDecimal.ZERO) >= 0) {
			transaction = TransactionTypes.DEPOSIT;
		}
		
		// Transfére
		this.setBalance(currency, after);
		this.log(currency, before, after, transaction, cause);
		
		return new ETransactionResult(this.plugin, this, currency, amount.abs(), contexts, ResultType.SUCCESS, transaction);
	}
	
	@Override
	public Map<Currency, TransactionResult> resetBalances(final Cause cause, final Set<Context> contexts) {		
		Map<Currency, TransactionResult> list = new HashMap<Currency, TransactionResult>();
		// Pour tous les monnaies
		for (Currency currency : this.currencies.keySet()){
			TransactionType transaction = TransactionTypes.WITHDRAW;
			
			BigDecimal before = this.getBalance(currency);
			BigDecimal after = getDefaultBalance(currency);
			BigDecimal amount = before.subtract(after);

			// Si le changement est supérieur ou égal à 0 c'est que l'on dépose de l'argent
			if (amount.compareTo(BigDecimal.ZERO) >= 0) {
				transaction = TransactionTypes.DEPOSIT;
			}
			
			// Transfére
			this.setBalance(currency, after);
			this.log(currency, before, after, transaction, cause);
			
			list.put(currency, new ETransactionResult(this.plugin, this, currency, amount.abs(), contexts, ResultType.SUCCESS, transaction));
		}
		this.plugin.getThreadAsync().execute(() -> this.delete());
		return list;
	}

	@Override
	public TransactionResult resetBalance(final Currency currency, final Cause cause, final Set<Context> contexts) {
		TransactionType transaction = TransactionTypes.WITHDRAW;
		
		BigDecimal before = this.getBalance(currency);
		BigDecimal after = getDefaultBalance(currency);
		BigDecimal amount = before.subtract(after);

		// Si le changement est supérieur ou égal à 0 c'est que l'on dépose de l'argent
		if (amount.compareTo(BigDecimal.ZERO) >= 0) {
			transaction = TransactionTypes.DEPOSIT;
		}
		
		// Transfére
		this.setBalance(currency, after);
		this.log(currency, before, after, transaction, cause);
		
		return new ETransactionResult(this.plugin, this, currency, amount.abs(), contexts, ResultType.SUCCESS, transaction);
	}

	@Override
	public TransactionResult deposit(final Currency currency, final BigDecimal amount, final Cause cause, final Set<Context> contexts) {
		BigDecimal before = this.getBalance(currency);
		BigDecimal after = before.add(amount);
		
		// Quantité positive
		if (amount.compareTo(BigDecimal.ZERO) >= 0) {
			// Inférieur au max
			if (after.compareTo(ECurrency.getBalanceMax(currency)) <= 0) {
				// Transfére
				this.setBalance(currency, after);
				this.log(currency, before, after, TransactionTypes.DEPOSIT, cause);
				return new ETransactionResult(this.plugin, this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.DEPOSIT);
			}
			return new ETransactionResult(this.plugin, this, currency, amount, contexts, ResultType.ACCOUNT_NO_SPACE, TransactionTypes.DEPOSIT);
		}
		return new ETransactionResult(this.plugin, this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.DEPOSIT);
	}

	@Override
	public TransactionResult withdraw(final Currency currency, final BigDecimal amount, final Cause cause, final Set<Context> contexts) {
		BigDecimal before = this.getBalance(currency);
		BigDecimal after = before.subtract(amount);
		
		// Quantité positive
		if (amount.compareTo(BigDecimal.ZERO) >= 0) {
			// Séperieur au min
			if (after.compareTo(ECurrency.getBalanceMin(currency)) >= 0) {
				// Transfére
				this.setBalance(currency, after);
				this.log(currency, before, after, TransactionTypes.WITHDRAW, cause);
				return new ETransactionResult(this.plugin, this, currency, amount, contexts, ResultType.SUCCESS, TransactionTypes.WITHDRAW);
			}
			return new ETransactionResult(this.plugin, this, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.WITHDRAW);
		}
		return new ETransactionResult(this.plugin, this, currency, amount, contexts, ResultType.FAILED, TransactionTypes.WITHDRAW);
	}

	@Override
	public TransferResult transfer(final Account to, final Currency currency, final BigDecimal amount, final Cause cause, final Set<Context> contexts) {
		ResultType result = ResultType.SUCCESS;
		
		BigDecimal player_before = this.getBalance(currency);
		BigDecimal player_after = player_before.subtract(amount);
		
		// Quantité positive
		if (amount.compareTo(BigDecimal.ZERO) > 0) {
			// Séperieur au min
			if (player_after.compareTo(ECurrency.getBalanceMin(currency)) >= 0) {
				// Transfére
				BigDecimal to_before = to.getBalance(currency);
				BigDecimal to_after = to_before.add(amount);
				
				// Inférieur au max
				if (to_after.compareTo(ECurrency.getBalanceMax(currency)) <= 0) {
					if (to instanceof EAccount) {
						EAccount account = (EAccount) to;
						this.setBalance(currency, player_after);
						account.setBalance(currency, to_after);
						
						this.log(currency, player_before, player_after, TransactionTypes.TRANSFER, cause, to.getIdentifier());
						account.log(currency, to_before, to_after, TransactionTypes.TRANSFER, cause, this.getIdentifier());
					} else {
						result = this.deposit(currency, amount, cause, contexts).getResult();
						if (result.equals(ResultType.SUCCESS)) {
							this.setBalance(currency, player_after);
							
							this.log(currency, player_before, player_after, TransactionTypes.TRANSFER, cause);
						}
					}
					return new ETransferResult(this.plugin, this, to, currency, amount, contexts, result, TransactionTypes.TRANSFER);
				} else {
					return new ETransferResult(this.plugin, this, to, currency, amount, contexts, ResultType.ACCOUNT_NO_SPACE, TransactionTypes.TRANSFER);
				}
			} else {
				return new ETransferResult(this.plugin, this, to, currency, amount, contexts, ResultType.ACCOUNT_NO_FUNDS, TransactionTypes.TRANSFER);
			}
		}
		return new ETransferResult(this.plugin, this, to, currency, amount, contexts, ResultType.FAILED, TransactionTypes.TRANSFER);
	}

	public void log(final Currency currency, final BigDecimal before, final BigDecimal after, final TransactionType transaction, final Cause cause) {
		this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().log(this.identifier, currency, before, after, transaction, cause, null));
	}
	
	public void log(final Currency currency, final BigDecimal before, final BigDecimal after, final TransactionType transaction, final Cause cause, final String to) {
		this.plugin.getThreadAsync().execute(() -> this.plugin.getDataBases().log(this.identifier, currency, before, after, transaction, cause, to));
	}
	
	public void select() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			String query = "SELECT `currency`, `balance` FROM `" + this.plugin.getDataBases().getTableAccount() + "` WHERE identifier = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.identifier);
			ResultSet list = preparedStatement.executeQuery();
			while(list.next()) {
				Optional<ECurrency> currency = this.plugin.getService().getCurrency(list.getString("currency"));
				if (currency.isPresent()){
					this.currencies.put(currency.get(), list.getBigDecimal("balance"));
					this.plugin.getELogger().debug("Log : (identifier='" + identifier + "';"
														+ "currency='" + currency.get().getId() + "';"
														+ "balance='" + list.getBigDecimal("balance").toString() + "')");
				}
			}
			for (Currency currency : this.plugin.getService().getCurrencies()) {
				if (!this.currencies.containsKey(currency)) {
					BigDecimal balance = getDefaultBalance(currency);
					this.currencies.put(currency, balance);
					this.insert(connection, currency);
				}
			}
		} catch (SQLException e) {
	    	this.plugin.getELogger().warn("Error during a change of account : (identifier:'" + this.identifier + "'): " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void insert(final Currency currency) {
		Connection connection = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			this.insert(connection, currency);
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {if (connection != null) connection.close();} catch (SQLException e) {}
	    }
	}
	
	public void insert(Connection connection, final Currency currency) {
		PreparedStatement preparedStatement = null;
		try {
			String query = "INSERT INTO `" + this.plugin.getDataBases().getTableAccount() + "` VALUES(?, ?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.identifier);
			preparedStatement.setString(2, currency.getId());
			preparedStatement.setBigDecimal(3, this.getBalance(currency));
			preparedStatement.execute();
			this.plugin.getELogger().debug("Adding to the database : (identifier='" + identifier + "';"
																	+ "currency='" + currency.getId() + "';"
																	+ "balance='" + this.getBalance(currency).toString() + "')");
		} catch (SQLException e) {
	    	this.plugin.getELogger().warn("Error during a change of account : (identifier:'" + this.identifier + "';"
	    																	+ "currency:'" + currency.getName() + "';"
	    																	+ "balance:" + this.getBalance(currency) + "'): " + e.getMessage());
		} finally {
			try {if (preparedStatement != null) preparedStatement.close();} catch (SQLException e) {}
	    }
	}
	
	public void update(final Currency currency) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			String query = "UPDATE `" + this.plugin.getDataBases().getTableAccount() + "` SET `balance` = ? WHERE `identifier` = ? AND `currency` = ?;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setBigDecimal(1, this.getBalance(currency));
			preparedStatement.setString(2, this.identifier);
			preparedStatement.setString(3, currency.getId());
			preparedStatement.execute();
			this.plugin.getELogger().debug("Updating the database : (identifier='" + identifier + "';"
																	+ "currency='" + currency.getId() + "';"
																	+ "balance='" + this.getBalance(currency).toString() + "')");
		} catch (SQLException e) {
	    	this.plugin.getELogger().warn("Error during a change of account : (uuid:'" + this.identifier + "';"
	    																   + "currency:'" + currency.getName() + "';"
	    																   + "balance:" + this.getBalance(currency) + "'): " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void delete(final Currency currency) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			String query = "DELETE FROM `" + this.plugin.getDataBases().getTableAccount() + "` WHERE `identifier` = ? AND `currency` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.identifier);
			preparedStatement.setString(2, currency.getId());
			preparedStatement.execute();
			this.plugin.getELogger().debug("Remove from database : (identifier='" + identifier + "';currency='" + currency.getId() + "')");
		} catch (SQLException e) {
	    	this.plugin.getELogger().warn("Error during a change of account : (identifier:'" + this.identifier + "';currency:'" + currency.getName() + "'): " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public void delete() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			String query = "DELETE FROM `" + this.plugin.getDataBases().getTableAccount() + "` WHERE `identifier` = ? ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, this.identifier);
			preparedStatement.execute();
			this.plugin.getELogger().debug("Remove from database : (identifier='" + identifier + "')");
		} catch (SQLException e) {
	    	this.plugin.getELogger().warn("Error during a change of account : (identifier:'" + this.identifier +  "'): " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
}
