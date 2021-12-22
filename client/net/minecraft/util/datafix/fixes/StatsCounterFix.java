package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;

public class StatsCounterFix extends DataFix {
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

   public TypeRewriteRule makeRule() {
      Type var1 = this.getOutputSchema().getType(References.STATS);
      return this.fixTypeEverywhereTyped("StatsCounterFix", this.getInputSchema().getType(References.STATS), var1, (var2) -> {
         Dynamic var3 = (Dynamic)var2.get(DSL.remainderFinder());
         HashMap var4 = Maps.newHashMap();
         Optional var5 = var3.getMapValues().result();
         if (var5.isPresent()) {
            Iterator var6 = ((Map)var5.get()).entrySet().iterator();

            while(true) {
               Entry var7;
               String var9;
               String var10;
               while(true) {
                  String var8;
                  do {
                     do {
                        if (!var6.hasNext()) {
                           return (Typed)((Pair)var1.readTyped(var3.emptyMap().set("stats", var3.createMap(var4))).result().orElseThrow(() -> {
                              return new IllegalStateException("Could not parse new stats object.");
                           })).getFirst();
                        }

                        var7 = (Entry)var6.next();
                     } while(!((Dynamic)var7.getValue()).asNumber().result().isPresent());

                     var8 = ((Dynamic)var7.getKey()).asString("");
                  } while(SKIP.contains(var8));

                  if (CUSTOM_MAP.containsKey(var8)) {
                     var9 = "minecraft:custom";
                     var10 = (String)CUSTOM_MAP.get(var8);
                     break;
                  }

                  int var11 = StringUtils.ordinalIndexOf(var8, ".", 2);
                  if (var11 >= 0) {
                     String var12 = var8.substring(0, var11);
                     if ("stat.mineBlock".equals(var12)) {
                        var9 = "minecraft:mined";
                        var10 = this.upgradeBlock(var8.substring(var11 + 1).replace('.', ':'));
                        break;
                     }

                     String var13;
                     if (ITEM_KEYS.containsKey(var12)) {
                        var9 = (String)ITEM_KEYS.get(var12);
                        var13 = var8.substring(var11 + 1).replace('.', ':');
                        String var14 = this.upgradeItem(var13);
                        var10 = var14 == null ? var13 : var14;
                        break;
                     }

                     if (ENTITY_KEYS.containsKey(var12)) {
                        var9 = (String)ENTITY_KEYS.get(var12);
                        var13 = var8.substring(var11 + 1).replace('.', ':');
                        var10 = (String)ENTITIES.getOrDefault(var13, var13);
                        break;
                     }
                  }
               }

               Dynamic var15 = var3.createString(var9);
               Dynamic var16 = (Dynamic)var4.computeIfAbsent(var15, (var1x) -> {
                  return var3.emptyMap();
               });
               var4.put(var15, var16.set(var10, (Dynamic)var7.getValue()));
            }
         } else {
            return (Typed)((Pair)var1.readTyped(var3.emptyMap().set("stats", var3.createMap(var4))).result().orElseThrow(() -> {
               return new IllegalStateException("Could not parse new stats object.");
            })).getFirst();
         }
      });
   }

   @Nullable
   protected String upgradeItem(String var1) {
      return ItemStackTheFlatteningFix.updateItem(var1, 0);
   }

   protected String upgradeBlock(String var1) {
      return BlockStateData.upgradeBlock(var1);
   }
}
