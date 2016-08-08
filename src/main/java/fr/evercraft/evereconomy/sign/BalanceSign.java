package fr.evercraft.evereconomy.sign;

import java.math.BigDecimal;

import org.spongepowered.api.block.tileentity.Sign;

import fr.evercraft.everapi.server.player.EPlayer;
import fr.evercraft.everapi.services.signs.SignSubject;
import fr.evercraft.everapi.text.ETextBuilder;
import fr.evercraft.evereconomy.EEMessage.EEMessages;
import fr.evercraft.evereconomy.EverEconomy;

public class BalanceSign implements SignSubject {
	
	private static final String NAME = "balance";
	
	private final EverEconomy plugin;
	
	public BalanceSign(final EverEconomy plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public String getName() {
		return BalanceSign.NAME;
	}

	@Override
	public boolean create(EPlayer player, Sign sign) {
		player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.SIGN_BALANCE_CREATE.getText()));
		return true;
	}

	@Override
	public boolean useEnable(EPlayer player, Sign sign) {
		BigDecimal balance = player.getBalance();
		player.sendMessage(
				ETextBuilder.toBuilder(EEMessages.PREFIX.get())
					.append(this.plugin.getService().replace(EEMessages.BALANCE_PLAYER.get())
							.replaceAll("<solde>", this.plugin.getService().getDefaultCurrency().cast(balance)))
					.replace("<solde_format>", this.plugin.getService().getDefaultCurrency().format(balance))
					.build());
		return true;
	}

	@Override
	public boolean useDisable(EPlayer player, Sign sign) {
		player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.SIGN_BALANCE_DISABLE.getText()));
		return true;
	}

	@Override
	public boolean remove(EPlayer player, Sign sign) {
		player.sendMessage(EEMessages.PREFIX.getText().concat(EEMessages.SIGN_BALANCE_BREAK.getText()));
		return false;
	}
	
	@Override
	public boolean valide(Sign sign) {
		return true;
	}
}
