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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.EAMessage.EAMessages;
import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.evereconomy.EEMessage.EEMessages;
import fr.evercraft.evereconomy.EEPermissions;
import fr.evercraft.evereconomy.EverEconomy;
import fr.evercraft.evereconomy.service.economy.EEconomyService;

public class EEBalanceTop extends ECommand<EverEconomy> {
	
	public EEBalanceTop(final EverEconomy plugin) {
        super(plugin, "balancetop", "moneytop");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(EEPermissions.BALANCE_TOP.get());
	}

	public Text description(final CommandSource source) {
		return EEMessages.BALANCE_TOP_DESCRIPTION.getFormat().toText(this.plugin.getService().getReplaces());
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	public Text help(final CommandSource source) {
		return Text.builder(this.getName())
				.onClick(TextActions.suggestCommand(this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	public boolean execute(final CommandSource staff, final List<String> args) throws CommandException {
		// Résultat de la commande :
		boolean resultat = false;
		
		// Nombre d'argument correct
		if (args.size() == 0) {
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.commandBalanceTop(staff))
				.name("commandBalanceTop").submit(this.plugin);
			resultat = true;
		// Nombre d'argument incorrect
		} else {
			staff.sendMessage(help(staff));
		}
		return resultat;
	}
	
	public boolean commandBalanceTop(final CommandSource staff) {
		// Résultat de la commande :
		boolean resultat = false; 
		
		// Si le service d'économie est bien EverEconomy
		if (this.plugin.getService() instanceof EEconomyService) {
			List<Text> lists = new ArrayList<Text>();
			Integer cpt = 1;
			for (Entry<UUID, BigDecimal> player : this.plugin.getService().topUniqueAccount(30).entrySet()) {
				Optional<EUser> user = this.plugin.getEServer().getEUser(player.getKey());
				// Si le User existe bien
				if (user.isPresent()){
					HashMap<String, EReplace<?>> replaces = new HashMap<String, EReplace<?>>();
					replaces.putAll(this.plugin.getService().getReplaces());
					replaces.put("<player>", EReplace.of(user.get().getName()));
					replaces.put("<number>", EReplace.of(cpt.toString()));
					replaces.put("<solde>", EReplace.of(() -> this.plugin.getService().getDefaultCurrency().cast(player.getValue())));
					replaces.put("<solde_format>", EReplace.of(() -> this.plugin.getService().getDefaultCurrency().format(player.getValue())));
					
					lists.add(EEMessages.BALANCE_TOP_LINE.getFormat().toText(replaces));
					cpt++;
				}
			}
			
			if (lists.isEmpty()) {
				lists.add(EEMessages.BALANCE_TOP_EMPTY.getText());
			}
			
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.BALANCE_TOP_TITLE.getText(), lists, staff);
		// Le service d'économie n'est pas EverEconomy
		} else {
			EAMessages.COMMAND_ERROR.sender()
				.prefix(EEMessages.PREFIX)
				.sendTo(staff);
		}
		return resultat;
	}
}
