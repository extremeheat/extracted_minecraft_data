package net.minecraft.util;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketCooldown;

public class CooldownTrackerServer extends CooldownTracker {
   private final EntityPlayerMP field_185149_a;

   public CooldownTrackerServer(EntityPlayerMP var1) {
      super();
      this.field_185149_a = var1;
   }

   protected void func_185140_b(Item var1, int var2) {
      super.func_185140_b(var1, var2);
      this.field_185149_a.field_71135_a.func_147359_a(new SPacketCooldown(var1, var2));
   }

   protected void func_185146_c(Item var1) {
      super.func_185146_c(var1);
      this.field_185149_a.field_71135_a.func_147359_a(new SPacketCooldown(var1, 0));
   }
}
