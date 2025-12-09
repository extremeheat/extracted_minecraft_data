package net.minecraft.server.players;

import com.google.gson.JsonObject;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionLevel;

public class ServerOpListEntry extends StoredUserEntry<NameAndId> {
   private final LevelBasedPermissionSet permissions;
   private final boolean bypassesPlayerLimit;

   public ServerOpListEntry(NameAndId var1, LevelBasedPermissionSet var2, boolean var3) {
      super(var1);
      this.permissions = var2;
      this.bypassesPlayerLimit = var3;
   }

   public ServerOpListEntry(JsonObject var1) {
      super(NameAndId.fromJson(var1));
      PermissionLevel var2 = var1.has("level") ? PermissionLevel.byId(var1.get("level").getAsInt()) : PermissionLevel.ALL;
      this.permissions = LevelBasedPermissionSet.forLevel(var2);
      this.bypassesPlayerLimit = var1.has("bypassesPlayerLimit") && var1.get("bypassesPlayerLimit").getAsBoolean();
   }

   public LevelBasedPermissionSet permissions() {
      return this.permissions;
   }

   public boolean getBypassesPlayerLimit() {
      return this.bypassesPlayerLimit;
   }

   protected void serialize(JsonObject var1) {
      if (this.getUser() != null) {
         ((NameAndId)this.getUser()).appendTo(var1);
         var1.addProperty("level", this.permissions.level().id());
         var1.addProperty("bypassesPlayerLimit", this.bypassesPlayerLimit);
      }
   }
}
