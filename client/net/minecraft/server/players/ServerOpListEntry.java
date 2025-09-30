package net.minecraft.server.players;

import com.google.gson.JsonObject;

public class ServerOpListEntry extends StoredUserEntry<NameAndId> {
   private final int level;
   private final boolean bypassesPlayerLimit;

   public ServerOpListEntry(NameAndId var1, int var2, boolean var3) {
      super(var1);
      this.level = var2;
      this.bypassesPlayerLimit = var3;
   }

   public ServerOpListEntry(JsonObject var1) {
      super(NameAndId.fromJson(var1));
      this.level = var1.has("level") ? var1.get("level").getAsInt() : 0;
      this.bypassesPlayerLimit = var1.has("bypassesPlayerLimit") && var1.get("bypassesPlayerLimit").getAsBoolean();
   }

   public int getLevel() {
      return this.level;
   }

   public boolean getBypassesPlayerLimit() {
      return this.bypassesPlayerLimit;
   }

   protected void serialize(JsonObject var1) {
      if (this.getUser() != null) {
         ((NameAndId)this.getUser()).appendTo(var1);
         var1.addProperty("level", this.level);
         var1.addProperty("bypassesPlayerLimit", this.bypassesPlayerLimit);
      }
   }
}
