package org.inksnow.ankh.core.hologram.hds;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.inventory.ItemStack;
import org.inksnow.ankh.core.api.hologram.HologramContent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HdsHologramContent implements HologramContent {

    public interface LineEntry {}

    @AllArgsConstructor
    @NoArgsConstructor
    private static class Text implements LineEntry {
        public String content;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private static class Item implements LineEntry {
        public ItemStack item;
    }

    private final List<LineEntry> lines;

    public HdsHologramContent(List<LineEntry> lines) {
        this.lines = Collections.unmodifiableList(lines);
    }

    public void applyToLines(HologramLines hdsLines) {
        while (hdsLines.size() > lines.size()) {
            hdsLines.remove(hdsLines.size() - 1);
        }

        int index = 0;
        for (LineEntry lineEntry : lines) {
            if (lineEntry instanceof Text) {
                if (hdsLines.size() <= 1) {
                    hdsLines.appendText(((Text) lineEntry).content);
                } else if (hdsLines.get(index) instanceof TextHologramLine) {
                    ((TextHologramLine) hdsLines.get(index)).setText(((Text) lineEntry).content);
                } else {
                    hdsLines.insertText(index, ((Text) lineEntry).content);
                    hdsLines.remove(index);
                }
            } else if (lineEntry instanceof Item) {
                if (hdsLines.size() <= 1) {
                    hdsLines.appendItem(((Item) lineEntry).item);
                } else if (hdsLines.get(index) instanceof ItemHologramLine) {
                    ((Item) hdsLines.get(index)).item = ((Item) lineEntry).item;
                } else {
                    hdsLines.insertItem(index, ((Item) lineEntry).item);
                    hdsLines.remove(index);
                }
            }
            index++;
        }
    }

    public static class Builder implements HologramContent.Builder {

        private final ArrayList<LineEntry> lines = new ArrayList<>();

        @Override
        public HologramContent.Builder appendContent(String content) {
            lines.add(new Text(content));
            return this;
        }

        @Override
        public HologramContent.Builder appendItem(ItemStack item) {
            lines.add(new Item(item));
            return this;
        }

        @NotNull
        @Override
        public HologramContent.Builder getThis() {
            return this;
        }

        @NotNull
        @Override
        public HologramContent build() {
            return new HdsHologramContent(lines);
        }
    }
}
