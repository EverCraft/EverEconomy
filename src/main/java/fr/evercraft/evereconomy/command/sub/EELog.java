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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.evereconomy.EECommand;
import fr.evercraft.evereconomy.EEPermissions;
import fr.evercraft.evereconomy.EverEconomy;
import fr.evercraft.evereconomy.EEMessage.EEMessages;
import fr.evercraft.evereconomy.service.economy.ELog;

public class EELog extends ESubCommand<EverEconomy> {

	public EELog(final EverEconomy plugin, final EECommand parent) {
        super(plugin, parent, "log");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.LOG.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.RESET_DESCRIPTION.getFormat().toText(this.plugin.getService().getReplaces());
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1){
			return this.getAllUsers(args.get(0));
		}
		return Arrays.asList();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_USER.getString() + ">")
				.onClick(TextActions.suggestCommand("/" + this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) {
		if (args.size() == 1) {
			final Optional<User> user = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if (user.isPresent()){
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.commandLog(source, user.get()))
					.name("commandLog").submit(this.plugin);
				return CompletableFuture.completedFuture(true);
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.replace("{player}", args.get(0))
					.sendTo(source);
			}
		} else if (args.size() == 2) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.LOG_PRINT.get())){
				final Optional<User> user = this.plugin.getEServer().getUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()){
					this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.commandLogPrint(source, user.get(), args.get(1)))
						.name("commandLogPrint").submit(this.plugin);
					return CompletableFuture.completedFuture(true);
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{player}", args.get(0))
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}

	private void commandLog(final CommandSource player, final User user) {
		List<Text> lists = new ArrayList<Text>();
		
		for (ELog log : this.plugin.getDataBases().selectLog(user.getIdentifier(), this.plugin.getService().getDefaultCurrency())) {
			lists.add(log.replace(EEMessages.LOG_LINE_TRANSACTION.getFormat(),
									EEMessages.LOG_LINE_TRANSFERT.getFormat()));
		}
		
		if (lists.isEmpty()) {
			lists.add(EEMessages.LOG_EMPTY.getText());
		}
		
		Map<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(this.plugin.getService().getReplaces());
		replaces.put(Pattern.compile("{player}"), EReplace.of(user.getName()));
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(
				EEMessages.LOG_TITLE.getFormat().toText(replaces), 
				lists, player);
	}
	
	private void commandLogPrint(final CommandSource player, final User user, final String string) {
		List<ELog> logs = this.plugin.getDataBases().selectLog(user.getIdentifier(), this.plugin.getService().getDefaultCurrency());
		
		if (logs.isEmpty()) {
			if (player.getIdentifier().equals(user.getIdentifier())) {
				EEMessages.LOG_PRINT_EMPTY_EQUALS.sender()
					.replace(this.plugin.getService().getReplaces())
					.sendTo(player);
			} else {
				EEMessages.LOG_PRINT_EMPTY.sender()
					.replace(this.plugin.getService().getReplaces())
					.sendTo(player);
			}
		} else {
			File file = this.plugin.getPath().resolve("logs/" + user.getName() + ".log").toFile();
			if (!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			
			FileWriter write = null;
			try {
				write = new FileWriter(file);
				for (ELog log : this.plugin.getDataBases().selectLog(user.getIdentifier(), this.plugin.getService().getDefaultCurrency())) {
					write.write(log.replace(EEMessages.LOG_PRINT_LINE_TRANSACTION.getFormat(),
											EEMessages.LOG_PRINT_LINE_TRANSFERT.getFormat()).toPlain() + "\n");
				}
				
				if (player.getIdentifier().equals(user.getIdentifier())) {
					EEMessages.LOG_PRINT.sender()
						.replace(this.plugin.getService().getReplaces())
						.replace("{player}", user.getName())
						.replace("{file}", file.getName())
						.sendTo(player);
				} else {
					EEMessages.LOG_PRINT_EQUALS.sender()
						.replace(this.plugin.getService().getReplaces())
						.replace("{player}", user.getName())
						.replace("{file}", file.getName())
						.sendTo(player);
				}
			} catch (IOException e) {
				EAMessages.COMMAND_ERROR.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(player);
			} finally {
				try {if (write != null) write.close();} catch (IOException e) {}
			}
		}
	}
}
