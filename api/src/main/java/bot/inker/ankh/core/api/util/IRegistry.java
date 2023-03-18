package bot.inker.ankh.core.api.util;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IRegistry<T extends Keyed> {
  void register(@Nonnull T instance);

  @Nonnull
  T require(@Nonnull NamespacedKey key);

  @Nullable
  T get(@Nullable NamespacedKey key);
}
