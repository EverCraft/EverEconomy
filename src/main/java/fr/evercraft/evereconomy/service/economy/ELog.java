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
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import fr.evercraft.everapi.message.format.EFormat;
import fr.evercraft.everapi.message.replace.EReplace;
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
	
	private final Map<Pattern, EReplace<?>> replaces;
	
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
		
		Builder<Pattern, EReplace<?>> builder = ImmutableMap.builder();
		builder.put(Pattern.compile("<before>"), EReplace.of(() -> this.before.setScale(this.currency.getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP).toString()));
		builder.put(Pattern.compile("<after>"), EReplace.of(() -> this.after.setScale(this.currency.getDefaultFractionDigits(), BigDecimal.ROUND_HALF_UP).toString()));
		builder.put(Pattern.compile("<transaction>"), EReplace.of(this.transaction));
		builder.put(Pattern.compile("<cause>"), EReplace.of(this.cause));
		builder.put(Pattern.compile("<time>"), EReplace.of(this.time));
		builder.put(Pattern.compile("<before_format>"), EReplace.of(() -> this.currency.format(this.after)));
		builder.put(Pattern.compile("<after_format>"), EReplace.of(() -> this.currency.format(this.before)));
		
		if (to != null) {
			Optional<User> user = this.plugin.getEServer().getUser(to);
			if (user.isPresent()){
				this.to = user.get().getName();
			} else {
				this.to = to;
			}
			
			builder.put(Pattern.compile("<player>"), EReplace.of(this.to));
		} else {
			this.to = null;
		}
		
		this.replaces = builder.build();
	}
	
	public Map<Pattern, EReplace<?>> getReplaces() {
		return this.replaces;
	}
	
	public Text replace(final EFormat transaction, final EFormat transfert) {
		if (this.to != null) {
			return transaction.toText(this.replaces);
		} else {
			return transfert.toText(this.replaces);
		}
	}
}
