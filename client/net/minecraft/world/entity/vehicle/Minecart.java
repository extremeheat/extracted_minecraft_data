package net.minecraft.world.entity.vehicle;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Minecart extends AbstractMinecart {
   public Minecart(EntityType<?> var1, Level var2) {
      super(var1, var2);
   }

   public Minecart(Level var1, double var2, double var4, double var6) {
      super(EntityType.MINECART, var1, var2, var4, var6);
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      if (var1.isSecondaryUseActive()) {
         return InteractionResult.PASS;
      } else if (this.isVehicle()) {
         return InteractionResult.PASS;
      } else if (!this.level().isClientSide) {
         return var1.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   protected Item getDropItem() {
      return Items.MINECART;
   }

   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
      if (var4) {
         if (this.isVehicle()) {
            this.ejectPassengers();
         }

         if (this.getHurtTime() == 0) {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(50.0F);
            this.markHurt();
         }
      }

   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.RIDEABLE;
   }
}
