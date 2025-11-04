package net.minecraft.world.level.gamerules;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import org.jspecify.annotations.Nullable;

public class GameRules {
   public static final GameRule<Boolean> ADVANCE_TIME;
   public static final GameRule<Boolean> ADVANCE_WEATHER;
   public static final GameRule<Boolean> ALLOW_ENTERING_NETHER_USING_PORTALS;
   public static final GameRule<Boolean> BLOCK_DROPS;
   public static final GameRule<Boolean> BLOCK_EXPLOSION_DROP_DECAY;
   public static final GameRule<Boolean> COMMAND_BLOCKS_WORK;
   public static final GameRule<Boolean> COMMAND_BLOCK_OUTPUT;
   public static final GameRule<Boolean> DROWNING_DAMAGE;
   public static final GameRule<Boolean> ELYTRA_MOVEMENT_CHECK;
   public static final GameRule<Boolean> ENDER_PEARLS_VANISH_ON_DEATH;
   public static final GameRule<Boolean> ENTITY_DROPS;
   public static final GameRule<Boolean> FALL_DAMAGE;
   public static final GameRule<Boolean> FIRE_DAMAGE;
   public static final GameRule<Integer> FIRE_SPREAD_RADIUS_AROUND_PLAYER;
   public static final GameRule<Boolean> FORGIVE_DEAD_PLAYERS;
   public static final GameRule<Boolean> FREEZE_DAMAGE;
   public static final GameRule<Boolean> GLOBAL_SOUND_EVENTS;
   public static final GameRule<Boolean> IMMEDIATE_RESPAWN;
   public static final GameRule<Boolean> KEEP_INVENTORY;
   public static final GameRule<Boolean> LAVA_SOURCE_CONVERSION;
   public static final GameRule<Boolean> LIMITED_CRAFTING;
   public static final GameRule<Boolean> LOCATOR_BAR;
   public static final GameRule<Boolean> LOG_ADMIN_COMMANDS;
   public static final GameRule<Integer> MAX_BLOCK_MODIFICATIONS;
   public static final GameRule<Integer> MAX_COMMAND_FORKS;
   public static final GameRule<Integer> MAX_COMMAND_SEQUENCE_LENGTH;
   public static final GameRule<Integer> MAX_ENTITY_CRAMMING;
   public static final GameRule<Integer> MAX_MINECART_SPEED;
   public static final GameRule<Integer> MAX_SNOW_ACCUMULATION_HEIGHT;
   public static final GameRule<Boolean> MOB_DROPS;
   public static final GameRule<Boolean> MOB_EXPLOSION_DROP_DECAY;
   public static final GameRule<Boolean> MOB_GRIEFING;
   public static final GameRule<Boolean> NATURAL_HEALTH_REGENERATION;
   public static final GameRule<Boolean> PLAYER_MOVEMENT_CHECK;
   public static final GameRule<Integer> PLAYERS_NETHER_PORTAL_CREATIVE_DELAY;
   public static final GameRule<Integer> PLAYERS_NETHER_PORTAL_DEFAULT_DELAY;
   public static final GameRule<Integer> PLAYERS_SLEEPING_PERCENTAGE;
   public static final GameRule<Boolean> PROJECTILES_CAN_BREAK_BLOCKS;
   public static final GameRule<Boolean> PVP;
   public static final GameRule<Boolean> RAIDS;
   public static final GameRule<Integer> RANDOM_TICK_SPEED;
   public static final GameRule<Boolean> REDUCED_DEBUG_INFO;
   public static final GameRule<Integer> RESPAWN_RADIUS;
   public static final GameRule<Boolean> SEND_COMMAND_FEEDBACK;
   public static final GameRule<Boolean> SHOW_ADVANCEMENT_MESSAGES;
   public static final GameRule<Boolean> SHOW_DEATH_MESSAGES;
   public static final GameRule<Boolean> SPAWNER_BLOCKS_WORK;
   public static final GameRule<Boolean> SPAWN_MOBS;
   public static final GameRule<Boolean> SPAWN_MONSTERS;
   public static final GameRule<Boolean> SPAWN_PATROLS;
   public static final GameRule<Boolean> SPAWN_PHANTOMS;
   public static final GameRule<Boolean> SPAWN_WANDERING_TRADERS;
   public static final GameRule<Boolean> SPAWN_WARDENS;
   public static final GameRule<Boolean> SPECTATORS_GENERATE_CHUNKS;
   public static final GameRule<Boolean> SPREAD_VINES;
   public static final GameRule<Boolean> TNT_EXPLODES;
   public static final GameRule<Boolean> TNT_EXPLOSION_DROP_DECAY;
   public static final GameRule<Boolean> UNIVERSAL_ANGER;
   public static final GameRule<Boolean> WATER_SOURCE_CONVERSION;
   private final GameRuleMap rules;

