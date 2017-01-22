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
package fr.evercraft.evereconomy.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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
import fr.evercraft.everapi.plugin.command.ECommand;
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
		return EEMessages.BALANCE_DESCRIPTION.getFormat().toText(this.plugin.getService().getReplaces());
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1 && source.hasPermission(EEPermissions.BALANCE_OTHERS.get())){
			suggests.addAll(this.getAllPlayers(source));
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		Text help;
		if (source.hasPermission(EEPermissions.BALANCE_OTHERS.get())){
			help = Text.builder("/balance [" + EAMessages.ARGS_PLAYER.getString() + "]")
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
		if (args.size() == 0) {
			// Si la source est bien un joueur
			if (source instanceof EPlayer) {
				resultat = executeBalance((EPlayer) source);
			// Si la source est une console ou un commande block
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sendTo(source);
			}
			
		// Si on connait le joueur
		} else if (args.size() == 1) {
			// Si il a la permission
			if (source.hasPermission(EEPermissions.BALANCE_OTHERS.get())){
				Optional<User> user = this.plugin.getEServer().getUser(args.get(0));
				// Le joueur existe
				if (user.isPresent()) {
					resultat = executeBalanceOthers(source, user.get());
				// Le joueur est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.sendTo(source);
				}
			// Il n'a pas la permission
			} else {
				EAMessages.NO_PERMISSION.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.getHelp(source).get());
		}
		return resultat;
	}
	
	public boolean executeBalance(final EPlayer player) {
		BigDecimal balance = player.getBalance();
		EEMessages.BALANCE_PLAYER.sender()
			.replace(this.plugin.getService().getReplaces())
			.replace("<solde>", () -> this.plugin.getService().getDefaultCurrency().cast(balance))
			.replace("<solde_format>", () -> this.plugin.getService().getDefaultCurrency().format(balance))
			.sendTo(player);
		return true;
	}
	
	public boolean executeBalanceOthers(final CommandSource staff, final User user) {
		// Résultat de la commande :
		boolean resultat = false;
		
		// La source et le joueur sont différent
		if (!user.getIdentifier().equals(staff.getIdentifier()) || !(staff instanceof EPlayer)){
			Optional<UniqueAccount> account = this.plugin.getService().getOrCreateAccount(user.getUniqueId());
			// Le compte existe
			if (account.isPresent()) {
				BigDecimal balance = account.get().getBalance(this.plugin.getService().getDefaultCurrency());
				EEMessages.BALANCE_OTHERS.sender()
					.replace(this.plugin.getService().getReplaces())
					.replace("<player>", () -> user.getName())
					.replace("<solde>", () -> this.plugin.getService().getDefaultCurrency().cast(balance))
					.replace("<solde_format>", () -> this.plugin.getService().getDefaultCurrency().format(balance))
					.sendTo(staff);
				resultat = true;
			// Le compte est introuvable
			} else {
				EAMessages.ACCOUNT_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(staff);
			}
		// La source et le joueur sont identique
		} else {
			this.executeBalance((EPlayer) staff);
		}
		return resultat;
	}
}