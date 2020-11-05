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
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BeehiveBlockEntity extends BlockEntity implements TickableBlockEntity {
   private final List<BeehiveBlockEntity.BeeData> stored = Lists.newArrayList();
   @Nullable
   private BlockPos savedFlowerPos = null;

   public BeehiveBlockEntity() {
      super(BlockEntityType.BEEHIVE);
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
         return this.releaseOccupant(var1, var4, var3, var2);
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

   protected void sendDebugPackets() {
      DebugPackets.sendHiveInfo(this);
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

         var1.remove();
      }
   }

   private boolean releaseOccupant(BlockState var1, BeehiveBlockEntity.BeeData var2, @Nullable List<Entity> var3, BeehiveBlockEntity.BeeReleaseStatus var4) {
      if ((this.level.isNight() || this.level.isRaining()) && var4 != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
         return false;
      } else {
         BlockPos var5 = this.getBlockPos();
         CompoundTag var6 = var2.entityData;
         var6.remove("Passengers");
         var6.remove("Leash");
         var6.remove("UUID");
         Direction var7 = (Direction)var1.getValue(BeehiveBlock.FACING);
         BlockPos var8 = var5.relative(var7);
         boolean var9 = !this.level.getBlockState(var8).getCollisionShape(this.level, var8).isEmpty();
         if (var9 && var4 != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
            return false;
         } else {
            Entity var10 = EntityType.loadEntityRecursive(var6, this.level, (var0) -> {
               return var0;
            });
            if (var10 != null) {
               if (!var10.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
                  return false;
               } else {
                  if (var10 instanceof Bee) {
                     Bee var11 = (Bee)var10;
                     if (this.hasSavedFlowerPos() && !var11.hasSavedFlowerPos() && this.level.random.nextFloat() < 0.9F) {
                        var11.setSavedFlowerPos(this.savedFlowerPos);
                     }

                     if (var4 == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
                        var11.dropOffNectar();
                        if (var1.getBlock().is((Tag)BlockTags.BEEHIVES)) {
                           int var12 = getHoneyLevel(var1);
                           if (var12 < 5) {
                              int var13 = this.level.random.nextInt(100) == 0 ? 2 : 1;
                              if (var12 + var13 > 5) {
                                 --var13;
                              }

                              this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)var1.setValue(BeehiveBlock.HONEY_LEVEL, var12 + var13));
                           }
                        }
                     }

                     this.setBeeReleaseData(var2.ticksInHive, var11);
                     if (var3 != null) {
                        var3.add(var11);
                     }

                     float var21 = var10.getBbWidth();
                     double var22 = var9 ? 0.0D : 0.55D + (double)(var21 / 2.0F);
                     double var15 = (double)var5.getX() + 0.5D + var22 * (double)var7.getStepX();
                     double var17 = (double)var5.getY() + 0.5D - (double)(var10.getBbHeight() / 2.0F);
                     double var19 = (double)var5.getZ() + 0.5D + var22 * (double)var7.getStepZ();
                     var10.moveTo(var15, var17, var19, var10.yRot, var10.xRot);
                  }

                  this.level.playSound((Player)null, (BlockPos)var5, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  return this.level.addFreshEntity(var10);
               }
            } else {
               return false;
            }
         }
      }
   }

   private void setBeeReleaseData(int var1, Bee var2) {
      int var3 = var2.getAge();
      if (var3 < 0) {
         var2.setAge(Math.min(0, var3 + var1));
      } else if (var3 > 0) {
         var2.setAge(Math.max(0, var3 - var1));
      }

      var2.setInLoveTime(Math.max(0, var2.getInLoveTime() - var1));
      var2.resetTicksWithoutNectarSinceExitingHive();
   }

   private boolean hasSavedFlowerPos() {
      return this.savedFlowerPos != null;
   }

   private void tickOccupants() {
      Iterator var1 = this.stored.iterator();

      BeehiveBlockEntity.BeeData var3;
      for(BlockState var2 = this.getBlockState(); var1.hasNext(); var3.ticksInHive++) {
         var3 = (BeehiveBlockEntity.BeeData)var1.next();
         if (var3.ticksInHive > var3.minOccupationTicks) {
            BeehiveBlockEntity.BeeReleaseStatus var4 = var3.entityData.getBoolean("HasNectar") ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
            if (this.releaseOccupant(var2, var3, (List)null, var4)) {
               var1.remove();
            }
         }
      }

   }

   public void tick() {
      if (!this.level.isClientSide) {
         this.tickOccupants();
         BlockPos var1 = this.getBlockPos();
         if (this.stored.size() > 0 && this.level.getRandom().nextDouble() < 0.005D) {
            double var2 = (double)var1.getX() + 0.5D;
            double var4 = (double)var1.getY();
            double var6 = (double)var1.getZ() + 0.5D;
            this.level.playSound((Player)null, var2, var4, var6, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
         }

         this.sendDebugPackets();
      }
   }

   public void load(BlockState var1, CompoundTag var2) {
      super.load(var1, var2);
      this.stored.clear();
      ListTag var3 = var2.getList("Bees", 10);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         CompoundTag var5 = var3.getCompound(var4);
         BeehiveBlockEntity.BeeData var6 = new BeehiveBlockEntity.BeeData(var5.getCompound("EntityData"), var5.getInt("TicksInHive"), var5.getInt("MinOccupationTicks"));
         this.stored.add(var6);
      }

      this.savedFlowerPos = null;
      if (var2.contains("FlowerPos")) {
         this.savedFlowerPos = NbtUtils.readBlockPos(var2.getCompound("FlowerPos"));
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
