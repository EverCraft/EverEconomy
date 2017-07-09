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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.message.replace.EReplace;
import fr.evercraft.everapi.plugin.command.ECommand;
import fr.evercraft.everapi.server.user.EUser;
import fr.evercraft.evereconomy.EEMessage.EEMessages;
import fr.evercraft.evereconomy.EEPermissions;
import fr.evercraft.evereconomy.EverEconomy;

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
	
	public Collection<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		return new ArrayList<String>();
	}

	public Text help(final CommandSource source) {
		return Text.builder(this.getName())
				.onClick(TextActions.suggestCommand(this.getName()))
				.color(TextColors.RED)
				.build();
	}
	
	public CompletableFuture<Boolean> execute(final CommandSource staff, final List<String> args) throws CommandException {
		// Nombre d'argument correct
		if (args.size() == 0) {
			this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.commandBalanceTop(staff))
				.name("commandBalanceTop").submit(this.plugin);
			return CompletableFuture.completedFuture(true);
		// Nombre d'argument incorrect
		} else {
			staff.sendMessage(this.help(staff));
		}
		return CompletableFuture.completedFuture(false);
	}
	
	public CompletableFuture<Boolean> commandBalanceTop(final CommandSource staff) {
		List<Text> lists = new ArrayList<Text>();
		Integer cpt = 1;
		for (Entry<UUID, BigDecimal> player : this.plugin.getService().topUniqueAccount(30).entrySet()) {
			Optional<EUser> user = this.plugin.getEServer().getEUser(player.getKey());
			// Si le User existe bien
			if (user.isPresent()){
				HashMap<Pattern, EReplace<?>> replaces = new HashMap<Pattern, EReplace<?>>();
				replaces.putAll(this.plugin.getService().getReplaces());
				replaces.put(Pattern.compile("<player>"), EReplace.of(user.get().getName()));
				replaces.put(Pattern.compile("<number>"), EReplace.of(cpt.toString()));
				replaces.put(Pattern.compile("<solde>"), EReplace.of(() -> this.plugin.getService().getDefaultCurrency().cast(player.getValue())));
				replaces.put(Pattern.compile("<solde_format>"), EReplace.of(() -> this.plugin.getService().getDefaultCurrency().format(player.getValue())));
				
				lists.add(EEMessages.BALANCE_TOP_LINE.getFormat().toText(replaces));
				cpt++;
			}
		}
		
		if (lists.isEmpty()) {
			lists.add(EEMessages.BALANCE_TOP_EMPTY.getText());
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EEMessages.BALANCE_TOP_TITLE.getText(), lists, staff);
		return CompletableFuture.completedFuture(true);
	}
}
