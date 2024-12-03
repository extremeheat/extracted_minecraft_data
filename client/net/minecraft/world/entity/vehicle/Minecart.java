package net.minecraft.world.entity.vehicle;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Minecart extends AbstractMinecart {
   private float rotationOffset;
   private float playerRotationOffset;

   public Minecart(EntityType<?> var1, Level var2) {
      super(var1, var2);
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      if (!var1.isSecondaryUseActive() && !this.isVehicle() && (this.level().isClientSide || var1.startRiding(this))) {
         this.playerRotationOffset = this.rotationOffset;
         if (!this.level().isClientSide) {
            return (InteractionResult)(var1.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS);
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   protected Item getDropItem() {
      return Items.MINECART;
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.MINECART);
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

   public boolean isRideable() {
      return true;
   }

   public void tick() {
      double var1 = (double)this.getYRot();
      Vec3 var3 = this.position();
      super.tick();
      double var4 = ((double)this.getYRot() - var1) % 360.0;
      if (this.level().isClientSide && var3.distanceTo(this.position()) > 0.01) {
         this.rotationOffset += (float)var4;
         this.rotationOffset %= 360.0F;
      }

   }

   protected void positionRider(Entity var1, Entity.MoveFunction var2) {
      super.positionRider(var1, var2);
      if (this.level().isClientSide && var1 instanceof Player var3) {
         if (var3.shouldRotateWithMinecart() && useExperimentalMovement(this.level())) {
            float var4 = (float)Mth.rotLerp(0.5, (double)this.playerRotationOffset, (double)this.rotationOffset);
            var3.setYRot(var3.getYRot() - (var4 - this.playerRotationOffset));
            this.playerRotationOffset = var4;
         }
      }

   }
}
