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

import fr.evercraft.everapi.plugin.EnumPermission;
import fr.evercraft.everapi.plugin.file.EnumMessage;
import fr.evercraft.evereconomy.EEMessage.EEMessages;

public enum EEPermissions implements EnumPermission {
	EVERECONOMY("commands.execute", EEMessages.PERMISSIONS_COMMANDS_EXECUTE),
	HELP("commands.help", EEMessages.PERMISSIONS_COMMANDS_HELP),
	RELOAD("commands.reload", EEMessages.PERMISSIONS_COMMANDS_RELOAD),
	GIVE("commands.give", EEMessages.PERMISSIONS_COMMANDS_GIVE),
	TAKE("commands.take", EEMessages.PERMISSIONS_COMMANDS_TAKE),
	RESET("commands.reset", EEMessages.PERMISSIONS_COMMANDS_RESET),
	LOG("commands.log.execute", EEMessages.PERMISSIONS_COMMANDS_LOG_EXECUTE),
	LOG_PRINT("commands.log.print", EEMessages.PERMISSIONS_COMMANDS_LOG_PRINT),
	
	BALANCE("commands.balance.execute", EEMessages.PERMISSIONS_COMMANDS_BALANCE_EXECUTE, true),
	BALANCE_OTHERS("commands.balance.others", EEMessages.PERMISSIONS_COMMANDS_BALANCE_OTHERS),
	
	BALANCE_TOP("commands.balancetop.execute", EEMessages.PERMISSIONS_COMMANDS_BALANCETOP_EXECUTE),
	
	PAY("commands.pay.execute", EEMessages.PERMISSIONS_COMMANDS_PAY_EXECUTE);
	
	private static final String PREFIX = "evereconomy";
	
	private final String permission;
	private final EnumMessage message;
	private final boolean value;
    
	private EEPermissions(final String permission, final EnumMessage message) {
    	this(permission, message, false);
    }
    
    private EEPermissions(final String permission, final EnumMessage message, final boolean value) {   	    	
    	this.permission = PREFIX + "." + permission;
    	this.message = message;
    	this.value = value;
    }

    @Override
    public String get() {
    	return this.permission;
	}

	@Override
	public boolean getDefault() {
		return this.value;
	}

	@Override
	public EnumMessage getMessage() {
		return this.message;
	}
}
