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
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsCause;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.evereconomy.EEMessage.EEMessages;
import fr.evercraft.evereconomy.EEPermissions;
import fr.evercraft.evereconomy.EverEconomy;

public class EEPay extends ECommand<EverEconomy> {
	
	public EEPay(final EverEconomy plugin) {
        super(plugin, "pay");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.PAY.get());
	}

	public Text description(final CommandSource source) {
		return EChat.of(this.plugin.getService().replace(EEMessages.PAY_DESCRIPTION.get()));
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			suggests = null;
		} else {
			suggests.add("1");
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.get() + "> "
												  + "<" + EAMessages.ARGS_AMOUNT.get() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public boolean execute(final CommandSource source, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// On connait le joueur et le montant
		if(args.size() == 2) {
			if(source instanceof EPlayer) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur destination existe
				if(optPlayer.isPresent()) {
					resultat = executePay((EPlayer) source, optPlayer.get(), args.get(1));
				// Le joueur destination est introuvable
				} else {
					source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.PLAYER_NOT_FOUND.getText()));
				}
			} else {
				source.sendMessage(EEMessages.PREFIX.getText().concat(EAMessages.COMMAND_ERROR_FOR_PLAYER.getText()));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(help(source));
		}
		return resultat;
	}
	
	public boolean executePay(final EPlayer staff, final EPlayer player, final String amount_name) {
		// Résultat de la commande :
		boolean resultat = false;
		Optional<UniqueAccount> staff_account = staff.getAccount();
		Optional<UniqueAccount> player_account = player.getAccount();
		// Le compte existe
		if(staff_account.isPresent() && player_account.isPresent()) {
			// Nombre valide
			try {
				BigDecimal amount = new BigDecimal(Double.parseDouble(amount_name));
				amount = amount.setScale(this.plugin.getService().getDefaultCurrency().getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP);
				// La source et le joueur sont différent
				if(!staff.equals(player)){
					ResultType result = staff_account.get().transfer(player_account.get(), this.plugin.getService().getDefaultCurrency(), amount, UtilsCause.command(this.plugin, staff)).getResult();
					BigDecimal staff_balance = staff_account.get().getBalance(this.plugin.getService().getDefaultCurrency());
					BigDecimal player_balance = player_account.get().getBalance(this.plugin.getService().getDefaultCurrency());
					
					// Transfert réussit
					if(result.equals(ResultType.SUCCESS)) {
						staff.sendMessage(
								ETextBuilder.toBuilder(EEMessages.PREFIX.get())
									.append(this.plugin.getService().replace(EEMessages.PAY_STAFF.get())
											.replaceAll("<player>", player.getName())
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(staff_balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(staff_balance))
									.build());
						player.sendMessage(
								ETextBuilder.toBuilder(EEMessages.PREFIX.get())
									.append(this.plugin.getService().replace(EEMessages.PAY_PLAYER.get())
											.replaceAll("<staff>", staff.getName())
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(player_balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(player_balance))
									.build());
					// Transfert erreur
					} else if(result.equals(ResultType.ACCOUNT_NO_FUNDS)) {
						staff.sendMessage(
								ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
									.append(this.plugin.getService().replace(EEMessages.PAY_ERROR_MIN.get())
											.replaceAll("<player>", player.getName())
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(staff_balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(staff_balance))
									.build());
					} else if(result.equals(ResultType.ACCOUNT_NO_SPACE)) {
						staff.sendMessage(
								ETextBuilder.toBuilder(EEMessages.PREFIX.getText())
									.append(this.plugin.getService().replace(EEMessages.PAY_ERROR_MAX.get())
											.replaceAll("<staff>", staff.getName())
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(staff_balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(staff_balance))
									.build());
					} else {
						staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.NUMBER_INVALID.get()));
					}
				// La source et le joueur sont identique
				} else {
					staff.sendMessage(
							ETextBuilder.toBuilder(EEMessages.PREFIX.get())
								.append(this.plugin.getService().replace(EEMessages.PAY_ERROR_EQUALS.get())
										.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount)))
								.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
								.build());
				}
			// Nombre invalide
			} catch(NumberFormatException e) {
				staff.sendMessage(EEMessages.PREFIX.get() + EAMessages.NUMBER_INVALID.get());
			}
		// Le compte est introuvable
		} else {
			staff.sendMessage(EChat.of(EEMessages.PREFIX.get() + EAMessages.ACCOUNT_NOT_FOUND.get()));
		}
		return resultat;
	}
}
