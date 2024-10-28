package net.minecraft.world.entity.decoration;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LeashFenceKnotEntity extends BlockAttachedEntity {
   public static final double OFFSET_Y = 0.375;

   public LeashFenceKnotEntity(EntityType<? extends LeashFenceKnotEntity> var1, Level var2) {
      super(var1, var2);
   }

   public LeashFenceKnotEntity(Level var1, BlockPos var2) {
      super(EntityType.LEASH_KNOT, var1, var2);
      this.setPos((double)var2.getX(), (double)var2.getY(), (double)var2.getZ());
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
   }

   protected void recalculateBoundingBox() {
      this.setPosRaw((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.375, (double)this.pos.getZ() + 0.5);
      double var1 = (double)this.getType().getWidth() / 2.0;
      double var3 = (double)this.getType().getHeight();
      this.setBoundingBox(new AABB(this.getX() - var1, this.getY(), this.getZ() - var1, this.getX() + var1, this.getY() + var3, this.getZ() + var1));
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      return var1 < 1024.0;
   }

   public void dropItem(@Nullable Entity var1) {
      this.playSound(SoundEvents.LEASH_KNOT_BREAK, 1.0F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
   }

   public void readAdditionalSaveData(CompoundTag var1) {
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      if (this.level().isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         boolean var3 = false;
         List var4 = LeadItem.leashableInArea(this.level(), this.getPos(), (var2x) -> {
            Entity var3 = var2x.getLeashHolder();
            return var3 == var1 || var3 == this;
         });
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Leashable var6 = (Leashable)var5.next();
            if (var6.getLeashHolder() == var1) {
               var6.setLeashedTo(this, true);
               var3 = true;
            }
         }

         boolean var8 = false;
         if (!var3) {
            this.discard();
            if (var1.getAbilities().instabuild) {
               Iterator var9 = var4.iterator();

               while(var9.hasNext()) {
                  Leashable var7 = (Leashable)var9.next();
                  if (var7.isLeashed() && var7.getLeashHolder() == this) {
                     var7.dropLeash(true, false);
                     var8 = true;
                  }
               }
            }
         }

         if (var3 || var8) {
            this.gameEvent(GameEvent.BLOCK_ATTACH, var1);
         }

         return InteractionResult.CONSUME;
      }
   }

   public boolean survives() {
      return this.level().getBlockState(this.pos).is(BlockTags.FENCES);
   }

   public static LeashFenceKnotEntity getOrCreateKnot(Level var0, BlockPos var1) {
      int var2 = var1.getX();
      int var3 = var1.getY();
      int var4 = var1.getZ();
      List var5 = var0.getEntitiesOfClass(LeashFenceKnotEntity.class, new AABB((double)var2 - 1.0, (double)var3 - 1.0, (double)var4 - 1.0, (double)var2 + 1.0, (double)var3 + 1.0, (double)var4 + 1.0));
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

   public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity var1) {
      return new ClientboundAddEntityPacket(this, 0, this.getPos());
   }

   public Vec3 getRopeHoldPosition(float var1) {
      return this.getPosition(var1).add(0.0, 0.2, 0.0);
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.LEAD);
   }
}