   public static Codec<GameRules> codec(FeatureFlagSet var0) {
      return GameRuleMap.CODEC.xmap((var1) -> new GameRules(var0, var1), (var0x) -> var0x.rules);
   }

   public GameRules(FeatureFlagSet var1, GameRuleMap var2) {
      this(var1);
      GameRuleMap var10000 = this.rules;
      GameRuleMap var10002 = this.rules;
      Objects.requireNonNull(var10002);
      var10000.setFromIf(var2, var10002::has);
   }

   public GameRules(FeatureFlagSet var1) {
      super();
      this.rules = GameRuleMap.of(BuiltInRegistries.GAME_RULE.stream().filter((var1x) -> var1x.requiredFeatures().isSubsetOf(var1)));
   }

   public Stream<GameRule<?>> availableRules() {
      return this.rules.keySet().stream();
   }

   public <T> T get(GameRule<T> var1) {
      Object var2 = this.rules.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Tried to access invalid game rule");
      } else {
         return (T)var2;
      }
   }

   public <T> void set(GameRule<T> var1, T var2, @Nullable MinecraftServer var3) {
      if (!this.rules.has(var1)) {
         throw new IllegalArgumentException("Tried to set invalid game rule");
      } else {
         this.rules.set(var1, var2);
         if (var3 != null) {
            var3.onGameRuleChanged(var1, var2);
         }

      }
   }

   public GameRules copy(FeatureFlagSet var1) {
      return new GameRules(var1, this.rules);
   }

   public void setAll(GameRules var1, @Nullable MinecraftServer var2) {
      this.setAll(var1.rules, var2);
   }

   public void setAll(GameRuleMap var1, @Nullable MinecraftServer var2) {
      var1.keySet().forEach((var3) -> this.setFromOther(var1, var3, var2));
   }

   private <T> void setFromOther(GameRuleMap var1, GameRule<T> var2, @Nullable MinecraftServer var3) {
      this.set(var2, Objects.requireNonNull(var1.get(var2)), var3);
   }

   public void visitGameRuleTypes(GameRuleTypeVisitor var1) {
      this.rules.keySet().forEach((var1x) -> {
         var1.visit(var1x);
         var1x.callVisitor(var1);
      });
   }

   private static GameRule<Boolean> registerBoolean(String var0, GameRuleCategory var1, boolean var2) {
      return register(var0, var1, GameRuleType.BOOL, BoolArgumentType.bool(), Codec.BOOL, var2, FeatureFlagSet.of(), GameRuleTypeVisitor::visitBoolean, (var0x) -> var0x ? 1 : 0);
   }

   private static GameRule<Integer> registerInteger(String var0, GameRuleCategory var1, int var2, int var3) {
      return registerInteger(var0, var1, var2, var3, 2147483647, FeatureFlagSet.of());
   }

   private static GameRule<Integer> registerInteger(String var0, GameRuleCategory var1, int var2, int var3, int var4) {
      return registerInteger(var0, var1, var2, var3, var4, FeatureFlagSet.of());
   }

   private static GameRule<Integer> registerInteger(String var0, GameRuleCategory var1, int var2, int var3, int var4, FeatureFlagSet var5) {
      return register(var0, var1, GameRuleType.INT, IntegerArgumentType.integer(var3, var4), Codec.intRange(var3, var4), var2, var5, GameRuleTypeVisitor::visitInteger, (var0x) -> var0x);
   }

   private static <T> GameRule<T> register(String var0, GameRuleCategory var1, GameRuleType var2, ArgumentType<T> var3, Codec<T> var4, T var5, FeatureFlagSet var6, VisitorCaller<T> var7, ToIntFunction<T> var8) {
      return (GameRule)Registry.register(BuiltInRegistries.GAME_RULE, (String)var0, new GameRule(var1, var2, var3, var7, var4, var8, var5, var6));
   }

   public static GameRule<?> bootstrap(Registry<GameRule<?>> var0) {
      return ADVANCE_TIME;
   }

   public <T> String getAsString(GameRule<T> var1) {
      return var1.serialize(this.get(var1));
   }

   static {
      ADVANCE_TIME = registerBoolean("advance_time", GameRuleCategory.UPDATES, !SharedConstants.DEBUG_WORLD_RECREATE);
      ADVANCE_WEATHER = registerBoolean("advance_weather", GameRuleCategory.UPDATES, !SharedConstants.DEBUG_WORLD_RECREATE);
      ALLOW_ENTERING_NETHER_USING_PORTALS = registerBoolean("allow_entering_nether_using_portals", GameRuleCategory.MISC, true);
      BLOCK_DROPS = registerBoolean("block_drops", GameRuleCategory.DROPS, true);
      BLOCK_EXPLOSION_DROP_DECAY = registerBoolean("block_explosion_drop_decay", GameRuleCategory.DROPS, true);
      COMMAND_BLOCKS_WORK = registerBoolean("command_blocks_work", GameRuleCategory.MISC, true);
      COMMAND_BLOCK_OUTPUT = registerBoolean("command_block_output", GameRuleCategory.CHAT, true);
      DROWNING_DAMAGE = registerBoolean("drowning_damage", GameRuleCategory.PLAYER, true);
      ELYTRA_MOVEMENT_CHECK = registerBoolean("elytra_movement_check", GameRuleCategory.PLAYER, true);
      ENDER_PEARLS_VANISH_ON_DEATH = registerBoolean("ender_pearls_vanish_on_death", GameRuleCategory.PLAYER, true);
      ENTITY_DROPS = registerBoolean("entity_drops", GameRuleCategory.DROPS, true);
      FALL_DAMAGE = registerBoolean("fall_damage", GameRuleCategory.PLAYER, true);
      FIRE_DAMAGE = registerBoolean("fire_damage", GameRuleCategory.PLAYER, true);
      FIRE_SPREAD_RADIUS_AROUND_PLAYER = registerInteger("fire_spread_radius_around_player", GameRuleCategory.UPDATES, 128, -1);
      FORGIVE_DEAD_PLAYERS = registerBoolean("forgive_dead_players", GameRuleCategory.MOBS, true);
      FREEZE_DAMAGE = registerBoolean("freeze_damage", GameRuleCategory.PLAYER, true);
      GLOBAL_SOUND_EVENTS = registerBoolean("global_sound_events", GameRuleCategory.MISC, true);
      IMMEDIATE_RESPAWN = registerBoolean("immediate_respawn", GameRuleCategory.PLAYER, false);
      KEEP_INVENTORY = registerBoolean("keep_inventory", GameRuleCategory.PLAYER, false);
      LAVA_SOURCE_CONVERSION = registerBoolean("lava_source_conversion", GameRuleCategory.UPDATES, false);
      LIMITED_CRAFTING = registerBoolean("limited_crafting", GameRuleCategory.PLAYER, false);
      LOCATOR_BAR = registerBoolean("locator_bar", GameRuleCategory.PLAYER, true);
      LOG_ADMIN_COMMANDS = registerBoolean("log_admin_commands", GameRuleCategory.CHAT, true);
      MAX_BLOCK_MODIFICATIONS = registerInteger("max_block_modifications", GameRuleCategory.MISC, 32768, 1);
      MAX_COMMAND_FORKS = registerInteger("max_command_forks", GameRuleCategory.MISC, 65536, 0);
      MAX_COMMAND_SEQUENCE_LENGTH = registerInteger("max_command_sequence_length", GameRuleCategory.MISC, 65536, 0);
      MAX_ENTITY_CRAMMING = registerInteger("max_entity_cramming", GameRuleCategory.MOBS, 24, 0);
      MAX_MINECART_SPEED = registerInteger("max_minecart_speed", GameRuleCategory.MISC, 8, 1, 1000, FeatureFlagSet.of(FeatureFlags.MINECART_IMPROVEMENTS));
      MAX_SNOW_ACCUMULATION_HEIGHT = registerInteger("max_snow_accumulation_height", GameRuleCategory.UPDATES, 1, 0, 8);
      MOB_DROPS = registerBoolean("mob_drops", GameRuleCategory.DROPS, true);
      MOB_EXPLOSION_DROP_DECAY = registerBoolean("mob_explosion_drop_decay", GameRuleCategory.DROPS, true);
      MOB_GRIEFING = registerBoolean("mob_griefing", GameRuleCategory.MOBS, true);
      NATURAL_HEALTH_REGENERATION = registerBoolean("natural_health_regeneration", GameRuleCategory.PLAYER, true);
      PLAYER_MOVEMENT_CHECK = registerBoolean("player_movement_check", GameRuleCategory.PLAYER, true);
      PLAYERS_NETHER_PORTAL_CREATIVE_DELAY = registerInteger("players_nether_portal_creative_delay", GameRuleCategory.PLAYER, 0, 0);
      PLAYERS_NETHER_PORTAL_DEFAULT_DELAY = registerInteger("players_nether_portal_default_delay", GameRuleCategory.PLAYER, 80, 0);
      PLAYERS_SLEEPING_PERCENTAGE = registerInteger("players_sleeping_percentage", GameRuleCategory.PLAYER, 100, 0);
      PROJECTILES_CAN_BREAK_BLOCKS = registerBoolean("projectiles_can_break_blocks", GameRuleCategory.DROPS, true);
      PVP = registerBoolean("pvp", GameRuleCategory.PLAYER, true);
      RAIDS = registerBoolean("raids", GameRuleCategory.MOBS, true);
      RANDOM_TICK_SPEED = registerInteger("random_tick_speed", GameRuleCategory.UPDATES, 3, 0);
      REDUCED_DEBUG_INFO = registerBoolean("reduced_debug_info", GameRuleCategory.MISC, false);
      RESPAWN_RADIUS = registerInteger("respawn_radius", GameRuleCategory.PLAYER, 10, 0);
      SEND_COMMAND_FEEDBACK = registerBoolean("send_command_feedback", GameRuleCategory.CHAT, true);
      SHOW_ADVANCEMENT_MESSAGES = registerBoolean("show_advancement_messages", GameRuleCategory.CHAT, true);
      SHOW_DEATH_MESSAGES = registerBoolean("show_death_messages", GameRuleCategory.CHAT, true);
      SPAWNER_BLOCKS_WORK = registerBoolean("spawner_blocks_work", GameRuleCategory.MISC, true);
      SPAWN_MOBS = registerBoolean("spawn_mobs", GameRuleCategory.SPAWNING, true);
      SPAWN_MONSTERS = registerBoolean("spawn_monsters", GameRuleCategory.SPAWNING, true);
      SPAWN_PATROLS = registerBoolean("spawn_patrols", GameRuleCategory.SPAWNING, true);
      SPAWN_PHANTOMS = registerBoolean("spawn_phantoms", GameRuleCategory.SPAWNING, true);
      SPAWN_WANDERING_TRADERS = registerBoolean("spawn_wandering_traders", GameRuleCategory.SPAWNING, true);
      SPAWN_WARDENS = registerBoolean("spawn_wardens", GameRuleCategory.SPAWNING, true);
      SPECTATORS_GENERATE_CHUNKS = registerBoolean("spectators_generate_chunks", GameRuleCategory.PLAYER, true);
      SPREAD_VINES = registerBoolean("spread_vines", GameRuleCategory.UPDATES, true);
      TNT_EXPLODES = registerBoolean("tnt_explodes", GameRuleCategory.MISC, true);
      TNT_EXPLOSION_DROP_DECAY = registerBoolean("tnt_explosion_drop_decay", GameRuleCategory.DROPS, false);
      UNIVERSAL_ANGER = registerBoolean("universal_anger", GameRuleCategory.MOBS, false);
      WATER_SOURCE_CONVERSION = registerBoolean("water_source_conversion", GameRuleCategory.UPDATES, true);
   }

   public interface VisitorCaller<T> {
      void call(GameRuleTypeVisitor var1, GameRule<T> var2);
   }
}
