package net.minecraft.util.debug;

import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;

public interface DebugValueSource {
   void registerDebugValues(ServerLevel level, Registration registration);

   public interface Registration {
      <T> void register(DebugSubscription<T> subscription, ValueGetter<T> getter);
   }

   public interface ValueGetter<T> {
      @Nullable T get();
   }
}
