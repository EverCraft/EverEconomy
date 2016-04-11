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

import fr.evercraft.everapi.plugin.file.EMessage;

public class EEMessage extends EMessage {

	public EEMessage(EverEconomy plugin) {
		super(plugin);
	}

	@Override
	public void loadDefault() {
		// Prefix
		addDefault("prefix", "[&4Ever&6&lEconomy&f] ");
		
		addDefault("description", "Gestion de l'Economie");
		
		addDefault("balance.description", "Connaître le nombre d'<money_singular> d'un joueur", "Give the current balance of a player.");
		addDefault("balance.player", "&7Vous avez actuellement &6<solde> <symbol>&7.", "&7Balance : &6<solde> <symbol>");
		addDefault("balance.others", "&6<player>&7 a actuellement &6<solde> <symbol>&7.", "&7Balance of <player> : &6<solde> <symbol>");
		
		addDefault("balancetop.description", "Connaître les joueurs qui possèdent le plus d'<money_singular>", "Display the top account balances.");
		addDefault("balancetop.title", "&aClassement : Economie", "&a Top Balances");
		addDefault("balancetop.line", "    &a<number>. &6<player> &7: &6<solde> <symbol>");
		addDefault("balancetop.empty", "    &7Aucun compte", "    &7No account");
		
		addDefault("pay.description", "Envoyer des <money_plural> à un joueur", "Pay specified player from your balance.");
		addDefault("pay.staff", "&7Vous avez envoyé &6<amount> <symbol>&7 à &6<player>&7.", "&7You sent &6<amount> <symbol>&7 &6<player>&7.");
		addDefault("pay.player", "&7Vous avez reçu &6<amount> <symbol>&7 de la part de &6<staff>&7.", "&7You have received &6<amount> <symbol>&7 from &6<staff>&7.");
		addDefault("pay.errorMin", "&cVous n'avez pas &6<amount>&c.", "&cYou don't have &6<amount>&c.");
		addDefault("pay.errorMax", "&6<player> &ca déjà trop <money_plural>&c.", "&6<player> &chas already too much <money_plural>&c.");
		addDefault("pay.errorEqual", "&cVous ne pouvez pas vous envoyer des <money_plural>&c." , "&cYou can't send you <money_plural>&c.");
		
		addDefault("give.description", "Donner des <money_plural> à un joueur", "Give <money_plural> to player");
		addDefault("give.player", "&7Vous vous êtes donné &6<amount><symbol>&7.", "&7You gave yourselves &6<amount><symbol>&7.");
		addDefault("give.othersPlayer", "&6<staff>&7 vous a donné &6<amount> <symbol>&7.", "&6<staff>&7 gave you &6<amount> <symbol>&7.");
		addDefault("give.othersStaff", "&7Vous avez donné &6<amount> <symbol>&7 à &6<player>&7.", "&7You gave &6<amount> <symbol>&7 &6<player>&7.");
		addDefault("give.errorMax", "&6<player> &ca déjà trop <money_plural>&c.", "&6<player> &chas already too much <money_plural>&c.");
		addDefault("give.errorMaxEquals", "&cVous avez déjà trop <money_plural>&c.", "&cYou have already too many <money_plural>&c.");
		
		addDefault("take.description", "Retirer des <money_plural> à un joueur", "Withdrawing <money_plural> from a player");
		addDefault("take.player", "&7Vous vous êtes retiré &6<amount> <symbol>&7.", "&7You withdrew &6<amount> <symbol>&7.");
		addDefault("take.othersPlayer", "&6<staff>&7 vous a retiré &6<amount> <symbol>&7.", "&6<amount> <symbol>&7has been taken from your account.");
		addDefault("take.othersStaff", "&7Vous avez retiré &6<amount> <symbol>&7 à &6<player>&7.", "&6<amount> <symbol>&7 taken from &6<player> &7a account.");
		addDefault("take.errorMin", "&6<player> &cn'a pas assez <money>&c.", "&6<player> &cdoesn't have enough <money>&c.");
		addDefault("take.errorMinEquals", "&cVous n'avez pas assez <money>&c.", "&cYou don't have enough <money>&c.");
		
		addDefault("reset.description", "Réinitialiser les <money_plural> d'un joueur", "Reset the currency of a player");
		addDefault("reset.player", "&7Vous avez réinitialisé votre compte.", "&7You have reset your account.");
		addDefault("reset.othersPlayer", "&7Votre monnaie a été réinitialisé par &6<staff>&7.", "&7Your money has been reset by &6<staff>&7.");
		addDefault("reset.othersStaff", "&7Vous avez réinitialisé le compte de &6<player>&7.", "&7You have reset the currency &6<player>&7.");
		
		addDefault("log.description", "Connaître l'historique des transactions d'un joueur", "Know the history of the transactions of a player");
		addDefault("log.title", "&aHistorique de &6<player> &a: &6<money_plural>", "&aTransactions of &6<player> &a: &6<money_plural>");
		addDefault("log.lineTransaction", "&c<time> &6: &7<transaction> &6: &b<before> &6: &a<after> &8: &7<cause>");
		addDefault("log.lineTransfert", "&c<time> &6: &7<transaction> &6: &b<before> &6: &a<after> &6: &dz<player> &8: &7<cause>");
		addDefault("log.empty", "    &7Aucune transaction", "    &7No transaction");
		addDefault("log.print", "&7L'historique des transactions de &6<player> &7a été transféré dans le fichier &6<file>&7.", "&7The history of &6<player> &7transactions was transferred to the file &6<file>&7.");
		addDefault("log.printEquals", "&7Vos logs ont été transféré dans le fichier &6<file>&7.", "&7The history of your transactions transferred into the file &6<file>&7.");
		addDefault("log.printLineTransaction", "[<time>] &7<transaction> &6: (before='<before>';after='<after>';cause='<cause>')");
		addDefault("log.printLineTransfert", "[<time>] &7<transaction> &6: (before='<before>';after='<after>';cause='<cause>')");
		addDefault("log.printEmpty", "&6<player> &7n'a fait aucune transaction.", "&6<player> &7hasn't traded");
		addDefault("log.printEmptyEquals", "&7Vous n'avez effectué aucune transaction.", "&7You haven't made any transaction.");
	}

