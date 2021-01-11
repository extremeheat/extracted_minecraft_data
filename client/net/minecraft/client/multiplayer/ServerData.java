package net.minecraft.client.multiplayer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class ServerData {
   public String field_78847_a;
   public String field_78845_b;
   public String field_78846_c;
   public String field_78843_d;
   public long field_78844_e;
   public int field_82821_f = 47;
   public String field_82822_g = "1.8.9";
   public boolean field_78841_f;
   public String field_147412_i;
   private ServerData.ServerResourceMode field_152587_j;
   private String field_147411_m;
   private boolean field_181042_l;

   public ServerData(String var1, String var2, boolean var3) {
      super();
      this.field_152587_j = ServerData.ServerResourceMode.PROMPT;
      this.field_78847_a = var1;
      this.field_78845_b = var2;
      this.field_181042_l = var3;
   }

   public NBTTagCompound func_78836_a() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74778_a("name", this.field_78847_a);
      var1.func_74778_a("ip", this.field_78845_b);
      if (this.field_147411_m != null) {
         var1.func_74778_a("icon", this.field_147411_m);
      }

      if (this.field_152587_j == ServerData.ServerResourceMode.ENABLED) {
         var1.func_74757_a("acceptTextures", true);
      } else if (this.field_152587_j == ServerData.ServerResourceMode.DISABLED) {
         var1.func_74757_a("acceptTextures", false);
      }

      return var1;
   }

   public ServerData.ServerResourceMode func_152586_b() {
      return this.field_152587_j;
   }

   public void func_152584_a(ServerData.ServerResourceMode var1) {
      this.field_152587_j = var1;
   }

   public static ServerData func_78837_a(NBTTagCompound var0) {
      ServerData var1 = new ServerData(var0.func_74779_i("name"), var0.func_74779_i("ip"), false);
      if (var0.func_150297_b("icon", 8)) {
         var1.func_147407_a(var0.func_74779_i("icon"));
      }

      if (var0.func_150297_b("acceptTextures", 1)) {
         if (var0.func_74767_n("acceptTextures")) {
            var1.func_152584_a(ServerData.ServerResourceMode.ENABLED);
         } else {
            var1.func_152584_a(ServerData.ServerResourceMode.DISABLED);
         }
      } else {
         var1.func_152584_a(ServerData.ServerResourceMode.PROMPT);
      }

      return var1;
   }

   public String func_147409_e() {
      return this.field_147411_m;
   }

   public void func_147407_a(String var1) {
      this.field_147411_m = var1;
   }

   public boolean func_181041_d() {
      return this.field_181042_l;
   }

   public void func_152583_a(ServerData var1) {
      this.field_78845_b = var1.field_78845_b;
      this.field_78847_a = var1.field_78847_a;
      this.func_152584_a(var1.func_152586_b());
      this.field_147411_m = var1.field_147411_m;
      this.field_181042_l = var1.field_181042_l;
   }

   public static enum ServerResourceMode {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final IChatComponent field_152594_d;

      private ServerResourceMode(String var3) {
         this.field_152594_d = new ChatComponentTranslation("addServer.resourcePack." + var3, new Object[0]);
      }

      public IChatComponent func_152589_a() {
         return this.field_152594_d;
      }
   }
}
