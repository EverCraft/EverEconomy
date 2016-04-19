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
package fr.evercraft.evereconomy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.LiteralText.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import fr.evercraft.everapi.services.pagination.ESubCommand;
import fr.evercraft.everapi.sponge.UtilsCause;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.ECommand;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.evereconomy.commands.EEBalance;
import fr.evercraft.evereconomy.commands.EEBalanceTop;
import fr.evercraft.evereconomy.commands.EEPay;
import fr.evercraft.evereconomy.service.economy.ELog;

public class EECommand extends ECommand<EverEconomy> {
	private final EEBalance balance;
	private final EEBalanceTop top;
	private final EEPay pay;
	
	public EECommand(final EverEconomy plugin) {
        super(plugin, "evereconomy", "evereco", "eco");
        
        this.balance = new EEBalance(this.plugin);
        this.top = new EEBalanceTop(this.plugin);
        this.pay = new EEPay(this.plugin);
    }
	
	public boolean testPermission(final CommandSource source) {
		return source.hasPermission(this.plugin.getPermissions().get("EVERECONOMY"));
	}

	public Text description(final CommandSource source) {
		return EChat.of(this.plugin.getService().replace(this.plugin.getMessages().getMessage("DESCRIPTION")));
	}
	
	public List<String> tabCompleter(final CommandSource source, final List<String> args) throws CommandException {
		List<String> suggests = new ArrayList<String>();
		if(args.size() == 1){
			if(source.hasPermission(this.plugin.getPermissions().get("HELP"))){
				suggests.add("help");
			}
			if(source.hasPermission(this.plugin.getPermissions().get("RELOAD"))){
				suggests.add("reload");
			}
			if(source.hasPermission(this.plugin.getPermissions().get("RESET"))){
				suggests.add("reset");
			}
			if(source.hasPermission(this.plugin.getPermissions().get("GIVE"))){
				suggests.add("give");
			}
			if(source.hasPermission(this.plugin.getPermissions().get("TAKE"))){
				suggests.add("take");
			}
			if(source.hasPermission(this.plugin.getPermissions().get("LOG"))){
				suggests.add("log");
			}
		} else if(args.size() == 2){
			if(args.get(0).equalsIgnoreCase("reset")) {
				if(source.hasPermission(this.plugin.getPermissions().get("RESET"))){
					suggests = null;
				}
			} else if(args.get(0).equalsIgnoreCase("give")) {
				if(source.hasPermission(this.plugin.getPermissions().get("GIVE"))){
					suggests = null;
				}
			} else if(args.get(0).equalsIgnoreCase("take")) {
				if(source.hasPermission(this.plugin.getPermissions().get("TAKE"))){
					suggests = null;
				}
			} else if(args.get(0).equalsIgnoreCase("log")) {
				if(source.hasPermission(this.plugin.getPermissions().get("LOG"))){
					suggests = null;
				}
			}
		} else if(args.size() == 2){
			if(args.get(0).equalsIgnoreCase("give")) {
				if(source.hasPermission(this.plugin.getPermissions().get("GIVE"))){
					suggests.add("1");
				}
			} else if(args.get(0).equalsIgnoreCase("take")) {
				if(source.hasPermission(this.plugin.getPermissions().get("TAKE"))){
					suggests.add("1");
				}
			} else if(args.get(0).equalsIgnoreCase("log")) {
				if(source.hasPermission(this.plugin.getPermissions().get("LOG")) && source.hasPermission(this.plugin.getPermissions().get("LOG_PRINT"))){
					suggests.add("print");
				}
			}
		}
		return suggests;
	}

