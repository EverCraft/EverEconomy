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
package fr.evercraft.evereconomy.service.economy;

import java.math.BigDecimal;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.java.UtilsDouble;
import fr.evercraft.everapi.plugin.EChat;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.evereconomy.EverEconomy;

public class ECurrency implements Currency {
	
	private final EverEconomy plugin;
	
	private String identifier;
	private String name;
	
	private String format;
	
	private Text singular;
	private Text plural;
	private Text symbol;
	
	private int numFractionDigits;
	
	private BigDecimal defaultBalance;
	private BigDecimal maxBalance;
	private BigDecimal minBalance;

	public ECurrency(EverEconomy plugin) {
		this.plugin = plugin;
		
		reload();
    }
	
	public void reload() {
		this.identifier = this.plugin.getConfigs().getCurrencyId();
		this.name = this.plugin.getConfigs().getCurrencyName();
		
		this.format = this.plugin.getConfigs().getCurrencyFormat();
		
        this.singular = EChat.of(this.plugin.getConfigs().getCurrencySingular());
        this.plural = EChat.of(this.plugin.getConfigs().getCurrencyPlural());
        this.symbol = EChat.of(this.plugin.getConfigs().getCurrencySymbol());
        
        this.numFractionDigits = this.plugin.getConfigs().getCurrencyNumFractionDigits();
        
        this.defaultBalance = new BigDecimal(this.plugin.getConfigs().getCurrencyBalanceDefault());
        this.minBalance = new BigDecimal(this.plugin.getConfigs().getCurrencyBalanceMin());
        this.maxBalance = new BigDecimal(this.plugin.getConfigs().getCurrencyBalanceMax());
	}
	
	@Override
	public String getId() {
		return this.identifier;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Text getDisplayName() {
		return this.singular;
	}

	@Override
	public Text getPluralDisplayName() {
		return this.plural;
	}

	@Override
	public Text getSymbol() {
		return this.symbol;
	}

	@Override
	public Text format(final BigDecimal amount, final int numFractionDigits) {
		if (amount.compareTo(BigDecimal.ONE) <= 0) {
			return ETextBuilder.toBuilder(this.format
						.replace("<amount>", amount.setScale(numFractionDigits, BigDecimal.ROUND_HALF_UP).toString()))
					.replace("<currency>", this.singular)
					.replace("<symbol>", this.symbol)
					.build();
		}
		return ETextBuilder.toBuilder(this.format
						.replace("<amount>", amount.setScale(numFractionDigits, BigDecimal.ROUND_HALF_UP).toString()))
					.replace("<currency>", this.plural)
					.replace("<symbol>", this.symbol)
					.build();
	}
	
	public String cast(BigDecimal amount) {
		amount.setScale(numFractionDigits, BigDecimal.ROUND_HALF_UP);
		return UtilsDouble.getString(amount);
	}

	@Override
	public int getDefaultFractionDigits() {
		return this.numFractionDigits;
	}

	@Override
	public boolean isDefault() {
		return true;
	}

	public BigDecimal getBalanceDefault() {
		return this.defaultBalance;
	}
	
	public BigDecimal getBalanceMin() {
		return this.minBalance;
	}
	
	public BigDecimal getBalanceMax() {
		return this.maxBalance;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Currency)) {
			return false;
		}
		return this.getId().equals(((Currency)obj).getId());
	}
	
	public static BigDecimal getBalanceMin(final Currency currency) {
		if (currency instanceof ECurrency) {
			return ((ECurrency) currency).getBalanceMin();
		}
		return BigDecimal.ZERO;
	}
	
	public static BigDecimal getBalanceMax(final Currency currency) {
		if (currency instanceof ECurrency) {
			return ((ECurrency) currency).getBalanceMax();
		}
		return BigDecimal.valueOf(Double.MAX_VALUE);
	}
}
