package bot.inker.ankh.core.hologram.hds

import bot.inker.ankh.core.api.hologram.HologramContent
import me.filoghost.holographicdisplays.api.hologram.HologramLines
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine
import org.bukkit.inventory.ItemStack
import java.util.*

class HdsHologramContent(
  lines: List<LineEntry>,
) : HologramContent {
  private val lines = Collections.unmodifiableList(ArrayList(lines))

  fun applyToLines(hdsLines: HologramLines) {
    // remove more entries
    while (hdsLines.size() > lines.size) {
      hdsLines.remove(hdsLines.size() - 1)
    }
    /*for (i in 0 until lines.size) {
      val lineEntry = lines[i]
      if (lineEntry is Text) {
        if(hdsLines.size() <= i) { // append new entry
          hdsLines.appendText(lineEntry.content)
        } else if(hdsLines[i] is TextHologramLine){ // hds line with same type, just update
          (hdsLines[i] as TextHologramLine).text = lineEntry.content
        } else { // hds line with different type, insert new one and remove it
          hdsLines.insertText(i, lineEntry.content)
          hdsLines.remove(i)
        }
      }else if(lineEntry is Item) {
        if(hdsLines.size() <= i){ // append new entry
          hdsLines.appendItem(lineEntry.item)
        }else if (hdsLines[i] is ItemHologramLine) { // hds line with same type, just update
          (hdsLines[i] as ItemHologramLine).itemStack = lineEntry.item
        }else{ // hds line with different type, insert new one and remove it
          hdsLines.insertItem(i, lineEntry.item)
          hdsLines.remove(i)
        }
      }
    }*/
    // refactor the code above with sequence api for readability
    // aims to avoid if and else
    lines.forEachIndexed { index, lineEntry ->
      if (hdsLines.size() <= index) { // append new entry
        when (lineEntry) {
          is Text -> hdsLines.appendText(lineEntry.content)
          is Item -> hdsLines.appendItem(lineEntry.item)
        }
      } else { // hdsLines.size() > index
        val hdsLine = hdsLines[index]
        when {
          lineEntry is Text && hdsLine is TextHologramLine -> hdsLine.text = lineEntry.content
          lineEntry is Item && hdsLine is ItemHologramLine -> hdsLine.itemStack = lineEntry.item
          else -> {
            when (lineEntry) {
              is Text -> hdsLines.insertText(index, lineEntry.content)
              is Item -> hdsLines.insertItem(index, lineEntry.item)
            }
            hdsLines.remove(index)
          }
        }
      }
    }
  }

  class Builder : HologramContent.Builder {
    override fun getThis() = this
    private val lines = ArrayList<LineEntry>()

    override fun build() = HdsHologramContent(lines)

    override fun appendContent(content: String) = apply {
      lines.add(Text(content))
    }

    override fun appendItem(item: ItemStack) = apply {
      lines.add(Item(item))
    }
  }

  sealed interface LineEntry
  class Text(val content: String) : LineEntry
  class Item(val item: ItemStack) : LineEntry
}