	public Text help(final CommandSource source) {
		boolean help = source.hasPermission(this.plugin.getPermissions().get("HELP"));
		boolean reload = source.hasPermission(this.plugin.getPermissions().get("RELOAD"));
		boolean give = source.hasPermission(this.plugin.getPermissions().get("GIVE"));
		boolean take = source.hasPermission(this.plugin.getPermissions().get("TAKE"));
		boolean reset = source.hasPermission(this.plugin.getPermissions().get("RESET"));
		boolean log = source.hasPermission(this.plugin.getPermissions().get("LOG"));

		Builder build;
		if(help || reload || give || take || reset || log){
			build = Text.builder("/eco <");
			if(help){
				build = build.append(Text.builder("help").onClick(TextActions.suggestCommand("/eco help")).build());
				if(reload || give || take || reset || log){
					build = build.append(Text.builder("|").build());
				}
			}
			if(reload){
				build = build.append(Text.builder("reload").onClick(TextActions.suggestCommand("/eco reload")).build());
				if(give || take || reset || log){
					build = build.append(Text.builder("|").build());
				}
			}
			if(give){
				build = build.append(Text.builder("give").onClick(TextActions.suggestCommand("/eco give ")).build());
				if(take || reset || log){
					build = build.append(Text.builder("|").build());
				}
			}
			if(take){
				build = build.append(Text.builder("take").onClick(TextActions.suggestCommand("/eco take ")).build());
				if(reset || log){
					build = build.append(Text.builder("|").build());
				}
			}
			if(reset){
				build = build.append(Text.builder("reset").onClick(TextActions.suggestCommand("/eco reset ")).build());
				if(log){
					build = build.append(Text.builder("|").build());
				}
			}
			if(log){
				build = build.append(Text.builder("log").onClick(TextActions.suggestCommand("/eco log ")).build());
			}
			build = build.append(Text.builder(">").build());
		} else {
			build = Text.builder("/eco").onClick(TextActions.suggestCommand("/eco"));
		}
		return build.color(TextColors.RED).build();
	}
	
