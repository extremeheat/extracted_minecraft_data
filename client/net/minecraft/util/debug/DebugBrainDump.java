package net.minecraft.util.debug;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringUtil;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.schedule.Activity;
import org.jspecify.annotations.Nullable;

public record DebugBrainDump(String name, String profession, int xp, float health, float maxHealth, String inventory, boolean wantsGolem, int angerLevel, List<String> activities, List<String> behaviors, List<String> memories, List<String> gossips, Set<BlockPos> pois, Set<BlockPos> potentialPois) {
   public static final StreamCodec<FriendlyByteBuf, DebugBrainDump> STREAM_CODEC = StreamCodec.<FriendlyByteBuf, DebugBrainDump>of((output, value) -> value.write(output), DebugBrainDump::new);

   public DebugBrainDump(final FriendlyByteBuf input) {
      this(input.readUtf(), input.readUtf(), input.readInt(), input.readFloat(), input.readFloat(), input.readUtf(), input.readBoolean(), input.readInt(), input.readList(FriendlyByteBuf::readUtf), input.readList(FriendlyByteBuf::readUtf), input.readList(FriendlyByteBuf::readUtf), input.readList(FriendlyByteBuf::readUtf), (Set)input.readCollection(HashSet::new, BlockPos.STREAM_CODEC), (Set)input.readCollection(HashSet::new, BlockPos.STREAM_CODEC));
   }

   public DebugBrainDump {
      super();
   }

   public void write(final FriendlyByteBuf output) {
      output.writeUtf(this.name);
      output.writeUtf(this.profession);
      output.writeInt(this.xp);
      output.writeFloat(this.health);
      output.writeFloat(this.maxHealth);
      output.writeUtf(this.inventory);
      output.writeBoolean(this.wantsGolem);
      output.writeInt(this.angerLevel);
      output.writeCollection(this.activities, FriendlyByteBuf::writeUtf);
      output.writeCollection(this.behaviors, FriendlyByteBuf::writeUtf);
      output.writeCollection(this.memories, FriendlyByteBuf::writeUtf);
      output.writeCollection(this.gossips, FriendlyByteBuf::writeUtf);
      output.writeCollection(this.pois, BlockPos.STREAM_CODEC);
      output.writeCollection(this.potentialPois, BlockPos.STREAM_CODEC);
   }

   public static DebugBrainDump takeBrainDump(final ServerLevel serverLevel, final LivingEntity entity) {
      String name = DebugEntityNameGenerator.getEntityName((Entity)entity);
      String profession;
      int xp;
      if (entity instanceof Villager villager) {
         profession = villager.getVillagerData().profession().getRegisteredName();
         xp = villager.getVillagerXp();
      } else {
         profession = "";
         xp = 0;
      }

      float health = entity.getHealth();
      float maxHealth = entity.getMaxHealth();
      Brain<?> brain = entity.getBrain();
      long gameTime = entity.level().getGameTime();
      String inventoryStr;
      if (entity instanceof InventoryCarrier inventoryCarrier) {
         Container inventory = inventoryCarrier.getInventory();
         inventoryStr = inventory.isEmpty() ? "" : inventory.toString();
      } else {
         inventoryStr = "";
      }

      boolean var10000;
      label36: {
         if (entity instanceof Villager villager) {
            if (villager.wantsToSpawnGolem(gameTime)) {
               var10000 = 1;
               break label36;
            }
         }

         var10000 = 0;
      }

      boolean wantsGolem = (boolean)var10000;
      if (entity instanceof Warden warden) {
         var10000 = warden.getClientAngerLevel();
      } else {
         var10000 = -1;
      }

      int angerLevel = var10000;
      List<String> activities = brain.getActiveActivities().stream().map(Activity::getName).toList();
      List<String> behaviors = brain.getRunningBehaviors().stream().map(BehaviorControl::debugString).toList();
      List<String> memories = getMemoryDescriptions(serverLevel, entity, gameTime).map((description) -> StringUtil.truncateStringIfNecessary(description, 255, true)).toList();
      Set<BlockPos> pois = getKnownBlockPositions(brain, MemoryModuleType.JOB_SITE, MemoryModuleType.HOME, MemoryModuleType.MEETING_POINT);
      Set<BlockPos> potentialPois = getKnownBlockPositions(brain, MemoryModuleType.POTENTIAL_JOB_SITE);
      List var26;
      if (entity instanceof Villager villager) {
         var26 = getVillagerGossips(villager);
      } else {
         var26 = List.of();
      }

      List<String> gossips = var26;
      return new DebugBrainDump(name, profession, xp, health, maxHealth, inventoryStr, wantsGolem, angerLevel, activities, behaviors, memories, gossips, pois, potentialPois);
   }

