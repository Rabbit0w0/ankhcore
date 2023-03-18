package bot.inker.ankh.core.block

import bot.inker.ankh.core.api.block.AnkhBlock
import bot.inker.ankh.core.api.block.BlockRegistry
import bot.inker.ankh.core.common.AbstractRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockRegisterService @Inject private constructor(

): AbstractRegistry<AnkhBlock.Factory>(), BlockRegistry {

}