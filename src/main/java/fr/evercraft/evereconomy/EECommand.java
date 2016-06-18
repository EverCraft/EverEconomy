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

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import fr.evercraft.everapi.command.EParentCommand;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.evereconomy.EEMessage.EEMessages;

public class EECommand extends EParentCommand<EverEconomy> {
	
	public EECommand(final EverEconomy plugin) {
        super(plugin, "evereconomy", "evereco", "eco");
    }
	
	@Override
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.EVERECONOMY.get());
	}

	@Override
	public Text description(final CommandSource source) {
		return EChat.of(this.plugin.getService().replace(EEMessages.DESCRIPTION.get()));
	}

	@Override
	public boolean testPermissionHelp(final CommandSource source) {
		return source.hasPermission(EEPermissions.HELP.get());
	}
}
