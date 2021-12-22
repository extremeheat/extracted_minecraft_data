package net.minecraft.util.profiling.jfr;

import net.minecraft.server.MinecraftServer;

public enum Environment {
   CLIENT("client"),
   SERVER("server");

   private final String description;

   private Environment(String var3) {
      this.description = var3;
   }

   public static Environment from(MinecraftServer var0) {
      return var0.isDedicatedServer() ? SERVER : CLIENT;
   }

   public String getDescription() {
      return this.description;
   }

   // $FF: synthetic method
   private static Environment[] $values() {
      return new Environment[]{CLIENT, SERVER};
   }
}
