package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAILookAtTradePlayer extends EntityAIWatchClosest {
   private final EntityVillager field_75335_b;

   public EntityAILookAtTradePlayer(EntityVillager var1) {
      super(var1, EntityPlayer.class, 8.0F);
      this.field_75335_b = var1;
   }

   public boolean func_75250_a() {
      if (this.field_75335_b.func_70940_q()) {
         this.field_75334_a = this.field_75335_b.func_70931_l_();
         return true;
      } else {
         return false;
      }
   }
}
