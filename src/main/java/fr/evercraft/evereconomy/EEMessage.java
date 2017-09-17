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
		PREFIX( 						"[&4Ever&6&lEconomy&f] "),
		
		DESCRIPTION( 					"Gestion de l'Economie"),
		
		BALANCE_DESCRIPTION( 			"Connaître le nombre d'{money_singular} d'un joueur", "Give the current balance of a player."),
		BALANCE_PLAYER( 				"&7Vous avez actuellement &6{solde} {symbol}&7.", "&7Balance : &6{solde} {symbol}"),
		BALANCE_OTHERS( 				"&6{player}&7 a actuellement &6{solde} {symbol}&7.", "&7Balance of {player} : &6{solde} {symbol}"),
		
		BALANCE_TOP_DESCRIPTION( 		"Connaître les joueurs qui possèdent le plus d'{money_singular}", 
										"Display the top account balances."),
		BALANCE_TOP_TITLE( 				"&aClassement : Economie", 
										"&a Top Balances"),
		BALANCE_TOP_LINE( 				"    &a{number}. &6{player} &7: &6{solde} {symbol}"),
		BALANCE_TOP_EMPTY( 				"    &7Aucun compte", 
										"    &7No account"),
		
		PAY_DESCRIPTION( 				"Envoyer des {money_plural} à un joueur", 
										"Pay specified player from your balance."),
		PAY_STAFF( 						"&7Vous avez envoyé &6{amount} {symbol}&7 à &6{player}&7.", 
										"&7You sent &6{amount} {symbol}&7 &6{player}&7."),
		PAY_PLAYER( 					"&7Vous avez reçu &6{amount} {symbol}&7 de la part de &6{staff}&7.", 
										"&7You have received &6{amount} {symbol}&7 from &6{staff}&7."),
		PAY_ERROR_MIN( 					"&cVous n'avez pas &6{amount}&c.", 
										"&cYou don't have &6{amount}&c."),
		PAY_ERROR_MAX( 					"&6{player} &ca déjà trop {money_plural}&c.", 
										"&6{player} &chas already too much {money_plural}&c."),
		PAY_ERROR_EQUALS( 				"&cVous ne pouvez pas vous envoyer des {money_plural}&c.", 
										"&cYou can't send you {money_plural}&c."),
		
		GIVE_DESCRIPTION( 				"Donner des {money_plural} à un joueur", 
										"Give {money_plural} to player"),
		GIVE_PLAYER( 					"&7Vous vous êtes donné &6{amount}{symbol}&7.", 
										"&7You gave yourselves &6{amount}{symbol}&7."),
		GIVE_OTHERS_PLAYER( 			"&6{staff}&7 vous a donné &6{amount} {symbol}&7.", 
										"&6{staff}&7 gave you &6{amount} {symbol}&7."),
		GIVE_OTHERS_STAFF( 				"&7Vous avez donné &6{amount} {symbol}&7 à &6{player}&7.", 
										"&7You gave &6{amount} {symbol}&7 &6{player}&7."),
		GIVE_ERROR_MAX( 				"&6{player} &ca déjà trop {money_plural}&c.", 
										"&6{player} &chas already too much {money_plural}&c."),
		GIVE_ERROR_MAX_EQUALS( 			"&cVous avez déjà trop {money_plural}&c.", 
										"&cYou have already too many {money_plural}&c."),
		
		TAKE_DESCRIPTION( 				"Retirer des {money_plural} à un joueur", 
										"Withdrawing {money_plural} from a player"),
		TAKE_PLAYER( 					"&7Vous vous êtes retiré &6{amount} {symbol}&7.", 
										"&7You withdrew &6{amount} {symbol}&7."),
		TAKE_OTHERS_PLAYER( 			"&6{staff}&7 vous a retiré &6{amount} {symbol}&7.", 
										"&6{amount} {symbol}&7has been taken from your account."),
		TAKE_OTHERS_STAFF( 				"&7Vous avez retiré &6{amount} {symbol}&7 à &6{player}&7.", 
										"&6{amount} {symbol}&7 taken from &6{player} &7a account."),
		TAKE_ERROR_MIN( 				"&6{player} &cn'a pas assez {money}&c.", 
										"&6{player} &cdoesn't have enough {money}&c."),
		TAKE_ERROR_MIN_EQUALS( 			"&cVous n'avez pas assez {money}&c.", 
										"&cYou don't have enough {money}&c."),
		
		RESET_DESCRIPTION( 				"Réinitialiser les {money_plural} d'un joueur", 
										"Reset the currency of a player"),
		RESET_PLAYER( 					"&7Vous avez réinitialisé votre compte.", 
										"&7You have reset your account."),
		RESET_OTHERS_PLAYER( 			"&7Votre monnaie a été réinitialisé par &6{staff}&7.", 
										"&7Your money has been reset by &6{staff}&7."),
		RESET_OTHERS_STAFF( 			"&7Vous avez réinitialisé le compte de &6{player}&7.", 
										"&7You have reset the currency &6{player}&7."),
		
		LOG_DESCRIPTION( 				"Connaître l'historique des transactions d'un joueur", 
										"Know the history of the transactions of a player"),
		LOG_TITLE( 						"&aHistorique de &6{player} &a: &6{money_plural}", 
										"&aTransactions of &6{player} &a: &6{money_plural}"),
		LOG_LINE_TRANSACTION( 			"&c{time} &6: &7{transaction} &6: &b{before} &6: &a{after} &8: &7{cause}"),
		LOG_LINE_TRANSFERT( 			"&c{time} &6: &7{transaction} &6: &b{before} &6: &a{after} &6: &d{player} &8: &7{cause}"),
		LOG_EMPTY( 						"    &7Aucune transaction", 
										"    &7No transaction"),
		LOG_PRINT( 						"&7L'historique des transactions de &6{player} &7a été transféré dans le fichier &6{file}&7.", 
										"&7The history of &6{player} &7transactions was transferred to the file &6{file}&7."),
		LOG_PRINT_EQUALS( 				"&7Vos logs ont été transféré dans le fichier &6{file}&7.", 
										"&7The history of your transactions transferred into the file &6{file}&7."),
		LOG_PRINT_LINE_TRANSACTION( 	"[{time}] &7{transaction} &6: (before='{before}';after='{after}';cause='{cause}')"),
		LOG_PRINT_LINE_TRANSFERT( 		"[{time}] &7{transaction} &6: (before='{before}';after='{after}';to='{player}';cause='{cause}')"),
		LOG_PRINT_EMPTY( 				"&6{player} &7n'a fait aucune transaction.", 
										"&6{player} &7hasn't traded"),
		LOG_PRINT_EMPTY_EQUALS( 		"&7Vous n'avez effectué aucune transaction.", 
										"&7You haven't made any transaction."),
		
		SIGN_BALANCE_CREATE(			"&7Le panneau a été créé avec succès."),
		SIGN_BALANCE_DISABLE(   		"&cCe panneau est désactivé."),
		SIGN_BALANCE_BREAK(				"&7Le panneau a été supprimé."),
		
		PERMISSIONS_COMMANDS_EXECUTE(				""),
		PERMISSIONS_COMMANDS_HELP(					""),
		PERMISSIONS_COMMANDS_RELOAD(				""),
		PERMISSIONS_COMMANDS_GIVE(					""),
		PERMISSIONS_COMMANDS_TAKE(					""),
		PERMISSIONS_COMMANDS_RESET(					""),
		PERMISSIONS_COMMANDS_LOG_EXECUTE(			""),
		PERMISSIONS_COMMANDS_LOG_PRINT(				""),
		PERMISSIONS_COMMANDS_BALANCE_EXECUTE(		""),
		PERMISSIONS_COMMANDS_BALANCE_OTHERS(		""),
		PERMISSIONS_COMMANDS_BALANCETOP_EXECUTE(	""),
		PERMISSIONS_COMMANDS_PAY_EXECUTE(			"");
		
		private final String path;
	    private final EMessageBuilder french;
	    private final EMessageBuilder english;
	    private EMessageFormat message;
	    private EMessageBuilder builder;
	    
	    private EEMessages(final String french) {   	
	    	this(EMessageFormat.builder().chat(new EFormatString(french), true));
	    }
	    
	    private EEMessages(final String french, final String english) {   	
	    	this(EMessageFormat.builder().chat(new EFormatString(french), true), 
	    		EMessageFormat.builder().chat(new EFormatString(english), true));
	    }
	    
	    private EEMessages(final EMessageBuilder french) {   	
	    	this(french, french);
	    }
	    
	    private EEMessages(final EMessageBuilder french, final EMessageBuilder english) {
	    	Preconditions.checkNotNull(french, "Le message '" + this.name() + "' n'est pas définit");
	    	
	    	this.path = this.resolvePath();	    	
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
