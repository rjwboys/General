package net.craftstars.general.items;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.nisovin.bookworm.Book;
import com.nisovin.bookworm.BookWorm;

public class BookWormData extends ItemData {
	@Override
	public boolean validate(int data) {
		Plugin bookworm = Bukkit.getPluginManager().getPlugin("BookWorm");
		if(bookworm == null) return data == 0;
		if(data > 0) {
			File bookFile = new File(bookworm.getDataFolder(), data + ".txt");
			boolean exists = true;
			if(!bookFile.exists()) {
				String[] filenames = bookworm.getDataFolder().list();
				exists = false;
				for(String file : filenames) {
					if(file.startsWith(Integer.toString(data) + "_") && file.endsWith(".txt")) {
						exists = true;
						break;
					}
				}
			}
			if(exists) {
				Book book = BookWorm.getBook((short)data);
				if(book != null)
					setDisplayName('"' + book.getTitle() + '"' + " by " + book.getAuthor());
				else return false;
			} else return false;
		}
		return true;
	}
}
