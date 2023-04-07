package org.inksnow.ankh.core.ioc;

import com.google.inject.Key;
import com.google.inject.Singleton;
import org.inksnow.ankh.core.api.ioc.AnkhIocKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class BridgerKey<T> implements AnkhIocKey<T> {

    final Key<T> key;

    public BridgerKey(Key<T> key) {
        this.key = key;
    }

    @Singleton
    static class Factory implements AnkhIocKey.Factory {
        @Override
        public <T> AnkhIocKey<T> get(Class<T> type) {
            return new BridgerKey<>(Key.get(type));
        }

        @Override
        public <T> AnkhIocKey<T> get(Class<T> type, Class<? extends Annotation> annotationType) {
            return new BridgerKey<>(Key.get(type, annotationType));
        }

        @Override
        public <T> AnkhIocKey<T> get(Class<T> type, Annotation annotation) {
            return new BridgerKey<>(Key.get(type, annotation));
        }

        @Override
        public AnkhIocKey<?> get(Type type) {
            return new BridgerKey<>(Key.get(type));
        }

        @Override
        public AnkhIocKey<?> get(Type type, Class<? extends Annotation> annotationType) {
            return new BridgerKey<>(Key.get(type, annotationType));
        }

        @Override
        public AnkhIocKey<?> get(Type type, Annotation annotation) {
            return new BridgerKey<>(Key.get(type, annotation));
        }
    }

}
