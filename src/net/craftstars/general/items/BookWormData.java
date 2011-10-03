package net.craftstars.general.items;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import com.nisovin.bookworm.Book;
import com.nisovin.bookworm.BookWorm;

public class BookWormData extends ItemData {
	@Override
	public boolean validate(ItemID id, Material check) {
		Plugin bookworm = Bukkit.getPluginManager().getPlugin("BookWorm");
		if(bookworm == null) return id.getData() == null || id.getData() == 0;
		if(id.getData() == null) id.setData(0);
		if(id.getData() > 0) {
			File bookFile = new File(bookworm.getDataFolder(), id.getData() + ".txt");
			boolean exists = true;
			if(!bookFile.exists()) {
				String[] filenames = bookworm.getDataFolder().list();
				exists = false;
				for(String file : filenames) {
					if(file.startsWith(Integer.toString(id.getData()) + "_") && file.endsWith(".txt")) {
						exists = true;
						break;
					}
				}
			}
			if(exists) {
				Book book = BookWorm.getBook(id.getData().shortValue());
				if(book != null)
					id.setName('"' + book.getTitle() + '"' + " by " + book.getAuthor());
				else return false;
			} else return false;
		}
		return true;
	}
}
