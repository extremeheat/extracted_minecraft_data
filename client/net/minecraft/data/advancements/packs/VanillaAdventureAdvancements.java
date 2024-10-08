package net.minecraft.data.advancements.packs;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ChanneledLightningTrigger;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.DistanceTrigger;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FallAfterExplosionTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemJukeboxPlayablePredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.KilledByArrowTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LightningBoltPredicate;
import net.minecraft.advancements.critereon.LightningStrikeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LootTableTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.advancements.critereon.ShotCrossbowTrigger;
import net.minecraft.advancements.critereon.SlideDownBlockTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.advancements.critereon.TargetBlockTrigger;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.advancements.critereon.UsingItemTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class VanillaAdventureAdvancements implements AdvancementSubProvider {
   private static final int DISTANCE_FROM_BOTTOM_TO_TOP = 384;
   private static final int Y_COORDINATE_AT_TOP = 320;
   private static final int Y_COORDINATE_AT_BOTTOM = -64;
   private static final int BEDROCK_THICKNESS = 5;
   protected static final List<EntityType<?>> MOBS_TO_KILL = Arrays.asList(
      EntityType.BLAZE,
      EntityType.BOGGED,
      EntityType.BREEZE,
      EntityType.CAVE_SPIDER,
      EntityType.CREEPER,
      EntityType.DROWNED,
      EntityType.ELDER_GUARDIAN,
      EntityType.ENDER_DRAGON,
      EntityType.ENDERMAN,
      EntityType.ENDERMITE,
      EntityType.EVOKER,
      EntityType.GHAST,
      EntityType.GUARDIAN,
      EntityType.HOGLIN,
      EntityType.HUSK,
      EntityType.MAGMA_CUBE,
      EntityType.PHANTOM,
      EntityType.PIGLIN,
      EntityType.PIGLIN_BRUTE,
      EntityType.PILLAGER,
      EntityType.RAVAGER,
      EntityType.SHULKER,
      EntityType.SILVERFISH,
      EntityType.SKELETON,
      EntityType.SLIME,
      EntityType.SPIDER,
      EntityType.STRAY,
      EntityType.VEX,
      EntityType.VINDICATOR,
      EntityType.WITCH,
      EntityType.WITHER_SKELETON,
      EntityType.WITHER,
      EntityType.ZOGLIN,
      EntityType.ZOMBIE_VILLAGER,
      EntityType.ZOMBIE,
      EntityType.ZOMBIFIED_PIGLIN
   );

   public VanillaAdventureAdvancements() {
      super();
   }

   private static Criterion<LightningStrikeTrigger.TriggerInstance> fireCountAndBystander(MinMaxBounds.Ints var0, Optional<EntityPredicate> var1) {
      return LightningStrikeTrigger.TriggerInstance.lightningStrike(
         Optional.of(
            EntityPredicate.Builder.entity()
               .distance(DistancePredicate.absolute(MinMaxBounds.Doubles.atMost(30.0)))
               .subPredicate(LightningBoltPredicate.blockSetOnFire(var0))
               .build()
         ),
         var1
      );
   }

   private static Criterion<UsingItemTrigger.TriggerInstance> lookAtThroughItem(EntityPredicate.Builder var0, ItemPredicate.Builder var1) {
      return UsingItemTrigger.TriggerInstance.lookingAt(
         EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().setLookingAt(var0).build()), var1
      );
   }

   @Override
   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      HolderLookup.RegistryLookup var3 = var1.lookupOrThrow(Registries.ENTITY_TYPE);
      HolderLookup.RegistryLookup var4 = var1.lookupOrThrow(Registries.ITEM);
      HolderLookup.RegistryLookup var5 = var1.lookupOrThrow(Registries.BLOCK);
      AdvancementHolder var6 = Advancement.Builder.advancement()
         .display(
            Items.MAP,
            Component.translatable("advancements.adventure.root.title"),
            Component.translatable("advancements.adventure.root.description"),
            ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/adventure.png"),
            AdvancementType.TASK,
            false,
            false,
            false
         )
         .requirements(AdvancementRequirements.Strategy.OR)
         .addCriterion("killed_something", KilledTrigger.TriggerInstance.playerKilledEntity())
         .addCriterion("killed_by_something", KilledTrigger.TriggerInstance.entityKilledPlayer())
         .save(var2, "adventure/root");
      AdvancementHolder var7 = Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Blocks.RED_BED,
            Component.translatable("advancements.adventure.sleep_in_bed.title"),
            Component.translatable("advancements.adventure.sleep_in_bed.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("slept_in_bed", PlayerTrigger.TriggerInstance.sleptInBed())
         .save(var2, "adventure/sleep_in_bed");
      createAdventuringTime(var1, var2, var7, MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD);
      AdvancementHolder var8 = Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Items.EMERALD,
            Component.translatable("advancements.adventure.trade.title"),
            Component.translatable("advancements.adventure.trade.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("traded", TradeTrigger.TriggerInstance.tradedWithVillager())
         .save(var2, "adventure/trade");
      Advancement.Builder.advancement()
         .parent(var8)
         .display(
            Items.EMERALD,
            Component.translatable("advancements.adventure.trade_at_world_height.title"),
            Component.translatable("advancements.adventure.trade_at_world_height.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "trade_at_world_height",
            TradeTrigger.TriggerInstance.tradedWithVillager(
               EntityPredicate.Builder.entity().located(LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atLeast(319.0)))
            )
         )
         .save(var2, "adventure/trade_at_world_height");
      AdvancementHolder var9 = createMonsterHunterAdvancement(var6, var2, var3, MOBS_TO_KILL);
      AdvancementHolder var10 = Advancement.Builder.advancement()
         .parent(var9)
         .display(
            Items.BOW,
            Component.translatable("advancements.adventure.shoot_arrow.title"),
            Component.translatable("advancements.adventure.shoot_arrow.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "shot_arrow",
            PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(
               DamagePredicate.Builder.damageInstance()
                  .type(
                     DamageSourcePredicate.Builder.damageType()
                        .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                        .direct(EntityPredicate.Builder.entity().of(var3, EntityTypeTags.ARROWS))
                  )
            )
         )
         .save(var2, "adventure/shoot_arrow");
      AdvancementHolder var11 = Advancement.Builder.advancement()
         .parent(var9)
         .display(
            Items.TRIDENT,
            Component.translatable("advancements.adventure.throw_trident.title"),
            Component.translatable("advancements.adventure.throw_trident.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "shot_trident",
            PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(
               DamagePredicate.Builder.damageInstance()
                  .type(
                     DamageSourcePredicate.Builder.damageType()
                        .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                        .direct(EntityPredicate.Builder.entity().of(var3, EntityType.TRIDENT))
                  )
            )
         )
         .save(var2, "adventure/throw_trident");
      Advancement.Builder.advancement()
         .parent(var11)
         .display(
            Items.TRIDENT,
            Component.translatable("advancements.adventure.very_very_frightening.title"),
            Component.translatable("advancements.adventure.very_very_frightening.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "struck_villager", ChanneledLightningTrigger.TriggerInstance.channeledLightning(EntityPredicate.Builder.entity().of(var3, EntityType.VILLAGER))
         )
         .save(var2, "adventure/very_very_frightening");
      Advancement.Builder.advancement()
         .parent(var8)
         .display(
            Blocks.CARVED_PUMPKIN,
            Component.translatable("advancements.adventure.summon_iron_golem.title"),
            Component.translatable("advancements.adventure.summon_iron_golem.description"),
            null,
            AdvancementType.GOAL,
            true,
            true,
            false
         )
         .addCriterion("summoned_golem", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(var3, EntityType.IRON_GOLEM)))
         .save(var2, "adventure/summon_iron_golem");
      Advancement.Builder.advancement()
         .parent(var10)
         .display(
            Items.ARROW,
            Component.translatable("advancements.adventure.sniper_duel.title"),
            Component.translatable("advancements.adventure.sniper_duel.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(50))
         .addCriterion(
            "killed_skeleton",
            KilledTrigger.TriggerInstance.playerKilledEntity(
               EntityPredicate.Builder.entity().of(var3, EntityType.SKELETON).distance(DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(50.0))),
               DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
            )
         )
         .save(var2, "adventure/sniper_duel");
      Advancement.Builder.advancement()
         .parent(var9)
         .display(
            Items.TOTEM_OF_UNDYING,
            Component.translatable("advancements.adventure.totem_of_undying.title"),
            Component.translatable("advancements.adventure.totem_of_undying.description"),
            null,
            AdvancementType.GOAL,
            true,
            true,
            false
         )
         .addCriterion("used_totem", UsedTotemTrigger.TriggerInstance.usedTotem(var4, Items.TOTEM_OF_UNDYING))
         .save(var2, "adventure/totem_of_undying");
      AdvancementHolder var12 = Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Items.CROSSBOW,
            Component.translatable("advancements.adventure.ol_betsy.title"),
            Component.translatable("advancements.adventure.ol_betsy.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("shot_crossbow", ShotCrossbowTrigger.TriggerInstance.shotCrossbow(var4, Items.CROSSBOW))
         .save(var2, "adventure/ol_betsy");
      Advancement.Builder.advancement()
         .parent(var12)
         .display(
            Items.CROSSBOW,
            Component.translatable("advancements.adventure.whos_the_pillager_now.title"),
            Component.translatable("advancements.adventure.whos_the_pillager_now.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "kill_pillager", KilledByArrowTrigger.TriggerInstance.crossbowKilled(var4, EntityPredicate.Builder.entity().of(var3, EntityType.PILLAGER))
         )
         .save(var2, "adventure/whos_the_pillager_now");
      Advancement.Builder.advancement()
         .parent(var12)
         .display(
            Items.CROSSBOW,
            Component.translatable("advancements.adventure.two_birds_one_arrow.title"),
            Component.translatable("advancements.adventure.two_birds_one_arrow.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(65))
         .addCriterion(
            "two_birds",
            KilledByArrowTrigger.TriggerInstance.crossbowKilled(
               var4, EntityPredicate.Builder.entity().of(var3, EntityType.PHANTOM), EntityPredicate.Builder.entity().of(var3, EntityType.PHANTOM)
            )
         )
         .save(var2, "adventure/two_birds_one_arrow");
      Advancement.Builder.advancement()
         .parent(var12)
         .display(
            Items.CROSSBOW,
            Component.translatable("advancements.adventure.arbalistic.title"),
            Component.translatable("advancements.adventure.arbalistic.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            true
         )
         .rewards(AdvancementRewards.Builder.experience(85))
         .addCriterion("arbalistic", KilledByArrowTrigger.TriggerInstance.crossbowKilled(var4, MinMaxBounds.Ints.exactly(5)))
         .save(var2, "adventure/arbalistic");
      HolderLookup.RegistryLookup var13 = var1.lookupOrThrow(Registries.BANNER_PATTERN);
      AdvancementHolder var14 = Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Raid.getOminousBannerInstance(var13),
            Component.translatable("advancements.adventure.voluntary_exile.title"),
            Component.translatable("advancements.adventure.voluntary_exile.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            true
         )
         .addCriterion(
            "voluntary_exile",
            KilledTrigger.TriggerInstance.playerKilledEntity(
               EntityPredicate.Builder.entity().of(var3, EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.captainPredicate(var4, var13))
            )
         )
         .save(var2, "adventure/voluntary_exile");
      Advancement.Builder.advancement()
         .parent(var14)
         .display(
            Raid.getOminousBannerInstance(var13),
            Component.translatable("advancements.adventure.hero_of_the_village.title"),
            Component.translatable("advancements.adventure.hero_of_the_village.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            true
         )
         .rewards(AdvancementRewards.Builder.experience(100))
         .addCriterion("hero_of_the_village", PlayerTrigger.TriggerInstance.raidWon())
         .save(var2, "adventure/hero_of_the_village");
      Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Blocks.HONEY_BLOCK.asItem(),
            Component.translatable("advancements.adventure.honey_block_slide.title"),
            Component.translatable("advancements.adventure.honey_block_slide.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("honey_block_slide", SlideDownBlockTrigger.TriggerInstance.slidesDownBlock(Blocks.HONEY_BLOCK))
         .save(var2, "adventure/honey_block_slide");
      Advancement.Builder.advancement()
         .parent(var10)
         .display(
            Blocks.TARGET.asItem(),
            Component.translatable("advancements.adventure.bullseye.title"),
            Component.translatable("advancements.adventure.bullseye.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(50))
         .addCriterion(
            "bullseye",
            TargetBlockTrigger.TriggerInstance.targetHit(
               MinMaxBounds.Ints.exactly(15),
               Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().distance(DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(30.0)))))
            )
         )
         .save(var2, "adventure/bullseye");
      Advancement.Builder.advancement()
         .parent(var7)
         .display(
            Items.LEATHER_BOOTS,
            Component.translatable("advancements.adventure.walk_on_powder_snow_with_leather_boots.title"),
            Component.translatable("advancements.adventure.walk_on_powder_snow_with_leather_boots.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "walk_on_powder_snow_with_leather_boots",
            PlayerTrigger.TriggerInstance.walkOnBlockWithEquipment(var5, var4, Blocks.POWDER_SNOW, Items.LEATHER_BOOTS)
         )
         .save(var2, "adventure/walk_on_powder_snow_with_leather_boots");
      Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Items.LIGHTNING_ROD,
            Component.translatable("advancements.adventure.lightning_rod_with_villager_no_fire.title"),
            Component.translatable("advancements.adventure.lightning_rod_with_villager_no_fire.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "lightning_rod_with_villager_no_fire",
            fireCountAndBystander(MinMaxBounds.Ints.exactly(0), Optional.of(EntityPredicate.Builder.entity().of(var3, EntityType.VILLAGER).build()))
         )
         .save(var2, "adventure/lightning_rod_with_villager_no_fire");
      AdvancementHolder var15 = Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Items.SPYGLASS,
            Component.translatable("advancements.adventure.spyglass_at_parrot.title"),
            Component.translatable("advancements.adventure.spyglass_at_parrot.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "spyglass_at_parrot",
            lookAtThroughItem(EntityPredicate.Builder.entity().of(var3, EntityType.PARROT), ItemPredicate.Builder.item().of(var4, Items.SPYGLASS))
         )
         .save(var2, "adventure/spyglass_at_parrot");
      AdvancementHolder var16 = Advancement.Builder.advancement()
         .parent(var15)
         .display(
            Items.SPYGLASS,
            Component.translatable("advancements.adventure.spyglass_at_ghast.title"),
            Component.translatable("advancements.adventure.spyglass_at_ghast.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "spyglass_at_ghast",
            lookAtThroughItem(EntityPredicate.Builder.entity().of(var3, EntityType.GHAST), ItemPredicate.Builder.item().of(var4, Items.SPYGLASS))
         )
         .save(var2, "adventure/spyglass_at_ghast");
      Advancement.Builder.advancement()
         .parent(var7)
         .display(
            Items.JUKEBOX,
            Component.translatable("advancements.adventure.play_jukebox_in_meadows.title"),
            Component.translatable("advancements.adventure.play_jukebox_in_meadows.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "play_jukebox_in_meadows",
            ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
               LocationPredicate.Builder.location()
                  .setBiomes(HolderSet.direct(var1.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.MEADOW)))
                  .setBlock(BlockPredicate.Builder.block().of(var5, Blocks.JUKEBOX)),
               ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.JUKEBOX_PLAYABLE, ItemJukeboxPlayablePredicate.any())
            )
         )
         .save(var2, "adventure/play_jukebox_in_meadows");
      Advancement.Builder.advancement()
         .parent(var16)
         .display(
            Items.SPYGLASS,
            Component.translatable("advancements.adventure.spyglass_at_dragon.title"),
            Component.translatable("advancements.adventure.spyglass_at_dragon.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "spyglass_at_dragon",
            lookAtThroughItem(EntityPredicate.Builder.entity().of(var3, EntityType.ENDER_DRAGON), ItemPredicate.Builder.item().of(var4, Items.SPYGLASS))
         )
         .save(var2, "adventure/spyglass_at_dragon");
      Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Items.WATER_BUCKET,
            Component.translatable("advancements.adventure.fall_from_world_height.title"),
            Component.translatable("advancements.adventure.fall_from_world_height.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "fall_from_world_height",
            DistanceTrigger.TriggerInstance.fallFromHeight(
               EntityPredicate.Builder.entity().located(LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atMost(-59.0))),
               DistancePredicate.vertical(MinMaxBounds.Doubles.atLeast(379.0)),
               LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atLeast(319.0))
            )
         )
         .save(var2, "adventure/fall_from_world_height");
      Advancement.Builder.advancement()
         .parent(var9)
         .display(
            Blocks.SCULK_CATALYST,
            Component.translatable("advancements.adventure.kill_mob_near_sculk_catalyst.title"),
            Component.translatable("advancements.adventure.kill_mob_near_sculk_catalyst.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .addCriterion("kill_mob_near_sculk_catalyst", KilledTrigger.TriggerInstance.playerKilledEntityNearSculkCatalyst())
         .save(var2, "adventure/kill_mob_near_sculk_catalyst");
      Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Blocks.SCULK_SENSOR,
            Component.translatable("advancements.adventure.avoid_vibration.title"),
            Component.translatable("advancements.adventure.avoid_vibration.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion("avoid_vibration", PlayerTrigger.TriggerInstance.avoidVibration())
         .save(var2, "adventure/avoid_vibration");
      AdvancementHolder var17 = respectingTheRemnantsCriterions(var4, Advancement.Builder.advancement())
         .parent(var6)
         .display(
            Items.BRUSH,
            Component.translatable("advancements.adventure.salvage_sherd.title"),
            Component.translatable("advancements.adventure.salvage_sherd.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .save(var2, "adventure/salvage_sherd");
      Advancement.Builder.advancement()
         .parent(var17)
         .display(
            DecoratedPotBlockEntity.createDecoratedPotItem(
               new PotDecorations(Optional.empty(), Optional.of(Items.HEART_POTTERY_SHERD), Optional.empty(), Optional.of(Items.EXPLORER_POTTERY_SHERD))
            ),
            Component.translatable("advancements.adventure.craft_decorated_pot_using_only_sherds.title"),
            Component.translatable("advancements.adventure.craft_decorated_pot_using_only_sherds.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "pot_crafted_using_only_sherds",
            RecipeCraftedTrigger.TriggerInstance.craftedItem(
               ResourceKey.create(Registries.RECIPE, ResourceLocation.withDefaultNamespace("decorated_pot")),
               List.of(
                  ItemPredicate.Builder.item().of(var4, ItemTags.DECORATED_POT_SHERDS),
                  ItemPredicate.Builder.item().of(var4, ItemTags.DECORATED_POT_SHERDS),
                  ItemPredicate.Builder.item().of(var4, ItemTags.DECORATED_POT_SHERDS),
                  ItemPredicate.Builder.item().of(var4, ItemTags.DECORATED_POT_SHERDS)
               )
            )
         )
         .save(var2, "adventure/craft_decorated_pot_using_only_sherds");
      AdvancementHolder var18 = craftingANewLook(Advancement.Builder.advancement())
         .parent(var6)
         .display(
            new ItemStack(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE),
            Component.translatable("advancements.adventure.trim_with_any_armor_pattern.title"),
            Component.translatable("advancements.adventure.trim_with_any_armor_pattern.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .save(var2, "adventure/trim_with_any_armor_pattern");
      smithingWithStyle(Advancement.Builder.advancement())
         .parent(var18)
         .display(
            new ItemStack(Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE),
            Component.translatable("advancements.adventure.trim_with_all_exclusive_armor_patterns.title"),
            Component.translatable("advancements.adventure.trim_with_all_exclusive_armor_patterns.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(150))
         .save(var2, "adventure/trim_with_all_exclusive_armor_patterns");
      Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Items.CHISELED_BOOKSHELF,
            Component.translatable("advancements.adventure.read_power_from_chiseled_bookshelf.title"),
            Component.translatable("advancements.adventure.read_power_from_chiseled_bookshelf.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .requirements(AdvancementRequirements.Strategy.OR)
         .addCriterion("chiseled_bookshelf", placedBlockReadByComparator(var5, Blocks.CHISELED_BOOKSHELF))
         .addCriterion("comparator", placedComparatorReadingBlock(var5, Blocks.CHISELED_BOOKSHELF))
         .save(var2, "adventure/read_power_of_chiseled_bookshelf");
      Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Items.ARMADILLO_SCUTE,
            Component.translatable("advancements.adventure.brush_armadillo.title"),
            Component.translatable("advancements.adventure.brush_armadillo.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "brush_armadillo",
            PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
               ItemPredicate.Builder.item().of(var4, Items.BRUSH),
               Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(var3, EntityType.ARMADILLO)))
            )
         )
         .save(var2, "adventure/brush_armadillo");
      AdvancementHolder var19 = Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Blocks.CHISELED_TUFF,
            Component.translatable("advancements.adventure.minecraft_trials_edition.title"),
            Component.translatable("advancements.adventure.minecraft_trials_edition.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "minecraft_trials_edition",
            PlayerTrigger.TriggerInstance.located(
               LocationPredicate.Builder.inStructure(var1.lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.TRIAL_CHAMBERS))
            )
         )
         .save(var2, "adventure/minecraft_trials_edition");
      Advancement.Builder.advancement()
         .parent(var19)
         .display(
            Items.COPPER_BULB,
            Component.translatable("advancements.adventure.lighten_up.title"),
            Component.translatable("advancements.adventure.lighten_up.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "lighten_up",
            ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
               LocationPredicate.Builder.location()
                  .setBlock(
                     BlockPredicate.Builder.block()
                        .of(
                           var5,
                           Blocks.OXIDIZED_COPPER_BULB,
                           Blocks.WEATHERED_COPPER_BULB,
                           Blocks.EXPOSED_COPPER_BULB,
                           Blocks.WAXED_OXIDIZED_COPPER_BULB,
                           Blocks.WAXED_WEATHERED_COPPER_BULB,
                           Blocks.WAXED_EXPOSED_COPPER_BULB
                        )
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CopperBulbBlock.LIT, true))
                  ),
               ItemPredicate.Builder.item().of(var4, VanillaHusbandryAdvancements.WAX_SCRAPING_TOOLS)
            )
         )
         .save(var2, "adventure/lighten_up");
      AdvancementHolder var20 = Advancement.Builder.advancement()
         .parent(var19)
         .display(
            Items.TRIAL_KEY,
            Component.translatable("advancements.adventure.under_lock_and_key.title"),
            Component.translatable("advancements.adventure.under_lock_and_key.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "under_lock_and_key",
            ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
               LocationPredicate.Builder.location()
                  .setBlock(
                     BlockPredicate.Builder.block()
                        .of(var5, Blocks.VAULT)
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VaultBlock.OMINOUS, false))
                  ),
               ItemPredicate.Builder.item().of(var4, Items.TRIAL_KEY)
            )
         )
         .save(var2, "adventure/under_lock_and_key");
      Advancement.Builder.advancement()
         .parent(var20)
         .display(
            Items.OMINOUS_TRIAL_KEY,
            Component.translatable("advancements.adventure.revaulting.title"),
            Component.translatable("advancements.adventure.revaulting.description"),
            null,
            AdvancementType.GOAL,
            true,
            true,
            false
         )
         .addCriterion(
            "revaulting",
            ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(
               LocationPredicate.Builder.location()
                  .setBlock(
                     BlockPredicate.Builder.block()
                        .of(var5, Blocks.VAULT)
                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VaultBlock.OMINOUS, true))
                  ),
               ItemPredicate.Builder.item().of(var4, Items.OMINOUS_TRIAL_KEY)
            )
         )
         .save(var2, "adventure/revaulting");
      Advancement.Builder.advancement()
         .parent(var19)
         .display(
            Items.WIND_CHARGE,
            Component.translatable("advancements.adventure.blowback.title"),
            Component.translatable("advancements.adventure.blowback.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(40))
         .addCriterion(
            "blowback",
            KilledTrigger.TriggerInstance.playerKilledEntity(
               EntityPredicate.Builder.entity().of(var3, EntityType.BREEZE),
               DamageSourcePredicate.Builder.damageType()
                  .tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE))
                  .direct(EntityPredicate.Builder.entity().of(var3, EntityType.BREEZE_WIND_CHARGE))
            )
         )
         .save(var2, "adventure/blowback");
      Advancement.Builder.advancement()
         .parent(var6)
         .display(
            Items.CRAFTER,
            Component.translatable("advancements.adventure.crafters_crafting_crafters.title"),
            Component.translatable("advancements.adventure.crafters_crafting_crafters.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "crafter_crafted_crafter",
            RecipeCraftedTrigger.TriggerInstance.crafterCraftedItem(ResourceKey.create(Registries.RECIPE, ResourceLocation.withDefaultNamespace("crafter")))
         )
         .save(var2, "adventure/crafters_crafting_crafters");
      Advancement.Builder.advancement()
         .parent(var19)
         .display(
            Items.WIND_CHARGE,
            Component.translatable("advancements.adventure.who_needs_rockets.title"),
            Component.translatable("advancements.adventure.who_needs_rockets.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .addCriterion(
            "who_needs_rockets",
            FallAfterExplosionTrigger.TriggerInstance.fallAfterExplosion(
               DistancePredicate.vertical(MinMaxBounds.Doubles.atLeast(7.0)), EntityPredicate.Builder.entity().of(var3, EntityType.WIND_CHARGE)
            )
         )
         .save(var2, "adventure/who_needs_rockets");
      Advancement.Builder.advancement()
         .parent(var19)
         .display(
            Items.MACE,
            Component.translatable("advancements.adventure.overoverkill.title"),
            Component.translatable("advancements.adventure.overoverkill.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(50))
         .addCriterion(
            "overoverkill",
            PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(
               DamagePredicate.Builder.damageInstance()
                  .dealtDamage(MinMaxBounds.Doubles.atLeast(100.0))
                  .type(
                     DamageSourcePredicate.Builder.damageType()
                        .tag(TagPredicate.is(DamageTypeTags.IS_MACE_SMASH))
                        .direct(
                           EntityPredicate.Builder.entity()
                              .of(var3, EntityType.PLAYER)
                              .equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(var4, Items.MACE)))
                        )
                  )
            )
         )
         .save(var2, "adventure/overoverkill");
   }

   public static AdvancementHolder createMonsterHunterAdvancement(
      AdvancementHolder var0, Consumer<AdvancementHolder> var1, HolderGetter<EntityType<?>> var2, List<EntityType<?>> var3
   ) {
      AdvancementHolder var4 = addMobsToKill(Advancement.Builder.advancement(), var2, var3)
         .parent(var0)
         .display(
            Items.IRON_SWORD,
            Component.translatable("advancements.adventure.kill_a_mob.title"),
            Component.translatable("advancements.adventure.kill_a_mob.description"),
            null,
            AdvancementType.TASK,
            true,
            true,
            false
         )
         .requirements(AdvancementRequirements.Strategy.OR)
         .save(var1, "adventure/kill_a_mob");
      addMobsToKill(Advancement.Builder.advancement(), var2, var3)
         .parent(var4)
         .display(
            Items.DIAMOND_SWORD,
            Component.translatable("advancements.adventure.kill_all_mobs.title"),
            Component.translatable("advancements.adventure.kill_all_mobs.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(100))
         .save(var1, "adventure/kill_all_mobs");
      return var4;
   }

   private static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedBlockReadByComparator(HolderGetter<Block> var0, Block var1) {
      LootItemCondition.Builder[] var2 = ComparatorBlock.FACING.getPossibleValues().stream().map(var1x -> {
         StatePropertiesPredicate.Builder var2x = StatePropertiesPredicate.Builder.properties().hasProperty(ComparatorBlock.FACING, var1x);
         BlockPredicate.Builder var3 = BlockPredicate.Builder.block().of(var0, Blocks.COMPARATOR).setProperties(var2x);
         return LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(var3), new BlockPos(var1x.getOpposite().getUnitVec3i()));
      }).toArray(LootItemCondition.Builder[]::new);
      return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(
         LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1), AnyOfCondition.anyOf(var2)
      );
   }

   private static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedComparatorReadingBlock(HolderGetter<Block> var0, Block var1) {
      LootItemCondition.Builder[] var2 = ComparatorBlock.FACING
         .getPossibleValues()
         .stream()
         .map(
            var2x -> {
               StatePropertiesPredicate.Builder var3 = StatePropertiesPredicate.Builder.properties().hasProperty(ComparatorBlock.FACING, var2x);
               LootItemBlockStatePropertyCondition.Builder var4 = new LootItemBlockStatePropertyCondition.Builder(Blocks.COMPARATOR).setProperties(var3);
               LootItemCondition.Builder var5 = LocationCheck.checkLocation(
                  LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var0, var1)), new BlockPos(var2x.getUnitVec3i())
               );
               return AllOfCondition.allOf(var4, var5);
            }
         )
         .toArray(LootItemCondition.Builder[]::new);
      return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(AnyOfCondition.anyOf(var2));
   }

   private static Advancement.Builder smithingWithStyle(Advancement.Builder var0) {
      var0.requirements(AdvancementRequirements.Strategy.AND);
      Set var1 = Set.of(
         Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
         Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
         Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
         Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
         Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE,
         Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
         Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
         Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE
      );
      VanillaRecipeProvider.smithingTrims()
         .filter(var1x -> var1.contains(var1x.template()))
         .forEach(var1x -> var0.addCriterion("armor_trimmed_" + var1x.id().location(), RecipeCraftedTrigger.TriggerInstance.craftedItem(var1x.id())));
      return var0;
   }

   private static Advancement.Builder craftingANewLook(Advancement.Builder var0) {
      var0.requirements(AdvancementRequirements.Strategy.OR);
      VanillaRecipeProvider.smithingTrims()
         .map(VanillaRecipeProvider.TrimTemplate::id)
         .forEach(var1 -> var0.addCriterion("armor_trimmed_" + var1.location(), RecipeCraftedTrigger.TriggerInstance.craftedItem((ResourceKey<Recipe<?>>)var1)));
      return var0;
   }

   private static Advancement.Builder respectingTheRemnantsCriterions(HolderGetter<Item> var0, Advancement.Builder var1) {
      List var2 = List.of(
         Pair.of("desert_pyramid", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY)),
         Pair.of("desert_well", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY)),
         Pair.of("ocean_ruin_cold", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY)),
         Pair.of("ocean_ruin_warm", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY)),
         Pair.of("trail_ruins_rare", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE)),
         Pair.of("trail_ruins_common", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON))
      );
      var2.forEach(var1x -> var1.addCriterion((String)var1x.getFirst(), (Criterion<?>)var1x.getSecond()));
      String var3 = "has_sherd";
      var1.addCriterion("has_sherd", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(var0, ItemTags.DECORATED_POT_SHERDS)));
      var1.requirements(new AdvancementRequirements(List.of(var2.stream().map(Pair::getFirst).toList(), List.of("has_sherd"))));
      return var1;
   }

   protected static void createAdventuringTime(
      HolderLookup.Provider var0, Consumer<AdvancementHolder> var1, AdvancementHolder var2, MultiNoiseBiomeSourceParameterList.Preset var3
   ) {
      addBiomes(Advancement.Builder.advancement(), var0, var3.usedBiomes().toList())
         .parent(var2)
         .display(
            Items.DIAMOND_BOOTS,
            Component.translatable("advancements.adventure.adventuring_time.title"),
            Component.translatable("advancements.adventure.adventuring_time.description"),
            null,
            AdvancementType.CHALLENGE,
            true,
            true,
            false
         )
         .rewards(AdvancementRewards.Builder.experience(500))
         .save(var1, "adventure/adventuring_time");
   }

   private static Advancement.Builder addMobsToKill(Advancement.Builder var0, HolderGetter<EntityType<?>> var1, List<EntityType<?>> var2) {
      var2.forEach(
         var2x -> var0.addCriterion(
               BuiltInRegistries.ENTITY_TYPE.getKey(var2x).toString(),
               KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(var1, var2x))
            )
      );
      return var0;
   }

   protected static Advancement.Builder addBiomes(Advancement.Builder var0, HolderLookup.Provider var1, List<ResourceKey<Biome>> var2) {
      HolderLookup.RegistryLookup var3 = var1.lookupOrThrow(Registries.BIOME);

      for (ResourceKey var5 : var2) {
         var0.addCriterion(var5.location().toString(), PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inBiome(var3.getOrThrow(var5))));
      }

      return var0;
   }
}
