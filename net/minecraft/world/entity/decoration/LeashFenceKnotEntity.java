package net.minecraft.world.entity.decoration;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class LeashFenceKnotEntity extends HangingEntity {
   public LeashFenceKnotEntity(EntityType var1, Level var2) {
      super(var1, var2);
   }

   public LeashFenceKnotEntity(Level var1, BlockPos var2) {
      super(EntityType.LEASH_KNOT, var1, var2);
      this.setPos((double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D);
      float var3 = 0.125F;
      float var4 = 0.1875F;
      float var5 = 0.25F;
      this.setBoundingBox(new AABB(this.getX() - 0.1875D, this.getY() - 0.25D + 0.125D, this.getZ() - 0.1875D, this.getX() + 0.1875D, this.getY() + 0.25D + 0.125D, this.getZ() + 0.1875D));
      this.forcedLoading = true;
   }

   public void setPos(double var1, double var3, double var5) {
      super.setPos((double)Mth.floor(var1) + 0.5D, (double)Mth.floor(var3) + 0.5D, (double)Mth.floor(var5) + 0.5D);
   }

   protected void recalculateBoundingBox() {
      this.setPosRaw((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D);
   }

   public void setDirection(Direction var1) {
   }

   public int getWidth() {
      return 9;
   }

   public int getHeight() {
      return 9;
   }

   protected float getEyeHeight(Pose var1, EntityDimensions var2) {
      return -0.0625F;
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      return var1 < 1024.0D;
   }

   public void dropItem(@Nullable Entity var1) {
      this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
   }

   public void readAdditionalSaveData(CompoundTag var1) {
   }

   public boolean interact(Player var1, InteractionHand var2) {
      if (this.level.isClientSide) {
         return true;
      } else {
         boolean var3 = false;
         double var4 = 7.0D;
         List var6 = this.level.getEntitiesOfClass(Mob.class, new AABB(this.getX() - 7.0D, this.getY() - 7.0D, this.getZ() - 7.0D, this.getX() + 7.0D, this.getY() + 7.0D, this.getZ() + 7.0D));
         Iterator var7 = var6.iterator();

         Mob var8;
         while(var7.hasNext()) {
            var8 = (Mob)var7.next();
            if (var8.getLeashHolder() == var1) {
               var8.setLeashedTo(this, true);
               var3 = true;
            }
         }

         if (!var3) {
            this.remove();
            if (var1.abilities.instabuild) {
               var7 = var6.iterator();

               while(var7.hasNext()) {
                  var8 = (Mob)var7.next();
                  if (var8.isLeashed() && var8.getLeashHolder() == this) {
                     var8.dropLeash(true, false);
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean survives() {
      return this.level.getBlockState(this.pos).getBlock().is(BlockTags.FENCES);
   }

   public static LeashFenceKnotEntity getOrCreateKnot(Level var0, BlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      List var5 = var0.getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB((double)var2 - 1.0D, (double)var3 - 1.0D, (double)var4 - 1.0D, (double)var2 + 1.0D, (double)var3 + 1.0D, (double)var4 + 1.0D));
      Iterator var6 = var5.iterator();

      LeashFenceKnotEntity var7;
      do {
         if (!var6.hasNext()) {
            LeashFenceKnotEntity var8 = new LeashFenceKnotEntity(var0, var1);
            var0.addFreshEntity(var8);
            var8.playPlacementSound();
            return var8;
         }

         var7 = (LeashFenceKnotEntity)var6.next();
      } while(!var7.getPos().equals(var1));

      return var7;
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
   }

   public Packet getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.getType(), 0, this.getPos());
   }
}
