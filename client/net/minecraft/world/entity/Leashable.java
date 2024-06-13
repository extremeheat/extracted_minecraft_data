package net.minecraft.world.entity;

import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Items;

public interface Leashable {
   String LEASH_TAG = "leash";
   double LEASH_TOO_FAR_DIST = 10.0;
   double LEASH_ELASTIC_DIST = 6.0;

   @Nullable
   Leashable.LeashData getLeashData();

   void setLeashData(@Nullable Leashable.LeashData var1);

   default boolean isLeashed() {
      return this.getLeashData() != null && this.getLeashData().leashHolder != null;
   }

   default boolean mayBeLeashed() {
      return this.getLeashData() != null;
   }

   default boolean canHaveALeashAttachedToIt() {
      return this.canBeLeashed() && !this.isLeashed();
   }

   default boolean canBeLeashed() {
      return true;
   }

   default void setDelayedLeashHolderId(int var1) {
      this.setLeashData(new Leashable.LeashData(var1));
      dropLeash((Entity)this, false, false);
   }

   @Nullable
   default Leashable.LeashData readLeashData(CompoundTag var1) {
      if (var1.contains("leash", 10)) {
         return new Leashable.LeashData(Either.left(var1.getCompound("leash").getUUID("UUID")));
      } else {
         if (var1.contains("leash", 11)) {
            Either var2 = NbtUtils.readBlockPos(var1, "leash").<Either>map(Either::right).orElse(null);
            if (var2 != null) {
               return new Leashable.LeashData(var2);
            }
         }

         return null;
      }
   }

   default void writeLeashData(CompoundTag var1, @Nullable Leashable.LeashData var2) {
      if (var2 != null) {
         Either var3 = var2.delayedLeashInfo;
         if (var2.leashHolder instanceof LeashFenceKnotEntity var4) {
            var3 = Either.right(var4.getPos());
         } else if (var2.leashHolder != null) {
            var3 = Either.left(var2.leashHolder.getUUID());
         }

         if (var3 != null) {
            var1.put("leash", (Tag)var3.map(var0 -> {
               CompoundTag var1x = new CompoundTag();
               var1x.putUUID("UUID", var0);
               return var1x;
            }, NbtUtils::writeBlockPos));
         }
      }
   }

   private static <E extends Entity & Leashable> void restoreLeashFromSave(E var0, Leashable.LeashData var1) {
      if (var1.delayedLeashInfo != null && var0.level() instanceof ServerLevel var2) {
         Optional var6 = var1.delayedLeashInfo.left();
         Optional var4 = var1.delayedLeashInfo.right();
         if (var6.isPresent()) {
            Entity var5 = var2.getEntity((UUID)var6.get());
            if (var5 != null) {
               setLeashedTo((E)var0, var5, true);
               return;
            }
         } else if (var4.isPresent()) {
            setLeashedTo((E)var0, LeashFenceKnotEntity.getOrCreateKnot(var2, (BlockPos)var4.get()), true);
            return;
         }

         if (var0.tickCount > 100) {
            var0.spawnAtLocation(Items.LEAD);
            ((Leashable)var0).setLeashData(null);
         }
      }
   }

   default void dropLeash(boolean var1, boolean var2) {
      dropLeash((Entity)this, var1, var2);
   }

   private static <E extends Entity & Leashable> void dropLeash(E var0, boolean var1, boolean var2) {
      Leashable.LeashData var3 = ((Leashable)var0).getLeashData();
      if (var3 != null && var3.leashHolder != null) {
         ((Leashable)var0).setLeashData(null);
         if (!var0.level().isClientSide && var2) {
            var0.spawnAtLocation(Items.LEAD);
         }

         if (var1 && var0.level() instanceof ServerLevel var4) {
            var4.getChunkSource().broadcast(var0, new ClientboundSetEntityLinkPacket(var0, null));
         }
      }
   }