	public Text helpReload(final CommandSource source) {
		return Text.builder("/" + this.getName() + " reload")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " reload"))
					.color(TextColors.RED)
					.build();
	}
	
	public Text descriptionGive(final CommandSource source) {
		return EChat.of(this.plugin.getService().replace(this.plugin.getMessages().getMessage("GIVE_DESCRIPTION")));
	}
	
	public Text helpGive(final CommandSource source) {
		return Text.builder("/" + this.getName() + " give <" + this.plugin.getEverAPI().getMessages().getArg("player") + "> "
													  + "<" + this.plugin.getEverAPI().getMessages().getArg("amount") + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " give"))
					.color(TextColors.RED)
					.build();
	}
	
	public Text descriptionTake(final CommandSource source) {
		return EChat.of(this.plugin.getService().replace(this.plugin.getMessages().getMessage("TAKE_DESCRIPTION")));
	}
	
	public Text helpTake(final CommandSource source) {
		return Text.builder("/" + this.getName() + " take <" + this.plugin.getEverAPI().getMessages().getArg("player") + "> "
													  + "<" + this.plugin.getEverAPI().getMessages().getArg("amount") + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " take"))
					.color(TextColors.RED)
					.build();
	}
	
	public Text descriptionReset(final CommandSource source) {
		return EChat.of(this.plugin.getService().replace(this.plugin.getMessages().getMessage("RESET_DESCRIPTION")));
	}
	
	public Text helpReset(final CommandSource source) {
		return Text.builder("/" + this.getName() + " reset <" + this.plugin.getEverAPI().getMessages().getArg("player") + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " reset"))
					.color(TextColors.RED)
					.build();
	}
	
	public Text descriptionLog(final CommandSource source) {
		return EChat.of(this.plugin.getService().replace(this.plugin.getMessages().getMessage("LOG_DESCRIPTION")));
	}
	
	public Text helpLog(final CommandSource source) {
		if(source.hasPermission(this.plugin.getPermissions().get("LOG_PRINT"))) {
			return Text.builder("/" + this.getName() + " log <" + this.plugin.getEverAPI().getMessages().getArg("player") + "> [print]")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " log"))
					.color(TextColors.RED)
					.build();
		}
		return Text.builder("/" + this.getName() + " log <" + this.plugin.getEverAPI().getMessages().getArg("player") + ">")
					.onClick(TextActions.suggestCommand("/" + this.getName() + " log"))
					.color(TextColors.RED)
					.build();
	}

	public boolean execute(final CommandSource source, final List<String> args) {
		// Résultat de la commande :
		boolean resultat = false;
		
		// HELP
		if(args.size() == 0 || (args.size() == 1 && args.get(0).equals("help"))) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("HELP"))){
				resultat = commandHelp(source);
			// Il n'a pas la permission
			} else {
				source.sendMessage(this.plugin.getPermissions().noPermission());
			}
		// RELOAD
		} else if(args.size() == 1 && args.get(0).equals("reload")) {
			// Si il a la permission
			if(source.hasPermission(this.plugin.getPermissions().get("RELOAD"))){
				resultat = commandReload(source);
			// Il n'a pas la permission
			} else {
				source.sendMessage(this.plugin.getPermissions().noPermission());
			}
		// RESET
		} else if(args.size() >= 2 && args.get(0).equals("reset")) {
			if(args.size() == 2) {
				// Si il a la permission
				if(source.hasPermission(this.plugin.getPermissions().get("RESET"))){
					Optional<User> user = this.plugin.getEServer().getUser(args.get(1));
					// Le joueur existe
					if(user.isPresent()){
						resultat = commandReset(source, user.get());
					// Le joueur est introuvable
					} else {
						source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
					}
				// Il n'a pas la permission
				} else {
					source.sendMessage(this.plugin.getPermissions().noPermission());
				}
			} else {
				source.sendMessage(this.helpReset(source));
			}
		// GIVE
		} else if(args.size() >= 3 && args.get(0).equals("give")){
			if(args.size() == 3) {
				// Si il a la permission
				if(source.hasPermission(this.plugin.getPermissions().get("GIVE"))){
					Optional<User> user = this.plugin.getEServer().getUser(args.get(1));
					// Le joueur existe
					if(user.isPresent()){
						resultat = commandGive(source, user.get(), args.get(2));
					// Le joueur est introuvable
					} else {
						source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
					}
				// Il n'a pas la permission
				} else {
					source.sendMessage(this.plugin.getPermissions().noPermission());
				}
			} else {
				source.sendMessage(this.helpGive(source));
			}
		// TAKE
		} else if(args.size() >= 3 && args.get(0).equals("take")){
			if(args.size() == 3) {
				// Si il a la permission
				if(source.hasPermission(this.plugin.getPermissions().get("TAKE"))){
					Optional<User> user = this.plugin.getEServer().getUser(args.get(1));
					// Le joueur existe
					if(user.isPresent()){
						resultat = commandTake(source, user.get(), args.get(2));
					// Le joueur est introuvable
					} else {
						source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
					}
				// Il n'a pas la permission
				} else {
					source.sendMessage(this.plugin.getPermissions().noPermission());
				}
			} else {
				source.sendMessage(this.helpTake(source));
			}
		// LOG
		} else if(args.size() >= 2 && args.get(0).equals("log")){
			if(args.size() == 2) {
				// Si il a la permission
				if(source.hasPermission(this.plugin.getPermissions().get("LOG"))){
					final Optional<User> user = this.plugin.getEServer().getUser(args.get(1));
					// Le joueur existe
					if(user.isPresent()){
						this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.commandLog(source, user.get()))
							.name("commandLog").submit(this.plugin);
						resultat = true;
					// Le joueur est introuvable
					} else {
						source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
					}
				// Il n'a pas la permission
				} else {
					source.sendMessage(this.plugin.getPermissions().noPermission());
				}
			} else if(args.size() == 3) {
				// Si il a la permission
				if(source.hasPermission(this.plugin.getPermissions().get("LOG_PRINT"))){
					final Optional<User> user = this.plugin.getEServer().getUser(args.get(1));
					// Le joueur existe
					if(user.isPresent()){
						this.plugin.getGame().getScheduler().createTaskBuilder().async().execute(() -> this.commandLogPrint(source, user.get(), args.get(2)))
							.name("commandLogPrint").submit(this.plugin);
						resultat = true;
					// Le joueur est introuvable
					} else {
						source.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("PLAYER_NOT_FOUND")));
					}
				// Il n'a pas la permission
				} else {
					source.sendMessage(this.plugin.getPermissions().noPermission());
				}
			} else {
				source.sendMessage(this.helpLog(source));
			}
		// Nombre d'argument incorrect
		} else {
			source.sendMessage(getHelp(source).get());
		}
		return resultat;
	}

	private boolean commandReload(final CommandSource player) {
		this.plugin.reload();
		player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("RELOAD_COMMAND")));
		return true;
	}
	
	private boolean commandHelp(final CommandSource source) {
		LinkedHashMap<String, ESubCommand> commands = new LinkedHashMap<String, ESubCommand>();
		if(this.balance.testPermission(source)) {
			commands.put(this.balance.getName(), new ESubCommand(this.balance.help(source), this.balance.description(source)));
		}
		if(this.top.testPermission(source)) {
			commands.put(this.top.getName(), new ESubCommand(this.top.help(source), this.top.description(source)));
		}
		if(this.pay.testPermission(source)) {
			commands.put(this.pay.getName(), new ESubCommand(this.pay.help(source), this.pay.description(source)));
		}
		if(source.hasPermission(this.plugin.getPermissions().get("RELOAD"))) {
			commands.put("eco reload", new ESubCommand(this.helpReload(source), this.plugin.getEverAPI().getMessages().getText("RELOAD_DESCRIPTION")));
		}
		if(source.hasPermission(this.plugin.getPermissions().get("GIVE"))) {
			commands.put("eco give", new ESubCommand(this.helpGive(source), this.descriptionGive(source)));
		}
		if(source.hasPermission(this.plugin.getPermissions().get("TAKE"))) {
			commands.put("eco take", new ESubCommand(this.helpTake(source), this.descriptionTake(source)));
		}
		if(source.hasPermission(this.plugin.getPermissions().get("RESET"))) {
			commands.put("eco reset", new ESubCommand(this.helpReset(source), this.descriptionReset(source)));
		}
		if(source.hasPermission(this.plugin.getPermissions().get("LOG"))) {
			commands.put("eco log", new ESubCommand(this.helpLog(source), this.descriptionLog(source)));
		}
		this.plugin.getEverAPI().getManagerService().getEPagination().helpSubCommand(commands, source, this.plugin);
		return true;
	}
	
	private boolean commandReset(final CommandSource staff, final User user) {
		boolean resultat = false;
		
		Optional<UniqueAccount> account = this.plugin.getService().getOrCreateAccount(user.getUniqueId());
		// Le compte existe
		if(account.isPresent()) {
			// Reset
			if(account.get().resetBalance(this.plugin.getService().getDefaultCurrency(), UtilsCause.command(this.plugin, staff)).getResult().equals(ResultType.SUCCESS)){
				BigDecimal balance = account.get().getBalance(this.plugin.getService().getDefaultCurrency());
				resultat = true;
				// La source et le joueur sont différent
				if(!user.getIdentifier().equals(staff.getIdentifier())){
					staff.sendMessage(ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
								.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("RESET_OTHERS_STAFF"))
										.replaceAll("<player>", user.getName())
										.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
								.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
								.build());
					Optional<Player> player = user.getPlayer();
					if(player.isPresent()) {
						player.get().sendMessage(
								ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
									.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("RESET_OTHERS_PLAYER"))
											.replaceAll("<staff>", staff.getName())
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
									.build());
					}
				// La source et le joueur sont identique
				} else {
					staff.sendMessage(
							ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
								.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("RESET_PLAYER"))
										.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
								.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
								.build());
				}
			// Impossible de reset
			} else {
				staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("COMMAND_ERROR")));
			}
		// Le compte est introuvable
		} else {
			staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("ACCOUNT_NOT_FOUND")));
		}
		return resultat;
	}
	
	private boolean commandGive(final CommandSource staff, final User user, final String amount_name) {
		boolean resultat = false;
		
		Optional<UniqueAccount> account = this.plugin.getService().getOrCreateAccount(user.getUniqueId());
		// Le compte existe
		if(account.isPresent()) {
			// Nombre valide
			try {
				BigDecimal amount = new BigDecimal(Double.parseDouble(amount_name));
				amount = amount.setScale(this.plugin.getService().getDefaultCurrency().getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP);
				
				ResultType result = account.get().deposit(this.plugin.getService().getDefaultCurrency(), amount, UtilsCause.command(this.plugin, staff)).getResult();
				BigDecimal balance = account.get().getBalance(this.plugin.getService().getDefaultCurrency());
				// Transaction réussit
				if(result.equals(ResultType.SUCCESS)) {
					resultat = true;
					// La source et le joueur sont différent
					if(!user.getIdentifier().equals(staff.getIdentifier())) {
						staff.sendMessage(
								ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
									.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("GIVE_OTHERS_STAFF"))
											.replaceAll("<player>", user.getName())
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
									.build());
						
						Optional<Player> player = user.getPlayer();
						if(player.isPresent()) {
							player.get().sendMessage(
									ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
										.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("GIVE_OTHERS_PLAYER"))
												.replaceAll("<staff>", staff.getName())
												.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
												.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
										.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
										.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
										.build());
						}
					// La source et le joueur sont identique
					} else {
						staff.sendMessage(
								ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
									.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("GIVE_PLAYER"))
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
									.build());
					}
				// Max quantité
				} else if(result.equals(ResultType.ACCOUNT_NO_SPACE)) {
					// La source et le joueur sont différent
					if(!user.getIdentifier().equals(staff.getIdentifier())) {
						staff.sendMessage(
								ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
									.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("GIVE_ERROR_MAX"))
											.replaceAll("<player>", user.getName())
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
									.build());
					// La source et le joueur sont identique
					} else {
						staff.sendMessage(
								ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
									.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("GIVE_ERROR_MAX_EQUALS"))
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
									.build());
					}
				} else {
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("NUMBER_INVALID")));
				}
			// Nombre invalide
			} catch(NumberFormatException e) {
				staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("NUMBER_INVALID")));
			}
		// Le compte est introuvable
		} else {
			staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("ACCOUNT_NOT_FOUND")));
		}
		return resultat;
	}
	
	private boolean commandTake(final CommandSource staff, final User user, final String amount_name) {
		boolean resultat = false;
		
		Optional<UniqueAccount> account = this.plugin.getService().getOrCreateAccount(user.getUniqueId());
		// Le compte existe
		if(account.isPresent()) {
			// Nombre valide
			try {
				BigDecimal amount = new BigDecimal(Double.parseDouble(amount_name));
				amount = amount.setScale(this.plugin.getService().getDefaultCurrency().getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP);
				
				ResultType result = account.get().withdraw(this.plugin.getService().getDefaultCurrency(), amount, UtilsCause.command(this.plugin, staff)).getResult();
				BigDecimal balance = account.get().getBalance(this.plugin.getService().getDefaultCurrency());
				// Le compte existe
				if(result.equals(ResultType.SUCCESS)) {
					resultat = true;
					// La source et le joueur sont différent
					if(!user.getIdentifier().equals(staff.getIdentifier())) {
						staff.sendMessage(
								ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
									.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("TAKE_OTHERS_STAFF"))
											.replaceAll("<player>", user.getName())
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
									.build());
						
						Optional<Player> player = user.getPlayer();
						if(player.isPresent()) {
							player.get().sendMessage(
									ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
										.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("TAKE_OTHERS_PLAYER"))
												.replaceAll("<staff>", staff.getName())
												.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
												.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
										.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
										.build());
						}
					// La source et le joueur sont identique
					} else {
						staff.sendMessage(
								ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
									.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("TAKE_PLAYER"))
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
									.build());
					}
				// Min quantité
				} else if(result.equals(ResultType.ACCOUNT_NO_FUNDS)) {
					// La source et le joueur sont différent
					if(!user.getIdentifier().equals(staff.getIdentifier())) {
						staff.sendMessage(
								ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
									.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("TAKE_ERROR_MIN"))
											.replaceAll("<player>", user.getName())
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
									.build());
					// La source et le joueur sont identique
					} else {
						staff.sendMessage(
								ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
									.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("TAKE_ERROR_MIN_EQUALS"))
											.replaceAll("<amount>", this.plugin.getService().getDefaultCurrency().cast(amount))
											.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
									.replace("<amount_format>", this.plugin.getService().getDefaultCurrency().format(amount))
									.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
									.build());
					}
				} else {
					staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("NUMBER_INVALID")));
				}
			// Nombre invalide
			} catch(NumberFormatException e) {
				staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("NUMBER_INVALID")));
			}
		// Le compte est introuvable
		} else {
			staff.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("ACCOUNT_NOT_FOUND")));
		}
		return resultat;
	}
	
	private void commandLog(final CommandSource player, final User user) {
		List<Text> lists = new ArrayList<Text>();
		
		for(ELog log : this.plugin.getDataBases().selectLog(user.getIdentifier(), this.plugin.getService().getDefaultCurrency())) {
			lists.add(log.replace(this.plugin.getMessages().getMessage("LOG_LINE_TRANSACTION"),
									this.plugin.getMessages().getMessage("LOG_LINE_TRANSFERT")));
		}
		
		if(lists.isEmpty()) {
			lists.add(this.plugin.getMessages().getText("LOG_EMPTY"));
		}
		
		this.plugin.getEverAPI().getManagerService().getEPagination().sendTo(EChat.of(this.plugin.getService().replace(
				this.plugin.getMessages().getMessage("LOG_TITLE")
					.replaceAll("<player>", user.getName()))), 
				lists, player);
	}
	
	private void commandLogPrint(final CommandSource player, final User user, final String string) {
		List<ELog> logs = this.plugin.getDataBases().selectLog(user.getIdentifier(), this.plugin.getService().getDefaultCurrency());
		
		if(logs.isEmpty()) {
			if(player.getIdentifier().equals(user.getIdentifier())) {
				player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getService().replace(this.plugin.getMessages().getMessage("LOG_PRINT_EMPTY_EQUALS"))));
			} else {
				player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getService().replace(this.plugin.getMessages().getMessage("LOG_PRINT_EMPTY"))));
			}
		} else {
			File file = this.plugin.getPath().resolve("logs/" + user.getName() + ".log").toFile();
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			
			FileWriter write = null;
			try {
				write = new FileWriter(file);
				for(ELog log : this.plugin.getDataBases().selectLog(user.getIdentifier(), this.plugin.getService().getDefaultCurrency())) {
					write.write(log.replace(this.plugin.getMessages().getMessage("LOG_PRINT_LINE_TRANSACTION"),
											this.plugin.getMessages().getMessage("LOG_PRINT_LINE_TRANSFERT")).toPlain());
				}
				
				if(player.getIdentifier().equals(user.getIdentifier())) {
					player.sendMessage(
							ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
								.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("LOG_PRINT"))
										.replaceAll("<player>", user.getName())
										.replaceAll("<file>", file.getName()))
								.build());
				} else {
					player.sendMessage(
							ETextBuilder.toBuilder(this.plugin.getMessages().getMessage("PREFIX"))
								.append(this.plugin.getService().replace(this.plugin.getMessages().getMessage("LOG_PRINT_EQUALS"))
										.replaceAll("<player>", user.getName())
										.replaceAll("<file>", file.getName()))
								.build());
				}
			} catch (IOException e) {
				player.sendMessage(EChat.of(this.plugin.getMessages().getMessage("PREFIX") + this.plugin.getEverAPI().getMessages().getMessage("COMMAND_ERROR")));
			} finally {
				try {if(write != null) write.close();} catch (IOException e) {}
			}
		}
	}
}
