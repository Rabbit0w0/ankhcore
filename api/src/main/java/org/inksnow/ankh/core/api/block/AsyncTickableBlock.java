package org.inksnow.ankh.core.api.block;

public interface AsyncTickableBlock extends AnkhBlock {
  void runAsyncTick();
}
