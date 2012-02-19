package net.craftstars.general.items;

import org.bukkit.command.CommandSender;

import net.craftstars.general.text.LanguageText;
import net.craftstars.general.text.Messaging;

public class InvalidItemException extends RuntimeException {
	private static final long serialVersionUID = 5942934532490181491L;

	public InvalidItemException(LanguageText msg, Object... params) {
		super(msg.value(params));
	}

	public void feedbackTo(CommandSender sender) {
		Messaging.send(sender, getMessage());
	}
}
