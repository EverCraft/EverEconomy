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

import fr.evercraft.everapi.plugin.file.EConfig;

public class EEConfig extends EConfig<EverEconomy> {

	public EEConfig(EverEconomy plugin) {
		super(plugin);
	}
	
	public void reload() {
		super.reload();
		this.plugin.getELogger().setDebug(this.isDebug());
	}
	
	@Override
	public List<String> getHeader() {
		return 	Arrays.asList(	"####################################################### #",
								"                  EverEconomy (By rexbut)                #",
								"    For more information : https://docs.evercraft.fr     #",
								"####################################################### #");
	}

	public void loadDefault() {
		this.configDefault();
		this.sqlDefault();
		
		// Currency
		addDefault("currency.id", "evereconomy_emeraude");
		addDefault("currency.name", "Emeraude");
		addDefault("currency.singular", "&aEmeraude");
		addDefault("currency.plural", "&aEmeraudes");
		addDefault("currency.symbol", "&aE");
		addDefault("currency.format", "&6<amount> <currency>", "Usage : <amount>, <currency>, <symbol>");
		addDefault("currency.starting-balance", 300);
		addDefault("currency.min-balance", 0);
		addDefault("currency.max-balance", 1000000);
		addDefault("currency.num-fraction-digits", 2);
		
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
		return this.get("currency.starting-balance").getDouble(0);
	}
	
	public int getCurrencyNumFractionDigits(){
		return this.get("currency.num-fraction-digits").getInt(2);
	}
	
	public double getCurrencyBalanceMin(){
		return this.get("currency.min-balance").getDouble(0);
	}
	
	public double getCurrencyBalanceMax(){
		return this.get("currency.max-balance").getDouble(Double.MAX_VALUE);
	}

	public String getCurrencyFormat() {
		return this.get("currency.format").getString("<amount> <currency>");
	}
}