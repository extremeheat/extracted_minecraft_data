package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.util.Date;

public class IPBanEntry extends BanEntry<String> {
   public IPBanEntry(String var1) {
      this(var1, (Date)null, (String)null, (Date)null, (String)null);
   }

   public IPBanEntry(String var1, Date var2, String var3, Date var4, String var5) {
      super(var1, var2, var3, var4, var5);
   }

   public IPBanEntry(JsonObject var1) {
      super(func_152647_b(var1), var1);
   }

   private static String func_152647_b(JsonObject var0) {
      return var0.has("ip") ? var0.get("ip").getAsString() : null;
   }

   protected void func_152641_a(JsonObject var1) {
      if (this.func_152640_f() != null) {
         var1.addProperty("ip", (String)this.func_152640_f());
         super.func_152641_a(var1);
      }
   }
}
