package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.util.Date;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class UserBanListEntry extends BanListEntry<NameAndId> {
   private static final Component MESSAGE_UNKNOWN_USER = Component.translatable("commands.banlist.entry.unknown");

   public UserBanListEntry(@Nullable NameAndId var1) {
      this(var1, (Date)null, (String)null, (Date)null, (String)null);
   }

   public UserBanListEntry(@Nullable NameAndId var1, @Nullable Date var2, @Nullable String var3, @Nullable Date var4, @Nullable String var5) {
      super(var1, var2, var3, var4, var5);
   }

   public UserBanListEntry(JsonObject var1) {
      super(NameAndId.fromJson(var1), var1);
   }

   protected void serialize(JsonObject var1) {
      if (this.getUser() != null) {
         ((NameAndId)this.getUser()).appendTo(var1);
         super.serialize(var1);
      }
   }

   public Component getDisplayName() {
      NameAndId var1 = (NameAndId)this.getUser();
      return (Component)(var1 != null ? Component.literal(var1.name()) : MESSAGE_UNKNOWN_USER);
   }
}
