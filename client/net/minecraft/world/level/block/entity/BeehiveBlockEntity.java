package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.slf4j.Logger;

public class BeehiveBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String TAG_FLOWER_POS = "flower_pos";
   private static final String BEES = "bees";
   static final List<String> IGNORED_BEE_TAGS = Arrays.asList("Air", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "SleepingX", "SleepingY", "SleepingZ", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "hive_pos", "Passengers", "leash", "UUID");
   public static final int MAX_OCCUPANTS = 3;
   private static final int MIN_TICKS_BEFORE_REENTERING_HIVE = 400;
   private static final int MIN_OCCUPATION_TICKS_NECTAR = 2400;
   public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 600;
   private final List<BeeData> stored = Lists.newArrayList();
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

   public void emptyAllLivingFromHive(@Nullable Player var1, BlockState var2, BeeReleaseStatus var3) {
      List var4 = this.releaseAllOccupants(var2, var3);
      if (var1 != null) {
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            Entity var6 = (Entity)var5.next();
            if (var6 instanceof Bee) {
               Bee var7 = (Bee)var6;
               if (var1.position().distanceToSqr(var6.position()) <= 16.0) {
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

   private List<Entity> releaseAllOccupants(BlockState var1, BeeReleaseStatus var2) {
      ArrayList var3 = Lists.newArrayList();
      this.stored.removeIf((var4) -> {
         return releaseOccupant(this.level, this.worldPosition, var1, var4.toOccupant(), var3, var2, this.savedFlowerPos);
      });
      if (!var3.isEmpty()) {
         super.setChanged();
      }

      return var3;
   }

   @VisibleForDebug
   public int getOccupantCount() {
      return this.stored.size();
   }

   public static int getHoneyLevel(BlockState var0) {
      return (Integer)var0.getValue(BeehiveBlock.HONEY_LEVEL);
   }

   @VisibleForDebug
   public boolean isSedated() {
      return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
   }

   public void addOccupant(Entity var1) {
      if (this.stored.size() < 3) {
         var1.stopRiding();
         var1.ejectPassengers();
         this.storeBee(BeehiveBlockEntity.Occupant.of(var1));
         if (this.level != null) {
            if (var1 instanceof Bee) {
               Bee var2 = (Bee)var1;
               if (var2.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.random.nextBoolean())) {
                  this.savedFlowerPos = var2.getSavedFlowerPos();
               }
            }

            BlockPos var3 = this.getBlockPos();
            this.level.playSound((Player)null, (double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, var3, GameEvent.Context.of(var1, this.getBlockState()));
         }

         var1.discard();
         super.setChanged();
      }
   }

   public void storeBee(Occupant var1) {
      this.stored.add(new BeeData(var1));
   }

   private static boolean releaseOccupant(Level var0, BlockPos var1, BlockState var2, Occupant var3, @Nullable List<Entity> var4, BeeReleaseStatus var5, @Nullable BlockPos var6) {
      if ((var0.isNight() || var0.isRaining()) && var5 != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
         return false;
      } else {
         Direction var7 = (Direction)var2.getValue(BeehiveBlock.FACING);
         BlockPos var8 = var1.relative(var7);
         boolean var9 = !var0.getBlockState(var8).getCollisionShape(var0, var8).isEmpty();
         if (var9 && var5 != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
            return false;
         } else {
            Entity var10 = var3.createEntity(var0, var1);
            if (var10 != null) {
               if (var10 instanceof Bee) {
                  Bee var11 = (Bee)var10;
                  if (var6 != null && !var11.hasSavedFlowerPos() && var0.random.nextFloat() < 0.9F) {
                     var11.setSavedFlowerPos(var6);
                  }

                  if (var5 == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
                     var11.dropOffNectar();
                     if (var2.is(BlockTags.BEEHIVES, (var0x) -> {
                        return var0x.hasProperty(BeehiveBlock.HONEY_LEVEL);
                     })) {
                        int var12 = getHoneyLevel(var2);
                        if (var12 < 5) {
                           int var13 = var0.random.nextInt(100) == 0 ? 2 : 1;
                           if (var12 + var13 > 5) {
                              --var13;
                           }

                           var0.setBlockAndUpdate(var1, (BlockState)var2.setValue(BeehiveBlock.HONEY_LEVEL, var12 + var13));
                        }
                     }
                  }

                  if (var4 != null) {
                     var4.add(var11);
                  }

                  float var21 = var10.getBbWidth();
                  double var22 = var9 ? 0.0 : 0.55 + (double)(var21 / 2.0F);
                  double var15 = (double)var1.getX() + 0.5 + var22 * (double)var7.getStepX();
                  double var17 = (double)var1.getY() + 0.5 - (double)(var10.getBbHeight() / 2.0F);
                  double var19 = (double)var1.getZ() + 0.5 + var22 * (double)var7.getStepZ();
                  var10.moveTo(var15, var17, var19, var10.getYRot(), var10.getXRot());
               }

               var0.playSound((Player)null, (BlockPos)var1, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
               var0.gameEvent(GameEvent.BLOCK_CHANGE, var1, GameEvent.Context.of(var10, var0.getBlockState(var1)));
               return var0.addFreshEntity(var10);
            } else {
               return false;
            }
         }
      }
   }

   private boolean hasSavedFlowerPos() {
      return this.savedFlowerPos != null;
   }

   private static void tickOccupants(Level var0, BlockPos var1, BlockState var2, List<BeeData> var3, @Nullable BlockPos var4) {
      boolean var5 = false;
      Iterator var6 = var3.iterator();

      while(var6.hasNext()) {
         BeeData var7 = (BeeData)var6.next();
         if (var7.tick()) {
            BeeReleaseStatus var8 = var7.hasNectar() ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
            if (releaseOccupant(var0, var1, var2, var7.toOccupant(), (List)null, var8, var4)) {
               var5 = true;
               var6.remove();
            }
         }
      }

      if (var5) {
         setChanged(var0, var1, var2);
      }

   }

   public static void serverTick(Level var0, BlockPos var1, BlockState var2, BeehiveBlockEntity var3) {
      tickOccupants(var0, var1, var2, var3.stored, var3.savedFlowerPos);
      if (!var3.stored.isEmpty() && var0.getRandom().nextDouble() < 0.005) {
         double var4 = (double)var1.getX() + 0.5;
         double var6 = (double)var1.getY();
         double var8 = (double)var1.getZ() + 0.5;
         var0.playSound((Player)null, var4, var6, var8, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      DebugPackets.sendHiveInfo(var0, var1, var2, var3);
   }

   protected void loadAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.loadAdditional(var1, var2);
      this.stored.clear();
      if (var1.contains("bees")) {
         BeehiveBlockEntity.Occupant.LIST_CODEC.parse(NbtOps.INSTANCE, var1.get("bees")).resultOrPartial((var0) -> {
            LOGGER.error("Failed to parse bees: '{}'", var0);
         }).ifPresent((var1x) -> {
            var1x.forEach(this::storeBee);
         });
      }

      this.savedFlowerPos = (BlockPos)NbtUtils.readBlockPos(var1, "flower_pos").orElse((Object)null);
   }

   protected void saveAdditional(CompoundTag var1, HolderLookup.Provider var2) {
      super.saveAdditional(var1, var2);
      var1.put("bees", (Tag)BeehiveBlockEntity.Occupant.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.getBees()).getOrThrow());
      if (this.hasSavedFlowerPos()) {
         var1.put("flower_pos", NbtUtils.writeBlockPos(this.savedFlowerPos));
      }

   }

   protected void applyImplicitComponents(BlockEntity.DataComponentInput var1) {
      super.applyImplicitComponents(var1);
      this.stored.clear();
      List var2 = (List)var1.getOrDefault(DataComponents.BEES, List.of());
      var2.forEach(this::storeBee);
   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      var1.set(DataComponents.BEES, this.getBees());
   }

   public void removeComponentsFromTag(CompoundTag var1) {
      super.removeComponentsFromTag(var1);
      var1.remove("bees");
   }

   private List<Occupant> getBees() {
      return this.stored.stream().map(BeeData::toOccupant).toList();
   }

   public static enum BeeReleaseStatus {
      HONEY_DELIVERED,
      BEE_RELEASED,
      EMERGENCY;

      private BeeReleaseStatus() {
      }

      // $FF: synthetic method
      private static BeeReleaseStatus[] $values() {
         return new BeeReleaseStatus[]{HONEY_DELIVERED, BEE_RELEASED, EMERGENCY};
      }
   }

   public static record Occupant(CustomData entityData, int ticksInHive, int minTicksInHive) {
      final CustomData entityData;
      final int minTicksInHive;
      public static final Codec<Occupant> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(CustomData.CODEC.optionalFieldOf("entity_data", CustomData.EMPTY).forGetter(Occupant::entityData), Codec.INT.fieldOf("ticks_in_hive").forGetter(Occupant::ticksInHive), Codec.INT.fieldOf("min_ticks_in_hive").forGetter(Occupant::minTicksInHive)).apply(var0, Occupant::new);
      });
      public static final Codec<List<Occupant>> LIST_CODEC;
      public static final StreamCodec<ByteBuf, Occupant> STREAM_CODEC;

      public Occupant(CustomData entityData, int ticksInHive, int minTicksInHive) {
         super();
         this.entityData = entityData;
         this.ticksInHive = ticksInHive;
         this.minTicksInHive = minTicksInHive;
      }

      public static Occupant of(Entity var0) {
         CompoundTag var1 = new CompoundTag();
         var0.save(var1);
         List var10000 = BeehiveBlockEntity.IGNORED_BEE_TAGS;
         Objects.requireNonNull(var1);
         var10000.forEach(var1::remove);
         boolean var2 = var1.getBoolean("HasNectar");
         return new Occupant(CustomData.of(var1), 0, var2 ? 2400 : 600);
      }

      public static Occupant create(int var0) {
         CompoundTag var1 = new CompoundTag();
         var1.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.BEE).toString());
         return new Occupant(CustomData.of(var1), var0, 600);
      }

      @Nullable
      public Entity createEntity(Level var1, BlockPos var2) {
         CompoundTag var3 = this.entityData.copyTag();
         List var10000 = BeehiveBlockEntity.IGNORED_BEE_TAGS;
         Objects.requireNonNull(var3);
         var10000.forEach(var3::remove);
         Entity var4 = EntityType.loadEntityRecursive(var3, var1, (var0) -> {
            return var0;
         });
         if (var4 != null && var4.getType().is(EntityTypeTags.BEEHIVE_INHABITORS)) {
            var4.setNoGravity(true);
            if (var4 instanceof Bee) {
               Bee var5 = (Bee)var4;
               var5.setHivePos(var2);
               setBeeReleaseData(this.ticksInHive, var5);
            }

            return var4;
         } else {
            return null;
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
      }

      public CustomData entityData() {
         return this.entityData;
      }

      public int ticksInHive() {
         return this.ticksInHive;
      }

      public int minTicksInHive() {
         return this.minTicksInHive;
      }

      static {
         LIST_CODEC = CODEC.listOf();
         STREAM_CODEC = StreamCodec.composite(CustomData.STREAM_CODEC, Occupant::entityData, ByteBufCodecs.VAR_INT, Occupant::ticksInHive, ByteBufCodecs.VAR_INT, Occupant::minTicksInHive, Occupant::new);
      }
   }

   static class BeeData {
      private final Occupant occupant;
      private int ticksInHive;

      BeeData(Occupant var1) {
         super();
         this.occupant = var1;
         this.ticksInHive = var1.ticksInHive();
      }

      public boolean tick() {
         return this.ticksInHive++ > this.occupant.minTicksInHive;
      }

      public Occupant toOccupant() {
         return new Occupant(this.occupant.entityData, this.ticksInHive, this.occupant.minTicksInHive);
      }

      public boolean hasNectar() {
         return this.occupant.entityData.getUnsafe().getBoolean("HasNectar");
      }
   }
}