   static <E extends Entity & Leashable> void tickLeash(E var0) {
      Leashable.LeashData var1 = ((Leashable)var0).getLeashData();
      if (var1 != null && var1.delayedLeashInfo != null) {
         restoreLeashFromSave((E)var0, var1);
      }

      if (var1 != null && var1.leashHolder != null) {
         if (!var0.isAlive() || !var1.leashHolder.isAlive()) {
            dropLeash((E)var0, true, true);
         }

         Entity var2 = ((Leashable)var0).getLeashHolder();
         if (var2 != null && var2.level() == var0.level()) {
            float var3 = var0.distanceTo(var2);
            if (!((Leashable)var0).handleLeashAtDistance(var2, var3)) {
               return;
            }

            if ((double)var3 > 10.0) {
               ((Leashable)var0).leashTooFarBehaviour();
            } else if ((double)var3 > 6.0) {
               ((Leashable)var0).elasticRangeLeashBehaviour(var2, var3);
               var0.checkSlowFallDistance();
            } else {
               ((Leashable)var0).closeRangeLeashBehaviour(var2);
            }
         }
      }
   }

   default boolean handleLeashAtDistance(Entity var1, float var2) {
      return true;
   }

   default void leashTooFarBehaviour() {
      this.dropLeash(true, true);
   }

   default void closeRangeLeashBehaviour(Entity var1) {
   }

   default void elasticRangeLeashBehaviour(Entity var1, float var2) {
      legacyElasticRangeLeashBehaviour((Entity)this, var1, var2);
   }

   private static <E extends Entity & Leashable> void legacyElasticRangeLeashBehaviour(E var0, Entity var1, float var2) {
      double var3 = (var1.getX() - var0.getX()) / (double)var2;
      double var5 = (var1.getY() - var0.getY()) / (double)var2;
      double var7 = (var1.getZ() - var0.getZ()) / (double)var2;
      var0.setDeltaMovement(
         var0.getDeltaMovement().add(Math.copySign(var3 * var3 * 0.4, var3), Math.copySign(var5 * var5 * 0.4, var5), Math.copySign(var7 * var7 * 0.4, var7))
      );
   }

   default void setLeashedTo(Entity var1, boolean var2) {
      setLeashedTo((Entity)this, var1, var2);
   }

   private static <E extends Entity & Leashable> void setLeashedTo(E var0, Entity var1, boolean var2) {
      Leashable.LeashData var3 = ((Leashable)var0).getLeashData();
      if (var3 == null) {
         var3 = new Leashable.LeashData(var1);
         ((Leashable)var0).setLeashData(var3);
      } else {
         var3.setLeashHolder(var1);
      }

      if (var2 && var0.level() instanceof ServerLevel var4) {
         var4.getChunkSource().broadcast(var0, new ClientboundSetEntityLinkPacket(var0, var1));
      }

      if (var0.isPassenger()) {
         var0.stopRiding();
      }
   }

   @Nullable
   default Entity getLeashHolder() {
      return getLeashHolder((Entity)this);
   }

   @Nullable
   private static <E extends Entity & Leashable> Entity getLeashHolder(E var0) {
      Leashable.LeashData var1 = ((Leashable)var0).getLeashData();
      if (var1 == null) {
         return null;
      } else {
         if (var1.delayedLeashHolderId != 0 && var0.level().isClientSide) {
            Entity var3 = var0.level().getEntity(var1.delayedLeashHolderId);
            if (var3 instanceof Entity) {
               var1.setLeashHolder(var3);
            }
         }

         return var1.leashHolder;
      }
   }

   public static final class LeashData {
      int delayedLeashHolderId;
      @Nullable
      public Entity leashHolder;
      @Nullable
      public Either<UUID, BlockPos> delayedLeashInfo;

      LeashData(Either<UUID, BlockPos> var1) {
         super();
         this.delayedLeashInfo = var1;
      }

      LeashData(Entity var1) {
         super();
         this.leashHolder = var1;
      }

      LeashData(int var1) {
         super();
         this.delayedLeashHolderId = var1;
      }

      public void setLeashHolder(Entity var1) {
         this.leashHolder = var1;
         this.delayedLeashInfo = null;
         this.delayedLeashHolderId = 0;
      }
   }
}
