package net.minecraft.data.advancements.packs;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.BlockPredicate;
import net.minecraft.advancements.criterion.ChanneledLightningTrigger;
import net.minecraft.advancements.criterion.DamagePredicate;
import net.minecraft.advancements.criterion.DamageSourcePredicate;
import net.minecraft.advancements.criterion.DataComponentMatchers;
import net.minecraft.advancements.criterion.DistancePredicate;
import net.minecraft.advancements.criterion.DistanceTrigger;
import net.minecraft.advancements.criterion.EntityEquipmentPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.FallAfterExplosionTrigger;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.criterion.KilledByArrowTrigger;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LightningBoltPredicate;
import net.minecraft.advancements.criterion.LightningStrikeTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.LootTableTrigger;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.PlayerHurtEntityTrigger;
import net.minecraft.advancements.criterion.PlayerInteractTrigger;
import net.minecraft.advancements.criterion.PlayerPredicate;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.advancements.criterion.RecipeCraftedTrigger;
import net.minecraft.advancements.criterion.ShotCrossbowTrigger;
import net.minecraft.advancements.criterion.SlideDownBlockTrigger;
import net.minecraft.advancements.criterion.SpearMobsTrigger;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.advancements.criterion.TagPredicate;
import net.minecraft.advancements.criterion.TargetBlockTrigger;
import net.minecraft.advancements.criterion.TradeTrigger;
import net.minecraft.advancements.criterion.UsedTotemTrigger;
import net.minecraft.advancements.criterion.UsingItemTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.JukeboxPlayablePredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.CreakingHeartBlock;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.PotDecorations;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.CreakingHeartState;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.AllOfCondition;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class VanillaAdventureAdvancements implements AdvancementSubProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DISTANCE_FROM_BOTTOM_TO_TOP = 384;
   private static final int Y_COORDINATE_AT_TOP = 320;
   private static final int Y_COORDINATE_AT_BOTTOM = -64;
   private static final int BEDROCK_THICKNESS = 5;
   private static final Map<MobCategory, Set<EntityType<?>>> EXCEPTIONS_BY_EXPECTED_CATEGORIES;
   private static final List<EntityType<?>> MOBS_TO_KILL;

   public VanillaAdventureAdvancements() {
      super();
   }

   private static Criterion<LightningStrikeTrigger.TriggerInstance> fireCountAndBystander(final MinMaxBounds.Ints fireCount, final Optional<EntityPredicate> bystander) {
      return LightningStrikeTrigger.TriggerInstance.lightningStrike(Optional.of(EntityPredicate.Builder.entity().distance(DistancePredicate.absolute(MinMaxBounds.Doubles.atMost(30.0))).subPredicate(LightningBoltPredicate.blockSetOnFire(fireCount)).build()), bystander);
   }

   private static Criterion<UsingItemTrigger.TriggerInstance> lookAtThroughItem(final EntityPredicate.Builder lookingAt, final ItemPredicate.Builder with) {
      return UsingItemTrigger.TriggerInstance.lookingAt(EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().setLookingAt(lookingAt).build()), with);
   }

   public void generate(final HolderLookup.Provider registries, final Consumer<AdvancementHolder> output) {
      HolderLookup<EntityType<?>> entityTypes = registries.lookupOrThrow(Registries.ENTITY_TYPE);
      HolderLookup<Item> items = registries.lookupOrThrow(Registries.ITEM);
      HolderLookup<Block> blocks = registries.lookupOrThrow(Registries.BLOCK);
      AdvancementHolder root = Advancement.Builder.advancement().display((ItemLike)Items.MAP, Component.translatable("advancements.adventure.root.title"), Component.translatable("advancements.adventure.root.description"), Identifier.withDefaultNamespace("gui/advancements/backgrounds/adventure"), AdvancementType.TASK, false, false, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("killed_something", KilledTrigger.TriggerInstance.playerKilledEntity()).addCriterion("killed_by_something", KilledTrigger.TriggerInstance.entityKilledPlayer()).save(output, "adventure/root");
      AdvancementHolder sleepInBed = Advancement.Builder.advancement().parent(root).display((ItemLike)Blocks.RED_BED, Component.translatable("advancements.adventure.sleep_in_bed.title"), Component.translatable("advancements.adventure.sleep_in_bed.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("slept_in_bed", PlayerTrigger.TriggerInstance.sleptInBed()).save(output, "adventure/sleep_in_bed");
      createAdventuringTime(registries, output, sleepInBed, MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD);
      AdvancementHolder trade = Advancement.Builder.advancement().parent(root).display((ItemLike)Items.EMERALD, Component.translatable("advancements.adventure.trade.title"), Component.translatable("advancements.adventure.trade.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("traded", TradeTrigger.TriggerInstance.tradedWithVillager()).save(output, "adventure/trade");
      Advancement.Builder.advancement().parent(trade).display((ItemLike)Items.EMERALD, Component.translatable("advancements.adventure.trade_at_world_height.title"), Component.translatable("advancements.adventure.trade_at_world_height.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("trade_at_world_height", TradeTrigger.TriggerInstance.tradedWithVillager(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atLeast(319.0))))).save(output, "adventure/trade_at_world_height");
      AdvancementHolder killAMob = createMonsterHunterAdvancement(root, output, entityTypes, validateMobsToKill(MOBS_TO_KILL, entityTypes));
      AdvancementHolder shootArrow = Advancement.Builder.advancement().parent(killAMob).display((ItemLike)Items.BOW, Component.translatable("advancements.adventure.shoot_arrow.title"), Component.translatable("advancements.adventure.shoot_arrow.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("shot_arrow", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE)).direct(EntityPredicate.Builder.entity().of(entityTypes, EntityTypeTags.ARROWS))))).save(output, "adventure/shoot_arrow");
      AdvancementHolder throwTrident = Advancement.Builder.advancement().parent(killAMob).display((ItemLike)Items.TRIDENT, Component.translatable("advancements.adventure.throw_trident.title"), Component.translatable("advancements.adventure.throw_trident.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("shot_trident", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE)).direct(EntityPredicate.Builder.entity().of(entityTypes, EntityType.TRIDENT))))).save(output, "adventure/throw_trident");
      Advancement.Builder.advancement().parent(throwTrident).display((ItemLike)Items.TRIDENT, Component.translatable("advancements.adventure.very_very_frightening.title"), Component.translatable("advancements.adventure.very_very_frightening.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("struck_villager", ChanneledLightningTrigger.TriggerInstance.channeledLightning(EntityPredicate.Builder.entity().of(entityTypes, EntityType.VILLAGER))).save(output, "adventure/very_very_frightening");
      Advancement.Builder.advancement().parent(trade).display((ItemLike)Blocks.CARVED_PUMPKIN, Component.translatable("advancements.adventure.summon_iron_golem.title"), Component.translatable("advancements.adventure.summon_iron_golem.description"), (Identifier)null, AdvancementType.GOAL, true, true, false).addCriterion("summoned_golem", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.IRON_GOLEM))).save(output, "adventure/summon_iron_golem");
      Advancement.Builder.advancement().parent(shootArrow).display((ItemLike)Items.ARROW, Component.translatable("advancements.adventure.sniper_duel.title"), Component.translatable("advancements.adventure.sniper_duel.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("killed_skeleton", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.SKELETON).distance(DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(50.0))), DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE)))).save(output, "adventure/sniper_duel");
      Advancement.Builder.advancement().parent(killAMob).display((ItemLike)Items.TOTEM_OF_UNDYING, Component.translatable("advancements.adventure.totem_of_undying.title"), Component.translatable("advancements.adventure.totem_of_undying.description"), (Identifier)null, AdvancementType.GOAL, true, true, false).addCriterion("used_totem", UsedTotemTrigger.TriggerInstance.usedTotem(items, Items.TOTEM_OF_UNDYING)).save(output, "adventure/totem_of_undying");
      Advancement.Builder.advancement().parent(killAMob).display((ItemLike)Items.IRON_SPEAR, Component.translatable("advancements.adventure.spear_many_mobs.title"), Component.translatable("advancements.adventure.spear_many_mobs.description"), (Identifier)null, AdvancementType.GOAL, true, true, false).addCriterion("spear_many_mobs", SpearMobsTrigger.TriggerInstance.spearMobs(5)).save(output, "adventure/spear_many_mobs");
      AdvancementHolder olBetsy = Advancement.Builder.advancement().parent(root).display((ItemLike)Items.CROSSBOW, Component.translatable("advancements.adventure.ol_betsy.title"), Component.translatable("advancements.adventure.ol_betsy.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("shot_crossbow", ShotCrossbowTrigger.TriggerInstance.shotCrossbow(items, Items.CROSSBOW)).save(output, "adventure/ol_betsy");
      Advancement.Builder.advancement().parent(olBetsy).display((ItemLike)Items.CROSSBOW, Component.translatable("advancements.adventure.whos_the_pillager_now.title"), Component.translatable("advancements.adventure.whos_the_pillager_now.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("kill_pillager", KilledByArrowTrigger.TriggerInstance.crossbowKilled(items, (EntityPredicate.Builder[])(EntityPredicate.Builder.entity().of(entityTypes, EntityType.PILLAGER)))).save(output, "adventure/whos_the_pillager_now");
      Advancement.Builder.advancement().parent(olBetsy).display((ItemLike)Items.CROSSBOW, Component.translatable("advancements.adventure.two_birds_one_arrow.title"), Component.translatable("advancements.adventure.two_birds_one_arrow.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(65)).addCriterion("two_birds", KilledByArrowTrigger.TriggerInstance.crossbowKilled(items, (EntityPredicate.Builder[])(EntityPredicate.Builder.entity().of(entityTypes, EntityType.PHANTOM), EntityPredicate.Builder.entity().of(entityTypes, EntityType.PHANTOM)))).save(output, "adventure/two_birds_one_arrow");
      Advancement.Builder.advancement().parent(olBetsy).display((ItemLike)Items.CROSSBOW, Component.translatable("advancements.adventure.arbalistic.title"), Component.translatable("advancements.adventure.arbalistic.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(85)).addCriterion("arbalistic", KilledByArrowTrigger.TriggerInstance.crossbowKilled(items, (MinMaxBounds.Ints)MinMaxBounds.Ints.exactly(5))).save(output, "adventure/arbalistic");
      HolderLookup.RegistryLookup<BannerPattern> patternLookup = registries.lookupOrThrow(Registries.BANNER_PATTERN);
      AdvancementHolder raidOmen = Advancement.Builder.advancement().parent(root).display((ItemStackTemplate)Raid.getOminousBannerTemplate(patternLookup), Component.translatable("advancements.adventure.voluntary_exile.title"), Component.translatable("advancements.adventure.voluntary_exile.description"), (Identifier)null, AdvancementType.TASK, true, true, true).addCriterion("voluntary_exile", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.captainPredicate(items, patternLookup)))).save(output, "adventure/voluntary_exile");
      Advancement.Builder.advancement().parent(raidOmen).display((ItemStackTemplate)Raid.getOminousBannerTemplate(patternLookup), Component.translatable("advancements.adventure.hero_of_the_village.title"), Component.translatable("advancements.adventure.hero_of_the_village.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("hero_of_the_village", PlayerTrigger.TriggerInstance.raidWon()).save(output, "adventure/hero_of_the_village");
      Advancement.Builder.advancement().parent(root).display((ItemLike)Items.HONEY_BLOCK, Component.translatable("advancements.adventure.honey_block_slide.title"), Component.translatable("advancements.adventure.honey_block_slide.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("honey_block_slide", SlideDownBlockTrigger.TriggerInstance.slidesDownBlock(Blocks.HONEY_BLOCK)).save(output, "adventure/honey_block_slide");
      Advancement.Builder.advancement().parent(shootArrow).display((ItemLike)Items.TARGET, Component.translatable("advancements.adventure.bullseye.title"), Component.translatable("advancements.adventure.bullseye.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("bullseye", TargetBlockTrigger.TriggerInstance.targetHit(MinMaxBounds.Ints.exactly(15), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().distance(DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(30.0))))))).save(output, "adventure/bullseye");
      Advancement.Builder.advancement().parent(sleepInBed).display((ItemLike)Items.LEATHER_BOOTS, Component.translatable("advancements.adventure.walk_on_powder_snow_with_leather_boots.title"), Component.translatable("advancements.adventure.walk_on_powder_snow_with_leather_boots.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("walk_on_powder_snow_with_leather_boots", PlayerTrigger.TriggerInstance.walkOnBlockWithEquipment(blocks, items, Blocks.POWDER_SNOW, Items.LEATHER_BOOTS)).save(output, "adventure/walk_on_powder_snow_with_leather_boots");
      Advancement.Builder.advancement().parent(root).display((ItemLike)Items.LIGHTNING_ROD, Component.translatable("advancements.adventure.lightning_rod_with_villager_no_fire.title"), Component.translatable("advancements.adventure.lightning_rod_with_villager_no_fire.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("lightning_rod_with_villager_no_fire", fireCountAndBystander(MinMaxBounds.Ints.exactly(0), Optional.of(EntityPredicate.Builder.entity().of(entityTypes, EntityType.VILLAGER).build()))).save(output, "adventure/lightning_rod_with_villager_no_fire");
      AdvancementHolder isItABird = Advancement.Builder.advancement().parent(root).display((ItemLike)Items.SPYGLASS, Component.translatable("advancements.adventure.spyglass_at_parrot.title"), Component.translatable("advancements.adventure.spyglass_at_parrot.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("spyglass_at_parrot", lookAtThroughItem(EntityPredicate.Builder.entity().of(entityTypes, EntityType.PARROT), ItemPredicate.Builder.item().of(items, Items.SPYGLASS))).save(output, "adventure/spyglass_at_parrot");
      AdvancementHolder isItABalloon = Advancement.Builder.advancement().parent(isItABird).display((ItemLike)Items.SPYGLASS, Component.translatable("advancements.adventure.spyglass_at_ghast.title"), Component.translatable("advancements.adventure.spyglass_at_ghast.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("spyglass_at_ghast", lookAtThroughItem(EntityPredicate.Builder.entity().of(entityTypes, EntityType.GHAST), ItemPredicate.Builder.item().of(items, Items.SPYGLASS))).save(output, "adventure/spyglass_at_ghast");
      Advancement.Builder.advancement().parent(sleepInBed).display((ItemLike)Items.JUKEBOX, Component.translatable("advancements.adventure.play_jukebox_in_meadows.title"), Component.translatable("advancements.adventure.play_jukebox_in_meadows.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("play_jukebox_in_meadows", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBiomes(HolderSet.direct(registries.lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.MEADOW))).setBlock(BlockPredicate.Builder.block().of(blocks, Blocks.JUKEBOX)), ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.JUKEBOX_PLAYABLE, JukeboxPlayablePredicate.any()).build()))).save(output, "adventure/play_jukebox_in_meadows");
      Advancement.Builder.advancement().parent(isItABalloon).display((ItemLike)Items.SPYGLASS, Component.translatable("advancements.adventure.spyglass_at_dragon.title"), Component.translatable("advancements.adventure.spyglass_at_dragon.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("spyglass_at_dragon", lookAtThroughItem(EntityPredicate.Builder.entity().of(entityTypes, EntityType.ENDER_DRAGON), ItemPredicate.Builder.item().of(items, Items.SPYGLASS))).save(output, "adventure/spyglass_at_dragon");
      Advancement.Builder.advancement().parent(root).display((ItemLike)Items.WATER_BUCKET, Component.translatable("advancements.adventure.fall_from_world_height.title"), Component.translatable("advancements.adventure.fall_from_world_height.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("fall_from_world_height", DistanceTrigger.TriggerInstance.fallFromHeight(EntityPredicate.Builder.entity().located(LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atMost(-59.0))), DistancePredicate.vertical(MinMaxBounds.Doubles.atLeast(379.0)), LocationPredicate.Builder.atYLocation(MinMaxBounds.Doubles.atLeast(319.0)))).save(output, "adventure/fall_from_world_height");
      Advancement.Builder.advancement().parent(killAMob).display((ItemLike)Blocks.SCULK_CATALYST, Component.translatable("advancements.adventure.kill_mob_near_sculk_catalyst.title"), Component.translatable("advancements.adventure.kill_mob_near_sculk_catalyst.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("kill_mob_near_sculk_catalyst", KilledTrigger.TriggerInstance.playerKilledEntityNearSculkCatalyst()).save(output, "adventure/kill_mob_near_sculk_catalyst");
      Advancement.Builder.advancement().parent(root).display((ItemLike)Blocks.SCULK_SENSOR, Component.translatable("advancements.adventure.avoid_vibration.title"), Component.translatable("advancements.adventure.avoid_vibration.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("avoid_vibration", PlayerTrigger.TriggerInstance.avoidVibration()).save(output, "adventure/avoid_vibration");
      AdvancementHolder respectingTheRemnants = respectingTheRemnantsCriterions(items, Advancement.Builder.advancement()).parent(root).display((ItemLike)Items.BRUSH, Component.translatable("advancements.adventure.salvage_sherd.title"), Component.translatable("advancements.adventure.salvage_sherd.description"), (Identifier)null, AdvancementType.TASK, true, true, false).save(output, "adventure/salvage_sherd");
      Advancement.Builder.advancement().parent(respectingTheRemnants).display((ItemStackTemplate)DecoratedPotBlockEntity.createDecoratedPotTemplate(new PotDecorations(Optional.empty(), Optional.of(Items.HEART_POTTERY_SHERD), Optional.empty(), Optional.of(Items.EXPLORER_POTTERY_SHERD))), Component.translatable("advancements.adventure.craft_decorated_pot_using_only_sherds.title"), Component.translatable("advancements.adventure.craft_decorated_pot_using_only_sherds.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("pot_crafted_using_only_sherds", RecipeCraftedTrigger.TriggerInstance.craftedItem(ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace("decorated_pot")), List.of(ItemPredicate.Builder.item().of(items, (TagKey)ItemTags.DECORATED_POT_SHERDS), ItemPredicate.Builder.item().of(items, (TagKey)ItemTags.DECORATED_POT_SHERDS), ItemPredicate.Builder.item().of(items, (TagKey)ItemTags.DECORATED_POT_SHERDS), ItemPredicate.Builder.item().of(items, (TagKey)ItemTags.DECORATED_POT_SHERDS)))).save(output, "adventure/craft_decorated_pot_using_only_sherds");
      AdvancementHolder craftingANewLook = craftingANewLook(Advancement.Builder.advancement()).parent(root).display((ItemLike)Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Component.translatable("advancements.adventure.trim_with_any_armor_pattern.title"), Component.translatable("advancements.adventure.trim_with_any_armor_pattern.description"), (Identifier)null, AdvancementType.TASK, true, true, false).save(output, "adventure/trim_with_any_armor_pattern");
      smithingWithStyle(Advancement.Builder.advancement()).parent(craftingANewLook).display((ItemLike)Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Component.translatable("advancements.adventure.trim_with_all_exclusive_armor_patterns.title"), Component.translatable("advancements.adventure.trim_with_all_exclusive_armor_patterns.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(150)).save(output, "adventure/trim_with_all_exclusive_armor_patterns");
      Advancement.Builder.advancement().parent(root).display((ItemLike)Items.CHISELED_BOOKSHELF, Component.translatable("advancements.adventure.read_power_from_chiseled_bookshelf.title"), Component.translatable("advancements.adventure.read_power_from_chiseled_bookshelf.description"), (Identifier)null, AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("chiseled_bookshelf", placedBlockReadByComparator(blocks, Blocks.CHISELED_BOOKSHELF)).addCriterion("comparator", placedComparatorReadingBlock(blocks, Blocks.CHISELED_BOOKSHELF)).save(output, "adventure/read_power_of_chiseled_bookshelf");
      Advancement.Builder.advancement().parent(root).display((ItemLike)Items.ARMADILLO_SCUTE, Component.translatable("advancements.adventure.brush_armadillo.title"), Component.translatable("advancements.adventure.brush_armadillo.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("brush_armadillo", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(items, Items.BRUSH), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityType.ARMADILLO))))).save(output, "adventure/brush_armadillo");
      AdvancementHolder trialsEdition = Advancement.Builder.advancement().parent(root).display((ItemLike)Blocks.CHISELED_TUFF, Component.translatable("advancements.adventure.minecraft_trials_edition.title"), Component.translatable("advancements.adventure.minecraft_trials_edition.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("minecraft_trials_edition", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.TRIAL_CHAMBERS)))).save(output, "adventure/minecraft_trials_edition");
      Advancement.Builder.advancement().parent(trialsEdition).display((ItemLike)Items.COPPER_BULB, Component.translatable("advancements.adventure.lighten_up.title"), Component.translatable("advancements.adventure.lighten_up.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("lighten_up", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, Blocks.OXIDIZED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB, Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CopperBulbBlock.LIT, true))), ItemPredicate.Builder.item().of(items, VanillaHusbandryAdvancements.WAX_SCRAPING_TOOLS))).save(output, "adventure/lighten_up");
      AdvancementHolder underLockAndKey = Advancement.Builder.advancement().parent(trialsEdition).display((ItemLike)Items.TRIAL_KEY, Component.translatable("advancements.adventure.under_lock_and_key.title"), Component.translatable("advancements.adventure.under_lock_and_key.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("under_lock_and_key", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, Blocks.VAULT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VaultBlock.OMINOUS, false))), ItemPredicate.Builder.item().of(items, Items.TRIAL_KEY))).save(output, "adventure/under_lock_and_key");
      Advancement.Builder.advancement().parent(underLockAndKey).display((ItemLike)Items.OMINOUS_TRIAL_KEY, Component.translatable("advancements.adventure.revaulting.title"), Component.translatable("advancements.adventure.revaulting.description"), (Identifier)null, AdvancementType.GOAL, true, true, false).addCriterion("revaulting", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, Blocks.VAULT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VaultBlock.OMINOUS, true))), ItemPredicate.Builder.item().of(items, Items.OMINOUS_TRIAL_KEY))).save(output, "adventure/revaulting");
      Advancement.Builder.advancement().parent(trialsEdition).display((ItemLike)Items.WIND_CHARGE, Component.translatable("advancements.adventure.blowback.title"), Component.translatable("advancements.adventure.blowback.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(40)).addCriterion("blowback", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.BREEZE), DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE)).direct(EntityPredicate.Builder.entity().of(entityTypes, EntityType.BREEZE_WIND_CHARGE)))).save(output, "adventure/blowback");
      Advancement.Builder.advancement().parent(root).display((ItemLike)Items.CRAFTER, Component.translatable("advancements.adventure.crafters_crafting_crafters.title"), Component.translatable("advancements.adventure.crafters_crafting_crafters.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("crafter_crafted_crafter", RecipeCraftedTrigger.TriggerInstance.crafterCraftedItem(ResourceKey.create(Registries.RECIPE, Identifier.withDefaultNamespace("crafter")))).save(output, "adventure/crafters_crafting_crafters");
      Advancement.Builder.advancement().parent(root).display((ItemLike)Items.LODESTONE, Component.translatable("advancements.adventure.use_lodestone.title"), Component.translatable("advancements.adventure.use_lodestone.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("use_lodestone", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, Blocks.LODESTONE)), ItemPredicate.Builder.item().of(items, Items.COMPASS))).save(output, "adventure/use_lodestone");
      Advancement.Builder.advancement().parent(trialsEdition).display((ItemLike)Items.WIND_CHARGE, Component.translatable("advancements.adventure.who_needs_rockets.title"), Component.translatable("advancements.adventure.who_needs_rockets.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("who_needs_rockets", FallAfterExplosionTrigger.TriggerInstance.fallAfterExplosion(DistancePredicate.vertical(MinMaxBounds.Doubles.atLeast(7.0)), EntityPredicate.Builder.entity().of(entityTypes, EntityType.WIND_CHARGE))).save(output, "adventure/who_needs_rockets");
      Advancement.Builder.advancement().parent(trialsEdition).display((ItemLike)Items.MACE, Component.translatable("advancements.adventure.overoverkill.title"), Component.translatable("advancements.adventure.overoverkill.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("overoverkill", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().dealtDamage(MinMaxBounds.Doubles.atLeast(100.0)).type(DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_MACE_SMASH)).direct(EntityPredicate.Builder.entity().of(entityTypes, EntityType.PLAYER).equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(items, Items.MACE))))))).save(output, "adventure/overoverkill");
      Advancement.Builder.advancement().parent(root).display((ItemLike)Blocks.CREAKING_HEART, Component.translatable("advancements.adventure.heart_transplanter.title"), Component.translatable("advancements.adventure.heart_transplanter.description"), (Identifier)null, AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("place_creaking_heart_dormant", ItemUsedOnLocationTrigger.TriggerInstance.placedBlockWithProperties(Blocks.CREAKING_HEART, BlockStateProperties.CREAKING_HEART_STATE, (Comparable)CreakingHeartState.DORMANT)).addCriterion("place_creaking_heart_awake", ItemUsedOnLocationTrigger.TriggerInstance.placedBlockWithProperties(Blocks.CREAKING_HEART, BlockStateProperties.CREAKING_HEART_STATE, (Comparable)CreakingHeartState.AWAKE)).addCriterion("place_pale_oak_log", placedBlockActivatesCreakingHeart(blocks, BlockTags.PALE_OAK_LOGS)).save(output, "adventure/heart_transplanter");
   }

   public static AdvancementHolder createMonsterHunterAdvancement(final AdvancementHolder parent, final Consumer<AdvancementHolder> output, final HolderGetter<EntityType<?>> entityTypes, final List<EntityType<?>> mobsToKill) {
      AdvancementHolder killAMob = addMobsToKill(Advancement.Builder.advancement(), entityTypes, mobsToKill).parent(parent).display((ItemLike)Items.IRON_SWORD, Component.translatable("advancements.adventure.kill_a_mob.title"), Component.translatable("advancements.adventure.kill_a_mob.description"), (Identifier)null, AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).save(output, "adventure/kill_a_mob");
      addMobsToKill(Advancement.Builder.advancement(), entityTypes, mobsToKill).parent(killAMob).display((ItemLike)Items.DIAMOND_SWORD, Component.translatable("advancements.adventure.kill_all_mobs.title"), Component.translatable("advancements.adventure.kill_all_mobs.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(output, "adventure/kill_all_mobs");
      return killAMob;
   }

   private static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedBlockReadByComparator(final HolderGetter<Block> blocks, final Block block) {
      LootItemCondition.Builder[] conditions = (LootItemCondition.Builder[])ComparatorBlock.FACING.getPossibleValues().stream().map((direction) -> {
         StatePropertiesPredicate.Builder comparatorProperties = StatePropertiesPredicate.Builder.properties().hasProperty(ComparatorBlock.FACING, (Comparable)direction);
         BlockPredicate.Builder comparatorTest = BlockPredicate.Builder.block().of(blocks, Blocks.COMPARATOR).setProperties(comparatorProperties);
         return LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(comparatorTest), new BlockPos(direction.getOpposite().getUnitVec3i()));
      }).toArray((x$0) -> new LootItemCondition.Builder[x$0]);
      return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(LootItemBlockStatePropertyCondition.hasBlockStateProperties(block), AnyOfCondition.anyOf(conditions));
   }

   private static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedComparatorReadingBlock(final HolderGetter<Block> blocks, final Block block) {
      LootItemCondition.Builder[] conditions = (LootItemCondition.Builder[])ComparatorBlock.FACING.getPossibleValues().stream().map((direction) -> {
         StatePropertiesPredicate.Builder comparatorProperties = StatePropertiesPredicate.Builder.properties().hasProperty(ComparatorBlock.FACING, (Comparable)direction);
         LootItemBlockStatePropertyCondition.Builder comparatorTest = (new LootItemBlockStatePropertyCondition.Builder(Blocks.COMPARATOR)).setProperties(comparatorProperties);
         LootItemCondition.Builder blockTest = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, block)), new BlockPos(direction.getUnitVec3i()));
         return AllOfCondition.allOf(comparatorTest, blockTest);
      }).toArray((x$0) -> new LootItemCondition.Builder[x$0]);
      return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(AnyOfCondition.anyOf(conditions));
   }

   private static Criterion<ItemUsedOnLocationTrigger.TriggerInstance> placedBlockActivatesCreakingHeart(final HolderGetter<Block> blocks, final TagKey<Block> block) {
      LootItemCondition.Builder[] conditions = (LootItemCondition.Builder[])Stream.of(Direction.values()).map((direction) -> {
         StatePropertiesPredicate.Builder creakingHeartProperties = StatePropertiesPredicate.Builder.properties().hasProperty(CreakingHeartBlock.AXIS, (Comparable)direction.getAxis());
         BlockPredicate.Builder placedPaleOakLogBlock = BlockPredicate.Builder.block().of(blocks, block).setProperties(creakingHeartProperties);
         Vec3i blockOffset = direction.getUnitVec3i();
         LootItemCondition.Builder placedPaleOakLogTest = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(placedPaleOakLogBlock));
         LootItemCondition.Builder creakingHeartBlockTest = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(blocks, Blocks.CREAKING_HEART).setProperties(creakingHeartProperties)), new BlockPos(blockOffset));
         LootItemCondition.Builder existingPaleOakLogTest = LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(placedPaleOakLogBlock), new BlockPos(blockOffset.multiply(2)));
         return AllOfCondition.allOf(placedPaleOakLogTest, creakingHeartBlockTest, existingPaleOakLogTest);
      }).toArray((x$0) -> new LootItemCondition.Builder[x$0]);
      return ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(AnyOfCondition.anyOf(conditions));
   }

   private static Advancement.Builder smithingWithStyle(final Advancement.Builder advancement) {
      advancement.requirements(AdvancementRequirements.Strategy.AND);
      Set<Item> required = Set.of(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE);
      VanillaRecipeProvider.smithingTrims().filter((trim) -> required.contains(trim.template())).forEach((trimTemplate) -> advancement.addCriterion("armor_trimmed_" + String.valueOf(trimTemplate.recipeId().identifier()), RecipeCraftedTrigger.TriggerInstance.craftedItem(trimTemplate.recipeId())));
      return advancement;
   }

   private static Advancement.Builder craftingANewLook(final Advancement.Builder advancement) {
      advancement.requirements(AdvancementRequirements.Strategy.OR);
      VanillaRecipeProvider.smithingTrims().map(VanillaRecipeProvider.TrimTemplate::recipeId).forEach((recipeId) -> advancement.addCriterion("armor_trimmed_" + String.valueOf(recipeId.identifier()), RecipeCraftedTrigger.TriggerInstance.craftedItem(recipeId)));
      return advancement;
   }

   private static Advancement.Builder respectingTheRemnantsCriterions(final HolderGetter<Item> items, final Advancement.Builder advancement) {
      List<Pair<String, Criterion<LootTableTrigger.TriggerInstance>>> lootCriteria = List.of(Pair.of("desert_pyramid", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY)), Pair.of("desert_well", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.DESERT_WELL_ARCHAEOLOGY)), Pair.of("ocean_ruin_cold", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY)), Pair.of("ocean_ruin_warm", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY)), Pair.of("trail_ruins_rare", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE)), Pair.of("trail_ruins_common", LootTableTrigger.TriggerInstance.lootTableUsed(BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON)));
      lootCriteria.forEach((p) -> advancement.addCriterion((String)p.getFirst(), (Criterion)p.getSecond()));
      String hasSherdCriterion = "has_sherd";
      advancement.addCriterion("has_sherd", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, ItemTags.DECORATED_POT_SHERDS)));
      advancement.requirements(new AdvancementRequirements(List.of(lootCriteria.stream().map(Pair::getFirst).toList(), List.of("has_sherd"))));
      return advancement;
   }

   protected static void createAdventuringTime(final HolderLookup.Provider registries, final Consumer<AdvancementHolder> output, final AdvancementHolder sleepInBed, final MultiNoiseBiomeSourceParameterList.Preset preset) {
      addBiomes(Advancement.Builder.advancement(), registries, preset.usedBiomes().toList()).parent(sleepInBed).display((ItemLike)Items.DIAMOND_BOOTS, Component.translatable("advancements.adventure.adventuring_time.title"), Component.translatable("advancements.adventure.adventuring_time.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(500)).save(output, "adventure/adventuring_time");
   }

   private static Advancement.Builder addMobsToKill(final Advancement.Builder advancement, final HolderGetter<EntityType<?>> entityTypes, final List<EntityType<?>> mobsToKill) {
      mobsToKill.forEach((mob) -> advancement.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(mob).toString(), KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityTypes, mob))));
      return advancement;
   }

   protected static Advancement.Builder addBiomes(final Advancement.Builder advancement, final HolderLookup.Provider registries, final List<ResourceKey<Biome>> explorableBiomes) {
      HolderGetter<Biome> biomeRegistry = registries.lookupOrThrow(Registries.BIOME);

      for(ResourceKey<Biome> biome : explorableBiomes) {
         advancement.addCriterion(biome.identifier().toString(), PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inBiome(biomeRegistry.getOrThrow(biome))));
      }

      return advancement;
   }

   private static List<EntityType<?>> validateMobsToKill(final List<EntityType<?>> data, final HolderLookup<EntityType<?>> entityTypes) {
      List<String> errors = new ArrayList();
      Set<? extends EntityType<?>> mobsToKill = Set.copyOf(data);
      Set<MobCategory> specifiedCategories = (Set)mobsToKill.stream().map(EntityType::getCategory).collect(Collectors.toSet());
      Set<MobCategory> categoryDifference = Sets.symmetricDifference(EXCEPTIONS_BY_EXPECTED_CATEGORIES.keySet(), specifiedCategories);
      if (!categoryDifference.isEmpty()) {
         Stream var10001 = categoryDifference.stream().map(Object::toString).sorted();
         errors.add("Found EntityType with MobCategory only in either expected exceptions or kill_all_mobs advancement: " + (String)var10001.collect(Collectors.joining(", ")));
      }

      Set<EntityType<?>> entityTypeOverlap = Sets.intersection((Set)EXCEPTIONS_BY_EXPECTED_CATEGORIES.values().stream().flatMap(Collection::stream).collect(Collectors.toSet()), mobsToKill);
      if (!entityTypeOverlap.isEmpty()) {
         Stream var8 = entityTypeOverlap.stream().map(Object::toString).sorted();
         errors.add("Found EntityType in both expected exceptions and kill_all_mobs advancement: " + (String)var8.collect(Collectors.joining(", ")));
      }

      Stream var10000 = entityTypes.listElements().map(Holder.Reference::value);
      Objects.requireNonNull(mobsToKill);
      Map<MobCategory, Set<EntityType<?>>> doNotKillByCategory = (Map)var10000.filter(Predicate.not(mobsToKill::contains)).collect(Collectors.groupingBy(EntityType::getCategory, Collectors.toSet()));
      EXCEPTIONS_BY_EXPECTED_CATEGORIES.forEach((exceptedCategory, exceptedTypes) -> {
         Set<EntityType<?>> exceptedDiff = Sets.difference((Set)doNotKillByCategory.getOrDefault(exceptedCategory, Set.of()), exceptedTypes);
         if (!exceptedDiff.isEmpty()) {
            errors.add(String.format(Locale.ROOT, "Found (new?) EntityType with MobCategory %s which are in neither expected exceptions nor kill_all_mobs advancement: %s", exceptedCategory, exceptedDiff.stream().map(Object::toString).sorted().collect(Collectors.joining(", "))));
         }

      });
      if (!errors.isEmpty()) {
         Logger var9 = LOGGER;
         Objects.requireNonNull(var9);
         errors.forEach(var9::error);
         throw new IllegalStateException("Found inconsistencies with kill_all_mobs advancement");
      } else {
         return data;
      }
   }

   static {
      EXCEPTIONS_BY_EXPECTED_CATEGORIES = Map.of(MobCategory.MONSTER, Set.of(EntityType.GIANT, EntityType.ILLUSIONER, EntityType.WARDEN));
      MOBS_TO_KILL = Arrays.asList(EntityType.BLAZE, EntityType.BOGGED, EntityType.BREEZE, EntityType.CAMEL_HUSK, EntityType.CAVE_SPIDER, EntityType.CREAKING, EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HOGLIN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.PARCHED, EntityType.PHANTOM, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.STRAY, EntityType.VEX, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.ZOGLIN, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE, EntityType.ZOMBIE_HORSE, EntityType.ZOMBIFIED_PIGLIN, EntityType.ZOMBIE_NAUTILUS);
   }
}
