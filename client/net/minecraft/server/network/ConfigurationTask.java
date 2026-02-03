package net.minecraft.server.network;

import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;

public interface ConfigurationTask {
   void start(Consumer<Packet<?>> connection);

   default boolean tick() {
      return false;
   }

   Type type();

   public static record Type(String id) {
      public Type {
         super();
      }

      public String toString() {
         return this.id;
      }
   }
}
