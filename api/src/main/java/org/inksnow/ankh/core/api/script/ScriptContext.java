package org.inksnow.ankh.core.api.script;

import org.bukkit.entity.Player;
import org.inksnow.ankh.core.api.ioc.DcLazy;
import org.inksnow.ankh.core.api.ioc.IocLazy;
import org.inksnow.ankh.core.api.util.IBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public interface ScriptContext {
  @Nullable Player player();
  @Nonnull Player requirePlayer();

  @Nullable Object get(@Nonnull String key);
  @Nonnull Object require(@Nonnull String key);
  void set(@Nonnull String key, @Nullable Object value);
  @Nullable Object remove(@Nonnull String key);
  boolean contains(@Nonnull String key);
  @Nonnull Map<String, Object> content();


  static @Nonnull Factory factory(){
    return $internal$actions$.FACTORY.get();
  }

  static @Nonnull Builder builder(){
    return factory().builder();
  }

  interface Factory {
    @Nonnull Builder builder();
  }

  interface Builder extends IBuilder<Builder, ScriptContext> {
    @Nonnull Builder player(@Nonnull Player player);

    @Nonnull Builder with(@Nonnull String key, @Nullable Object value);
  }

  static class $internal$actions$ {
    private static final DcLazy<Factory> FACTORY = IocLazy.of(Factory.class);
  }
}
