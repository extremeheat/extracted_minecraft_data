package net.minecraft.server.network;

import java.util.function.Consumer;
import net.minecraft.network.protocol.Packet;

public interface ConfigurationTask {
   void start(Consumer<Packet<?>> var1);

   Type type();

   public static record Type(String id) {
      public Type(String var1) {
         super();
         this.id = var1;
      }

      public String toString() {
         return this.id;
      }
   }
}
