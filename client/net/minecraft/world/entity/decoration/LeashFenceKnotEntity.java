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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LeashFenceKnotEntity extends HangingEntity {
   public static final double OFFSET_Y = 0.375D;

   public LeashFenceKnotEntity(EntityType<? extends LeashFenceKnotEntity> var1, Level var2) {
      super(var1, var2);
   }

   public LeashFenceKnotEntity(Level var1, BlockPos var2) {
      super(EntityType.LEASH_KNOT, var1, var2);
      this.setPos((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
   }

   protected void recalculateBoundingBox() {
      this.setPosRaw((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.375D, (double)this.pos.getZ() + 0.5D);
      double var1 = (double)this.getType().getWidth() / 2.0D;
      double var3 = (double)this.getType().getHeight();
      this.setBoundingBox(new AABB(this.getX() - var1, this.getY(), this.getZ() - var1, this.getX() + var1, this.getY() + var3, this.getZ() + var1));
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
      return 0.0625F;
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

   public InteractionResult interact(Player var1, InteractionHand var2) {
      if (this.level.isClientSide) {
         return InteractionResult.SUCCESS;
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
            this.discard();
            if (var1.getAbilities().instabuild) {
               var7 = var6.iterator();

               while(var7.hasNext()) {
                  var8 = (Mob)var7.next();
                  if (var8.isLeashed() && var8.getLeashHolder() == this) {
                     var8.dropLeash(true, false);
                  }
               }
            }
         }

         return InteractionResult.CONSUME;
      }
   }

   public boolean survives() {
      return this.level.getBlockState(this.pos).is(BlockTags.FENCES);
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
            return var8;
         }

         var7 = (LeashFenceKnotEntity)var6.next();
      } while(!var7.getPos().equals(var1));

      return var7;
   }

   public void playPlacementSound() {
      this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, this.getType(), 0, this.getPos());
   }

   public Vec3 getRopeHoldPosition(float var1) {
      return this.getPosition(var1).add(0.0D, 0.2D, 0.0D);
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.LEAD);
   }
}
