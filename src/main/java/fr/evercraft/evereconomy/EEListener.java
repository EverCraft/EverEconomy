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

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class EEListener {
	private EverEconomy plugin;

	public EEListener(EverEconomy plugin) {
        this.plugin = plugin;
	}
    
    /**
	 * Ajoute le joueur dans le cache
	 */
	@Listener
    public void onClientConnectionEvent(final ClientConnectionEvent.Auth event) {
		this.plugin.getService().getOrCreateAccount(event.getProfile().getUniqueId().toString());
    }
	
	/**
	 * Ajoute le joueur à la liste
	 */
	@Listener
    public void onClientConnectionEvent(final ClientConnectionEvent.Join event) {
		this.plugin.getService().registerPlayer(event.getTargetEntity().getUniqueId());
    }
    
	/**
	 * Supprime le joueur de la liste
	 */
    @Listener
    public void onClientConnectionEvent(final ClientConnectionEvent.Disconnect event) {
    	this.plugin.getService().removePlayer(event.getTargetEntity().getUniqueId());
    }
	
}
