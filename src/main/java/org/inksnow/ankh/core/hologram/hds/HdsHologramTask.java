package org.inksnow.ankh.core.hologram.hds;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.hologram.HologramContent;
import org.inksnow.ankh.core.api.hologram.HologramTask;
import org.inksnow.ankh.core.common.util.CheckUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Objects;

public class HdsHologramTask implements HologramTask {

    private final Hologram hologram;

    public HdsHologramTask(Hologram hologram) {
        this.hologram = hologram;
    }

    @Override
    public void updateContent(@NotNull HologramContent content) {
        CheckUtil.ensureMainThread();
        ((HdsHologramContent) content).applyToLines(hologram.getLines());
    }

    @Override
    public void delete() {
        CheckUtil.ensureMainThread();
        hologram.delete();
    }

    public static class Builder implements HologramTask.Builder {

        private final HolographicDisplaysAPI hdApi;
        private Location location;
        private HdsHologramContent content;

        public Builder(HolographicDisplaysAPI hdApi) {
            this.hdApi = hdApi;
        }

        @NotNull
        @Override
        public InnerContentBuilder content() {
            return new InnerContentBuilder(this);
        }

        @NotNull
        @Override
        public HologramTask.Builder content(@NotNull HologramContent content) {
            this.content = (HdsHologramContent) content;
            return this;
        }

        @NotNull
        @Override
        public HologramTask.Builder location(@NotNull Location location) {
            this.location = location;
            return this;
        }

        @NotNull
        @Override
        public HologramTask.Builder getThis() {
            return this;
        }

        @NotNull
        @Override
        public HologramTask build() {
            CheckUtil.ensureMainThread();
            HdsHologramContent c = this.content != null ? this.content : new HdsHologramContent(Collections.emptyList());
            Hologram h = hdApi.createHologram(Objects.requireNonNull(location));
            c.applyToLines(h.getLines());
            return new HdsHologramTask(h);
        }
    }

    public static class InnerContentBuilder implements HologramTask.InnerContentBuilder {

        private final Builder parent;
        private final HdsHologramContent.Builder delegateBuilder = new HdsHologramContent.Builder();

        public InnerContentBuilder(Builder parent) {
            this.parent = parent;
        }

        @NotNull
        @Override
        public InnerContentBuilder appendContent(@NotNull String content) {
            delegateBuilder.appendContent(content);
            return this;
        }

        @NotNull
        @Override
        public InnerContentBuilder appendItem(@NotNull ItemStack item) {
            delegateBuilder.appendItem(item);
            return this;
        }

        @NotNull
        @Override
        public HologramTask.InnerContentBuilder getThis() {
            return this;
        }

        @NotNull
        @Override
        public Builder build() {
            parent.content(delegateBuilder.build());
            return parent;
        }
    }

}
