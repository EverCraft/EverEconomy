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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.sponge.UtilsCause;
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
		return EEMessages.PAY_DESCRIPTION.getFormat().toText(this.plugin.getService().getReplaces());
	}
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		if (args.size() == 1) {
			return this.getAllPlayers(source, true);
		} else if (args.size() == 2) {
			return Arrays.asList("1");
		}
		return Arrays.asList();
	}

	public Text help(final CommandSource source) {
		return Text.builder("/" + this.getName() + " <" + EAMessages.ARGS_PLAYER.getString() + "> "
												  + "<" + EAMessages.ARGS_AMOUNT.getString() + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName()))
					.color(TextColors.RED)
					.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource source, final List<String> args) throws CommandException {
		// On connait le joueur et le montant
		if (args.size() == 2) {
			if (source instanceof EPlayer) {
				Optional<EPlayer> optPlayer = this.plugin.getEServer().getEPlayer(args.get(0));
				// Le joueur destination existe
				if (optPlayer.isPresent()) {
					return this.executePay((EPlayer) source, optPlayer.get(), args.get(1));
				// Le joueur destination est introuvable
				} else {
					EAMessages.PLAYER_NOT_FOUND.sender()
						.prefix(EEMessages.PREFIX)
						.replace("{player}", args.get(0))
						.sendTo(source);
				}
			} else {
				EAMessages.COMMAND_ERROR_FOR_PLAYER.sender().prefix(EEMessages.PREFIX).sendTo(source);
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(this.help(source));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	public CompletableFuture<Boolean> executePay(final EPlayer staff, final EPlayer player, final String amount_name) {
		Optional<UniqueAccount> staff_account = staff.getAccount();
		Optional<UniqueAccount> player_account = player.getAccount();
		
		// Le compte existe
		if (staff_account.isPresent() && player_account.isPresent()) {
			EAMessages.ACCOUNT_NOT_FOUND.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		// Nombre valide
		BigDecimal amount = null;
		try {
			amount = new BigDecimal(Double.parseDouble(amount_name));
		// Nombre invalide
		} catch(NumberFormatException e) {
			EAMessages.IS_NOT_NUMBER.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
			
		amount = amount.setScale(this.plugin.getService().getDefaultCurrency().getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP);
		
		HashMap<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
		replaces.putAll(this.plugin.getService().getReplaces());
		replaces.put(Pattern.compile("\\{player}"), EReplace.of(player.getName()));
		replaces.put(Pattern.compile("\\{amount}"), EReplace.of(this.plugin.getService().getDefaultCurrency().cast(amount)));
		replaces.put(Pattern.compile("\\{amount_format}"), EReplace.of(this.plugin.getService().getDefaultCurrency().format(amount)));
		
		// La source et le joueur sont identique
		if (staff.equals(player)) {
			EEMessages.PAY_ERROR_EQUALS.sender().replace(replaces).sendTo(staff);
			return CompletableFuture.completedFuture(false);
		}
		
		ResultType result = staff_account.get().transfer(player_account.get(), this.plugin.getService().getDefaultCurrency(), amount, UtilsCause.command(this.plugin, staff)).getResult();
		BigDecimal staff_balance = staff_account.get().getBalance(this.plugin.getService().getDefaultCurrency());
		BigDecimal player_balance = player_account.get().getBalance(this.plugin.getService().getDefaultCurrency());
		
		replaces.put(Pattern.compile("\\{solde}"), EReplace.of(() -> this.plugin.getService().getDefaultCurrency().cast(staff_balance)));
		replaces.put(Pattern.compile("\\{solde_format}"), EReplace.of(() -> this.plugin.getService().getDefaultCurrency().format(staff_balance)));
		
		// Transfert réussit
		if (result.equals(ResultType.SUCCESS)) {
			EEMessages.PAY_STAFF.sender().replace(replaces).sendTo(staff);
			
			replaces.put(Pattern.compile("\\{solde}"), EReplace.of(() -> this.plugin.getService().getDefaultCurrency().cast(player_balance)));
			replaces.put(Pattern.compile("\\{solde_format}"), EReplace.of(() -> this.plugin.getService().getDefaultCurrency().format(player_balance)));
			EEMessages.PAY_PLAYER.sender().replace(replaces).sendTo(player);
		// Transfert erreur
		} else if (result.equals(ResultType.ACCOUNT_NO_FUNDS)) {
			EEMessages.PAY_ERROR_MIN.sender().replace(replaces).sendTo(staff);
		} else if (result.equals(ResultType.ACCOUNT_NO_SPACE)) {
			EEMessages.PAY_ERROR_MAX.sender().replace(replaces).sendTo(staff);
		} else {
			EAMessages.NUMBER_INVALID.sender().prefix(EEMessages.PREFIX).sendTo(staff);
		}
		return CompletableFuture.completedFuture(true);
	}
}
