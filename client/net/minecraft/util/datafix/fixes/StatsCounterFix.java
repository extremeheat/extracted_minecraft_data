package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.datafix.schemas.V1451_6;
import org.apache.commons.lang3.StringUtils;

public class StatsCounterFix extends DataFix {
   private static final Set<String> SPECIAL_OBJECTIVE_CRITERIA = Set.of("dummy", "trigger", "deathCount", "playerKillCount", "totalKillCount", "health", "food", "air", "armor", "xp", "level", "killedByTeam.aqua", "killedByTeam.black", "killedByTeam.blue", "killedByTeam.dark_aqua", "killedByTeam.dark_blue", "killedByTeam.dark_gray", "killedByTeam.dark_green", "killedByTeam.dark_purple", "killedByTeam.dark_red", "killedByTeam.gold", "killedByTeam.gray", "killedByTeam.green", "killedByTeam.light_purple", "killedByTeam.red", "killedByTeam.white", "killedByTeam.yellow", "teamkill.aqua", "teamkill.black", "teamkill.blue", "teamkill.dark_aqua", "teamkill.dark_blue", "teamkill.dark_gray", "teamkill.dark_green", "teamkill.dark_purple", "teamkill.dark_red", "teamkill.gold", "teamkill.gray", "teamkill.green", "teamkill.light_purple", "teamkill.red", "teamkill.white", "teamkill.yellow");
   private static final Set<String> SKIP = ImmutableSet.builder().add("stat.craftItem.minecraft.spawn_egg").add("stat.useItem.minecraft.spawn_egg").add("stat.breakItem.minecraft.spawn_egg").add("stat.pickup.minecraft.spawn_egg").add("stat.drop.minecraft.spawn_egg").build();
   private static final Map<String, String> CUSTOM_MAP = ImmutableMap.builder().put("stat.leaveGame", "minecraft:leave_game").put("stat.playOneMinute", "minecraft:play_one_minute").put("stat.timeSinceDeath", "minecraft:time_since_death").put("stat.sneakTime", "minecraft:sneak_time").put("stat.walkOneCm", "minecraft:walk_one_cm").put("stat.crouchOneCm", "minecraft:crouch_one_cm").put("stat.sprintOneCm", "minecraft:sprint_one_cm").put("stat.swimOneCm", "minecraft:swim_one_cm").put("stat.fallOneCm", "minecraft:fall_one_cm").put("stat.climbOneCm", "minecraft:climb_one_cm").put("stat.flyOneCm", "minecraft:fly_one_cm").put("stat.diveOneCm", "minecraft:dive_one_cm").put("stat.minecartOneCm", "minecraft:minecart_one_cm").put("stat.boatOneCm", "minecraft:boat_one_cm").put("stat.pigOneCm", "minecraft:pig_one_cm").put("stat.horseOneCm", "minecraft:horse_one_cm").put("stat.aviateOneCm", "minecraft:aviate_one_cm").put("stat.jump", "minecraft:jump").put("stat.drop", "minecraft:drop").put("stat.damageDealt", "minecraft:damage_dealt").put("stat.damageTaken", "minecraft:damage_taken").put("stat.deaths", "minecraft:deaths").put("stat.mobKills", "minecraft:mob_kills").put("stat.animalsBred", "minecraft:animals_bred").put("stat.playerKills", "minecraft:player_kills").put("stat.fishCaught", "minecraft:fish_caught").put("stat.talkedToVillager", "minecraft:talked_to_villager").put("stat.tradedWithVillager", "minecraft:traded_with_villager").put("stat.cakeSlicesEaten", "minecraft:eat_cake_slice").put("stat.cauldronFilled", "minecraft:fill_cauldron").put("stat.cauldronUsed", "minecraft:use_cauldron").put("stat.armorCleaned", "minecraft:clean_armor").put("stat.bannerCleaned", "minecraft:clean_banner").put("stat.brewingstandInteraction", "minecraft:interact_with_brewingstand").put("stat.beaconInteraction", "minecraft:interact_with_beacon").put("stat.dropperInspected", "minecraft:inspect_dropper").put("stat.hopperInspected", "minecraft:inspect_hopper").put("stat.dispenserInspected", "minecraft:inspect_dispenser").put("stat.noteblockPlayed", "minecraft:play_noteblock").put("stat.noteblockTuned", "minecraft:tune_noteblock").put("stat.flowerPotted", "minecraft:pot_flower").put("stat.trappedChestTriggered", "minecraft:trigger_trapped_chest").put("stat.enderchestOpened", "minecraft:open_enderchest").put("stat.itemEnchanted", "minecraft:enchant_item").put("stat.recordPlayed", "minecraft:play_record").put("stat.furnaceInteraction", "minecraft:interact_with_furnace").put("stat.craftingTableInteraction", "minecraft:interact_with_crafting_table").put("stat.chestOpened", "minecraft:open_chest").put("stat.sleepInBed", "minecraft:sleep_in_bed").put("stat.shulkerBoxOpened", "minecraft:open_shulker_box").build();
   private static final String BLOCK_KEY = "stat.mineBlock";
   private static final String NEW_BLOCK_KEY = "minecraft:mined";
   private static final Map<String, String> ITEM_KEYS = ImmutableMap.builder().put("stat.craftItem", "minecraft:crafted").put("stat.useItem", "minecraft:used").put("stat.breakItem", "minecraft:broken").put("stat.pickup", "minecraft:picked_up").put("stat.drop", "minecraft:dropped").build();
   private static final Map<String, String> ENTITY_KEYS = ImmutableMap.builder().put("stat.entityKilledBy", "minecraft:killed_by").put("stat.killEntity", "minecraft:killed").build();
   private static final Map<String, String> ENTITIES = ImmutableMap.builder().put("Bat", "minecraft:bat").put("Blaze", "minecraft:blaze").put("CaveSpider", "minecraft:cave_spider").put("Chicken", "minecraft:chicken").put("Cow", "minecraft:cow").put("Creeper", "minecraft:creeper").put("Donkey", "minecraft:donkey").put("ElderGuardian", "minecraft:elder_guardian").put("Enderman", "minecraft:enderman").put("Endermite", "minecraft:endermite").put("EvocationIllager", "minecraft:evocation_illager").put("Ghast", "minecraft:ghast").put("Guardian", "minecraft:guardian").put("Horse", "minecraft:horse").put("Husk", "minecraft:husk").put("Llama", "minecraft:llama").put("LavaSlime", "minecraft:magma_cube").put("MushroomCow", "minecraft:mooshroom").put("Mule", "minecraft:mule").put("Ozelot", "minecraft:ocelot").put("Parrot", "minecraft:parrot").put("Pig", "minecraft:pig").put("PolarBear", "minecraft:polar_bear").put("Rabbit", "minecraft:rabbit").put("Sheep", "minecraft:sheep").put("Shulker", "minecraft:shulker").put("Silverfish", "minecraft:silverfish").put("SkeletonHorse", "minecraft:skeleton_horse").put("Skeleton", "minecraft:skeleton").put("Slime", "minecraft:slime").put("Spider", "minecraft:spider").put("Squid", "minecraft:squid").put("Stray", "minecraft:stray").put("Vex", "minecraft:vex").put("Villager", "minecraft:villager").put("VindicationIllager", "minecraft:vindication_illager").put("Witch", "minecraft:witch").put("WitherSkeleton", "minecraft:wither_skeleton").put("Wolf", "minecraft:wolf").put("ZombieHorse", "minecraft:zombie_horse").put("PigZombie", "minecraft:zombie_pigman").put("ZombieVillager", "minecraft:zombie_villager").put("Zombie", "minecraft:zombie").build();
   private static final String NEW_CUSTOM_KEY = "minecraft:custom";

