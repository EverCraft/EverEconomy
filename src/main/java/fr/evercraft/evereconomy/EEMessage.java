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

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.message.EMessageBuilder;
import fr.evercraft.everapi.message.EMessageFormat;
import fr.evercraft.everapi.message.format.EFormatString;
import fr.evercraft.everapi.plugin.file.EMessage;
import fr.evercraft.everapi.plugin.file.EnumMessage;

public class EEMessage extends EMessage<EverEconomy> {

	public EEMessage(EverEconomy plugin) {
		super(plugin, EEMessages.values());
	}

	public enum EEMessages implements EnumMessage {
		PREFIX("PREFIX", 									"[&4Ever&6&lEconomy&f] "),
		
		DESCRIPTION("description", 							"Gestion de l'Economie"),
		
		BALANCE_DESCRIPTION("balanceDescription", 			"Connaître le nombre d'{money_singular} d'un joueur", "Give the current balance of a player."),
		BALANCE_PLAYER("balancePlayer", 					"&7Vous avez actuellement &6{solde} {symbol}&7.", "&7Balance : &6{solde} {symbol}"),
		BALANCE_OTHERS("balanceOthers", 					"&6{player}&7 a actuellement &6{solde} {symbol}&7.", "&7Balance of {player} : &6{solde} {symbol}"),
		
		BALANCE_TOP_DESCRIPTION("balancetopDescription", 	"Connaître les joueurs qui possèdent le plus d'{money_singular}", 
															"Display the top account balances."),
		BALANCE_TOP_TITLE("balancetopTitle", 				"&aClassement : Economie", 
															"&a Top Balances"),
		BALANCE_TOP_LINE("balancetopLine", 					"    &a{number}. &6{player} &7: &6{solde} {symbol}"),
		BALANCE_TOP_EMPTY("balancetopEmpty", 				"    &7Aucun compte", 
															"    &7No account"),
		
		PAY_DESCRIPTION("payDescription", 					"Envoyer des {money_plural} à un joueur", 
															"Pay specified player from your balance."),
		PAY_STAFF("payStaff", 								"&7Vous avez envoyé &6{amount} {symbol}&7 à &6{player}&7.", 
															"&7You sent &6{amount} {symbol}&7 &6{player}&7."),
		PAY_PLAYER("payPlayer", 							"&7Vous avez reçu &6{amount} {symbol}&7 de la part de &6{staff}&7.", 
															"&7You have received &6{amount} {symbol}&7 from &6{staff}&7."),
		PAY_ERROR_MIN("payErrorMin", 						"&cVous n'avez pas &6{amount}&c.", 
															"&cYou don't have &6{amount}&c."),
		PAY_ERROR_MAX("payErrorMax", 						"&6{player} &ca déjà trop {money_plural}&c.", 
															"&6{player} &chas already too much {money_plural}&c."),
		PAY_ERROR_EQUALS("payErrorEqual", 					"&cVous ne pouvez pas vous envoyer des {money_plural}&c.", 
															"&cYou can't send you {money_plural}&c."),
		
		GIVE_DESCRIPTION("giveDescription", 				"Donner des {money_plural} à un joueur", 
															"Give {money_plural} to player"),
		GIVE_PLAYER("givePlayer", 							"&7Vous vous êtes donné &6{amount}{symbol}&7.", 
															"&7You gave yourselves &6{amount}{symbol}&7."),
		GIVE_OTHERS_PLAYER("giveOthersPlayer", 				"&6{staff}&7 vous a donné &6{amount} {symbol}&7.", 
															"&6{staff}&7 gave you &6{amount} {symbol}&7."),
		GIVE_OTHERS_STAFF("giveOthersStaff", 				"&7Vous avez donné &6{amount} {symbol}&7 à &6{player}&7.", 
															"&7You gave &6{amount} {symbol}&7 &6{player}&7."),
		GIVE_ERROR_MAX("giveErrorMax", 						"&6{player} &ca déjà trop {money_plural}&c.", 
															"&6{player} &chas already too much {money_plural}&c."),
		GIVE_ERROR_MAX_EQUALS("giveErrorMaxEquals", 		"&cVous avez déjà trop {money_plural}&c.", 
															"&cYou have already too many {money_plural}&c."),
		
		TAKE_DESCRIPTION("takeDescription", 				"Retirer des {money_plural} à un joueur", 
															"Withdrawing {money_plural} from a player"),
		TAKE_PLAYER("takePlayer", 							"&7Vous vous êtes retiré &6{amount} {symbol}&7.", 
															"&7You withdrew &6{amount} {symbol}&7."),
		TAKE_OTHERS_PLAYER("takeOthersPlayer", 				"&6{staff}&7 vous a retiré &6{amount} {symbol}&7.", 
															"&6{amount} {symbol}&7has been taken from your account."),
		TAKE_OTHERS_STAFF("takeOthersStaff", 				"&7Vous avez retiré &6{amount} {symbol}&7 à &6{player}&7.", 
															"&6{amount} {symbol}&7 taken from &6{player} &7a account."),
		TAKE_ERROR_MIN("takeErrorMin", 						"&6{player} &cn'a pas assez {money}&c.", 
															"&6{player} &cdoesn't have enough {money}&c."),
		TAKE_ERROR_MIN_EQUALS("takeErrorMinEquals", 		"&cVous n'avez pas assez {money}&c.", 
															"&cYou don't have enough {money}&c."),
		