   @SafeVarargs
   private static Set<BlockPos> getKnownBlockPositions(final Brain<?> brain, final MemoryModuleType<GlobalPos>... memories) {
      Stream var10000 = Stream.of(memories);
      Objects.requireNonNull(brain);
      var10000 = var10000.filter(brain::hasMemoryValue);
      Objects.requireNonNull(brain);
      return (Set)var10000.map(brain::getMemory).flatMap(Optional::stream).map(GlobalPos::pos).collect(Collectors.toSet());
   }

   private static List<String> getVillagerGossips(final Villager villager) {
      List<String> gossips = new ArrayList();
      villager.getGossips().getGossipEntries().forEach((uuid, entries) -> {
         String gossipeeName = DebugEntityNameGenerator.getEntityName(uuid);
         entries.forEach((gossipType, value) -> gossips.add(gossipeeName + ": " + String.valueOf(gossipType) + ": " + value));
      });
      return gossips;
   }

   private static Stream<String> getMemoryDescriptions(final ServerLevel level, final LivingEntity body, final long timestamp) {
      return body.getBrain().getMemories().entrySet().stream().map((entry) -> {
         MemoryModuleType<?> memoryType = (MemoryModuleType)entry.getKey();
         Optional<? extends ExpirableValue<?>> optionalExpirableValue = (Optional)entry.getValue();
         return getMemoryDescription(level, timestamp, memoryType, optionalExpirableValue);
      }).sorted();
   }

   private static String getMemoryDescription(final ServerLevel level, final long timestamp, final MemoryModuleType<?> memoryType, final Optional<? extends ExpirableValue<?>> maybeValue) {
      String description;
      if (maybeValue.isPresent()) {
         ExpirableValue<?> expirableValue = (ExpirableValue)maybeValue.get();
         Object value = expirableValue.getValue();
         if (memoryType == MemoryModuleType.HEARD_BELL_TIME) {
            long timeSince = timestamp - (Long)value;
            description = timeSince + " ticks ago";
         } else if (expirableValue.canExpire()) {
            String var10000 = getShortDescription(level, value);
            description = var10000 + " (ttl: " + expirableValue.getTimeToLive() + ")";
         } else {
            description = getShortDescription(level, value);
         }
      } else {
         description = "-";
      }

      String var10 = BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(memoryType).getPath();
      return var10 + ": " + description;
   }

   private static String getShortDescription(final ServerLevel level, final @Nullable Object obj) {
      byte var3 = 0;
      String var10000;
      //$FF: var3->value
      //0->java/util/UUID
      //1->net/minecraft/world/entity/Entity
      //2->net/minecraft/world/entity/ai/memory/WalkTarget
      //3->net/minecraft/world/entity/ai/behavior/EntityTracker
      //4->net/minecraft/core/GlobalPos
      //5->net/minecraft/world/entity/ai/behavior/BlockPosTracker
      //6->net/minecraft/world/damagesource/DamageSource
      //7->net/minecraft/world/entity/ai/memory/NearestVisibleLivingEntities
      //8->java/util/Collection
      switch (((Class)obj).typeSwitch<invokedynamic>(obj, var3)) {
         case -1:
            var10000 = "-";
            break;
         case 0:
            UUID uuid = (UUID)obj;
            var10000 = getShortDescription(level, level.getEntity(uuid));
            break;
         case 1:
            Entity entity = (Entity)obj;
            var10000 = DebugEntityNameGenerator.getEntityName(entity);
            break;
         case 2:
            WalkTarget walkTarget = (WalkTarget)obj;
            var10000 = getShortDescription(level, walkTarget.getTarget());
            break;
         case 3:
            EntityTracker entityTracker = (EntityTracker)obj;
            var10000 = getShortDescription(level, entityTracker.getEntity());
            break;
         case 4:
            GlobalPos globalPos = (GlobalPos)obj;
            var10000 = getShortDescription(level, globalPos.pos());
            break;
         case 5:
            BlockPosTracker tracker = (BlockPosTracker)obj;
            var10000 = getShortDescription(level, tracker.currentBlockPosition());
            break;
         case 6:
            DamageSource damageSource = (DamageSource)obj;
            Entity entity = damageSource.getEntity();
            var10000 = entity == null ? obj.toString() : getShortDescription(level, entity);
            break;
         case 7:
            NearestVisibleLivingEntities visibleEntities = (NearestVisibleLivingEntities)obj;
            var10000 = getShortDescription(level, visibleEntities.nearbyEntities());
            break;
         case 8:
            Collection<?> collection = (Collection)obj;
            var10000 = "[" + (String)collection.stream().map((element) -> getShortDescription(level, element)).collect(Collectors.joining(", ")) + "]";
            break;
         default:
            var10000 = obj.toString();
      }

      return var10000;
   }

   public boolean hasPoi(final BlockPos poiPos) {
      return this.pois.contains(poiPos);
   }

   public boolean hasPotentialPoi(final BlockPos poiPos) {
      return this.potentialPois.contains(poiPos);
   }
}
