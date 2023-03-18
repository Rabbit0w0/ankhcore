package bot.inker.ankh.core.api.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AnkhBukkitPlugin extends JavaPlugin implements Listener {
  public AnkhBukkitPlugin() {
    getContainer().callInit(this);
  }

  protected static AnkhPluginContainer initial(Class<? extends AnkhBukkitPlugin> mainClass) {
    AnkhPluginContainer container = $internal$actions$.thisRef.get().initial(mainClass);
    container.callClinit();
    return container;
  }

  protected abstract AnkhPluginContainer getContainer();

  @Override
  public final void onLoad() {
    getContainer().callLoad();
  }

  @Override
  public final void onDisable() {
    getContainer().callDisable();
  }

  @Override
  public void onEnable() {
    getContainer().callEnable();
  }

  @Override
  public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    return super.onCommand(sender, command, label, args);
  }

  @Override
  public final List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    return super.onTabComplete(sender, command, alias, args);
  }

  public interface $internal$actions$ {
    AtomicReference<$internal$actions$> thisRef = new AtomicReference<>();

    AnkhPluginContainer initial(Class<? extends AnkhBukkitPlugin> mainClass);
  }
}
