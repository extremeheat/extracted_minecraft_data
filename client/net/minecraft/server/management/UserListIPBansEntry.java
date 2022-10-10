package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class UserListIPBansEntry extends UserListEntryBan<String> {
   public UserListIPBansEntry(String var1) {
      this(var1, (Date)null, (String)null, (Date)null, (String)null);
   }

   public UserListIPBansEntry(String var1, @Nullable Date var2, @Nullable String var3, @Nullable Date var4, @Nullable String var5) {
      super(var1, var2, var3, var4, var5);
   }

   public ITextComponent func_199041_e() {
      return new TextComponentString((String)this.func_152640_f());
   }

   public UserListIPBansEntry(JsonObject var1) {
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