		RESET_DESCRIPTION("resetDescription", 				"Réinitialiser les {money_plural} d'un joueur", 
															"Reset the currency of a player"),
		RESET_PLAYER("resetPlayer", 						"&7Vous avez réinitialisé votre compte.", 
															"&7You have reset your account."),
		RESET_OTHERS_PLAYER("resetOthersPlayer", 			"&7Votre monnaie a été réinitialisé par &6{staff}&7.", 
															"&7Your money has been reset by &6{staff}&7."),
		RESET_OTHERS_STAFF("resetOthersStaff", 				"&7Vous avez réinitialisé le compte de &6{player}&7.", 
															"&7You have reset the currency &6{player}&7."),
		
		LOG_DESCRIPTION("logDescription", 					"Connaître l'historique des transactions d'un joueur", 
															"Know the history of the transactions of a player"),
		LOG_TITLE("logTitle", 								"&aHistorique de &6{player} &a: &6{money_plural}", 
															"&aTransactions of &6{player} &a: &6{money_plural}"),
		LOG_LINE_TRANSACTION("logLineTransaction", 			"&c{time} &6: &7{transaction} &6: &b{before} &6: &a{after} &8: &7{cause}"),
		LOG_LINE_TRANSFERT("logLineTransfert", 				"&c{time} &6: &7{transaction} &6: &b{before} &6: &a{after} &6: &d{player} &8: &7{cause}"),
		LOG_EMPTY("logEmpty", 								"    &7Aucune transaction", 
															"    &7No transaction"),
		LOG_PRINT("logPrint", 								"&7L'historique des transactions de &6{player} &7a été transféré dans le fichier &6{file}&7.", 
															"&7The history of &6{player} &7transactions was transferred to the file &6{file}&7."),
		LOG_PRINT_EQUALS("logPrintEquals", 					"&7Vos logs ont été transféré dans le fichier &6{file}&7.", 
															"&7The history of your transactions transferred into the file &6{file}&7."),
		LOG_PRINT_LINE_TRANSACTION("logPrintLineTransaction", "[{time}] &7{transaction} &6: (before='{before}';after='{after}';cause='{cause}')"),
		LOG_PRINT_LINE_TRANSFERT("logPrintLineTransfert", 	"[{time}] &7{transaction} &6: (before='{before}';after='{after}';to='{player}';cause='{cause}')"),
		LOG_PRINT_EMPTY("logPrintEmpty", 					"&6{player} &7n'a fait aucune transaction.", 
															"&6{player} &7hasn't traded"),
		LOG_PRINT_EMPTY_EQUALS("logPrintEmptyEquals", 		"&7Vous n'avez effectué aucune transaction.", 
															"&7You haven't made any transaction."),
		
		SIGN_BALANCE_CREATE("signBalanceCreate",			"&7Le panneau a été créé avec succès."),
		SIGN_BALANCE_DISABLE("signBalanceDisable",   		 "&cCe panneau est désactivé."),
		SIGN_BALANCE_BREAK("signBalanceBreak",				"&7Le panneau a été supprimé.");
		
		private final String path;
	    private final EMessageBuilder french;
	    private final EMessageBuilder english;
	    private EMessageFormat message;
	    private EMessageBuilder builder;
	    
	    private EEMessages(final String path, final String french) {   	
	    	this(path, EMessageFormat.builder().chat(new EFormatString(french), true));
	    }
	    
	    private EEMessages(final String path, final String french, final String english) {   	
	    	this(path, 
	    		EMessageFormat.builder().chat(new EFormatString(french), true), 
	    		EMessageFormat.builder().chat(new EFormatString(english), true));
	    }
	    
	    private EEMessages(final String path, final EMessageBuilder french) {   	
	    	this(path, french, french);
	    }
	    
	    private EEMessages(final String path, final EMessageBuilder french, final EMessageBuilder english) {
	    	Preconditions.checkNotNull(french, "Le message '" + this.name() + "' n'est pas définit");
	    	
	    	this.path = path;	    	
	    	this.french = french;
	    	this.english = english;
	    	this.message = french.build();
	    }

	    public String getName() {
			return this.name();
		}
	    
		public String getPath() {
			return this.path;
		}

		public EMessageBuilder getFrench() {
			return this.french;
		}

		public EMessageBuilder getEnglish() {
			return this.english;
		}
		
		public EMessageFormat getMessage() {
			return this.message;
		}
		
		public EMessageBuilder getBuilder() {
			return this.builder;
		}
		
		public void set(EMessageBuilder message) {
			this.message = message.build();
			this.builder = message;
		}
	}
}
