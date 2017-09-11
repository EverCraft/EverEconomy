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
package fr.evercraft.evereconomy.service.economy.event;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.economy.EconomyTransactionEvent;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

public class EEconomyTransactionEvent implements EconomyTransactionEvent {

	public final Cause cause;
	public final TransactionResult transaction;
	
	public EEconomyTransactionEvent(final Cause cause, final TransactionResult transaction) {
		this.cause = cause;
		this.transaction = transaction;
	}
	
	@Override
	public Cause getCause() {
		return this.cause;
	}

	@Override
	public TransactionResult getTransactionResult() {
		return this.transaction;
	}

}
