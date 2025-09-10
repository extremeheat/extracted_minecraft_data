package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public abstract class BanListEntry<T> extends StoredUserEntry<T> {
   public static final SimpleDateFormat DATE_FORMAT;
   public static final String EXPIRES_NEVER = "forever";
   protected final Date created;
   protected final String source;
   @Nullable
   protected final Date expires;
   @Nullable
   protected final String reason;

   public BanListEntry(@Nullable T var1, @Nullable Date var2, @Nullable String var3, @Nullable Date var4, @Nullable String var5) {
      super(var1);
      this.created = var2 == null ? new Date() : var2;
      this.source = var3 == null ? "(Unknown)" : var3;
      this.expires = var4;
      this.reason = var5;
   }

   protected BanListEntry(@Nullable T var1, JsonObject var2) {
      super(var1);

      Date var3;
      try {
         var3 = var2.has("created") ? DATE_FORMAT.parse(var2.get("created").getAsString()) : new Date();
      } catch (ParseException var7) {
         var3 = new Date();
      }

      this.created = var3;
      this.source = var2.has("source") ? var2.get("source").getAsString() : "(Unknown)";

      Date var4;
      try {
         var4 = var2.has("expires") ? DATE_FORMAT.parse(var2.get("expires").getAsString()) : null;
      } catch (ParseException var6) {
         var4 = null;
      }

      this.expires = var4;
      this.reason = var2.has("reason") ? var2.get("reason").getAsString() : null;
   }

   public Date getCreated() {
      return this.created;
   }

   public String getSource() {
      return this.source;
   }

   @Nullable
   public Date getExpires() {
      return this.expires;
   }

   @Nullable
   public String getReason() {
      return this.reason;
   }

   public Component getReasonMessage() {
      String var1 = this.getReason();
      return var1 == null ? Component.translatable("multiplayer.disconnect.banned.reason.default") : Component.literal(var1);
   }

   public abstract Component getDisplayName();

   boolean hasExpired() {
      return this.expires == null ? false : this.expires.before(new Date());
   }

   protected void serialize(JsonObject var1) {
      var1.addProperty("created", DATE_FORMAT.format(this.created));
      var1.addProperty("source", this.source);
      var1.addProperty("expires", this.expires == null ? "forever" : DATE_FORMAT.format(this.expires));
      var1.addProperty("reason", this.reason);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         BanListEntry var2 = (BanListEntry)var1;
         return Objects.equals(this.source, var2.source) && Objects.equals(this.expires, var2.expires) && Objects.equals(this.reason, var2.reason) && Objects.equals(this.getUser(), var2.getUser());
      } else {
         return false;
      }
   }

   static {
      DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
   }
}
