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
package fr.evercraft.evereconomy;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.plugin.file.EMessage;
import fr.evercraft.everapi.plugin.file.EnumMessage;

public class EEMessage extends EMessage {

	public EEMessage(EverEconomy plugin) {
		super(plugin, EEMessages.values());
	}

	public enum EEMessages implements EnumMessage {
		PREFIX("prefix", 									"[&4Ever&6&lEconomy&f] "),
		
		DESCRIPTION("description", 							"Gestion de l'Economie"),
		
		BALANCE_DESCRIPTION("balance.description", 			"Connaître le nombre d'<money_singular> d'un joueur", "Give the current balance of a player."),
		BALANCE_PLAYER("balance.player", 					"&7Vous avez actuellement &6<solde> <symbol>&7.", "&7Balance : &6<solde> <symbol>"),
		BALANCE_OTHERS("balance.others", 					"&6<player>&7 a actuellement &6<solde> <symbol>&7.", "&7Balance of <player> : &6<solde> <symbol>"),
		
		BALANCE_TOP_DESCRIPTION("balancetop.description", 	"Connaître les joueurs qui possèdent le plus d'<money_singular>", 
															"Display the top account balances."),
		BALANCE_TOP_TITLE("balancetop.title", 				"&aClassement : Economie", 
															"&a Top Balances"),
		BALANCE_TOP_LINE("balancetop.line", 				"    &a<number>. &6<player> &7: &6<solde> <symbol>"),
		BALANCE_TOP_EMPTY("balancetop.empty", 				"    &7Aucun compte", 
															"    &7No account"),
		
		PAY_DESCRIPTION("pay.description", 					"Envoyer des <money_plural> à un joueur", 
															"Pay specified player from your balance."),
		PAY_STAFF("pay.staff", 								"&7Vous avez envoyé &6<amount> <symbol>&7 à &6<player>&7.", 
															"&7You sent &6<amount> <symbol>&7 &6<player>&7."),
		PAY_PLAYER("pay.player", 							"&7Vous avez reçu &6<amount> <symbol>&7 de la part de &6<staff>&7.", 
															"&7You have received &6<amount> <symbol>&7 from &6<staff>&7."),
		PAY_ERROR_MIN("pay.errorMin", 						"&cVous n'avez pas &6<amount>&c.", 
															"&cYou don't have &6<amount>&c."),
		PAY_ERROR_MAX("pay.errorMax", 						"&6<player> &ca déjà trop <money_plural>&c.", 
															"&6<player> &chas already too much <money_plural>&c."),
		PAY_ERROR_EQUALS("pay.errorEqual", 					"&cVous ne pouvez pas vous envoyer des <money_plural>&c.", 
															"&cYou can't send you <money_plural>&c."),
		
		GIVE_DESCRIPTION("give.description", 				"Donner des <money_plural> à un joueur", 
															"Give <money_plural> to player"),
		GIVE_PLAYER("give.player", 							"&7Vous vous êtes donné &6<amount><symbol>&7.", 
															"&7You gave yourselves &6<amount><symbol>&7."),
		GIVE_OTHERS_PLAYER("give.othersPlayer", 			"&6<staff>&7 vous a donné &6<amount> <symbol>&7.", 
															"&6<staff>&7 gave you &6<amount> <symbol>&7."),
		GIVE_OTHERS_STAFF("give.othersStaff", 				"&7Vous avez donné &6<amount> <symbol>&7 à &6<player>&7.", 
															"&7You gave &6<amount> <symbol>&7 &6<player>&7."),
		GIVE_ERROR_MAX("give.errorMax", 					"&6<player> &ca déjà trop <money_plural>&c.", 
															"&6<player> &chas already too much <money_plural>&c."),
		GIVE_ERROR_MAX_EQUALS("give.errorMaxEquals", 		"&cVous avez déjà trop <money_plural>&c.", 
															"&cYou have already too many <money_plural>&c."),
		
		TAKE_DESCRIPTION("take.description", 				"Retirer des <money_plural> à un joueur", 
															"Withdrawing <money_plural> from a player"),
		TAKE_PLAYER("take.player", 							"&7Vous vous êtes retiré &6<amount> <symbol>&7.", 
															"&7You withdrew &6<amount> <symbol>&7."),
		TAKE_OTHERS_PLAYER("take.othersPlayer", 			"&6<staff>&7 vous a retiré &6<amount> <symbol>&7.", 
															"&6<amount> <symbol>&7has been taken from your account."),
		TAKE_OTHERS_STAFF("take.othersStaff", 				"&7Vous avez retiré &6<amount> <symbol>&7 à &6<player>&7.", 
															"&6<amount> <symbol>&7 taken from &6<player> &7a account."),
		TAKE_ERROR_MIN("take.errorMin", 					"&6<player> &cn'a pas assez <money>&c.", 
															"&6<player> &cdoesn't have enough <money>&c."),
		TAKE_ERROR_MIN_EQUALS("take.errorMinEquals", 		"&cVous n'avez pas assez <money>&c.", 
															"&cYou don't have enough <money>&c."),
		
