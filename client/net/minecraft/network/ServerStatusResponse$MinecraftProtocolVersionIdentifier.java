package net.minecraft.network;

public class ServerStatusResponse$MinecraftProtocolVersionIdentifier {
   private final String field_151306_a;
   private final int field_151305_b;

   public ServerStatusResponse$MinecraftProtocolVersionIdentifier(String var1, int var2) {
      super();
      this.field_151306_a = var1;
      this.field_151305_b = var2;
   }

   public String func_151303_a() {
      return this.field_151306_a;
   }

   public int func_151304_b() {
      return this.field_151305_b;
   }
}
