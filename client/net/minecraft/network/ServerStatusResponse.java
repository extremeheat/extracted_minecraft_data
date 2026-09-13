package net.minecraft.network;

import net.minecraft.util.IChatComponent;

public class ServerStatusResponse {
   private IChatComponent field_151326_a;
   private ServerStatusResponse$PlayerCountData field_151324_b;
   private ServerStatusResponse$MinecraftProtocolVersionIdentifier field_151325_c;
   private String field_151323_d;

   public ServerStatusResponse() {
      super();
   }

   public IChatComponent func_151317_a() {
      return this.field_151326_a;
   }

   public void func_151315_a(IChatComponent var1) {
      this.field_151326_a = var1;
   }

   public ServerStatusResponse$PlayerCountData func_151318_b() {
      return this.field_151324_b;
   }

   public void func_151319_a(ServerStatusResponse$PlayerCountData var1) {
      this.field_151324_b = var1;
   }

   public ServerStatusResponse$MinecraftProtocolVersionIdentifier func_151322_c() {
      return this.field_151325_c;
   }

   public void func_151321_a(ServerStatusResponse$MinecraftProtocolVersionIdentifier var1) {
      this.field_151325_c = var1;
   }

   public void func_151320_a(String var1) {
      this.field_151323_d = var1;
   }

   public String func_151316_d() {
      return this.field_151323_d;
   }
}