		RESET_DESCRIPTION("reset.description", 				"Réinitialiser les <money_plural> d'un joueur", 
															"Reset the currency of a player"),
		RESET_PLAYER("reset.player", 						"&7Vous avez réinitialisé votre compte.", 
															"&7You have reset your account."),
		RESET_OTHERS_PLAYER("reset.othersPlayer", 			"&7Votre monnaie a été réinitialisé par &6<staff>&7.", 
															"&7Your money has been reset by &6<staff>&7."),
		RESET_OTHERS_STAFF("reset.othersStaff", 			"&7Vous avez réinitialisé le compte de &6<player>&7.", 
															"&7You have reset the currency &6<player>&7."),
		
		LOG_DESCRIPTION("log.description", 					"Connaître l'historique des transactions d'un joueur", 
															"Know the history of the transactions of a player"),
		LOG_TITLE("log.title", 								"&aHistorique de &6<player> &a: &6<money_plural>", 
															"&aTransactions of &6<player> &a: &6<money_plural>"),
		LOG_LINE_TRANSACTION("log.lineTransaction", 		"&c<time> &6: &7<transaction> &6: &b<before> &6: &a<after> &8: &7<cause>"),
		LOG_LINE_TRANSFERT("log.lineTransfert", 			"&c<time> &6: &7<transaction> &6: &b<before> &6: &a<after> &6: &d<player> &8: &7<cause>"),
		LOG_EMPTY("log.empty", 								"    &7Aucune transaction", 
															"    &7No transaction"),
		LOG_PRINT("log.print", 								"&7L'historique des transactions de &6<player> &7a été transféré dans le fichier &6<file>&7.", 
															"&7The history of &6<player> &7transactions was transferred to the file &6<file>&7."),
		LOG_PRINT_EQUALS("log.printEquals", 				"&7Vos logs ont été transféré dans le fichier &6<file>&7.", 
															"&7The history of your transactions transferred into the file &6<file>&7."),
		LOG_PRINT_LINE_TRANSACTION("log.printLineTransaction", "[<time>] &7<transaction> &6: (before='<before>';after='<after>';cause='<cause>')"),
		LOG_PRINT_LINE_TRANSFERT("log.printLineTransfert", 	"[<time>] &7<transaction> &6: (before='<before>';after='<after>';to='<player>';cause='<cause>')"),
		LOG_PRINT_EMPTY("log.printEmpty", 					"&6<player> &7n'a fait aucune transaction.", 
															"&6<player> &7hasn't traded"),
		LOG_PRINT_EMPTY_EQUALS("log.printEmptyEquals", 		"&7Vous n'avez effectué aucune transaction.", 
															"&7You haven't made any transaction."),
		
		SIGN_BALANCE_CREATE("sign.balance.create",		"&7Le panneau a été créé avec succès."),
		SIGN_BALANCE_DISABLE("sign.balance.disable",    "&cCe panneau est désactivé."),
		SIGN_BALANCE_BREAK("sign.balance.break",		"&7Le panneau a été supprimé.");
		
		private final String path;
	    private final Object french;
	    private final Object english;
	    private Object message;
	    
	    private EEMessages(final String path, final Object french) {   	
	    	this(path, french, french);
	    }
	    
	    private EEMessages(final String path, final Object french, final Object english) {
	    	Preconditions.checkNotNull(french, "Le message '" + this.name() + "' n'est pas définit");
	    	
	    	this.path = path;	    	
	    	this.french = french;
	    	this.english = english;
	    	this.message = french;
	    }

	    public String getName() {
			return this.name();
		}
	    
		public String getPath() {
			return this.path;
		}

		public Object getFrench() {
			return this.french;
		}

		public Object getEnglish() {
			return this.english;
		}
		
		public String get() {
			if(this.message instanceof String) {
				return (String) this.message;
			}
			return this.message.toString();
		}
			
		@SuppressWarnings("unchecked")
		public List<String> getList() {
			if(this.message instanceof List) {
				return (List<String>) this.message;
			}
			return Arrays.asList(this.message.toString());
		}
		
		public void set(Object message) {
			this.message = message;
		}

		public Text getText() {
			return EChat.of(this.get());
		}
		
		public TextColor getColor() {
			return EChat.getTextColor(this.get());
		}
	}
}
