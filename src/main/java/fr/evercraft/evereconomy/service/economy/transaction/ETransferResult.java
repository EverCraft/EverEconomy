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
package fr.evercraft.evereconomy.service.economy.transaction;

import java.math.BigDecimal;
import java.util.Set;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import fr.evercraft.everapi.plugin.EPlugin;

public class ETransferResult implements TransferResult {

	private final Account account;
	private final Account accountTo;
	private final Currency currency;
	private final BigDecimal amount;
	private final Set<Context> contexts;
	private final ResultType result;
	private final TransactionType transaction;

	public ETransferResult(final EPlugin plugin, final Account account, final Account accountTo, final Currency currency, final BigDecimal amount, 
			final Set<Context> contexts, final ResultType result, final TransactionType transaction){
		this.account = account;
		this.accountTo = accountTo;
		this.currency = currency;
		this.amount = amount;
		this.contexts = contexts;
		this.result = result;
		this.transaction = transaction;
		
		new ETransactionResult(plugin, account, currency, amount, contexts, result, TransactionTypes.WITHDRAW);
		new ETransactionResult(plugin, accountTo, currency, amount, contexts, result, TransactionTypes.DEPOSIT);
	}
	
	@Override
	public Account getAccount() {
		return this.account;
	}

	@Override
	public Account getAccountTo() {
		return this.accountTo;
	}
	
	@Override
	public Currency getCurrency() {
		return this.currency;
	}

	@Override
	public BigDecimal getAmount() {
		return this.amount;
	}

	@Override
	public Set<Context> getContexts() {
		return this.contexts;
	}

	@Override
	public ResultType getResult() {
		return this.result;
	}

	@Override
	public TransactionType getType() {
		return this.transaction;
	}
}
