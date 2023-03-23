## AnkhCore loader

`AnkhCore` 中创建两个 `ClassLoader`，分别为 `AnkhLoggerLoader` `AnkhClassLoader`
。在同一个实例中有且只有一个 `AnkhLoggerLoader`
和多个 `AnkhClassLoader`。`AnkhClassLoader` 中传递 `Bukkit` 依赖管理信息，会将所有的依赖 `ClassLoader` 作为 parent
ClassLoader。

`AnkhCore` 会扫描插件 jar，在 jar `ankh-api` 中的 jar 作为 api 加载，`ankh-impl` 中的 jar 作为实现加载。`ankh-self-as-api`
为配置
插件 jar 本身中的类是否作为 api 类（暴露给其他插件的类），默认为实现类。`ankh-api` 中的类可以被其他插件直接访问到，但不可以访问
impl 中但类。
impl 中的类可以访问依赖的类及 api 类，但不可被其他插件访问到。

`AnkhClassLoader` 可配置选择类，通过在 `plugin.yml` 中配置 `ankh-classpath`，可选的值为 `PARENT_ONLY`，`SELF_ONLY`，
`PARENT_FIRST`，`SELF_FIRST`。`PARENT_ONLY` 为仅在父类加载器加载，`SELF_ONLY` 为仅在自己的类加载器加载，`PARENT_FIRST`
为先在父类加
载器加载，`SELF_FIRST` 为先在自己的加载器加载。

以下为 `AnkhCore` 的 `plugin.yml` 中的片段

```yml
ankh-self-as-api: true
ankh-classpath:
  "kotlin.": SELF_ONLY
  "org.apache.logging.": SELF_ONLY
  "io.github.bakedlibs.": SELF_ONLY
  "com.google.inject.": SELF_ONLY
  "org.hibernate.": SELF_ONLY
  "it.unimi.dsi.fastutil.": SELF_ONLY
```

所有 `AnkhClassLoader` 中，`org.slf4j.` 包由 `AnkhLoggerLoader` 加载，使得日志名带上 `ankh` 标记。
