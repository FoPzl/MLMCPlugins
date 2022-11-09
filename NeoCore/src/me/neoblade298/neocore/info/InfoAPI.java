package me.neoblade298.neocore.info;

import java.io.File;
import java.util.HashMap;

import me.neoblade298.neocore.NeoCore;
import me.neoblade298.neocore.exceptions.NeoIOException;
import me.neoblade298.neocore.io.FileLoader;

public class InfoAPI {
	private static HashMap<String, BossInfo> bossInfo = new HashMap<String, BossInfo>();
	private static final FileLoader bossLoader;
	
	static {
		bossLoader = (cfg, file) -> {
			for (String key : cfg.getKeys(false)) {
				BossInfo bi = new BossInfo(cfg.getConfigurationSection(key));
				bossInfo.put(bi.getKey(), bi);
			}
		};
	}
	
	public static BossInfo getBossInfo(String boss) {
		return bossInfo.get(boss);
	}
	
	public static void reload() {
		bossInfo.clear();
		try {
			NeoCore.loadFiles(new File(NeoCore.inst().getDataFolder() + "/info/bosses.yml"), bossLoader);
		} catch (NeoIOException e) {
			e.printStackTrace();
		}
	}
}
