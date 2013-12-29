package hide92795.bukkit.plugin.remotecontroller;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
	public final int port;
	public final int log_max;
	public final int chat_max;
	public final List<String> editable_extension;
	public final boolean enable_dynmap_feature;
	public final String dynmap_address;
	public final boolean console_only;
	public final LinkedHashMap<String, Modifiable> file_access;

	public Config(RemoteController plugin, FileConfiguration config) throws IOException {
		this.port = config.getInt("Port");
		this.log_max = config.getInt("MaxLog");
		this.chat_max = config.getInt("MaxChat");
		this.editable_extension = config.getStringList("EditableExtension");
		this.enable_dynmap_feature = config.getBoolean("EnableDynmap");
		this.dynmap_address = config.getString("DynmapAddress");
		this.console_only = config.getBoolean("ConsoleOnly");
		this.file_access = createFileAccess(plugin, config.getMapList("FileAccess"));
	}

	private LinkedHashMap<String, Modifiable> createFileAccess(RemoteController plugin, List<Map<?, ?>> mapList) throws IOException {
		LinkedHashMap<String, Modifiable> files = new LinkedHashMap<>();
		File root = plugin.getRoot();
		for (Map<?, ?> map : mapList) {
			Set<?> s = map.keySet();
			for (Object object : s) {
				String file_s = (String) object;
				String mode = (String) map.get(object);
				File file_f = new File(root, file_s);
				String file = file_f.getCanonicalPath();
				files.put(file, new Modifiable(mode));
			}
		}
		return files;
	}
}
