package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.debug.DebugHiveInfo;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueSource;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityProcessor;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.Bees;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class BeehiveBlockEntity extends BlockEntity {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String TAG_FLOWER_POS = "flower_pos";
   private static final String BEES = "bees";
   private static final List<String> IGNORED_BEE_TAGS = Arrays.asList("Air", "drop_chances", "equipment", "Brain", "CanPickUpLoot", "DeathTime", "fall_distance", "FallFlying", "Fire", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "sleeping_pos", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "hive_pos", "Passengers", "leash", "UUID");
   public static final int MAX_OCCUPANTS = 3;
   private static final int MIN_TICKS_BEFORE_REENTERING_HIVE = 400;
   private static final int MIN_OCCUPATION_TICKS_NECTAR = 2400;
   public static final int MIN_OCCUPATION_TICKS_NECTARLESS = 600;
   private final List<BeeData> stored = Lists.newArrayList();
   private @Nullable BlockPos savedFlowerPos;

   public BeehiveBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityType.BEEHIVE, worldPosition, blockState);
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
         for(BlockPos pos : BlockPos.betweenClosed(this.worldPosition.offset(-1, -1, -1), this.worldPosition.offset(1, 1, 1))) {
            if (this.level.getBlockState(pos).getBlock() instanceof FireBlock) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isEmpty() {
      return this.stored.isEmpty();
   }

   public boolean isFull() {
      return this.stored.size() == 3;
   }

   public void emptyAllLivingFromHive(final @Nullable Player player, final BlockState state, final BeeReleaseStatus releaseReason) {
      List<Entity> releasedFromHive = this.releaseAllOccupants(state, releaseReason);
      if (player != null) {
         for(Entity released : releasedFromHive) {
            if (released instanceof Bee) {
               Bee bee = (Bee)released;
               if (player.position().distanceToSqr(released.position()) <= 16.0) {
                  if (!this.isSedated()) {
                     bee.setTarget(player);
                  } else {
                     bee.setStayOutOfHiveCountdown(400);
                  }
               }
            }
         }
      }

   }

   private List<Entity> releaseAllOccupants(final BlockState state, final BeeReleaseStatus releaseStatus) {
      List<Entity> spawned = Lists.newArrayList();
      this.stored.removeIf((occupantEntry) -> releaseOccupant(this.level, this.worldPosition, state, occupantEntry.toOccupant(), spawned, releaseStatus, this.savedFlowerPos));
      if (!spawned.isEmpty()) {
         super.setChanged();
      }

      return spawned;
   }

   @VisibleForDebug
   public int getOccupantCount() {
      return this.stored.size();
   }

   public static int getHoneyLevel(final BlockState blockState) {
      return (Integer)blockState.getValue(BeehiveBlock.HONEY_LEVEL);
   }

   @VisibleForDebug
   public boolean isSedated() {
      return CampfireBlock.isSmokeyPos(this.level, this.getBlockPos());
   }

   public void addOccupant(final Bee bee) {
      if (this.stored.size() < 3) {
         bee.stopRiding();
         bee.ejectPassengers();
         bee.dropLeash();
         this.storeBee(BeehiveBlockEntity.Occupant.of(bee));
         if (this.level != null) {
            if (bee.hasSavedFlowerPos() && (!this.hasSavedFlowerPos() || this.level.getRandom().nextBoolean())) {
               this.savedFlowerPos = bee.getSavedFlowerPos();
            }

            BlockPos blockPos = this.getBlockPos();
            this.level.playSound((Entity)null, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(bee, this.getBlockState()));
         }

         bee.discard();
         super.setChanged();
      }
   }

   public void storeBee(final Occupant occupant) {
      this.stored.add(new BeeData(occupant));
   }

   private static boolean releaseOccupant(final Level level, final BlockPos blockPos, final BlockState state, final Occupant beeData, final @Nullable List<Entity> spawned, final BeeReleaseStatus releaseStatus, final @Nullable BlockPos savedFlowerPos) {
      if ((Boolean)level.environmentAttributes().getValue(EnvironmentAttributes.BEES_STAY_IN_HIVE, blockPos) && releaseStatus != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
         return false;
      } else {
         Direction facing = (Direction)state.getValue(BeehiveBlock.FACING);
         BlockPos facingPos = blockPos.relative(facing);
         boolean frontBlocked = !level.getBlockState(facingPos).getCollisionShape(level, facingPos).isEmpty();
         if (frontBlocked && releaseStatus != BeehiveBlockEntity.BeeReleaseStatus.EMERGENCY) {
            return false;
         } else {
            Entity entity = beeData.createEntity(level, blockPos);
            if (entity != null) {
               if (entity instanceof Bee) {
                  Bee bee = (Bee)entity;
                  RandomSource random = level.getRandom();
                  if (savedFlowerPos != null && !bee.hasSavedFlowerPos() && random.nextFloat() < 0.9F) {
                     bee.setSavedFlowerPos(savedFlowerPos);
                  }

                  if (releaseStatus == BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED) {
                     bee.dropOffNectar();
                     if (state.is(BlockTags.BEEHIVES, (s) -> s.hasProperty(BeehiveBlock.HONEY_LEVEL))) {
                        int honeyLevel = getHoneyLevel(state);
                        if (honeyLevel < 5) {
                           int levelIncrease = random.nextInt(100) == 0 ? 2 : 1;
                           if (honeyLevel + levelIncrease > 5) {
                              --levelIncrease;
                           }

                           level.setBlockAndUpdate(blockPos, (BlockState)state.setValue(BeehiveBlock.HONEY_LEVEL, honeyLevel + levelIncrease));
                        }
                     }
                  }

                  if (spawned != null) {
                     spawned.add(bee);
                  }

                  float bbWidth = entity.getBbWidth();
                  double delta = frontBlocked ? 0.0 : 0.55 + (double)(bbWidth / 2.0F);
                  double spawnX = (double)blockPos.getX() + 0.5 + delta * (double)facing.getStepX();
                  double spawnY = (double)blockPos.getY() + 0.5 - (double)(entity.getBbHeight() / 2.0F);
                  double spawnZ = (double)blockPos.getZ() + 0.5 + delta * (double)facing.getStepZ();
                  entity.snapTo(spawnX, spawnY, spawnZ, entity.getYRot(), entity.getXRot());
               }

               level.playSound((Entity)null, (BlockPos)blockPos, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
               level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(entity, level.getBlockState(blockPos)));
               return level.addFreshEntity(entity);
            } else {
               return false;
            }
         }
      }
   }

   private boolean hasSavedFlowerPos() {
      return this.savedFlowerPos != null;
   }

   private static void tickOccupants(final Level level, final BlockPos pos, final BlockState state, final List<BeeData> stored, final @Nullable BlockPos savedFlowerPos) {
      boolean changed = false;
      Iterator<BeeData> iterator = stored.iterator();

      while(iterator.hasNext()) {
         BeeData data = (BeeData)iterator.next();
         if (data.tick()) {
            BeeReleaseStatus releaseStatus = data.hasNectar() ? BeehiveBlockEntity.BeeReleaseStatus.HONEY_DELIVERED : BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED;
            if (releaseOccupant(level, pos, state, data.toOccupant(), (List)null, releaseStatus, savedFlowerPos)) {
               changed = true;
               iterator.remove();
            }
         }
      }

      if (changed) {
         setChanged(level, pos, state);
      }

   }

   public static void serverTick(final Level level, final BlockPos blockPos, final BlockState state, final BeehiveBlockEntity entity) {
      tickOccupants(level, blockPos, state, entity.stored, entity.savedFlowerPos);
      if (!entity.stored.isEmpty() && level.getRandom().nextDouble() < 0.005) {
         double x = (double)blockPos.getX() + 0.5;
         double y = (double)blockPos.getY();
         double z = (double)blockPos.getZ() + 0.5;
         level.playSound((Entity)null, x, y, z, SoundEvents.BEEHIVE_WORK, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      this.stored.clear();
      ((List)input.read("bees", BeehiveBlockEntity.Occupant.LIST_CODEC).orElse(List.of())).forEach(this::storeBee);
      this.savedFlowerPos = (BlockPos)input.read("flower_pos", BlockPos.CODEC).orElse((Object)null);
   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      output.store("bees", BeehiveBlockEntity.Occupant.LIST_CODEC, this.getBees());
      output.storeNullable("flower_pos", BlockPos.CODEC, this.savedFlowerPos);
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      super.applyImplicitComponents(components);
      this.stored.clear();
      List<Occupant> bees = ((Bees)components.getOrDefault(DataComponents.BEES, Bees.EMPTY)).bees();
      bees.forEach(this::storeBee);
   }

   protected void collectImplicitComponents(final DataComponentMap.Builder components) {
      super.collectImplicitComponents(components);
      components.set(DataComponents.BEES, new Bees(this.getBees()));
   }

   public void removeComponentsFromTag(final ValueOutput output) {
      super.removeComponentsFromTag(output);
      output.discard("bees");
   }

   private List<Occupant> getBees() {
      return this.stored.stream().map(BeeData::toOccupant).toList();
   }

   public void registerDebugValues(final ServerLevel level, final DebugValueSource.Registration registration) {
      registration.register(DebugSubscriptions.BEE_HIVES, () -> DebugHiveInfo.pack(this));
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

   private static class BeeData {
      private final Occupant occupant;
      private int ticksInHive;

      private BeeData(final Occupant occupant) {
         super();
         this.occupant = occupant;
         this.ticksInHive = occupant.ticksInHive();
      }

      public boolean tick() {
         return this.ticksInHive++ > this.occupant.minTicksInHive;
      }

      public Occupant toOccupant() {
         return new Occupant(this.occupant.entityData, this.ticksInHive, this.occupant.minTicksInHive);
      }

      public boolean hasNectar() {
         return this.occupant.entityData.getUnsafe().getBooleanOr("HasNectar", false);
      }
   }

   public static record Occupant(TypedEntityData<EntityType<?>> entityData, int ticksInHive, int minTicksInHive) {
      public static final Codec<Occupant> CODEC = RecordCodecBuilder.create((i) -> i.group(TypedEntityData.codec(EntityType.CODEC).fieldOf("entity_data").forGetter(Occupant::entityData), Codec.INT.fieldOf("ticks_in_hive").forGetter(Occupant::ticksInHive), Codec.INT.fieldOf("min_ticks_in_hive").forGetter(Occupant::minTicksInHive)).apply(i, Occupant::new));
      public static final Codec<List<Occupant>> LIST_CODEC;
      public static final StreamCodec<RegistryFriendlyByteBuf, Occupant> STREAM_CODEC;

      public Occupant {
         super();
      }

      public static Occupant of(final Entity entity) {
         Occupant var5;
         try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), BeehiveBlockEntity.LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(reporter, entity.registryAccess());
            entity.save(output);
            List var10000 = BeehiveBlockEntity.IGNORED_BEE_TAGS;
            Objects.requireNonNull(output);
            var10000.forEach(output::discard);
            CompoundTag entityTag = output.buildResult();
            boolean hasNectar = entityTag.getBooleanOr("HasNectar", false);
            var5 = new Occupant(TypedEntityData.of(entity.getType(), entityTag), 0, hasNectar ? 2400 : 600);
         }

         return var5;
      }

      public static Occupant create(final int ticksInHive) {
         return new Occupant(TypedEntityData.of(EntityType.BEE, new CompoundTag()), ticksInHive, 600);
      }

      public @Nullable Entity createEntity(final Level level, final BlockPos hivePos) {
         CompoundTag entityTag = this.entityData.copyTagWithoutId();
         List var10000 = BeehiveBlockEntity.IGNORED_BEE_TAGS;
         Objects.requireNonNull(entityTag);
         var10000.forEach(entityTag::remove);
         Entity entity = EntityType.loadEntityRecursive(this.entityData.type(), entityTag, level, EntitySpawnReason.LOAD, EntityProcessor.NOP);
         if (entity != null && entity.is(EntityTypeTags.BEEHIVE_INHABITORS)) {
            entity.setNoGravity(true);
            if (entity instanceof Bee) {
               Bee bee = (Bee)entity;
               bee.setHivePos(hivePos);
               setBeeReleaseData(this.ticksInHive, bee);
            }

            return entity;
         } else {
            return null;
         }
      }

      private static void setBeeReleaseData(final int ticksInHive, final Bee bee) {
         updateBeeAge(ticksInHive, bee);
         bee.setInLoveTime(Math.max(0, bee.getInLoveTime() - ticksInHive));
      }

      private static void updateBeeAge(final int ticksInHive, final Bee bee) {
         if (!bee.isAgeLocked()) {
            int age = bee.getAge();
            if (age < 0) {
               bee.setAge(Math.min(0, age + ticksInHive));
            } else if (age > 0) {
               bee.setAge(Math.max(0, age - ticksInHive));
            }

         }
      }

      static {
         LIST_CODEC = CODEC.listOf();
         STREAM_CODEC = StreamCodec.composite(TypedEntityData.streamCodec(EntityType.STREAM_CODEC), Occupant::entityData, ByteBufCodecs.VAR_INT, Occupant::ticksInHive, ByteBufCodecs.VAR_INT, Occupant::minTicksInHive, Occupant::new);
      }
   }
}
