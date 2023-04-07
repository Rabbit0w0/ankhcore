package org.inksnow.ankh.core.ioc;

import com.google.inject.Injector;
import org.inksnow.ankh.core.api.ioc.AnkhInjector;
import org.inksnow.ankh.core.api.ioc.AnkhIocKey;

import javax.inject.Provider;

public class BridgerInjector implements AnkhInjector {

    private final Injector injector;

    public BridgerInjector(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void injectMembers(Object instance) {
        injector.injectMembers(instance);
    }

    @Override
    public <T> Provider<T> getProvider(AnkhIocKey<T> key) {
        return injector.getProvider(((BridgerKey<T>) key).key);
    }

    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        return injector.getProvider(type);
    }

    @Override
    public <T> T getInstance(AnkhIocKey<T> key) {
        return injector.getInstance(((BridgerKey<T>) key).key);
    }

    @Override
    public <T> T getInstance(Class<T> type) {
        return injector.getInstance(type);
    }

    @Override
    public AnkhInjector getParent() {
        return new BridgerInjector(injector.getParent());
    }
}
