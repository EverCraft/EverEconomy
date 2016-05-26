package fr.evercraft.evereconomy;

import org.spongepowered.api.command.CommandSource;

import com.google.common.base.Preconditions;

import fr.evercraft.everapi.plugin.EnumPermission;

public enum EEPermissions implements EnumPermission {
	HELP("help"),
	RELOAD("reload"),
	
	BALANCE("balance.command"),
	BALANCE_OTHERS("balance.others"),
	BALANCE_TOP("balancetop"),
	
	PAY("pay"),
	
	EVERECONOMY("command"),
	GIVE("give"),
	TAKE("take"),
	RESET("reset"),
	
	LOG("log.command"),
	LOG_PRINT("log.print");
	
	private final static String prefix = "evereconomy";
	
	private final String permission;
    
    private EEPermissions(final String permission) {   	
    	Preconditions.checkNotNull(permission, "La permission '" + this.name() + "' n'est pas définit");
    	
    	this.permission = permission;
    }

    public String get() {
		return EEPermissions.prefix + "." + this.permission;
	}
    
    public boolean has(CommandSource player) {
    	return player.hasPermission(this.get());
    }
}