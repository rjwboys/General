package net.craftstars.general.mobs;

import org.bukkit.command.CommandSender;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;

public class InvalidMobException extends RuntimeException {
	private static final long serialVersionUID = 9072496179873948959L;

	public InvalidMobException(LanguageText msg, Object... params) {
		super(msg.value(params));
	}
	
	public InvalidMobException(Throwable cause, LanguageText msg, Object... params) {
		super(msg.value(params), cause);
	}

	public void feedbackTo(CommandSender sender) {
		Messaging.send(sender, getMessage());
	}
}
