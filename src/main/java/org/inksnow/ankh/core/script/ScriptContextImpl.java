package org.inksnow.ankh.core.script;

import org.bukkit.entity.Player;
import org.inksnow.ankh.core.api.script.ScriptContext;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ScriptContextImpl implements ScriptContext {
  private final Map<String, Object> content;

  public ScriptContextImpl(Map<String, Object> content) {
    this.content = new HashMap<>(content);
  }

  @Override
  public @Nullable Player player() {
    return (Player) get("player");
  }

  @Override
  public @Nonnull Player requirePlayer() {
    return (Player) require("player");
  }

  @Override
  public @Nullable Object get(@Nonnull String key) {
    return content.get(key);
  }

  @Override
  public @Nonnull Object require(@Nonnull String key) {
    if (!content.containsKey(key)) {
      throw new IllegalArgumentException("required script context " + key + " not found.");
    }
    return content.get(key);
  }

  @Override
  public void set(@Nonnull String key, @Nullable Object value) {
    content.put(key, value);
  }

  @Override
  public @Nullable Object remove(@NotNull String key) {
    return content.remove(key);
  }

  @Override
  public boolean contains(@NotNull String key) {
    return content.containsKey(key);
  }

  @Override
  public @Nonnull Map<String, Object> content() {
    return content;
  }

  public static class Factory implements ScriptContext.Factory {
    @Override
    public @Nonnull Builder builder() {
      return new Builder();
    }
  }

  public static class Builder implements ScriptContext.Builder {
    private final Map<String, Object> content = new HashMap<>();

    @Override
    public @Nonnull Builder player(@Nonnull Player player) {
      content.put("player", player);
      return this;
    }

    @Override
    public @Nonnull Builder with(@Nonnull String key, @Nullable Object value) {
      content.put(key, value);
      return this;
    }

    @Override
    public @Nonnull Builder getThis() {
      return this;
    }

    @Override
    public @Nonnull ScriptContextImpl build() {
      return new ScriptContextImpl(content);
    }
  }
}
