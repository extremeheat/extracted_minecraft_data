package net.minecraft.data.advancements.packs;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FallAfterExplosionTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.RecipeCraftedTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.VaultBlock;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;

public class UpdateOneTwentyOneAdventureAdvancements implements AdvancementSubProvider {
   public UpdateOneTwentyOneAdventureAdvancements() {
      super();
   }

   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      AdvancementHolder var3 = AdvancementSubProvider.createPlaceholder("adventure/root");
      VanillaAdventureAdvancements.createMonsterHunterAdvancement(var3, var2, (List)Stream.concat(VanillaAdventureAdvancements.MOBS_TO_KILL.stream(), Stream.of(EntityType.BREEZE, EntityType.BOGGED)).collect(Collectors.toList()));
      AdvancementHolder var4 = Advancement.Builder.advancement().parent(var3).display((ItemLike)Blocks.CHISELED_TUFF, Component.translatable("advancements.adventure.minecraft_trials_edition.title"), Component.translatable("advancements.adventure.minecraft_trials_edition.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("minecraft_trials_edition", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(var1.lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.TRIAL_CHAMBERS)))).save(var2, "adventure/minecraft_trials_edition");
      Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.COPPER_BULB, Component.translatable("advancements.adventure.lighten_up.title"), Component.translatable("advancements.adventure.lighten_up.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("lighten_up", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.OXIDIZED_COPPER_BULB, Blocks.WEATHERED_COPPER_BULB, Blocks.EXPOSED_COPPER_BULB, Blocks.WAXED_OXIDIZED_COPPER_BULB, Blocks.WAXED_WEATHERED_COPPER_BULB, Blocks.WAXED_EXPOSED_COPPER_BULB).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CopperBulbBlock.LIT, true))), ItemPredicate.Builder.item().of((ItemLike[])VanillaHusbandryAdvancements.WAX_SCRAPING_TOOLS))).save(var2, "adventure/lighten_up");
      AdvancementHolder var5 = Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.TRIAL_KEY, Component.translatable("advancements.adventure.under_lock_and_key.title"), Component.translatable("advancements.adventure.under_lock_and_key.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("under_lock_and_key", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.VAULT)), ItemPredicate.Builder.item().of(Items.TRIAL_KEY))).save(var2, "adventure/under_lock_and_key");
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.OMINOUS_TRIAL_KEY, Component.translatable("advancements.adventure.revaulting.title"), Component.translatable("advancements.adventure.revaulting.description"), (ResourceLocation)null, AdvancementType.GOAL, true, true, false).addCriterion("revaulting", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.VAULT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(VaultBlock.OMINOUS, true))), ItemPredicate.Builder.item().of(Items.OMINOUS_TRIAL_KEY))).save(var2, "adventure/revaulting");
      Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.WIND_CHARGE, Component.translatable("advancements.adventure.blowback.title"), Component.translatable("advancements.adventure.blowback.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(40)).addCriterion("blowback", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.BREEZE), DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE)).direct(EntityPredicate.Builder.entity().of(EntityType.BREEZE_WIND_CHARGE)))).save(var2, "adventure/blowback");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.CRAFTER, Component.translatable("advancements.adventure.crafters_crafting_crafters.title"), Component.translatable("advancements.adventure.crafters_crafting_crafters.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("crafter_crafted_crafter", RecipeCraftedTrigger.TriggerInstance.crafterCraftedItem(new ResourceLocation("minecraft:crafter"))).save(var2, "adventure/crafters_crafting_crafters");
      Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.WIND_CHARGE, Component.translatable("advancements.adventure.who_needs_rockets.title"), Component.translatable("advancements.adventure.who_needs_rockets.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("who_needs_rockets", FallAfterExplosionTrigger.TriggerInstance.fallAfterExplosion(DistancePredicate.vertical(MinMaxBounds.Doubles.atLeast(7.0)), EntityPredicate.Builder.entity().of(EntityType.WIND_CHARGE))).save(var2, "adventure/who_needs_rockets");
      Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.MACE, Component.translatable("advancements.adventure.overoverkill.title"), Component.translatable("advancements.adventure.overoverkill.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("overoverkill", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().dealtDamage(MinMaxBounds.Doubles.atLeast(100.0)).type(DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PLAYER_ATTACK)).direct(EntityPredicate.Builder.entity().of(EntityType.PLAYER).equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(Items.MACE))))))).save(var2, "adventure/overoverkill");
   }
}
