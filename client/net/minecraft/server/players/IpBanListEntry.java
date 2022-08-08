package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public class IpBanListEntry extends BanListEntry<String> {
   public IpBanListEntry(String var1) {
      this(var1, (Date)null, (String)null, (Date)null, (String)null);
   }

   public IpBanListEntry(String var1, @Nullable Date var2, @Nullable String var3, @Nullable Date var4, @Nullable String var5) {
      super(var1, var2, var3, var4, var5);
   }

   public Component getDisplayName() {
      return Component.literal(String.valueOf(this.getUser()));
   }

   public IpBanListEntry(JsonObject var1) {
      super(createIpInfo(var1), var1);
   }

   private static String createIpInfo(JsonObject var0) {
      return var0.has("ip") ? var0.get("ip").getAsString() : null;
   }

   protected void serialize(JsonObject var1) {
      if (this.getUser() != null) {
         var1.addProperty("ip", (String)this.getUser());
         super.serialize(var1);
      }
   }
}
