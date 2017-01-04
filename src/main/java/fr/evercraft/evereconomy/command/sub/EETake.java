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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

public class EETake extends ESubCommand<EverEconomy> {
	public EETake(final EverEconomy plugin, final EECommand parent) {
        super(plugin, parent, "take");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.TAKE.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.TAKE_DESCRIPTION.getFormat().toText(this.plugin.getService().getReplaces());
	}
	
	public List<String> subTabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if (args.size() == 1){
			suggests = null;
		} else if (args.size() == 2){
			suggests.add("1");
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.getString() + "> "
				  + "<" + EAMessages.ARGS_AMOUNT.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean subExecute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		
		if (args.size() == 2) {
			Optional<User> user = this.plugin.getEServer().getUser(args.get(0));
			// Le joueur existe
			if (user.isPresent()){
				resultat = commandTake(source, user.get(), args.get(1));
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

	private boolean commandTake(final CommandSource staff, final User user, final String amount_string) {
		Optional<UniqueAccount> account = this.plugin.getService().getOrCreateAccount(user.getUniqueId());
		// Le compte est introuvable
		if (!account.isPresent()) {
			EAMessages.ACCOUNT_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
			return false;
		}
		
		boolean resultat = false;

		// Nombre valide
		try {
			BigDecimal amount = new BigDecimal(Double.parseDouble(amount_string));
			amount = amount.setScale(this.plugin.getService().getDefaultCurrency().getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP);
			
			ResultType result = account.get().withdraw(this.plugin.getService().getDefaultCurrency(), amount, UtilsCause.command(this.plugin, staff)).getResult();
			BigDecimal balance = account.get().getBalance(this.plugin.getService().getDefaultCurrency());
			
			HashMap<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
			replaces.putAll(this.plugin.getService().getReplaces());
			replaces.put("<player>", EReplace.of(user.getName()));
			replaces.put("<staff>", EReplace.of(staff.getName()));
			replaces.put("<amount>", EReplace.of(this.plugin.getService().getDefaultCurrency().cast(amount)));
			replaces.put("<amount_format>", EReplace.of(this.plugin.getService().getDefaultCurrency().format(amount)));
			replaces.put("<solde>", EReplace.of(() -> this.plugin.getService().getDefaultCurrency().cast(balance)));
			replaces.put("<solde_format>", EReplace.of(() -> this.plugin.getService().getDefaultCurrency().format(balance)));
			
			// Le compte existe
			if (result.equals(ResultType.SUCCESS)) {
				resultat = true;
				// La source et le joueur sont différent
				if (!user.getIdentifier().equals(staff.getIdentifier())) {
					EEMessages.TAKE_OTHERS_STAFF.sender()
						.replace(replaces)
						.sendTo(staff);
					
					user.getPlayer().ifPresent(player -> 
						EEMessages.TAKE_OTHERS_PLAYER.sender()
							.replace(replaces)
							.sendTo(player));
				// La source et le joueur sont identique
				} else {
					EEMessages.TAKE_PLAYER.sender()
						.replace(replaces)
						.sendTo(staff);
				}
			// Min quantité
			} else if (result.equals(ResultType.ACCOUNT_NO_FUNDS)) {
				// La source et le joueur sont différent
				if (!user.getIdentifier().equals(staff.getIdentifier())) {
					EEMessages.TAKE_ERROR_MIN.sender()
						.replace(replaces)
						.sendTo(staff);
				// La source et le joueur sont identique
				} else {
					EEMessages.GIVE_ERROR_MAX_EQUALS.sender()
						.replace(replaces)
						.sendTo(staff);
				}
			} else {
				EAMessages.NUMBER_INVALID.sender()
					.prefix(EEMessages.PREFIX)
					.replace(replaces)
					.sendTo(staff);
			}
		// Nombre invalide
		} catch(NumberFormatException e) {
			EAMessages.NUMBER_INVALID.sender()
				.prefix(EEMessages.PREFIX)
				.replace("<number>", amount_string)
				.sendTo(staff);
		}
		return resultat;
	}
}
