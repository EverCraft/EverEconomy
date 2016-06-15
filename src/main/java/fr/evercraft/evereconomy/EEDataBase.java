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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.TransactionType;

import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.exception.ServerDisableException;
import fr.evercraft.everapi.plugin.EDataBase;
import fr.evercraft.evereconomy.service.economy.ELog;

public class EEDataBase extends EDataBase<EverEconomy> {
	private String table_account;
	private String table_log;

	public EEDataBase(EverEconomy plugin) throws PluginDisableException {
		super(plugin, true);
	}

	public boolean init() throws ServerDisableException {
		this.table_account = "account";
		String account = 	"CREATE TABLE IF NOT EXISTS <table> (" +
							"`identifier` varchar(36) NOT NULL," +
							"`currency` varchar(36) NOT NULL," +
							"`balance` DECIMAL NOT NULL," +
							"PRIMARY KEY (`identifier`, `currency`));";
		initTable(this.getTableAccount(), account);
		
		this.table_log = "log";
		String log = 		"CREATE TABLE IF NOT EXISTS <table> (" +
							"`id` MEDIUMINT NOT NULL AUTO_INCREMENT," +
							"`time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
							"`identifier` varchar(36) NOT NULL," +
							"`currency` varchar(36) NOT NULL," +
							"`before` DECIMAL NOT NULL," +
							"`after` DECIMAL NOT NULL," +
							"`transaction` varchar(50) NOT NULL," +
							"`to` varchar(36)," +
							"`cause` varchar(255) NOT NULL," +
							"PRIMARY KEY (`id`));";
		initTable(this.getTableLog(), log);
		
		return true;
	}
	
	public String getTableAccount() {
		return this.getPrefix() + this.table_account;
	}
	
	public String getTableLog() {
		return this.getPrefix() + this.table_log;
	}
	
	public void log(final String identifier, final Currency currency, final BigDecimal before, final BigDecimal after, final TransactionType transaction, final Cause cause, final String to) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			String query = 	  "INSERT INTO `" + this.getTableLog() + "` "
							+ "(`identifier`, `currency`, `before`, `after`, `transaction`, `to`, `cause`) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?);";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, identifier);
			preparedStatement.setString(2, currency.getId());
			preparedStatement.setBigDecimal(3, before);
			preparedStatement.setBigDecimal(4, after);
			preparedStatement.setString(5, transaction.getName());
			preparedStatement.setString(6, to);
			preparedStatement.setString(7, String.join(", ", cause.getNamedCauses().keySet()));
			
			preparedStatement.execute();
			this.plugin.getLogger().debug("Log : (identifier='" + identifier + "';"
													+ "currency='" + currency.getId() + "';"
													+ "before='" + before + "';"
													+ "after='" + after + "';"
													+ "transaction='" + transaction.getName() + "';"
													+ "to='" + to + "';"
													+ "cause='" + String.join(", ", cause.getNamedCauses().keySet()) + "')");
		} catch (SQLException e) {
	    	this.plugin.getLogger().warn("Error during a change of log : " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
	}
	
	public List<ELog> selectLog(String identifier, Currency currency) {
		List<ELog> logs = new ArrayList<ELog>();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = this.plugin.getDataBases().getConnection();
			String query = "SELECT * FROM `" + this.getTableLog() + "` WHERE identifier = ? AND currency = ?;";
			preparedStatement = this.plugin.getDataBases().getConnection().prepareStatement(query);
			preparedStatement.setString(1, identifier);
			preparedStatement.setString(2, currency.getId());
			ResultSet list = preparedStatement.executeQuery();
			while(list.next()){
				logs.add(new ELog(this.plugin, list.getTimestamp("time"), identifier,  currency, list.getBigDecimal("before"), list.getBigDecimal("after"), list.getString("transaction"), list.getString("to"), list.getString("cause")));
			}
		} catch (SQLException e) {
	    	this.plugin.getLogger().warn("Error during a change of log : (identifier:'" + identifier + "'): " + e.getMessage());
		} catch (ServerDisableException e) {
			e.execute();
		} finally {
			try {
				if (preparedStatement != null) preparedStatement.close();
				if (connection != null) connection.close();
			} catch (SQLException e) {}
	    }
		return logs;
	}
}
