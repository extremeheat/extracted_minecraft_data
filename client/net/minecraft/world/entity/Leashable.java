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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public interface Leashable {
   String LEASH_TAG = "leash";
   double LEASH_TOO_FAR_DIST = 10.0;
   double LEASH_ELASTIC_DIST = 6.0;

   @Nullable
   LeashData getLeashData();

   void setLeashData(@Nullable LeashData var1);

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
      this.setLeashData(new LeashData(var1));
      dropLeash((Entity)this, false, false);
   }

   @Nullable
   default LeashData readLeashData(CompoundTag var1) {
      if (var1.contains("leash", 10)) {
         return new LeashData(Either.left(var1.getCompound("leash").getUUID("UUID")));
      } else {
         if (var1.contains("leash", 11)) {
            Either var2 = (Either)NbtUtils.readBlockPos(var1, "leash").map(Either::right).orElse((Object)null);
            if (var2 != null) {
               return new LeashData(var2);
            }
         }

         return null;
      }
   }

   default void writeLeashData(CompoundTag var1, @Nullable LeashData var2) {
      if (var2 != null) {
         Either var3 = var2.delayedLeashInfo;
         Entity var5 = var2.leashHolder;
         if (var5 instanceof LeashFenceKnotEntity) {
            LeashFenceKnotEntity var4 = (LeashFenceKnotEntity)var5;
            var3 = Either.right(var4.getPos());
         } else if (var2.leashHolder != null) {
            var3 = Either.left(var2.leashHolder.getUUID());
         }

         if (var3 != null) {
            var1.put("leash", (Tag)var3.map((var0) -> {
               CompoundTag var1 = new CompoundTag();
               var1.putUUID("UUID", var0);
               return var1;
            }, NbtUtils::writeBlockPos));
         }
      }
   }

   private static <E extends Entity & Leashable> void restoreLeashFromSave(E var0, LeashData var1) {
      if (var1.delayedLeashInfo != null) {
         Level var3 = var0.level();
         if (var3 instanceof ServerLevel) {
            ServerLevel var2 = (ServerLevel)var3;
            Optional var6 = var1.delayedLeashInfo.left();
            Optional var4 = var1.delayedLeashInfo.right();
            if (var6.isPresent()) {
               Entity var5 = var2.getEntity((UUID)var6.get());
               if (var5 != null) {
                  setLeashedTo(var0, var5, true);
                  return;
               }
            } else if (var4.isPresent()) {
               setLeashedTo(var0, LeashFenceKnotEntity.getOrCreateKnot(var2, (BlockPos)var4.get()), true);
               return;
            }

            if (var0.tickCount > 100) {
               var0.spawnAtLocation(var2, (ItemLike)Items.LEAD);
               ((Leashable)var0).setLeashData((LeashData)null);
            }
         }
      }

   }

   default void dropLeash(boolean var1, boolean var2) {
      dropLeash((Entity)this, var1, var2);
   }

   private static <E extends Entity & Leashable> void dropLeash(E var0, boolean var1, boolean var2) {
      LeashData var3 = ((Leashable)var0).getLeashData();
      if (var3 != null && var3.leashHolder != null) {
         ((Leashable)var0).setLeashData((LeashData)null);
         Level var5 = var0.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel var4 = (ServerLevel)var5;
            if (var2) {
               var0.spawnAtLocation(var4, (ItemLike)Items.LEAD);
            }

            if (var1) {
               var4.getChunkSource().broadcast(var0, new ClientboundSetEntityLinkPacket(var0, (Entity)null));
            }
         }
      }

   }

   static <E extends Entity & Leashable> void tickLeash(ServerLevel var0, E var1) {
      LeashData var2 = ((Leashable)var1).getLeashData();
      if (var2 != null && var2.delayedLeashInfo != null) {
         restoreLeashFromSave(var1, var2);
      }

      if (var2 != null && var2.leashHolder != null) {
         if (!var1.isAlive() || !var2.leashHolder.isAlive()) {
            dropLeash(var1, true, var0.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS));
         }

         Entity var3 = ((Leashable)var1).getLeashHolder();
         if (var3 != null && var3.level() == var1.level()) {
            float var4 = var1.distanceTo(var3);
            if (!((Leashable)var1).handleLeashAtDistance(var3, var4)) {
               return;
            }

            if ((double)var4 > 10.0) {
               ((Leashable)var1).leashTooFarBehaviour();
            } else if ((double)var4 > 6.0) {
               ((Leashable)var1).elasticRangeLeashBehaviour(var3, var4);
               var1.checkSlowFallDistance();
            } else {
               ((Leashable)var1).closeRangeLeashBehaviour(var3);
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
      var0.setDeltaMovement(var0.getDeltaMovement().add(Math.copySign(var3 * var3 * 0.4, var3), Math.copySign(var5 * var5 * 0.4, var5), Math.copySign(var7 * var7 * 0.4, var7)));
   }

   default void setLeashedTo(Entity var1, boolean var2) {
      setLeashedTo((Entity)this, var1, var2);
   }

   private static <E extends Entity & Leashable> void setLeashedTo(E var0, Entity var1, boolean var2) {
      LeashData var3 = ((Leashable)var0).getLeashData();
      if (var3 == null) {
         var3 = new LeashData(var1);
         ((Leashable)var0).setLeashData(var3);
      } else {
         var3.setLeashHolder(var1);
      }

      if (var2) {
         Level var5 = var0.level();
         if (var5 instanceof ServerLevel) {
            ServerLevel var4 = (ServerLevel)var5;
            var4.getChunkSource().broadcast(var0, new ClientboundSetEntityLinkPacket(var0, var1));
         }
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
      LeashData var1 = ((Leashable)var0).getLeashData();
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
