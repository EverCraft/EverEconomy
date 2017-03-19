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

import fr.evercraft.everapi.plugin.file.EConfig;
import fr.evercraft.everapi.plugin.file.EMessage;

public class EEConfig extends EConfig<EverEconomy> {

	public EEConfig(EverEconomy plugin) {
		super(plugin);
	}
	
	public void reload() {
		super.reload();
		this.plugin.getELogger().setDebug(this.isDebug());
	}

	public void loadDefault() {
		addDefault("DEBUG", false, "Displays plugin performance in the logs");
		addDefault("LANGUAGE", EMessage.FRENCH, "Select language messages", "Examples : ", "  French : FR_fr", "  English : EN_en");
		
		addComment("SQL", 	"Save the user in a database : ",
				" H2 : \"jdbc:h2:" + this.plugin.getPath().toAbsolutePath() + "/data\"",
				" SQL : \"jdbc:mysql://[login[:password]@]<host>:<port>/<database>\"",
				" Default users are saving in the 'data'");
		addDefault("SQL.enable", false);
		addDefault("SQL.url", "jdbc:mysql://root:password@localhost:3306/minecraft");
		addDefault("SQL.prefix", "evereconomy_");
		
		// Currency
		addDefault("currency.id", "evereconomy_emeraude");
		addDefault("currency.name", "Emeraude");
		addDefault("currency.singular", "&aEmeraude");
		addDefault("currency.plural", "&aEmeraudes");
		addDefault("currency.symbol", "&aE");
		addDefault("currency.format", "&6<amount> <currency>", "Usage : <amount>, <currency>, <symbol>");
		addDefault("currency.balanceStarting", 300);
		addDefault("currency.balanceMin", 0);
		addDefault("currency.balanceMax", 1000000);
		addDefault("currency.numFractionDigits", 2);
		
		addDefault("bypass", Arrays.asList("86f8f95b-e5e6-45c4-bf85-4d64dbd0903f"));
	}
	
	public String getCurrencyId(){
		return this.get("currency.id").getString("Emeraude");
	}
	
	public String getCurrencyName(){
		return this.get("currency.name").getString("Emeraude");
	}
	
	public String getCurrencySingular(){
		return this.get("currency.singular").getString("Emeraude");
	}
	
	public String getCurrencyPlural(){
		return this.get("currency.plural").getString("Emeraudes");
	}
	
	public String getCurrencySymbol(){
		return this.get("currency.symbol").getString("E");
	}
	
	public double getCurrencyBalanceDefault(){
		return this.get("currency.balanceStarting").getDouble(0);
	}
	
	public int getCurrencyNumFractionDigits(){
		return this.get("currency.numFractionDigits").getInt(2);
	}
	
	public double getCurrencyBalanceMin(){
		return this.get("currency.minBalance").getDouble(0);
	}
	
	public double getCurrencyBalanceMax(){
		return this.get("currency.maxBalance").getDouble(Double.MAX_VALUE);
	}

	public String getCurrencyFormat() {
		return this.get("currency.format").getString("<amount> <currency>");
	}
}