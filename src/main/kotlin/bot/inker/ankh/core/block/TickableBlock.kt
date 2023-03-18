package bot.inker.ankh.core.block

import bot.inker.ankh.core.api.block.AnkhBlock
import bot.inker.ankh.core.common.entity.LocationEmbedded

interface TickableBlock : AnkhBlock {
  fun runTick(location: LocationEmbedded)
}