   public StatsCounterFix(Schema var1, boolean var2) {
      super(var1, var2);
   }

   @Nullable
   private static StatType unpackLegacyKey(String var0) {
      if (SKIP.contains(var0)) {
         return null;
      } else {
         String var1 = (String)CUSTOM_MAP.get(var0);
         if (var1 != null) {
            return new StatType("minecraft:custom", var1);
         } else {
            int var2 = StringUtils.ordinalIndexOf(var0, ".", 2);
            if (var2 < 0) {
               return null;
            } else {
               String var3 = var0.substring(0, var2);
               String var4;
               if ("stat.mineBlock".equals(var3)) {
                  var4 = upgradeBlock(var0.substring(var2 + 1).replace('.', ':'));
                  return new StatType("minecraft:mined", var4);
               } else {
                  var4 = (String)ITEM_KEYS.get(var3);
                  String var5;
                  String var6;
                  String var7;
                  if (var4 != null) {
                     var5 = var0.substring(var2 + 1).replace('.', ':');
                     var6 = upgradeItem(var5);
                     var7 = var6 == null ? var5 : var6;
                     return new StatType(var4, var7);
                  } else {
                     var5 = (String)ENTITY_KEYS.get(var3);
                     if (var5 != null) {
                        var6 = var0.substring(var2 + 1).replace('.', ':');
                        var7 = (String)ENTITIES.getOrDefault(var6, var6);
                        return new StatType(var5, var7);
                     } else {
                        return null;
                     }
                  }
               }
            }
         }
      }
   }