	@Override
	public void loadConfig() {
		// Prefix
		addMessage("PREFIX", "prefix");
		addMessage("DESCRIPTION", "description");
		
		addMessage("BALANCE_DESCRIPTION", "balance.description");
		addMessage("BALANCE_PLAYER", "balance.player");
		addMessage("BALANCE_OTHERS", "balance.others");
		
		addMessage("BALANCE_TOP_DESCRIPTION", "balancetop.description");
		addMessage("BALANCE_TOP_TITLE", "balancetop.title");
		addMessage("BALANCE_TOP_LINE", "balancetop.line");
		addMessage("BALANCE_TOP_EMPTY", "balancetop.empty");
		
		addMessage("PAY_DESCRIPTION", "pay.description");
		addMessage("PAY_STAFF", "pay.staff");
		addMessage("PAY_PLAYER", "pay.player");
		addMessage("PAY_ERROR_MIN", "pay.errorMin");
		addMessage("PAY_ERROR_MAX", "pay.errorMax");
		addMessage("PAY_ERROR_EQUALS", "pay.errorEquals");
		
		addMessage("GIVE_DESCRIPTION", "give.description");
		addMessage("GIVE_PLAYER", "give.player");
		addMessage("GIVE_OTHERS_PLAYER", "give.othersPlayer");
		addMessage("GIVE_OTHERS_STAFF", "give.othersStaff");
		addMessage("GIVE_ERROR_MAX", "give.errorMax");
		addMessage("GIVE_ERROR_MAX_EQUALS", "give.errorMaxEquals");
		
		addMessage("TAKE_DESCRIPTION", "take.description");
		addMessage("TAKE_PLAYER", "take.player");
		addMessage("TAKE_OTHERS_PLAYER", "take.othersPlayer");
		addMessage("TAKE_OTHERS_STAFF", "take.othersStaff");
		addMessage("TAKE_ERROR_MIN", "take.errorMin");
		addMessage("TAKE_ERROR_MIN_EQUALS", "take.errorMinEquals");
		
		addMessage("RESET_DESCRIPTION", "reset.description");
		addMessage("RESET_PLAYER", "reset.player");
		addMessage("RESET_OTHERS_PLAYER", "reset.othersPlayer");
		addMessage("RESET_OTHERS_STAFF", "reset.othersStaff");
		
		addMessage("LOG_DESCRIPTION", "log.description");
		addMessage("LOG_TITLE", "log.title");
		addMessage("LOG_LINE_TRANSACTION", "log.lineTransaction");
		addMessage("LOG_LINE_TRANSFERT", "log.lineTransfert");
		addMessage("LOG_EMPTY", "log.empty");
		addMessage("LOG_PRINT", "log.print");
		addMessage("LOG_PRINT_EQUALS", "log.printEquals");
		addMessage("LOG_PRINT_LINE_TRANSACTION", "log.printLineTransaction");
		addMessage("LOG_PRINT_LINE_TRANSFERT", "log.printLineTransfert");
		addMessage("LOG_PRINT_EMPTY", "log.printEmpty");
		addMessage("LOG_PRINT_EMPTY_EQUALS", "log.printEmptyEquals");
	}
}
