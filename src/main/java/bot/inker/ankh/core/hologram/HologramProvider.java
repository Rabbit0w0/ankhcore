package bot.inker.ankh.core.hologram;

import bot.inker.ankh.core.api.hologram.HologramService;
import bot.inker.ankh.core.api.ioc.DcLazy;
import bot.inker.ankh.core.common.config.AnkhConfig;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class HologramProvider extends DcLazy<HologramService> implements Provider<HologramService> {
  private final Injector injector;
  private final AnkhConfig config;

  @Inject
  private HologramProvider(Injector injector, AnkhConfig config) {
    this.injector = injector;
    this.config = config;
  }

  @Override
  protected HologramService initialize() throws Throwable {
    // use config special hologram service
    val configHologram = config.getService().getHologram();
    if(configHologram != null && !configHologram.isEmpty()){
      logger.info("use special hologram service: {}", configHologram);
      return injector.getInstance(Key.get(HologramService.class, Names.named(configHologram)));
    }

    // test and use support service
    if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
      return injector.getInstance(Key.get(HologramService.class, Names.named("holographic-displays")));
    }

    // no support service found, use nop
    logger.info("====================================================");
    logger.info("No support hologram plugin found, all hologram will not display");
    logger.info("We recommend bukkit plugin 'HolographicDisplays' to support it");
    logger.info("You can download it from https://dev.bukkit.org/projects/holographic-displays");
    logger.info("====================================================");
    return injector.getInstance(Key.get(HologramService.class, Names.named("nop")));
  }
}
