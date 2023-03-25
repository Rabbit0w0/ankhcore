package org.inksnow.ankh.core.block;

import org.inksnow.ankh.core.api.block.AnkhBlock;
import org.inksnow.ankh.core.api.block.BlockRegistry;
import org.inksnow.ankh.core.common.AbstractRegistry;

import javax.inject.Singleton;

@Singleton
public class BlockRegisterService extends AbstractRegistry<AnkhBlock.Factory> implements BlockRegistry {
}
