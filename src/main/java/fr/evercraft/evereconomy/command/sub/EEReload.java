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
package fr.evercraft.evereconomy.command.sub;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.evereconomy.EECommand;
import fr.evercraft.evereconomy.EEPermissions;
import fr.evercraft.evereconomy.EverEconomy;
import fr.evercraft.evereconomy.EEMessage.EEMessages;

public class EEReload extends ESubCommand<EverEconomy> {
	public EEReload(final EverEconomy plugin, final EECommand command) {
        super(plugin, command, "reload");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.RELOAD.get());
	}

	public Text description(final CommandSource source) {
		return EAMessages.RELOAD_DESCRIPTION.getFormat().toText(this.plugin.getService().getReplaces());
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return Arrays.asList();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName())
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if (args.size() == 0) {
			return this.commandReload(source);
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}

	private CompletableFuture<Boolean> commandReload(final CommandSource player) {
		this.plugin.reload();
		EAMessages.RELOAD_COMMAND.sender()
			.prefix(EEMessages.PREFIX)
			.sendTo(player);
		return CompletableFuture.completedFuture(true);
	}
}
