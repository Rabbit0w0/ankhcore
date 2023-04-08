package org.inksnow.ankh.core.item;

import lombok.extern.slf4j.Slf4j;
import org.inksnow.ankh.core.api.item.AnkhItem;
import org.inksnow.ankh.core.api.item.AnkhItemRegistry;
import org.inksnow.ankh.core.common.AbstractRegistry;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@Slf4j
public class ItemRegisterService extends AbstractRegistry<AnkhItem> implements AnkhItemRegistry {
  @Inject
  private ItemRegisterService() {
    //
  }
}
