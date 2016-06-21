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

import java.util.UUID;

import org.spongepowered.api.service.economy.account.UniqueAccount;

import fr.evercraft.evereconomy.EverEconomy;

public class EUniqueAccount extends EAccount implements UniqueAccount {

	private final UUID uuid;
	
	public EUniqueAccount(final EverEconomy plugin, final UUID uuid) {
		super(plugin, uuid.toString());
		this.uuid = uuid;
	}
	
	@Override
	public UUID getUniqueId() {
		return this.uuid;
	}
}
