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
package fr.evercraft.evereconomy.service.economy;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.evereconomy.EverEconomy;

public class ELog {
	private final EverEconomy plugin;
	
	private final String time;
	private final Currency currency;
	private final BigDecimal before;
	private final BigDecimal after;
	private final String transaction;
	private final String to;
	private final String cause;
	
	public ELog(final EverEconomy plugin, final Timestamp time, final String identifier, final Currency currency,
			final BigDecimal before, final BigDecimal after, final String transaction, final String to,
			final String cause) {
		this.plugin = plugin;
		this.time = this.plugin.getEverAPI().getManagerUtils().getDate().parseDateTime(time.getTime());
		this.currency = currency;
		this.before = before;
		this.after = after;
		this.transaction = transaction;
		this.cause = cause;
		
		if(to != null) {
			Optional<User> user = this.plugin.getEServer().getUser(to);
			if(user.isPresent()){
				this.to = user.get().getName();
			} else {
				this.to = to;
			}
		} else {
			this.to = null;
		}
	}
	
	public Text replace(final String transaction, final String transfer) {
		if(to != null) {
			return ETextBuilder.toBuilder(transfer
						.replaceAll("<before>", this.before.setScale(this.currency.getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP).toString())
						.replaceAll("<after>", this.after.setScale(this.currency.getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP).toString())
						.replaceAll("<transaction>", this.transaction)
						.replaceAll("<player>", this.to)
						.replaceAll("<cause>", this.cause)
						.replaceAll("<time>", this.time))
					.replace("<before_format>", this.currency.format(this.after))
					.replace("<after_format>", this.currency.format(this.before))
					.build();
		} else {
			return ETextBuilder.toBuilder(transaction
					.replaceAll("<before>", this.before.setScale(this.currency.getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP).toString())
					.replaceAll("<after>", this.after.setScale(this.currency.getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP).toString())
					.replaceAll("<transaction>", this.transaction)
					.replaceAll("<cause>", this.cause)
					.replaceAll("<time>", this.time))
				.replace("<before_format>", this.currency.format(this.after))
				.replace("<after_format>", this.currency.format(this.before))
				.build();
		}
	}
}
