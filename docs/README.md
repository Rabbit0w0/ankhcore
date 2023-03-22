# AnkhCore 开发文档

在 Minecraft Bukkit 中实现 mod 的相关功能

## 拓展加载系统

创建插件实现类，并在 `plugin.yml` 中设置
```java
package bot.inker.ankh.testplugin;

import bot.inker.ankh.core.api.plugin.AnkhBukkitPlugin;
import bot.inker.ankh.core.api.plugin.AnkhPluginContainer;

public class TestBukkitPluginLoader extends AnkhBukkitPlugin {
  private static final AnkhPluginContainer container = AnkhBukkitPlugin.initial(TestBukkitPluginLoader.class);

  @Override
  protected AnkhPluginContainer getContainer() {
    return container;
  }
}
```

## 日志系统

正常情况下不需要注意实现，请使用 `slf4j` 作为日志 api，并不要暴露 `Logger` 实例。

相关实现：

使用 `slf4j` 作为 api，同时适配 `log4j2` api。在插件中使用 `LoggerFactory.getLogger("xxx")` 获取日志实例。`slf4j` 位于独立的
`ClassLoader`，破坏原有的委派模型，即不使用父类加载器的 `slf4j`。在独立的 `ClassLoader` 中有 `log4j2` `jul` `simple-stdout`
的
`bridge`， 将会根据环境确定所使用的 `bridge`。

TODO: 为每个 Ankh 插件使用独立的 `LoggerFactory` 实现，以便根据 `ClassLoader` 提供日志名。（目前全部使用 `ankh:xxx` 作为日志名）