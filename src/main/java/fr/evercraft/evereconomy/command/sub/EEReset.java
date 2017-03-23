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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.sponge.UtilsCause;
import fr.evercraft.everapi.plugin.command.ESubCommand;
import fr.evercraft.evereconomy.EECommand;
import fr.evercraft.evereconomy.EEPermissions;
import fr.evercraft.evereconomy.EverEconomy;
import fr.evercraft.evereconomy.EEMessage.EEMessages;

public class EEReset extends ESubCommand<EverEconomy> {
	public EEReset(final EverEconomy plugin, final EECommand parent) {
        super(plugin, parent, "reset");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.RESET.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.RESET_DESCRIPTION.getFormat().toText(this.plugin.getService().getReplaces());
	}
	
	public Collection<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
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
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 1) {
			Optional<User> user = this.plugin.getEServer().getUser(args.get(1));
			// Le joueur existe
			if (user.isPresent()){
				resultat = commandReset(source, user.get());
			// Le joueur est introuvable
			} else {
				EAMessages.PLAYER_NOT_FOUND.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(source);
			}
		} else {
			source.sendMessage(this.help(source));
		}
		return resultat;
	}

	private boolean commandReset(final CommandSource staff, final User user) {
		boolean resultat = false;
		
		Optional<UniqueAccount> account = this.plugin.getService().getOrCreateAccount(user.getUniqueId());
		// Le compte existe
		if (account.isPresent()) {
			// Reset
			if (account.get().resetBalance(this.plugin.getService().getDefaultCurrency(), UtilsCause.command(this.plugin, staff)).getResult().equals(ResultType.SUCCESS)){
				BigDecimal balance = account.get().getBalance(this.plugin.getService().getDefaultCurrency());
				resultat = true;
				
				HashMap<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
				replaces.putAll(this.plugin.getService().getReplaces());
				replaces.put(Pattern.compile("<player>"), EReplace.of(user.getName()));
				replaces.put(Pattern.compile("<staff>"), EReplace.of(staff.getName()));
				replaces.put(Pattern.compile("<solde>"), EReplace.of(() -> this.plugin.getService().getDefaultCurrency().cast(balance)));
				replaces.put(Pattern.compile("<solde_format>"), EReplace.of(() -> this.plugin.getService().getDefaultCurrency().format(balance)));
				
				// La source et le joueur sont différent
				if (!user.getIdentifier().equals(staff.getIdentifier())) {
					EEMessages.RESET_OTHERS_STAFF.sender()
						.replace(replaces)
						.sendTo(staff);
						
					user.getPlayer().ifPresent(player -> 
						EEMessages.RESET_OTHERS_PLAYER.sender()
						.replace(replaces)
						.sendTo(player));
				// La source et le joueur sont identique
				} else {
					EEMessages.RESET_PLAYER.sender()
						.replace(replaces)
						.sendTo(staff);
				}
			// Impossible de reset
			} else {
				EAMessages.COMMAND_ERROR.sender()
					.prefix(EEMessages.PREFIX)
					.sendTo(staff);
			}
		// Le compte est introuvable
		} else {
			EAMessages.ACCOUNT_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
		}
		return resultat;
	}
}
