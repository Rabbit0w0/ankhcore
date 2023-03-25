package bot.inker.ankh.core.common.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class CheckUtil {
  public static void ensureMainThread() {
    if (!Bukkit.isPrimaryThread()) {
      throw new UnsupportedOperationException("called main thread only method in async thread.");
    }
  }

  public static void ensureAsyncThread() {
    if (Bukkit.isPrimaryThread()) {
      throw new UnsupportedOperationException("called async thread only method in main thread.");
    }
  }
}
