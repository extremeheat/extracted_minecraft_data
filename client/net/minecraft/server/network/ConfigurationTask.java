package net.minecraft.server.network;

import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;

public interface ConfigurationTask {
   void start(Consumer<Packet<?>> var1);

   ConfigurationTask.Type type();

   public static record Type(String id) {
      public Type(String id) {
         super();
         this.id = id;
      }

      public String toString() {
         return this.id;
      }
   }
}