   public TypeRewriteRule makeRule() {
      return TypeRewriteRule.seq(this.makeStatFixer(), this.makeObjectiveFixer());
   }

   private TypeRewriteRule makeStatFixer() {
      Type var1 = this.getInputSchema().getType(References.STATS);
      Type var2 = this.getOutputSchema().getType(References.STATS);
      return this.fixTypeEverywhereTyped("StatsCounterFix", var1, var2, (var1x) -> {
         Dynamic var2x = (Dynamic)var1x.get(DSL.remainderFinder());
         HashMap var3 = Maps.newHashMap();
         Optional var4 = var2x.getMapValues().result();
         if (var4.isPresent()) {
            Iterator var5 = ((Map)var4.get()).entrySet().iterator();

            while(var5.hasNext()) {
               Map.Entry var6 = (Map.Entry)var5.next();
               if (((Dynamic)var6.getValue()).asNumber().result().isPresent()) {
                  String var7 = ((Dynamic)var6.getKey()).asString("");
                  StatType var8 = unpackLegacyKey(var7);
                  if (var8 != null) {
                     Dynamic var9 = var2x.createString(var8.type());
                     Dynamic var10 = (Dynamic)var3.computeIfAbsent(var9, (var1) -> {
                        return var2x.emptyMap();
                     });
                     var3.put(var9, var10.set(var8.typeKey(), (Dynamic)var6.getValue()));
                  }
               }
            }
         }

         return Util.readTypedOrThrow(var2, var2x.emptyMap().set("stats", var2x.createMap(var3)));
      });
   }

   private TypeRewriteRule makeObjectiveFixer() {
      Type var1 = this.getInputSchema().getType(References.OBJECTIVE);
      Type var2 = this.getOutputSchema().getType(References.OBJECTIVE);
      return this.fixTypeEverywhereTyped("ObjectiveStatFix", var1, var2, (var1x) -> {
         Dynamic var2x = (Dynamic)var1x.get(DSL.remainderFinder());
         Dynamic var3 = var2x.update("CriteriaName", (var0) -> {
            Optional var10000 = var0.asString().result().map((var0x) -> {
               if (SPECIAL_OBJECTIVE_CRITERIA.contains(var0x)) {
                  return var0x;
               } else {
                  StatType var1 = unpackLegacyKey(var0x);
                  if (var1 == null) {
                     return "dummy";
                  } else {
                     String var10000 = V1451_6.packNamespacedWithDot(var1.type);
                     return var10000 + ":" + V1451_6.packNamespacedWithDot(var1.typeKey);
                  }
               }
            });
            Objects.requireNonNull(var0);
            return (Dynamic)DataFixUtils.orElse(var10000.map(var0::createString), var0);
         });
         return Util.readTypedOrThrow(var2, var3);
      });
   }

   @Nullable
   private static String upgradeItem(String var0) {
      return ItemStackTheFlatteningFix.updateItem(var0, 0);
   }

   private static String upgradeBlock(String var0) {
      return BlockStateData.upgradeBlock(var0);
   }

   static record StatType(String type, String typeKey) {
      final String type;
      final String typeKey;

      StatType(String var1, String var2) {
         super();
         this.type = var1;
         this.typeKey = var2;
      }

      public String type() {
         return this.type;
      }

      public String typeKey() {
         return this.typeKey;
      }
   }
}
