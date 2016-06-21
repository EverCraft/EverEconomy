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

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;

import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.evereconomy.service.economy.event.EEconomyTransactionEvent;

public class ETransactionResult implements TransactionResult {

	private final Account account;
	private final Currency currency;
	private final BigDecimal amount;
	private final Set<Context> contexts;
	private final ResultType result;
	private final TransactionType transaction;

	public ETransactionResult(final EPlugin plugin, final Account account, final Currency currency, 
			final BigDecimal amount, final Set<Context> contexts, final ResultType result, final TransactionType transaction) {
		this.account = account;
		this.currency = currency;
		this.amount = amount;
		this.contexts = contexts;
		this.result = result;
		this.transaction = transaction;
		
		plugin.getLogger().debug("Event EconomyTransactionEvent : (Account='" + this.account.getIdentifier() +"')");
		EconomyTransactionEvent event = new EEconomyTransactionEvent(Cause.source(plugin).build(), this);
		plugin.getGame().getEventManager().post(event);
	}

	@Override
	public Account getAccount() {
		return this.account;
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
