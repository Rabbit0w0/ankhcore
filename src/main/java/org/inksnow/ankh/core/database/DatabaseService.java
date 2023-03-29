package org.inksnow.ankh.core.database;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bukkit.event.EventPriority;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
import org.hibernate.boot.registry.classloading.internal.TcclLookupPrecedence;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.inksnow.ankh.core.api.plugin.PluginLifeCycle;
import org.inksnow.ankh.core.api.plugin.annotations.SubscriptLifecycle;
import org.inksnow.ankh.core.common.config.AnkhConfig;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class DatabaseService {
  private final AnkhConfig config;
  private final Set<Class<?>> entityClasses = new LinkedHashSet<>();
  @Getter
  private SessionFactory sessionFactory;

  @Inject
  private DatabaseService(AnkhConfig config) {
    this.config = config;
  }

  public DatabaseService registerEntity(Class<?> entityClass) {
    entityClasses.add(entityClass);
    return this;
  }

  @SubscriptLifecycle(PluginLifeCycle.ENABLE)
  private void onEnable() {
    val database = config.database();
    val configuration = new Configuration();
    configuration.setProperty(
      AvailableSettings.CONNECTION_PROVIDER,
      HikariCPConnectionProvider.class.getName()
    );
    configuration.setProperty(AvailableSettings.DRIVER, database.driver().driverClass());
    configuration.setProperty(AvailableSettings.DIALECT, database.driver().dialectClass());
    configuration.setProperty(AvailableSettings.URL, database.url());
    configuration.setProperty(AvailableSettings.USER, database.username());
    configuration.setProperty(AvailableSettings.PASS, database.password());
    configuration.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");
    configuration.setProperty(AvailableSettings.AUTO_CLOSE_SESSION, "true");

    entityClasses.forEach(configuration::addAnnotatedClass);

    val serviceRegistry = new StandardServiceRegistryBuilder()
      .applySettings(configuration.getProperties())
      .addService(
        ClassLoaderService.class,
        new ClassLoaderServiceImpl(
          entityClasses.stream()
            .map(Class::getClassLoader)
            .collect(Collectors.toSet()),
          TcclLookupPrecedence.NEVER
        )
      )
      .build();
    sessionFactory = configuration.buildSessionFactory(serviceRegistry);
  }

  @SubscriptLifecycle(
    value = PluginLifeCycle.DISABLE,
    priority = EventPriority.MONITOR
  )
  private void onDisable() {
    sessionFactory.close();
  }
}
