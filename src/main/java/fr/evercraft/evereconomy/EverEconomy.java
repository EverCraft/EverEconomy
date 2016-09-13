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

import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.economy.EconomyService;

import fr.evercraft.everapi.EverAPI;
import fr.evercraft.everapi.exception.PluginDisableException;
import fr.evercraft.everapi.plugin.EPlugin;
import fr.evercraft.everapi.services.TopEconomyService;
import fr.evercraft.evereconomy.command.EEBalance;
import fr.evercraft.evereconomy.command.EEBalanceTop;
import fr.evercraft.evereconomy.command.EEPay;
import fr.evercraft.evereconomy.command.sub.EEGive;
import fr.evercraft.evereconomy.command.sub.EELog;
import fr.evercraft.evereconomy.command.sub.EEReload;
import fr.evercraft.evereconomy.command.sub.EEReset;
import fr.evercraft.evereconomy.command.sub.EETake;
import fr.evercraft.evereconomy.service.economy.EEconomyService;
import fr.evercraft.evereconomy.sign.BalanceSign;

@Plugin(id = "evereconomy", 
		name = "EverEconomy", 
		version = EverAPI.VERSION, 
		description = "Management of the economy",
		url = "http://evercraft.fr/",
		authors = {"rexbut","lesbleu"},
		dependencies = {
		    @Dependency(id = "everapi", version = EverAPI.VERSION),
		    @Dependency(id = "everchat", version = EverAPI.VERSION, optional = true)
		})
public class EverEconomy extends EPlugin {

	private EEConfig configs;
	private EEMessage messages;
	private EEDataBase databases;

	private EEconomyService service;
	
	@Override
	protected void onPreEnable() throws PluginDisableException {
		// Configurations
		this.configs = new EEConfig(this);
		this.messages = new EEMessage(this);
		
		// MySQL
		this.databases = new EEDataBase(this);
		
		// Economy
		this.service = new EEconomyService(this);
		this.getGame().getServiceManager().setProvider(this, EconomyService.class, this.service);
		this.getGame().getServiceManager().setProvider(this, TopEconomyService.class, this.service);
	}
	
	@Override
	protected void onCompleteEnable() throws PluginDisableException {
		// Economy
		if (!this.getEverAPI().getManagerService().getEconomy().isPresent()) {
			throw new PluginDisableException("Il n'y a pas de système d'économie !");
		}
		
		// Commands
		EECommand command = new EECommand(this);
		
		command.add(new EEBalance(this));
		command.add(new EEBalanceTop(this));
		command.add(new EEPay(this));
		
		command.add(new EEReload(this, command));
		command.add(new EEGive(this, command));
		command.add(new EETake(this, command));
		command.add(new EEReset(this, command));
		command.add(new EELog(this, command));
		
		// Listerners
		this.getGame().getEventManager().registerListeners(this, new EEListener(this));
		
		if (this.getEverAPI().getManagerService().getSign().isPresent()) {
			this.getEverAPI().getManagerService().getSign().get().add(new BalanceSign(this));
		}
	}
	
	@Override
	protected void onReload() throws PluginDisableException {
		// Configurations
		this.reloadConfigurations();
		this.databases.reload();

		// Economy
		this.service.reload();
		
		if (!this.getEverAPI().getManagerService().getEconomy().isPresent()){
			throw new PluginDisableException("Il n'y a pas de système d'économie !");
		}
	}
	
	@Override
	protected void onDisable() {
	}
	
	public EEConfig getConfigs(){
		return this.configs;
	}
	
	public EEMessage getMessages(){
		return this.messages;
	}
	
	public EEDataBase getDataBases(){
		return this.databases;
	}
	
	public EEconomyService getService(){
		return this.service;
	}
}
