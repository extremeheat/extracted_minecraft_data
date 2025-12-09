package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.util.Mth;

public class GameRuleRegistryFix extends DataFix {
   public GameRuleRegistryFix(Schema var1) {
      super(var1, false);
   }

   protected TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("GameRuleRegistryFix", this.getInputSchema().getType(References.LEVEL), (var0) -> var0.update(DSL.remainderFinder(), (var0x) -> var0x.renameAndFixField("GameRules", "game_rules", (var0) -> {
               boolean var1 = Boolean.parseBoolean(var0.get("doFireTick").asString("true"));
               boolean var2 = Boolean.parseBoolean(var0.get("allowFireTicksAwayFromPlayer").asString("false"));
               short var3;
               if (!var1) {
                  var3 = 0;
               } else if (!var2) {
                  var3 = 128;
               } else {
                  var3 = -1;
               }

               if (var3 != 128) {
                  var0 = var0.set("minecraft:fire_spread_radius_around_player", var0.createInt(var3));
               }

               return var0.remove("spawnChunkRadius").remove("entitiesWithPassengersCanUsePortals").remove("doFireTick").remove("allowFireTicksAwayFromPlayer").renameAndFixField("allowEnteringNetherUsingPortals", "minecraft:allow_entering_nether_using_portals", GameRuleRegistryFix::convertBoolean).renameAndFixField("announceAdvancements", "minecraft:show_advancement_messages", GameRuleRegistryFix::convertBoolean).renameAndFixField("blockExplosionDropDecay", "minecraft:block_explosion_drop_decay", GameRuleRegistryFix::convertBoolean).renameAndFixField("commandBlockOutput", "minecraft:command_block_output", GameRuleRegistryFix::convertBoolean).renameAndFixField("enableCommandBlocks", "minecraft:command_blocks_work", GameRuleRegistryFix::convertBoolean).renameAndFixField("commandBlocksEnabled", "minecraft:command_blocks_work", GameRuleRegistryFix::convertBoolean).renameAndFixField("commandModificationBlockLimit", "minecraft:max_block_modifications", (var0x) -> convertInteger(var0x, 1)).renameAndFixField("disableElytraMovementCheck", "minecraft:elytra_movement_check", GameRuleRegistryFix::convertBooleanInverted).renameAndFixField("disablePlayerMovementCheck", "minecraft:player_movement_check", GameRuleRegistryFix::convertBooleanInverted).renameAndFixField("disableRaids", "minecraft:raids", GameRuleRegistryFix::convertBooleanInverted).renameAndFixField("doDaylightCycle", "minecraft:advance_time", GameRuleRegistryFix::convertBoolean).renameAndFixField("doEntityDrops", "minecraft:entity_drops", GameRuleRegistryFix::convertBoolean).renameAndFixField("doImmediateRespawn", "minecraft:immediate_respawn", GameRuleRegistryFix::convertBoolean).renameAndFixField("doInsomnia", "minecraft:spawn_phantoms", GameRuleRegistryFix::convertBoolean).renameAndFixField("doLimitedCrafting", "minecraft:limited_crafting", GameRuleRegistryFix::convertBoolean).renameAndFixField("doMobLoot", "minecraft:mob_drops", GameRuleRegistryFix::convertBoolean).renameAndFixField("doMobSpawning", "minecraft:spawn_mobs", GameRuleRegistryFix::convertBoolean).renameAndFixField("doPatrolSpawning", "minecraft:spawn_patrols", GameRuleRegistryFix::convertBoolean).renameAndFixField("doTileDrops", "minecraft:block_drops", GameRuleRegistryFix::convertBoolean).renameAndFixField("doTraderSpawning", "minecraft:spawn_wandering_traders", GameRuleRegistryFix::convertBoolean).renameAndFixField("doVinesSpread", "minecraft:spread_vines", GameRuleRegistryFix::convertBoolean).renameAndFixField("doWardenSpawning", "minecraft:spawn_wardens", GameRuleRegistryFix::convertBoolean).renameAndFixField("doWeatherCycle", "minecraft:advance_weather", GameRuleRegistryFix::convertBoolean).renameAndFixField("drowningDamage", "minecraft:drowning_damage", GameRuleRegistryFix::convertBoolean).renameAndFixField("enderPearlsVanishOnDeath", "minecraft:ender_pearls_vanish_on_death", GameRuleRegistryFix::convertBoolean).renameAndFixField("fallDamage", "minecraft:fall_damage", GameRuleRegistryFix::convertBoolean).renameAndFixField("fireDamage", "minecraft:fire_damage", GameRuleRegistryFix::convertBoolean).renameAndFixField("forgiveDeadPlayers", "minecraft:forgive_dead_players", GameRuleRegistryFix::convertBoolean).renameAndFixField("freezeDamage", "minecraft:freeze_damage", GameRuleRegistryFix::convertBoolean).renameAndFixField("globalSoundEvents", "minecraft:global_sound_events", GameRuleRegistryFix::convertBoolean).renameAndFixField("keepInventory", "minecraft:keep_inventory", GameRuleRegistryFix::convertBoolean).renameAndFixField("lavaSourceConversion", "minecraft:lava_source_conversion", GameRuleRegistryFix::convertBoolean).renameAndFixField("locatorBar", "minecraft:locator_bar", GameRuleRegistryFix::convertBoolean).renameAndFixField("logAdminCommands", "minecraft:log_admin_commands", GameRuleRegistryFix::convertBoolean).renameAndFixField("maxCommandChainLength", "minecraft:max_command_sequence_length", (var0x) -> convertInteger(var0x, 0)).renameAndFixField("maxCommandForkCount", "minecraft:max_command_forks", (var0x) -> convertInteger(var0x, 0)).renameAndFixField("maxEntityCramming", "minecraft:max_entity_cramming", (var0x) -> convertInteger(var0x, 0)).renameAndFixField("minecartMaxSpeed", "minecraft:max_minecart_speed", GameRuleRegistryFix::convertInteger).renameAndFixField("mobExplosionDropDecay", "minecraft:mob_explosion_drop_decay", GameRuleRegistryFix::convertBoolean).renameAndFixField("mobGriefing", "minecraft:mob_griefing", GameRuleRegistryFix::convertBoolean).renameAndFixField("naturalRegeneration", "minecraft:natural_health_regeneration", GameRuleRegistryFix::convertBoolean).renameAndFixField("playersNetherPortalCreativeDelay", "minecraft:players_nether_portal_creative_delay", (var0x) -> convertInteger(var0x, 0)).renameAndFixField("playersNetherPortalDefaultDelay", "minecraft:players_nether_portal_default_delay", (var0x) -> convertInteger(var0x, 0)).renameAndFixField("playersSleepingPercentage", "minecraft:players_sleeping_percentage", (var0x) -> convertInteger(var0x, 0)).renameAndFixField("projectilesCanBreakBlocks", "minecraft:projectiles_can_break_blocks", GameRuleRegistryFix::convertBoolean).renameAndFixField("pvp", "minecraft:pvp", GameRuleRegistryFix::convertBoolean).renameAndFixField("randomTickSpeed", "minecraft:random_tick_speed", (var0x) -> convertInteger(var0x, 0)).renameAndFixField("reducedDebugInfo", "minecraft:reduced_debug_info", GameRuleRegistryFix::convertBoolean).renameAndFixField("sendCommandFeedback", "minecraft:send_command_feedback", GameRuleRegistryFix::convertBoolean).renameAndFixField("showDeathMessages", "minecraft:show_death_messages", GameRuleRegistryFix::convertBoolean).renameAndFixField("snowAccumulationHeight", "minecraft:max_snow_accumulation_height", (var0x) -> convertInteger(var0x, 0, 8)).renameAndFixField("spawnMonsters", "minecraft:spawn_monsters", GameRuleRegistryFix::convertBoolean).renameAndFixField("spawnRadius", "minecraft:respawn_radius", GameRuleRegistryFix::convertInteger).renameAndFixField("spawnerBlocksEnabled", "minecraft:spawner_blocks_work", GameRuleRegistryFix::convertBoolean).renameAndFixField("spectatorsGenerateChunks", "minecraft:spectators_generate_chunks", GameRuleRegistryFix::convertBoolean).renameAndFixField("tntExplodes", "minecraft:tnt_explodes", GameRuleRegistryFix::convertBoolean).renameAndFixField("tntExplosionDropDecay", "minecraft:tnt_explosion_drop_decay", GameRuleRegistryFix::convertBoolean).renameAndFixField("universalAnger", "minecraft:universal_anger", GameRuleRegistryFix::convertBoolean).renameAndFixField("waterSourceConversion", "minecraft:water_source_conversion", GameRuleRegistryFix::convertBoolean);
            })));
   }

   private static Dynamic<?> convertInteger(Dynamic<?> var0) {
      return convertInteger(var0, -2147483648, 2147483647);
   }

   private static Dynamic<?> convertInteger(Dynamic<?> var0, int var1) {
      return convertInteger(var0, var1, 2147483647);
   }

   private static Dynamic<?> convertInteger(Dynamic<?> var0, int var1, int var2) {
      String var3 = var0.asString("");

      try {
         int var4 = Integer.parseInt(var3);
         return var0.createInt(Mth.clamp(var4, var1, var2));
      } catch (NumberFormatException var5) {
         return var0;
      }
   }

   private static Dynamic<?> convertBoolean(Dynamic<?> var0) {
      return var0.createBoolean(Boolean.parseBoolean(var0.asString("")));
   }

   private static Dynamic<?> convertBooleanInverted(Dynamic<?> var0) {
      return var0.createBoolean(!Boolean.parseBoolean(var0.asString("")));
   }
}
