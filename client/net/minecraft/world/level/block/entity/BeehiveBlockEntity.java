package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BeehiveBlockEntity extends BlockEntity {
   private final List<BeehiveBlockEntity.BeeData> stored = Lists.newArrayList();
   @Nullable
   private BlockPos savedFlowerPos;

   public BeehiveBlockEntity(BlockPos var1, BlockState var2) {
      super(BlockEntityType.BEEHIVE, var1, var2);
   }

   public void setChanged() {
      if (this.isFireNearby()) {
         this.emptyAllLivingFromHive((Player)null, this.level.getBlockState(this.getBlockPos()), BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY);
      }

      super.setChanged();
   }

   public boolean isFireNearby() {
      if (this.level == null) {
         return false;
      } else {
         Iterator var1 = BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1)).iterator();

         BlockPos var2;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            var2 = (BlockPos)var1.next();
         } while(!(this.level.getBlockState(var2).getBlock() instanceof FireBlock));

         return true;
      }
   }

   public boolean isEmpty() {
      return this.stored.isEmpty();
   }

   public boolean isFull() {
      return this.stored.size() == 3;
   }

   public void emptyAllLivingFromHive(@Nullable Player var1, BlockState var2, BeehiveBlockEntity.BeeReleaseStatus var3) {
      List var4 = this.releaseAllOccupants(var2, var3);
      if (var1 != null) {
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Entity var6 = (Entity)var5.next();
            if (var6 instanceof Bee) {
               Bee var7 = (Bee)var6;
               if (var1.position().distanceToSqr(var6.position()) <= 16.0D) {
                  if (!this.isSedated()) {
                     var7.setTarget(var1);
                  } else {
                     var7.setStayOutOfHiveCountdown(400);
                  }
               }
            }
         }
      }

   }

   private List<Entity> releaseAllOccupants(BlockState var1, BeehiveBlockEntity.BeeReleaseStatus var2) {
      ArrayList var3 = Lists.newArrayList();
      this.stored.removeIf((var4) -> {
         return releaseOccupant(this.level, this.worldPosition, var1, var4, var3, var2, this.savedFlowerPos);
      });
      return var3;
   }

   public void addOccupant(Entity var1, boolean var2) {
      this.addOccupantWithPresetTicks(var1, var2, 0);
   }

   public int getOccupantCount() {
      return this.stored.size();
   }

   public static int getHoneyLevel(BlockState var0) {
      return (Integer)var0.getValue(BeehiveBlock.HONEY_LEVEL);
   }

   public boolean isSedated() {
      return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
   }

   public void addOccupantWithPresetTicks(Entity var1, boolean var2, int var3) {
      if (this.stored.size() < 3) {
         var1.stopRiding();
         var1.ejectPassengers();
         CompoundTag var4 = new CompoundTag();
         var1.save(var4);
         this.stored.add(new BeehiveBlockEntity.BeeData(var4, var3, var2 ? 2400 : 600));
         if (this.level != null) {
            if (var1 instanceof Bee) {
               Bee var5 = (Bee)var1;
               if (var5.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                  this.savedFlowerPos = var5.getSavedFlowerPos();
               }
            }

            BlockPos var6 = this.getBlockPos();
            this.level.playSound((Player)null, (double)var6.getX(), (double)var6.getY(), (double)var6.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         var1.discard();
      }
   }

   private static boolean releaseOccupant(Level var0, BlockPos var1, BlockState var2, BeehiveBlockEntity.BeeData var3, @Nullable List<Entity> var4, BeehiveBlockEntity.BeeReleaseStatus var5, @Nullable BlockPos var6) {
      if ((var0.isNight() || var0.isRaining()) && var5 != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
         return false;
      } else {
         CompoundTag var7 = var3.entityData;
         var7.remove("Passengers");
         var7.remove("Leash");
         var7.remove("UUID");
         Direction var8 = (Direction)var2.getValue(BeehiveBlock.FACING);
         BlockPos var9 = var1.relative(var8);
         boolean var10 = !var0.getBlockState(var9).getCollisionShape(var0, var9).isEmpty();
         if (var10 && var5 != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
            return false;
         } else {
            Entity var11 = EntityType.loadEntityRecursive(var7, var0, (var0x) -> {
               return var0x;
            });
            if (var11 != null) {
               if (!var11.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                  return false;
               } else {
                  if (var11 instanceof Bee) {
                     Bee var12 = (Bee)var11;
                     if (var6 != null && !var12.hasSavedFlowerPos() && var0.random.nextFloat() < 0.9F) {
                        var12.setSavedFlowerPos(var6);
                     }

                     if (var5 == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
                        var12.dropOffNectar();
                        if (var2.is(BlockTags.BEEHIVES)) {
                           int var13 = getHoneyLevel(var2);
                           if (var13 < 5) {
                              int var14 = var0.random.nextInt(100) == 0 ? 2 : 1;
                              if (var13 + var14 > 5) {
                                 --var14;
                              }

                              var0.setBlockAndUpdate(var1, (BlockState)var2.setValue(BeehiveBlock.HONEY_LEVEL, var13 + var14));
                           }
                        }
                     }

                     setBeeReleaseData(var3.ticksInHive, var12);
                     if (var4 != null) {
                        var4.add(var12);
                     }

                     float var22 = var11.getBbWidth();
                     double var23 = var10 ? 0.0D : 0.55D + (double)(var22 / 2.0F);
                     double var16 = (double)var1.getX() + 0.5D + var23 * (double)var8.getStepX();
                     double var18 = (double)var1.getY() + 0.5D - (double)(var11.getBbHeight() / 2.0F);
                     double var20 = (double)var1.getZ() + 0.5D + var23 * (double)var8.getStepZ();
                     var11.moveTo(var16, var18, var20, var11.yRot, var11.xRot);
                  }

                  var0.playSound((Player)null, (BlockPos)var1, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  return var0.addFreshEntity(var11);
               }
            } else {
               return false;
            }
         }
      }
   }

   private static void setBeeReleaseData(int var0, Bee var1) {
      int var2 = var1.getAge();
      if (var2 < 0) {
         var1.setAge(Math.min(0, var2 + var0));
      } else if (var2 > 0) {
         var1.setAge(Math.max(0, var2 - var0));
      }

      var1.setInLoveTime(Math.max(0, var1.getInLoveTime() - var0));
      var1.resetTicksWithoutNectarSinceExitingHive();
   }

   private boolean hasSavedFlowerPos() {
      return this.savedFlowerPos != null;
   }

   private static void tickOccupants(Level var0, BlockPos var1, BlockState var2, List<BeehiveBlockEntity.BeeData> var3, @Nullable BlockPos var4) {
      BeehiveBlockEntity.BeeData var6;
      for(Iterator var5 = var3.iterator(); var5.hasNext(); var6.ticksInHive++) {
         var6 = (BeehiveBlockEntity.BeeData)var5.next();
         if (var6.ticksInHive > var6.minOccupationTicks) {
            BeehiveBlockEntity.BeeReleaseStatus var7 = var6.entityData.getBoolean("HasNectar") ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
            if (releaseOccupant(var0, var1, var2, var6, (List)null, var7, var4)) {
               var5.remove();
            }
         }
      }

   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, BeehiveBlockEntity var3) {
      tickOccupants(var0, var1, var2, var3.stored, var3.savedFlowerPos);
      if (!var3.stored.isEmpty() && var0.getRandom().nextDouble() < 0.005D) {
         double var4 = (double)var1.getX() + 0.5D;
         double var6 = (double)var1.getY();
         double var8 = (double)var1.getZ() + 0.5D;
         var0.playSound((Player)null, var4, var6, var8, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      DebugPackets.sendHiveInfo(var0, var1, var2, var3);
   }

   public void load(CompoundTag var1) {
      super.load(var1);
      this.stored.clear();
      ListTag var2 = var1.getList("Bees", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         CompoundTag var4 = var2.getCompound(var3);
         BeehiveBlockEntity.BeeData var5 = new BeehiveBlockEntity.BeeData(var4.getCompound("EntityData"), var4.getInt("TicksInHive"), var4.getInt("MinOccupationTicks"));
         this.stored.add(var5);
      }

      this.savedFlowerPos = null;
      if (var1.contains("FlowerPos")) {
         this.savedFlowerPos = NbtUtils.readBlockPos(var1.getCompound("FlowerPos"));
      }

   }

   public CompoundTag save(CompoundTag var1) {
      super.save(var1);
      var1.put("Bees", this.writeBees());
      if (this.hasSavedFlowerPos()) {
         var1.put("FlowerPos", NbtUtils.writeBlockPos(this.savedFlowerPos));
      }

      return var1;
   }

   public ListTag writeBees() {
      ListTag var1 = new ListTag();
      Iterator var2 = this.stored.iterator();

      while(var2.hasNext()) {
         BeehiveBlockEntity.BeeData var3 = (BeehiveBlockEntity.BeeData)var2.next();
         var3.entityData.remove("UUID");
         CompoundTag var4 = new CompoundTag();
         var4.put("EntityData", var3.entityData);
         var4.putInt("TicksInHive", var3.ticksInHive);
         var4.putInt("MinOccupationTicks", var3.minOccupationTicks);
         var1.add(var4);
      }

      return var1;
   }

   static class BeeData {
      private final CompoundTag entityData;
      private int ticksInHive;
      private final int minOccupationTicks;

      private BeeData(CompoundTag var1, int var2, int var3) {
         super();
         var1.remove("UUID");
         this.entityData = var1;
         this.ticksInHive = var2;
         this.minOccupationTicks = var3;
      }

      // $FF: synthetic method
      BeeData(CompoundTag var1, int var2, int var3, Object var4) {
         this(var1, var2, var3);
      }
   }

   public static enum BeeReleaseStatus {
      HONEY_DELIVERED,
      BEE_RELEASED,
      EMERGENCY;

      private BeeReleaseStatus() {
      }
   }
}
