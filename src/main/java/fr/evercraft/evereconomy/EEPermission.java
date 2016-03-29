/**
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
package fr.evercraft.evereconomy;

import fr.evercraft.everapi.plugin.EPermission;
import fr.evercraft.everapi.plugin.EPlugin;

public class EEPermission extends EPermission {

	public EEPermission(EPlugin plugin) {
		super(plugin);
	}

	@Override
	protected void load() {
		add("HELP", "help");
		add("RELOAD", "reload");
		
		add("BALANCE", "balance.command");
		add("BALANCE_OTHERS", "balance.others");
		add("BALANCE_TOP", "balancetop");
		
		add("PAY", "pay");
		
		add("EVERECONOMY", "command");
		add("GIVE", "give");
		add("TAKE", "take");
		add("RESET", "reset");
		
		add("LOG", "log.command");
		add("LOG_PRINT", "log.print");
	}
}
