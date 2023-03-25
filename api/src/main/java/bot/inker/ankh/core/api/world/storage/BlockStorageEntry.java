package bot.inker.ankh.core.api.world.storage;

import bot.inker.ankh.core.api.ioc.DcLazy;
import bot.inker.ankh.core.api.ioc.IocLazy;
import bot.inker.ankh.core.api.storage.LocationStorage;
import bot.inker.ankh.core.api.util.IBuilder;
import net.kyori.adventure.key.Key;

import javax.annotation.Nonnull;

public interface BlockStorageEntry {
  static @Nonnull Factory factory() {
    return Factory.INSTANCE.get();
  }

  static @Nonnull Builder builder() {
    return factory().builder();
  }

  static @Nonnull BlockStorageEntry of(@Nonnull LocationStorage location, @Nonnull Key blockId, @Nonnull byte[] content) {
    return factory().of(location, blockId, content);
  }

  @Nonnull
  LocationStorage location();

  @Nonnull
  Key blockId();

  @Nonnull
  byte[] content();

  interface Factory {
    DcLazy<Factory> INSTANCE = new IocLazy<>(Factory.class);

    @Nonnull
    Builder builder();

    @Nonnull
    BlockStorageEntry of(@Nonnull LocationStorage location, @Nonnull Key blockId, @Nonnull byte[] content);
  }

  interface Builder extends IBuilder<Builder, BlockStorageEntry> {
    @Nonnull
    Builder location(@Nonnull LocationStorage location);

    @Nonnull
    Builder blockId(@Nonnull Key blockId);

    @Nonnull
    Builder content(@Nonnull byte[] content);
  }
}
