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
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.sponge.UtilsChat;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.evereconomy.EverEconomy;
import fr.evercraft.evereconomy.service.economy.EEconomyService;

public class EEBalanceTop extends ECommand<EverEconomy> {
	
	public EEBalanceTop(final EverEconomy plugin) {
        super(plugin, "balancetop", "moneytop");
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("BALANCE_TOP"));
	}

	public Text description(final CommandSource source) {
		return UtilsChat.of(this.plugin.getService().replace(this.plugin.getMessages().getMessage("BALANCE_TOP_DESCRIPTION")));
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
		if(args.size() == 0) {
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
		if(this.plugin.getService() instanceof EEconomyService) {
			List<Text> lists = new ArrayList<Text>();
			Integer cpt = 1;
			for(Entry<UUID, BigDecimal> player : this.plugin.getService().topUniqueAccount(30).entrySet()) {
				Optional<User> user = this.plugin.getEServer().getUser(player.getKey());
				// Si le User existe bien
				if(user.isPresent()){
					lists.add(ETextBuilder.toBuilder(this.plugin.getService().replace(this.plugin.getMessages().getMessage("BALANCE_TOP_LINE"))
									.replaceAll("<player>", user.get().getName())
									.replaceAll("<number>", cpt.toString())
									.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(player.getValue())))
							.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(player.getValue()))
							.build());
					cpt++;
				}
			}
			
			if(lists.isEmpty()) {
				lists.add(this.plugin.getMessages().getText("BALANCE_TOP_EMPTY"));
			}
			
			this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(this.plugin.getMessages().getText("BALANCE_TOP_TITLE"), lists, staff);
		// Le service d'économie n'est pas EverEconomy
		} else {
			staff.sendMessage(UtilsChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("COMMAND_ERROR")));
		}
		return resultat;
	}
}
