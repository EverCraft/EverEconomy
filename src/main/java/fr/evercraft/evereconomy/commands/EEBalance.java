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
package fr.evercraft.evereconomy.commands;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.evereconomy.EEMessage.EEMessages;
import fr.evercraft.evereconomy.EEPermissions;
import fr.evercraft.evereconomy.EverEconomy;

public class EEBalance extends ECommand<EverEconomy> {
	
	public EEBalance(final EverEconomy plugin) {
        super(plugin, "balance", "money");
    }
		
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BALANCE.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(this.plugin.getService().replace(EEMessages.BALANCE_DESCRIPTION.get()));
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1 && source.hasPermission(EEPermissions.BALANCE_OTHERS.get())){
			suggests = null;
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		Text help;
		if(source.hasPermission(EEPermissions.BALANCE_OTHERS.get())){
			help = Text.builder("/balance [" + EAMessages.ARGS_PLAYER.get() + "]")
					.onClick(TextActions.suggestCommand("/balance "))
					.color(TextColors.RED)
					.build();
		} else {
			help = Text.builder("/balance")
					.onClick(TextActions.suggestCommand("/balance"))
					.color(TextColors.RED)
					.build();
		}
		return help;
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Si ne connais pas le joueur
		if(args.size() == 0) {
			// Si la source est bien un joueur
			if(source instanceof EPlayer) {
				resultat = executeBalance((EPlayer) source);
			// Si la source est une console ou un commande block
			} else {
				source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.COMMAND_ERROR_FOR_PLAYER.get()));
			}
			
		// Si on connait le joueur
		} else if(args.size() == 1) {
			// Si il a la permission
			if(source.hasPermission(EEPermissions.BALANCE_OTHERS.get())){
				Optional<User> user = this.plugin.getEServer().getUser(args.get(0));
				// Le joueur existe
				if(user.isPresent()) {
					resultat = executeBalanceOthers(source, user.get());
				// Le joueur est introuvable
				} else {
					source.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.PLAYER_NOT_FOUND.get()));
				}
			// Il n'a pas la permission
			} else {
				source.sendMessage(EAMessages.NO_PERMISSION.getText());
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(getHelp(source).get());
		}
		return resultat;
	}
	
	public boolean executeBalance(final EPlayer player) {
		BigDecimal balance = player.getBalance();
		player.sendMessage(
				ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(this.plugin.getService().replace(EEMessages.BALANCE_PLAYER.get())
							.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
					.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
					.build());
		return true;
	}
	
	public boolean executeBalanceOthers(final CommandSource staff, final User user) {
		// Résultat de la commande :
		boolean resultat = false;
		
		// La source et le joueur sont différent
		if(!user.getIdentifier().equals(staff.getIdentifier()) || !(staff instanceof EPlayer)){
			Optional<UniqueAccount> account = this.plugin.getService().getOrCreateAccount(user.getUniqueId());
			// Le compte existe
			if(account.isPresent()) {
				BigDecimal balance = account.get().getBalance(this.plugin.getService().getDefaultCurrency());
				staff.sendMessage(
						ETextBuilder.toBuilder(EEMessages.PREFIX.get())
							.append(this.plugin.getService().replace(EEMessages.BALANCE_OTHERS.get())
									.replaceAll("<player>", user.getName())
									.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
							.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
							.build());
				resultat = true;
			// Le compte est introuvable
			} else {
				staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.ACCOUNT_NOT_FOUND.get()));
			}
		// La source et le joueur sont identique
		} else {
			executeBalance((EPlayer) staff);
		}
		return resultat;
	}
}
