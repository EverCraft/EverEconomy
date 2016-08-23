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
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.everapi.text.ETextBuilder;
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
		return EChat.of(this.plugin.getService().replace(EEMessages.RESET_DESCRIPTION.get()));
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests = null;
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.get() + ">")
				.onClick(TextActions.suggestCommand("/" + this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 1) {
			final Optional<User> user = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if (user.isPresent()){
				this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.commandLog(source, user.get()))
					.name("commandLog").submit(this.plugin);
				resultat = true;
			// Le joueur est introuvable
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
			}
		} else if (args.size() == 2) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.LOG_PRINT.get())){
				final Optional<User> user = this.plugin.getEServer().getUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()){
					this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.commandLogPrint(source, user.get(), args.get(1)))
						.name("commandLogPrint").submit(this.plugin);
					resultat = true;
				// Le joueur est introuvable
				} else {
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	private void commandLog(final CommandSource player, final User user) {
		List<Text> lists = new ArrayList<Text>();
		
		for (ELog log : this.plugin.getDataBases().selectLog(user.getIdentifier(), this.plugin.getService().getDefaultCurrency())) {
			lists.add(log.replace(EEMessages.LOG_LINE_TRANSACTION.get(),
									EEMessages.LOG_LINE_TRANSFERT.get()));
		}
		
		if (lists.isEmpty()) {
			lists.add(EEMessages.LOG_EMPTY.getText());
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(this.plugin.getService().replace(
				EEMessages.LOG_TITLE.get()
					.replaceAll("<player>", user.getName()))), 
				lists, player);
	}
	
	private void commandLogPrint(final CommandSource player, final User user, final String string) {
		List<ELog> logs = this.plugin.getDataBases().selectLog(user.getIdentifier(), this.plugin.getService().getDefaultCurrency());
		
		if (logs.isEmpty()) {
			if (player.getIdentifier().equals(user.getIdentifier())) {
				player.sendMessage(EChat.of(EEMessages.PREFIX.get() + this.plugin.getService().replace(EEMessages.LOG_PRINT_EMPTY_EQUALS.get())));
			} else {
				player.sendMessage(EChat.of(EEMessages.PREFIX.get() + this.plugin.getService().replace(EEMessages.LOG_PRINT_EMPTY.get())));
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
					write.write(log.replace(EEMessages.LOG_PRINT_LINE_TRANSACTION.get(),
											EEMessages.LOG_PRINT_LINE_TRANSFERT.get()).toPlain() + "\n");
				}
				
				if (player.getIdentifier().equals(user.getIdentifier())) {
					player.sendMessage(
							ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(this.plugin.getService().replace(EEMessages.LOG_PRINT.get())
										.replaceAll("<player>", user.getName())
										.replaceAll("<file>", file.getName()))
								.build());
				} else {
					player.sendMessage(
							ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(this.plugin.getService().replace(EEMessages.LOG_PRINT_EQUALS.get())
										.replaceAll("<player>", user.getName())
										.replaceAll("<file>", file.getName()))
								.build());
				}
			} catch (IOException e) {
				player.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR.get()));
			} finally {
				try {if (write != null) write.close();} catch (IOException e) {}
			}
		}
	}
}
