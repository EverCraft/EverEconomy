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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.service.context.ContextCalculator;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.java.Chronometer;
import fr.evercraft.everapi.services.TopEconomyService;
import fr.evercraft.evereconomy.EverEconomy;
import fr.evercraft.evereconomy.service.economy.account.EUniqueAccount;
import fr.evercraft.evereconomy.service.economy.account.EVirtualAccount;

public class EEconomyService implements TopEconomyService, EconomyService {
	private final EverEconomy plugin;
	
	private final ConcurrentMap<String, ECurrency> currencies;
	
	private final ConcurrentMap<String, EVirtualAccount> virtuals;
	
	private final ConcurrentMap<UUID, EUniqueAccount> players;
	private final LoadingCache<UUID, EUniqueAccount> cache;
	
	private final ECurrency currency;
	
	public EEconomyService(final EverEconomy plugin){
		this.plugin = plugin;
		
		this.players = new ConcurrentHashMap<UUID, EUniqueAccount>();
		this.cache = CacheBuilder.newBuilder()
			    .maximumSize(100)
			    .expireAfterAccess(5, TimeUnit.MINUTES)
			    .build(new CacheLoader<UUID, EUniqueAccount>() {
			    	/**
			    	 * Ajoute un joueur au cache
			    	 */
			        @Override
			        public EUniqueAccount load(UUID uuid){
			        	Chronometer chronometer = new Chronometer();
			        	
			        	EUniqueAccount subject = new EUniqueAccount(EEconomyService.this.plugin, uuid);
			        	EEconomyService.this.plugin.getLogger().debug("Loading user '" + uuid.toString() + "' in " +  chronometer.getMilliseconds().toString() + " ms");
			            
			            return subject;
			        }
			    });
		
		this.virtuals = new ConcurrentHashMap<String, EVirtualAccount>();
		this.currencies = new ConcurrentHashMap<String, ECurrency>();
		
		this.currency = new ECurrency(this.plugin);
		registerCurrencies(this.currency);
		
		reload();
	}
	
	public void reload() {
		this.currency.reload();
		
		this.cache.cleanUp();
		for(EUniqueAccount account : this.players.values()) {
			account.reload();
		}
		for(EVirtualAccount account : this.virtuals.values()) {
			account.reload();
		}
	}
	
	@Override
	public void registerContextCalculator(ContextCalculator<Account> calculator) {}

	@Override
	public ECurrency getDefaultCurrency() {
		return this.currency;
	}

	@Override
	public Set<Currency> getCurrencies() {
		return new HashSet<Currency>(this.currencies.values());
	}
	
	public Optional<ECurrency> getCurrency(String identifier) {
		return Optional.ofNullable(this.currencies.get(identifier));
	}
	
	public void registerCurrencies(final ECurrency currency) {
		this.currencies.put(currency.getId(), currency);
	}

	@Override
	public Optional<UniqueAccount> getOrCreateAccount(final UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		try {
			if(!this.players.containsKey(uuid)) {
				return Optional.of(this.cache.get(uuid));
	    	}
	    	return Optional.of(this.players.get(uuid));
		} catch (ExecutionException e) {
			this.plugin.getLogger().warn("Error : Loading user (identifier='" + uuid.toString() + "';message='" + e.getMessage() + "')");
		}
		return Optional.empty();
	}

	@Override
	public Optional<Account> getOrCreateAccount(final String identifier) {
		Preconditions.checkNotNull(identifier, "identifier");
		
		if(!this.virtuals.containsKey(identifier)){
			this.virtuals.put(identifier, new EVirtualAccount(this.plugin, identifier));
		}
		return Optional.ofNullable(this.virtuals.get(identifier));
	}

	@Override
	public boolean hasAccount(final UUID uuid) {
		return this.players.containsKey(uuid);
	}

	@Override
	public boolean hasAccount(final String identifier) {
		return this.virtuals.containsKey(identifier);
	}
	
	/*
	 * Player cache
	 */
	
	/**
	 * Ajoute un joueur à la liste
	 * @param identifier L'UUID du joueur
	 */
	public void registerPlayer(final UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		EUniqueAccount player = this.cache.getIfPresent(uuid);
		// Si le joueur est dans le cache
		if(player != null) {
			this.players.putIfAbsent(uuid, player);
			this.plugin.getLogger().debug("Loading player cache : " + uuid.toString());
		// Si le joueur n'est pas dans le cache
		} else {
			Chronometer chronometer = new Chronometer();
			player = new EUniqueAccount(this.plugin, uuid);
			this.players.putIfAbsent(uuid, player);
			this.plugin.getLogger().debug("Loading player '" + uuid.toString() + "' in " +  chronometer.getMilliseconds().toString() + " ms");
		}
	}
	
	/**
	 * Supprime un joueur à la liste et l'ajoute au cache
	 * @param identifier L'UUID du joueur
	 */
	public void removePlayer(final UUID uuid) {
		Preconditions.checkNotNull(uuid, "uuid");
		
		EUniqueAccount player = this.players.remove(uuid);
		// Si le joueur existe
		if(player != null) {
			this.cache.put(uuid, player);
			this.plugin.getLogger().debug("Unloading the player : " + uuid.toString());
		}
	}
	
	/*
	 * Top
	 */
	
	public LinkedHashMap<UUID, BigDecimal> topUniqueAccount(final int count) {
		return topUniqueAccount(this.currency, count);
	}
	
	public LinkedHashMap<UUID, BigDecimal> topUniqueAccount(final Currency currency, final int count) {
		LinkedHashMap<UUID, BigDecimal> top = new LinkedHashMap<UUID, BigDecimal>();
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			String query = "SELECT `identifier`, `balance` "
						+ "FROM `" + this.plugin.getDataBases().getTableAccount() + "` "
						+ "WHERE `currency` = ? "
						+ "AND `identifier` NOT IN ( '" + String.join("' , '", this.plugin.getConfigs().getListString("bypass")) + "' ) "
						+ "AND LENGTH(identifier) = 36 "
						+ "ORDER BY `balance` DESC, `identifier` ASC "
						+ "LIMIT 0, " + count + " ;";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, currency.getId());
			ResultSet list = preparedStatement.executeQuery();
			while(list.next()){
				try {
					top.put(UUID.fromString(list.getString("identifier")), list.getBigDecimal("balance"));
				} catch (IllegalArgumentException e) {}
			}
			this.plugin.getLogger().debug("Top economy : (currency:'" + currency.getId() + "')");
		} catch (SQLException e) {
			this.plugin.getLogger().warn("Error during a change of top : (currency:'" + currency.getId() + "'): " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
		return top;
	}
	
	public String replace(final String message) {
		return message.replaceAll("<symbol>", this.plugin.getService().getDefaultCurrency().getSymbol().toPlain())
				.replaceAll("<money_singular>", this.plugin.getService().getDefaultCurrency().getDisplayName().toPlain())
				.replaceAll("<money_plural>", this.plugin.getService().getDefaultCurrency().getPluralDisplayName().toPlain());
	}
}
