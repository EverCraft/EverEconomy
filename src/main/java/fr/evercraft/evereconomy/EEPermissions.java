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
package fr.evercraft.evereconomy;

import org.spongepowered.api.command.CommandSource;

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.plugin.EnumPermission;

public enum EEPermissions implements EnumPermission {
	HELP("help"),
	RELOAD("reload"),
	
	BALANCE("balance.command"),
	BALANCE_OTHERS("balance.others"),
	BALANCE_TOP("balancetop"),
	
	PAY("pay"),
	
	EVERECONOMY("command"),
	GIVE("give"),
	TAKE("take"),
	RESET("reset"),
	
	LOG("log.command"),
	LOG_PRINT("log.print");
	
	private final static String prefix = "evereconomy";
	
	private final String permission;
    
    private EEPermissions(final String permission) {   	
    	Preconditions.checkNotNull(permission, "La permission '" + this.name() + "' n'est pas définit");
    	
    	this.permission = permission;
    }

    public String get() {
		return EEPermissions.prefix + "." + this.permission;
	}
    
    public boolean has(CommandSource player) {
    	return player.hasPermission(this.get());
    }
}