package bot.inker.ankh.core.api.util;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IRegistry<T extends Keyed> {
  void register(@Nonnull T instance);

  @Nonnull
  T require(@Nonnull Key key);

  @Nullable
  T get(@Nonnull Key key);
}
