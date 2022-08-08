package net.minecraft.world.entity.ai.memory;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MemoryModuleType<U> {
   public static final MemoryModuleType<Void> DUMMY = register("dummy");
   public static final MemoryModuleType<GlobalPos> HOME;
   public static final MemoryModuleType<GlobalPos> JOB_SITE;
   public static final MemoryModuleType<GlobalPos> POTENTIAL_JOB_SITE;
   public static final MemoryModuleType<GlobalPos> MEETING_POINT;
   public static final MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE;
   public static final MemoryModuleType<List<LivingEntity>> NEAREST_LIVING_ENTITIES;
   public static final MemoryModuleType<NearestVisibleLivingEntities> NEAREST_VISIBLE_LIVING_ENTITIES;
   public static final MemoryModuleType<List<LivingEntity>> VISIBLE_VILLAGER_BABIES;
   public static final MemoryModuleType<List<Player>> NEAREST_PLAYERS;
   public static final MemoryModuleType<Player> NEAREST_VISIBLE_PLAYER;
   public static final MemoryModuleType<Player> NEAREST_VISIBLE_ATTACKABLE_PLAYER;
   public static final MemoryModuleType<WalkTarget> WALK_TARGET;
   public static final MemoryModuleType<PositionTracker> LOOK_TARGET;
   public static final MemoryModuleType<LivingEntity> ATTACK_TARGET;
   public static final MemoryModuleType<Boolean> ATTACK_COOLING_DOWN;
   public static final MemoryModuleType<LivingEntity> INTERACTION_TARGET;
   public static final MemoryModuleType<AgeableMob> BREED_TARGET;
   public static final MemoryModuleType<Entity> RIDE_TARGET;
   public static final MemoryModuleType<Path> PATH;
   public static final MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS;
   public static final MemoryModuleType<Set<GlobalPos>> DOORS_TO_CLOSE;
   public static final MemoryModuleType<BlockPos> NEAREST_BED;
   public static final MemoryModuleType<DamageSource> HURT_BY;
   public static final MemoryModuleType<LivingEntity> HURT_BY_ENTITY;
   public static final MemoryModuleType<LivingEntity> AVOID_TARGET;
   public static final MemoryModuleType<LivingEntity> NEAREST_HOSTILE;
   public static final MemoryModuleType<LivingEntity> NEAREST_ATTACKABLE;
   public static final MemoryModuleType<GlobalPos> HIDING_PLACE;
   public static final MemoryModuleType<Long> HEARD_BELL_TIME;
   public static final MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE;
   public static final MemoryModuleType<Boolean> GOLEM_DETECTED_RECENTLY;
   public static final MemoryModuleType<Long> LAST_SLEPT;
   public static final MemoryModuleType<Long> LAST_WOKEN;
   public static final MemoryModuleType<Long> LAST_WORKED_AT_POI;
   public static final MemoryModuleType<AgeableMob> NEAREST_VISIBLE_ADULT;
   public static final MemoryModuleType<ItemEntity> NEAREST_VISIBLE_WANTED_ITEM;
   public static final MemoryModuleType<Mob> NEAREST_VISIBLE_NEMESIS;
   public static final MemoryModuleType<Integer> PLAY_DEAD_TICKS;
   public static final MemoryModuleType<Player> TEMPTING_PLAYER;
   public static final MemoryModuleType<Integer> TEMPTATION_COOLDOWN_TICKS;
   public static final MemoryModuleType<Boolean> IS_TEMPTED;
   public static final MemoryModuleType<Integer> LONG_JUMP_COOLDOWN_TICKS;
   public static final MemoryModuleType<Boolean> LONG_JUMP_MID_JUMP;
   public static final MemoryModuleType<Boolean> HAS_HUNTING_COOLDOWN;
   public static final MemoryModuleType<Integer> RAM_COOLDOWN_TICKS;
   public static final MemoryModuleType<Vec3> RAM_TARGET;
   public static final MemoryModuleType<Unit> IS_IN_WATER;
   public static final MemoryModuleType<Unit> IS_PREGNANT;
   public static final MemoryModuleType<Boolean> IS_PANICKING;
   public static final MemoryModuleType<List<UUID>> UNREACHABLE_TONGUE_TARGETS;
   public static final MemoryModuleType<UUID> ANGRY_AT;
   public static final MemoryModuleType<Boolean> UNIVERSAL_ANGER;
   public static final MemoryModuleType<Boolean> ADMIRING_ITEM;
   public static final MemoryModuleType<Integer> TIME_TRYING_TO_REACH_ADMIRE_ITEM;
   public static final MemoryModuleType<Boolean> DISABLE_WALK_TO_ADMIRE_ITEM;
   public static final MemoryModuleType<Boolean> ADMIRING_DISABLED;
   public static final MemoryModuleType<Boolean> HUNTED_RECENTLY;
   public static final MemoryModuleType<BlockPos> CELEBRATE_LOCATION;
   public static final MemoryModuleType<Boolean> DANCING;
   public static final MemoryModuleType<Hoglin> NEAREST_VISIBLE_HUNTABLE_HOGLIN;
   public static final MemoryModuleType<Hoglin> NEAREST_VISIBLE_BABY_HOGLIN;
   public static final MemoryModuleType<Player> NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD;
   public static final MemoryModuleType<List<AbstractPiglin>> NEARBY_ADULT_PIGLINS;
   public static final MemoryModuleType<List<AbstractPiglin>> NEAREST_VISIBLE_ADULT_PIGLINS;
   public static final MemoryModuleType<List<Hoglin>> NEAREST_VISIBLE_ADULT_HOGLINS;
   public static final MemoryModuleType<AbstractPiglin> NEAREST_VISIBLE_ADULT_PIGLIN;
   public static final MemoryModuleType<LivingEntity> NEAREST_VISIBLE_ZOMBIFIED;
   public static final MemoryModuleType<Integer> VISIBLE_ADULT_PIGLIN_COUNT;
   public static final MemoryModuleType<Integer> VISIBLE_ADULT_HOGLIN_COUNT;
   public static final MemoryModuleType<Player> NEAREST_PLAYER_HOLDING_WANTED_ITEM;
   public static final MemoryModuleType<Boolean> ATE_RECENTLY;
   public static final MemoryModuleType<BlockPos> NEAREST_REPELLENT;
   public static final MemoryModuleType<Boolean> PACIFIED;
   public static final MemoryModuleType<LivingEntity> ROAR_TARGET;
   public static final MemoryModuleType<BlockPos> DISTURBANCE_LOCATION;
   public static final MemoryModuleType<Unit> RECENT_PROJECTILE;
   public static final MemoryModuleType<Unit> IS_SNIFFING;
   public static final MemoryModuleType<Unit> IS_EMERGING;
   public static final MemoryModuleType<Unit> ROAR_SOUND_DELAY;
   public static final MemoryModuleType<Unit> DIG_COOLDOWN;
   public static final MemoryModuleType<Unit> ROAR_SOUND_COOLDOWN;
   public static final MemoryModuleType<Unit> SNIFF_COOLDOWN;
   public static final MemoryModuleType<Unit> TOUCH_COOLDOWN;
   public static final MemoryModuleType<Unit> VIBRATION_COOLDOWN;
   public static final MemoryModuleType<Unit> SONIC_BOOM_COOLDOWN;
   public static final MemoryModuleType<Unit> SONIC_BOOM_SOUND_COOLDOWN;
   public static final MemoryModuleType<Unit> SONIC_BOOM_SOUND_DELAY;
   public static final MemoryModuleType<UUID> LIKED_PLAYER;
   public static final MemoryModuleType<GlobalPos> LIKED_NOTEBLOCK_POSITION;
   public static final MemoryModuleType<Integer> LIKED_NOTEBLOCK_COOLDOWN_TICKS;
   public static final MemoryModuleType<Integer> ITEM_PICKUP_COOLDOWN_TICKS;
   private final Optional<Codec<ExpirableValue<U>>> codec;

   @VisibleForTesting
   public MemoryModuleType(Optional<Codec<U>> var1) {
      super();
      this.codec = var1.map(ExpirableValue::codec);
   }

   public String toString() {
      return Registry.MEMORY_MODULE_TYPE.getKey(this).toString();
   }

   public Optional<Codec<ExpirableValue<U>>> getCodec() {
      return this.codec;
   }

   private static <U> MemoryModuleType<U> register(String var0, Codec<U> var1) {
      return (MemoryModuleType)Registry.register(Registry.MEMORY_MODULE_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new MemoryModuleType(Optional.of(var1)));
   }

   private static <U> MemoryModuleType<U> register(String var0) {
      return (MemoryModuleType)Registry.register(Registry.MEMORY_MODULE_TYPE, (ResourceLocation)(new ResourceLocation(var0)), new MemoryModuleType(Optional.empty()));
   }

   static {
      HOME = register("home", GlobalPos.CODEC);
      JOB_SITE = register("job_site", GlobalPos.CODEC);
      POTENTIAL_JOB_SITE = register("potential_job_site", GlobalPos.CODEC);
      MEETING_POINT = register("meeting_point", GlobalPos.CODEC);
      SECONDARY_JOB_SITE = register("secondary_job_site");
      NEAREST_LIVING_ENTITIES = register("mobs");
      NEAREST_VISIBLE_LIVING_ENTITIES = register("visible_mobs");
      VISIBLE_VILLAGER_BABIES = register("visible_villager_babies");
      NEAREST_PLAYERS = register("nearest_players");
      NEAREST_VISIBLE_PLAYER = register("nearest_visible_player");
      NEAREST_VISIBLE_ATTACKABLE_PLAYER = register("nearest_visible_targetable_player");
      WALK_TARGET = register("walk_target");
      LOOK_TARGET = register("look_target");
      ATTACK_TARGET = register("attack_target");
      ATTACK_COOLING_DOWN = register("attack_cooling_down");
      INTERACTION_TARGET = register("interaction_target");
      BREED_TARGET = register("breed_target");
      RIDE_TARGET = register("ride_target");
      PATH = register("path");
      INTERACTABLE_DOORS = register("interactable_doors");
      DOORS_TO_CLOSE = register("doors_to_close");
      NEAREST_BED = register("nearest_bed");
      HURT_BY = register("hurt_by");
      HURT_BY_ENTITY = register("hurt_by_entity");
      AVOID_TARGET = register("avoid_target");
      NEAREST_HOSTILE = register("nearest_hostile");
      NEAREST_ATTACKABLE = register("nearest_attackable");
      HIDING_PLACE = register("hiding_place");
      HEARD_BELL_TIME = register("heard_bell_time");
      CANT_REACH_WALK_TARGET_SINCE = register("cant_reach_walk_target_since");
      GOLEM_DETECTED_RECENTLY = register("golem_detected_recently", Codec.BOOL);
      LAST_SLEPT = register("last_slept", Codec.LONG);
      LAST_WOKEN = register("last_woken", Codec.LONG);
      LAST_WORKED_AT_POI = register("last_worked_at_poi", Codec.LONG);
      NEAREST_VISIBLE_ADULT = register("nearest_visible_adult");
      NEAREST_VISIBLE_WANTED_ITEM = register("nearest_visible_wanted_item");
      NEAREST_VISIBLE_NEMESIS = register("nearest_visible_nemesis");
      PLAY_DEAD_TICKS = register("play_dead_ticks", Codec.INT);
      TEMPTING_PLAYER = register("tempting_player");
      TEMPTATION_COOLDOWN_TICKS = register("temptation_cooldown_ticks", Codec.INT);
      IS_TEMPTED = register("is_tempted", Codec.BOOL);
      LONG_JUMP_COOLDOWN_TICKS = register("long_jump_cooling_down", Codec.INT);
      LONG_JUMP_MID_JUMP = register("long_jump_mid_jump");
      HAS_HUNTING_COOLDOWN = register("has_hunting_cooldown", Codec.BOOL);
      RAM_COOLDOWN_TICKS = register("ram_cooldown_ticks", Codec.INT);
      RAM_TARGET = register("ram_target");
      IS_IN_WATER = register("is_in_water", Codec.unit(Unit.INSTANCE));
      IS_PREGNANT = register("is_pregnant", Codec.unit(Unit.INSTANCE));
      IS_PANICKING = register("is_panicking", Codec.BOOL);
      UNREACHABLE_TONGUE_TARGETS = register("unreachable_tongue_targets");
      ANGRY_AT = register("angry_at", UUIDUtil.CODEC);
      UNIVERSAL_ANGER = register("universal_anger", Codec.BOOL);
      ADMIRING_ITEM = register("admiring_item", Codec.BOOL);
      TIME_TRYING_TO_REACH_ADMIRE_ITEM = register("time_trying_to_reach_admire_item");
      DISABLE_WALK_TO_ADMIRE_ITEM = register("disable_walk_to_admire_item");
      ADMIRING_DISABLED = register("admiring_disabled", Codec.BOOL);
      HUNTED_RECENTLY = register("hunted_recently", Codec.BOOL);
      CELEBRATE_LOCATION = register("celebrate_location");
      DANCING = register("dancing");
      NEAREST_VISIBLE_HUNTABLE_HOGLIN = register("nearest_visible_huntable_hoglin");
      NEAREST_VISIBLE_BABY_HOGLIN = register("nearest_visible_baby_hoglin");
      NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD = register("nearest_targetable_player_not_wearing_gold");
      NEARBY_ADULT_PIGLINS = register("nearby_adult_piglins");
      NEAREST_VISIBLE_ADULT_PIGLINS = register("nearest_visible_adult_piglins");
      NEAREST_VISIBLE_ADULT_HOGLINS = register("nearest_visible_adult_hoglins");
      NEAREST_VISIBLE_ADULT_PIGLIN = register("nearest_visible_adult_piglin");
      NEAREST_VISIBLE_ZOMBIFIED = register("nearest_visible_zombified");
      VISIBLE_ADULT_PIGLIN_COUNT = register("visible_adult_piglin_count");
      VISIBLE_ADULT_HOGLIN_COUNT = register("visible_adult_hoglin_count");
      NEAREST_PLAYER_HOLDING_WANTED_ITEM = register("nearest_player_holding_wanted_item");
      ATE_RECENTLY = register("ate_recently");
      NEAREST_REPELLENT = register("nearest_repellent");
      PACIFIED = register("pacified");
      ROAR_TARGET = register("roar_target");
      DISTURBANCE_LOCATION = register("disturbance_location");
      RECENT_PROJECTILE = register("recent_projectile", Codec.unit(Unit.INSTANCE));
      IS_SNIFFING = register("is_sniffing", Codec.unit(Unit.INSTANCE));
      IS_EMERGING = register("is_emerging", Codec.unit(Unit.INSTANCE));
      ROAR_SOUND_DELAY = register("roar_sound_delay", Codec.unit(Unit.INSTANCE));
      DIG_COOLDOWN = register("dig_cooldown", Codec.unit(Unit.INSTANCE));
      ROAR_SOUND_COOLDOWN = register("roar_sound_cooldown", Codec.unit(Unit.INSTANCE));
      SNIFF_COOLDOWN = register("sniff_cooldown", Codec.unit(Unit.INSTANCE));
      TOUCH_COOLDOWN = register("touch_cooldown", Codec.unit(Unit.INSTANCE));
      VIBRATION_COOLDOWN = register("vibration_cooldown", Codec.unit(Unit.INSTANCE));
      SONIC_BOOM_COOLDOWN = register("sonic_boom_cooldown", Codec.unit(Unit.INSTANCE));
      SONIC_BOOM_SOUND_COOLDOWN = register("sonic_boom_sound_cooldown", Codec.unit(Unit.INSTANCE));
      SONIC_BOOM_SOUND_DELAY = register("sonic_boom_sound_delay", Codec.unit(Unit.INSTANCE));
      LIKED_PLAYER = register("liked_player", UUIDUtil.CODEC);
      LIKED_NOTEBLOCK_POSITION = register("liked_noteblock", GlobalPos.CODEC);
      LIKED_NOTEBLOCK_COOLDOWN_TICKS = register("liked_noteblock_cooldown_ticks", Codec.INT);
      ITEM_PICKUP_COOLDOWN_TICKS = register("item_pickup_cooldown_ticks", Codec.INT);
   }
}
