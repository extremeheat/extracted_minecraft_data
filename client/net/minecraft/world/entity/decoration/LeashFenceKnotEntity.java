package net.minecraft.world.entity.decoration;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LeashFenceKnotEntity extends HangingEntity {
   public static final double OFFSET_Y = 0.375;

   public LeashFenceKnotEntity(EntityType<? extends LeashFenceKnotEntity> var1, Level var2) {
      super(var1, var2);
   }

   public LeashFenceKnotEntity(Level var1, BlockPos var2) {
      super(EntityType.LEASH_KNOT, var1, var2);
      this.setPos((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
   }

   @Override
   protected void recalculateBoundingBox() {
      this.setPosRaw((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.375, (double)this.pos.getZ() + 0.5);
      double var1 = (double)this.getType().getWidth() / 2.0;
      double var3 = (double)this.getType().getHeight();
      this.setBoundingBox(new AABB(this.getX() - var1, this.getY(), this.getZ() - var1, this.getX() + var1, this.getY() + var3, this.getZ() + var1));
   }

   @Override
   public void setDirection(Direction var1) {
   }

   @Override
   public int getWidth() {
      return 9;
   }

   @Override
   public int getHeight() {
      return 9;
   }

   @Override
   public boolean shouldRenderAtSqrDistance(double var1) {
      return var1 < 1024.0;
   }

   @Override
   public void dropItem(@Nullable Entity var1) {
      this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
   }

   @Override
   public InteractionResult interact(Player var1, InteractionHand var2) {
      if (this.level().isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         boolean var3 = false;
         double var4 = 7.0;
         List var6 = this.level()
            .getEntitiesOfClass(
               Mob.class, new AABB(this.getX() - 7.0, this.getY() - 7.0, this.getZ() - 7.0, this.getX() + 7.0, this.getY() + 7.0, this.getZ() + 7.0)
            );

         for (Mob var8 : var6) {
            if (var8.getLeashHolder() == var1) {
               var8.setLeashedTo(this, true);
               var3 = true;
            }
         }

         boolean var10 = false;
         if (!var3) {
            this.discard();
            if (var1.getAbilities().instabuild) {
               for (Mob var9 : var6) {
                  if (var9.isLeashed() && var9.getLeashHolder() == this) {
                     var9.dropLeash(true, false);
                     var10 = true;
                  }
               }
            }
         }

         if (var3 || var10) {
            this.gameEvent(GameEvent.BLOCK_ATTACH, var1);
         }

         return InteractionResult.CONSUME;
      }
   }

   @Override
   public boolean survives() {
      return this.level().getBlockState(this.pos).is(BlockTags.FENCES);
   }

   public static LeashFenceKnotEntity getOrCreateKnot(Level var0, BlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();

      for (LeashFenceKnotEntity var7 : var0.getEntitiesOfClass(
         LeashFenceKnotEntity.class,
         new AABB((double)var2 - 1.0, (double)var3 - 1.0, (double)var4 - 1.0, (double)var2 + 1.0, (double)var3 + 1.0, (double)var4 + 1.0)
      )) {
         if (var7.getPos().equals(var1)) {
            return var7;
         }
      }

      LeashFenceKnotEntity var8 = new LeashFenceKnotEntity(var0, var1);
      var0.addFreshEntity(var8);
      return var8;
   }

   @Override
   public void playPlacementSound() {
      this.playSound(SoundEvents.LEASH_KNOT_PLACE, 1.0F, 1.0F);
   }

   @Override
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this, 0, this.getPos());
   }

   @Override
   public Vec3 getRopeHoldPosition(float var1) {
      return this.getPosition(var1).add(0.0, 0.2, 0.0);
   }

   @Override
   public ItemStack getPickResult() {
      return new ItemStack(Items.LEAD);
   }
}
