package org.inksnow.ankh.core.api.script;

import org.inksnow.ankh.core.api.ioc.DcLazy;
import org.inksnow.ankh.core.api.ioc.IocLazy;

import javax.annotation.Nonnull;

public interface AnkhScriptService {

  static @Nonnull AnkhScriptService instance(){
    return $internal$actions$.INSTANCE.get();
  }

  class $internal$actions$ {
    private static final DcLazy<AnkhScriptService> INSTANCE = IocLazy.of(AnkhScriptService.class);
  }
}
