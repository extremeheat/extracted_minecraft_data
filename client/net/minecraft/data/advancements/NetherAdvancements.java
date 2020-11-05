package net.minecraft.data.advancements;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.BrewedPotionTrigger;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.ConstructBeaconTrigger;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemDurabilityTrigger;
import net.minecraft.advancements.critereon.ItemPickedUpByEntityTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnBlockTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.advancements.critereon.LootTableTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.NetherTravelTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;

public class NetherAdvancements implements Consumer<Consumer<Advancement>> {
   private static final List<ResourceKey<Biome>> EXPLORABLE_BIOMES;
   private static final EntityPredicate.Composite DISTRACT_PIGLIN_PLAYER_ARMOR_PREDICATE;

   public NetherAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.advancement().display((ItemLike)Blocks.RED_NETHER_BRICKS, new TranslatableComponent("advancements.nether.root.title"), new TranslatableComponent("advancements.nether.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/nether.png"), FrameType.TASK, false, false, false).addCriterion("entered_nether", (CriterionTriggerInstance)ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.NETHER)).save(var1, "nether/root");
      Advancement var3 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.FIRE_CHARGE, new TranslatableComponent("advancements.nether.return_to_sender.title"), new TranslatableComponent("advancements.nether.return_to_sender.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("killed_ghast", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.GHAST), DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.entity().of(EntityType.FIREBALL)))).save(var1, "nether/return_to_sender");
      Advancement var4 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Blocks.NETHER_BRICKS, new TranslatableComponent("advancements.nether.find_fortress.title"), new TranslatableComponent("advancements.nether.find_fortress.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("fortress", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(StructureFeature.NETHER_BRIDGE))).save(var1, "nether/find_fortress");
      Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.MAP, new TranslatableComponent("advancements.nether.fast_travel.title"), new TranslatableComponent("advancements.nether.fast_travel.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("travelled", (CriterionTriggerInstance)NetherTravelTrigger.TriggerInstance.travelledThroughNether(DistancePredicate.horizontal(MinMaxBounds.Floats.atLeast(7000.0F)))).save(var1, "nether/fast_travel");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.GHAST_TEAR, new TranslatableComponent("advancements.nether.uneasy_alliance.title"), new TranslatableComponent("advancements.nether.uneasy_alliance.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("killed_ghast", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.GHAST).located(LocationPredicate.inDimension(Level.OVERWORLD)))).save(var1, "nether/uneasy_alliance");
      Advancement var5 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Blocks.WITHER_SKELETON_SKULL, new TranslatableComponent("advancements.nether.get_wither_skull.title"), new TranslatableComponent("advancements.nether.get_wither_skull.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("wither_skull", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Blocks.WITHER_SKELETON_SKULL)).save(var1, "nether/get_wither_skull");
      Advancement var6 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.NETHER_STAR, new TranslatableComponent("advancements.nether.summon_wither.title"), new TranslatableComponent("advancements.nether.summon_wither.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("summoned", (CriterionTriggerInstance)SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.WITHER))).save(var1, "nether/summon_wither");
      Advancement var7 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.BLAZE_ROD, new TranslatableComponent("advancements.nether.obtain_blaze_rod.title"), new TranslatableComponent("advancements.nether.obtain_blaze_rod.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("blaze_rod", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.BLAZE_ROD)).save(var1, "nether/obtain_blaze_rod");
      Advancement var8 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Blocks.BEACON, new TranslatableComponent("advancements.nether.create_beacon.title"), new TranslatableComponent("advancements.nether.create_beacon.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("beacon", (CriterionTriggerInstance)ConstructBeaconTrigger.TriggerInstance.constructedBeacon(MinMaxBounds.Ints.atLeast(1))).save(var1, "nether/create_beacon");
      Advancement.Builder.advancement().parent(var8).display((ItemLike)Blocks.BEACON, new TranslatableComponent("advancements.nether.create_full_beacon.title"), new TranslatableComponent("advancements.nether.create_full_beacon.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("beacon", (CriterionTriggerInstance)ConstructBeaconTrigger.TriggerInstance.constructedBeacon(MinMaxBounds.Ints.exactly(4))).save(var1, "nether/create_full_beacon");
      Advancement var9 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.POTION, new TranslatableComponent("advancements.nether.brew_potion.title"), new TranslatableComponent("advancements.nether.brew_potion.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("potion", (CriterionTriggerInstance)BrewedPotionTrigger.TriggerInstance.brewedPotion()).save(var1, "nether/brew_potion");
      Advancement var10 = Advancement.Builder.advancement().parent(var9).display((ItemLike)Items.MILK_BUCKET, new TranslatableComponent("advancements.nether.all_potions.title"), new TranslatableComponent("advancements.nether.all_potions.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("all_effects", (CriterionTriggerInstance)EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.effects().and(MobEffects.MOVEMENT_SPEED).and(MobEffects.MOVEMENT_SLOWDOWN).and(MobEffects.DAMAGE_BOOST).and(MobEffects.JUMP).and(MobEffects.REGENERATION).and(MobEffects.FIRE_RESISTANCE).and(MobEffects.WATER_BREATHING).and(MobEffects.INVISIBILITY).and(MobEffects.NIGHT_VISION).and(MobEffects.WEAKNESS).and(MobEffects.POISON).and(MobEffects.SLOW_FALLING).and(MobEffects.DAMAGE_RESISTANCE))).save(var1, "nether/all_potions");
      Advancement.Builder.advancement().parent(var10).display((ItemLike)Items.BUCKET, new TranslatableComponent("advancements.nether.all_effects.title"), new TranslatableComponent("advancements.nether.all_effects.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(1000)).addCriterion("all_effects", (CriterionTriggerInstance)EffectsChangedTrigger.TriggerInstance.hasEffects(MobEffectsPredicate.effects().and(MobEffects.MOVEMENT_SPEED).and(MobEffects.MOVEMENT_SLOWDOWN).and(MobEffects.DAMAGE_BOOST).and(MobEffects.JUMP).and(MobEffects.REGENERATION).and(MobEffects.FIRE_RESISTANCE).and(MobEffects.WATER_BREATHING).and(MobEffects.INVISIBILITY).and(MobEffects.NIGHT_VISION).and(MobEffects.WEAKNESS).and(MobEffects.POISON).and(MobEffects.WITHER).and(MobEffects.DIG_SPEED).and(MobEffects.DIG_SLOWDOWN).and(MobEffects.LEVITATION).and(MobEffects.GLOWING).and(MobEffects.ABSORPTION).and(MobEffects.HUNGER).and(MobEffects.CONFUSION).and(MobEffects.DAMAGE_RESISTANCE).and(MobEffects.SLOW_FALLING).and(MobEffects.CONDUIT_POWER).and(MobEffects.DOLPHINS_GRACE).and(MobEffects.BLINDNESS).and(MobEffects.BAD_OMEN).and(MobEffects.HERO_OF_THE_VILLAGE))).save(var1, "nether/all_effects");
      Advancement var11 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.ANCIENT_DEBRIS, new TranslatableComponent("advancements.nether.obtain_ancient_debris.title"), new TranslatableComponent("advancements.nether.obtain_ancient_debris.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("ancient_debris", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.ANCIENT_DEBRIS)).save(var1, "nether/obtain_ancient_debris");
      Advancement.Builder.advancement().parent(var11).display((ItemLike)Items.NETHERITE_CHESTPLATE, new TranslatableComponent("advancements.nether.netherite_armor.title"), new TranslatableComponent("advancements.nether.netherite_armor.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("netherite_armor", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS)).save(var1, "nether/netherite_armor");
      Advancement.Builder.advancement().parent(var11).display((ItemLike)Items.LODESTONE, new TranslatableComponent("advancements.nether.use_lodestone.title"), new TranslatableComponent("advancements.nether.use_lodestone.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("use_lodestone", (CriterionTriggerInstance)ItemUsedOnBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.LODESTONE).build()), ItemPredicate.Builder.item().of((ItemLike)Items.COMPASS))).save(var1, "nether/use_lodestone");
      Advancement var12 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.CRYING_OBSIDIAN, new TranslatableComponent("advancements.nether.obtain_crying_obsidian.title"), new TranslatableComponent("advancements.nether.obtain_crying_obsidian.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("crying_obsidian", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.CRYING_OBSIDIAN)).save(var1, "nether/obtain_crying_obsidian");
      Advancement.Builder.advancement().parent(var12).display((ItemLike)Items.RESPAWN_ANCHOR, new TranslatableComponent("advancements.nether.charge_respawn_anchor.title"), new TranslatableComponent("advancements.nether.charge_respawn_anchor.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("charge_respawn_anchor", (CriterionTriggerInstance)ItemUsedOnBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.RESPAWN_ANCHOR).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(RespawnAnchorBlock.CHARGE, 4).build()).build()), ItemPredicate.Builder.item().of((ItemLike)Blocks.GLOWSTONE))).save(var1, "nether/charge_respawn_anchor");
      Advancement var13 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.WARPED_FUNGUS_ON_A_STICK, new TranslatableComponent("advancements.nether.ride_strider.title"), new TranslatableComponent("advancements.nether.ride_strider.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("used_warped_fungus_on_a_stick", (CriterionTriggerInstance)ItemDurabilityTrigger.TriggerInstance.changedDurability(EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity().of(EntityType.STRIDER).build()).build()), ItemPredicate.Builder.item().of((ItemLike)Items.WARPED_FUNGUS_ON_A_STICK).build(), MinMaxBounds.Ints.ANY)).save(var1, "nether/ride_strider");
      AdventureAdvancements.addBiomes(Advancement.Builder.advancement(), EXPLORABLE_BIOMES).parent(var13).display((ItemLike)Items.NETHERITE_BOOTS, new TranslatableComponent("advancements.nether.explore_nether.title"), new TranslatableComponent("advancements.nether.explore_nether.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(500)).save(var1, "nether/explore_nether");
      Advancement var14 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.POLISHED_BLACKSTONE_BRICKS, new TranslatableComponent("advancements.nether.find_bastion.title"), new TranslatableComponent("advancements.nether.find_bastion.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("bastion", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.located(LocationPredicate.inFeature(StructureFeature.BASTION_REMNANT))).save(var1, "nether/find_bastion");
      Advancement.Builder.advancement().parent(var14).display((ItemLike)Blocks.CHEST, new TranslatableComponent("advancements.nether.loot_bastion.title"), new TranslatableComponent("advancements.nether.loot_bastion.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("loot_bastion_other", (CriterionTriggerInstance)LootTableTrigger.TriggerInstance.lootTableUsed(new ResourceLocation("minecraft:chests/bastion_other"))).addCriterion("loot_bastion_treasure", (CriterionTriggerInstance)LootTableTrigger.TriggerInstance.lootTableUsed(new ResourceLocation("minecraft:chests/bastion_treasure"))).addCriterion("loot_bastion_hoglin_stable", (CriterionTriggerInstance)LootTableTrigger.TriggerInstance.lootTableUsed(new ResourceLocation("minecraft:chests/bastion_hoglin_stable"))).addCriterion("loot_bastion_bridge", (CriterionTriggerInstance)LootTableTrigger.TriggerInstance.lootTableUsed(new ResourceLocation("minecraft:chests/bastion_bridge"))).save(var1, "nether/loot_bastion");
      Advancement.Builder.advancement().parent(var2).requirements(RequirementsStrategy.OR).display((ItemLike)Items.GOLD_INGOT, new TranslatableComponent("advancements.nether.distract_piglin.title"), new TranslatableComponent("advancements.nether.distract_piglin.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("distract_piglin", (CriterionTriggerInstance)ItemPickedUpByEntityTrigger.TriggerInstance.itemPickedUpByEntity(DISTRACT_PIGLIN_PLAYER_ARMOR_PREDICATE, ItemPredicate.Builder.item().of((Tag)ItemTags.PIGLIN_LOVED), EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().of(EntityType.PIGLIN).flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false).build()).build()))).addCriterion("distract_piglin_directly", (CriterionTriggerInstance)PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(DISTRACT_PIGLIN_PLAYER_ARMOR_PREDICATE, ItemPredicate.Builder.item().of((ItemLike)PiglinAi.BARTERING_ITEM), EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().of(EntityType.PIGLIN).flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false).build()).build()))).save(var1, "nether/distract_piglin");
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }

   static {
      EXPLORABLE_BIOMES = ImmutableList.of(Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY, Biomes.WARPED_FOREST, Biomes.CRIMSON_FOREST, Biomes.BASALT_DELTAS);
      DISTRACT_PIGLIN_PLAYER_ARMOR_PREDICATE = EntityPredicate.Composite.create(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().head(ItemPredicate.Builder.item().of((ItemLike)Items.GOLDEN_HELMET).build()).build())).invert().build(), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().chest(ItemPredicate.Builder.item().of((ItemLike)Items.GOLDEN_CHESTPLATE).build()).build())).invert().build(), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().legs(ItemPredicate.Builder.item().of((ItemLike)Items.GOLDEN_LEGGINGS).build()).build())).invert().build(), LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().feet(ItemPredicate.Builder.item().of((ItemLike)Items.GOLDEN_BOOTS).build()).build())).invert().build());
   }
}
