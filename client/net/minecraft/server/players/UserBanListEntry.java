package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.util.Date;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class UserBanListEntry extends BanListEntry<NameAndId> {
   private static final Component MESSAGE_UNKNOWN_USER = Component.translatable("commands.banlist.entry.unknown");

   public UserBanListEntry(final @Nullable NameAndId user) {
      this(user, (Date)null, (String)null, (Date)null, (String)null);
   }

   public UserBanListEntry(final @Nullable NameAndId user, final @Nullable Date created, final @Nullable String source, final @Nullable Date expires, final @Nullable String reason) {
      super(user, created, source, expires, reason);
   }

   public UserBanListEntry(final JsonObject object) {
      super(NameAndId.fromJson(object), object);
   }

   protected void serialize(final JsonObject object) {
      if (this.getUser() != null) {
         ((NameAndId)this.getUser()).appendTo(object);
         super.serialize(object);
      }
   }

   public Component getDisplayName() {
      NameAndId user = (NameAndId)this.getUser();
      return (Component)(user != null ? Component.literal(user.name()) : MESSAGE_UNKNOWN_USER);
   }
}
