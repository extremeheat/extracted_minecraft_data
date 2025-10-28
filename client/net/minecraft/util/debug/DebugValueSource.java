package net.minecraft.util.debug;

import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.Nullable;

public interface DebugValueSource {
   void registerDebugValues(ServerLevel var1, Registration var2);

   public interface Registration {
      <T> void register(DebugSubscription<T> var1, ValueGetter<T> var2);
   }

   public interface ValueGetter<T> {
      @Nullable T get();
   }
}
