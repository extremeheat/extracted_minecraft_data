package net.minecraft.server.management;

import com.mojang.authlib.GameProfile;
import java.util.Date;

class PlayerProfileCache$ProfileEntry {
   private final GameProfile field_152672_b;
   private final Date field_152673_c;

   private PlayerProfileCache$ProfileEntry(PlayerProfileCache var1, GameProfile var2, Date var3) {
      super();
      this.field_152671_a = var1;
      this.field_152672_b = var2;
      this.field_152673_c = var3;
   }

   public GameProfile func_152668_a() {
      return this.field_152672_b;
   }

   public Date func_152670_b() {
      return this.field_152673_c;
   }
}
