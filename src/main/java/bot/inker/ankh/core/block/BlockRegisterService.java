package bot.inker.ankh.core.block;

import bot.inker.ankh.core.api.block.AnkhBlock;
import bot.inker.ankh.core.api.block.BlockRegistry;
import bot.inker.ankh.core.common.AbstractRegistry;

import javax.inject.Singleton;

@Singleton
public class BlockRegisterService extends AbstractRegistry<AnkhBlock.Factory> implements BlockRegistry {
}
