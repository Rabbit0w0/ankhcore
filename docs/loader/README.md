## AnkhCore loader

AnkhCore 中创建两个 ClassLoader，分别为 `AnkhLoggerLoader` `AnkhClassLoader`。在同一个实例中有且只有一个 `AnkhLoggerLoader`
和多个 `AnkhClassLoader`。

所有 `AnkhClassLoader` 中，`org.slf4j.` 包由 `AnkhLoggerLoader` 加载，使得日志名带上 `ankh` 标记。