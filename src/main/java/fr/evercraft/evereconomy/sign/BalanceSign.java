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
package fr.evercraft.evereconomy.sign;

import java.math.BigDecimal;
import java.util.List;

import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.sign.SignSubject;
import fr.evercraft.evereconomy.EEMessage.EEMessages;
import fr.evercraft.evereconomy.EverEconomy;

public class BalanceSign implements SignSubject {
	
	private static final String NAME = "balance";
	
	private final EverEconomy plugin;
	
	public BalanceSign(final EverEconomy plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String getName() {
		return BalanceSign.NAME;
	}

	@Override
	public boolean create(EPlayer player, Location<World> location, SignData data) {
		EEMessages.SIGN_BALANCE_CREATE.sendTo(player);
		return true;
	}

	@Override
	public boolean useEnable(EPlayer player, Sign sign) {
		BigDecimal balance = player.getBalance();
		EEMessages.BALANCE_PLAYER.sender()
			.replace(this.plugin.getService().getReplaces())
			.replace("{solde}", () -> this.plugin.getService().getDefaultCurrency().cast(balance))
			.replace("{solde_format}", () -> this.plugin.getService().getDefaultCurrency().format(balance))
			.sendTo(player);
		return true;
	}

	@Override
	public boolean useDisable(EPlayer player, Sign sign) {
		EEMessages.SIGN_BALANCE_DISABLE.sendTo(player);
		return true;
	}

	@Override
	public boolean remove(EPlayer player, Location<World> location, final List<Text> sign) {
		EEMessages.SIGN_BALANCE_BREAK.sendTo(player);
		return false;
	}
	
	@Override
	public boolean valide(Sign sign) {
		return true;
	}
}
