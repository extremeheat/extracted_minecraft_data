package net.minecraft.data.advancements.packs;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
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
import net.minecraft.advancements.criterion.BarrelRollTrigger;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.advancements.criterion.KilledTrigger;
import net.minecraft.advancements.criterion.LivingBlockPredicate;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.advancements.criterion.SummonedEntityTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import org.slf4j.Logger;

public class VanillaAdventureAdvancements implements AdvancementSubProvider {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<MobCategory, Set<EntityType<?>>> EXCEPTIONS_BY_EXPECTED_CATEGORIES;
   private static final List<EntityType<?>> MOBS_TO_KILL;

   public VanillaAdventureAdvancements() {
      super();
   }

   public void generate(final HolderLookup.Provider registries, final Consumer<AdvancementHolder> output) {
      HolderLookup<EntityType<?>> entityTypes = registries.lookupOrThrow(Registries.ENTITY_TYPE);
      HolderLookup<Item> items = registries.lookupOrThrow(Registries.ITEM);
      AdvancementHolder root = Advancement.Builder.advancement().display((ItemLike)Items.MAP, Component.translatable("advancements.adventure.root.title"), Component.translatable("advancements.adventure.root.description"), Identifier.withDefaultNamespace("gui/advancements/backgrounds/adventure"), AdvancementType.TASK, false, false, false).addCriterion("root", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BUILD_ACTION)).save(output, "adventure/root");
      AdvancementHolder mineABlock = Advancement.Builder.advancement().parent(root).display((ItemLike)Items.PUNCH_ACTION, Component.translatable("advancements.adventure.mine_a_block.title"), Component.translatable("advancements.adventure.mine_a_block.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("mine_a_block", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK))).save(output, "adventure/mine_a_block");
      AdvancementHolder craftACraftingTable = Advancement.Builder.advancement().parent(mineABlock).display((ItemLike)Items.CRAFTING_TABLE, Component.translatable("advancements.adventure.crafting_table.title"), Component.translatable("advancements.adventure.crafting_table.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("craft_table", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK).subPredicate(LivingBlockPredicate.ofItem(ItemPredicate.Builder.item().of(items, Items.CRAFTING_TABLE).build())))).save(output, "adventure/craft_table");
      AdvancementHolder craftASword = Advancement.Builder.advancement().parent(craftACraftingTable).display((ItemLike)Items.WOODEN_SWORD, Component.translatable("advancements.adventure.craft_sword.title"), Component.translatable("advancements.adventure.craft_sword.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("craft_sword", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK).subPredicate(LivingBlockPredicate.ofItem(ItemPredicate.Builder.item().of(items, (TagKey)ItemTags.SWORDS).build())))).save(output, "adventure/craft_sword");
      createMonsterHunterAdvancement(craftASword, output, entityTypes, validateMobsToKill(MOBS_TO_KILL, entityTypes));
      AdvancementHolder craftADoor = Advancement.Builder.advancement().parent(craftACraftingTable).display((ItemLike)Items.OAK_DOOR, Component.translatable("advancements.adventure.door.title"), Component.translatable("advancements.adventure.door.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("door", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK).subPredicate(LivingBlockPredicate.ofItem(ItemPredicate.Builder.item().of(items, (TagKey)ItemTags.DOORS).build())))).save(output, "adventure/door");
      Advancement.Builder.advancement().parent(craftADoor).display((ItemLike)Items.PALE_OAK_DOOR, Component.translatable("advancements.adventure.build_house.title"), Component.translatable("advancements.adventure.build_house.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("build_house", PlayerTrigger.TriggerInstance.houseBuilt()).save(output, "adventure/build_house");
      AdvancementHolder woodenPickaxe = Advancement.Builder.advancement().parent(craftACraftingTable).display((ItemLike)Items.WOODEN_PICKAXE, Component.translatable("advancements.adventure.wooden_pickaxe.title"), Component.translatable("advancements.adventure.wooden_pickaxe.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("wooden_pickaxe", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK).subPredicate(LivingBlockPredicate.ofItem(ItemPredicate.Builder.item().of(items, Items.WOODEN_PICKAXE).build())))).save(output, "adventure/wooden_pickaxe");
      AdvancementHolder furnace = Advancement.Builder.advancement().parent(woodenPickaxe).display((ItemLike)Items.FURNACE, Component.translatable("advancements.adventure.furnace.title"), Component.translatable("advancements.adventure.furnace.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("furnace", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK).subPredicate(LivingBlockPredicate.ofItem(ItemPredicate.Builder.item().of(items, Items.FURNACE).build())))).save(output, "adventure/furnace");
      AdvancementHolder ironIngot = Advancement.Builder.advancement().parent(furnace).display((ItemLike)Items.IRON_INGOT, Component.translatable("advancements.adventure.iron_ingot.title"), Component.translatable("advancements.adventure.iron_ingot.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("iron_ingot", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK).subPredicate(LivingBlockPredicate.ofItem(ItemPredicate.Builder.item().of(items, Items.IRON_INGOT).build())))).save(output, "adventure/iron_ingot");
      Advancement.Builder.advancement().parent(ironIngot).display((ItemLike)Items.IRON_CHESTPLATE, Component.translatable("advancements.story.obtain_armor.title"), Component.translatable("advancements.story.obtain_armor.description"), (Identifier)null, AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("iron_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_HELMET)).addCriterion("iron_chestplate", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_CHESTPLATE)).addCriterion("iron_leggings", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_LEGGINGS)).addCriterion("iron_boots", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_BOOTS)).save(output, "adventure/obtain_armor");
      Advancement.Builder.advancement().parent(ironIngot).display((ItemLike)Items.IRON_BARS, Component.translatable("advancements.adventure.build_trap.title"), Component.translatable("advancements.adventure.build_trap.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("build_trap", PlayerTrigger.TriggerInstance.trapBuilt()).save(output, "adventure/build_trap");
      AdvancementHolder crafter = Advancement.Builder.advancement().parent(ironIngot).display((ItemLike)Items.CRAFTER, Component.translatable("advancements.adventure.crafter.title"), Component.translatable("advancements.adventure.crafter.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("crafter", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK).subPredicate(LivingBlockPredicate.ofItem(ItemPredicate.Builder.item().of(items, Items.CRAFTER).build())))).save(output, "adventure/crafter");
      AdvancementHolder chest = Advancement.Builder.advancement().parent(craftACraftingTable).display((ItemLike)Items.CHEST, Component.translatable("advancements.adventure.craft_chest.title"), Component.translatable("advancements.adventure.craft_chest.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("craft_chest", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK).subPredicate(LivingBlockPredicate.ofItem(ItemPredicate.Builder.item().of(items, Items.CHEST).build())))).save(output, "adventure/craft_chest");
      AdvancementHolder obsidian = Advancement.Builder.advancement().parent(craftACraftingTable).display((ItemLike)Blocks.OBSIDIAN, Component.translatable("advancements.adventure.obsidian.title"), Component.translatable("advancements.adventure.obsidian.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("obsidian", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK).subPredicate(LivingBlockPredicate.ofItem(ItemPredicate.Builder.item().of(items, Items.OBSIDIAN).build())))).save(output, "adventure/obsidian");
      AdvancementHolder netherPortal = Advancement.Builder.advancement().parent(obsidian).display((ItemLike)Items.FLINT_AND_STEEL, Component.translatable("advancements.adventure.nether_portal.title"), Component.translatable("advancements.adventure.nether_portal.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("craft_nether_portal", PlayerTrigger.TriggerInstance.netherPortalCrafted()).save(output, "adventure/craft_nether_portal");
      AdvancementHolder findFortress = Advancement.Builder.advancement().parent(netherPortal).display((ItemLike)Blocks.NETHER_BRICKS, Component.translatable("advancements.nether.find_fortress.title"), Component.translatable("advancements.nether.find_fortress.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("fortress", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.FORTRESS)))).save(output, "adventure/find_fortress");
      AdvancementHolder blazeRod = Advancement.Builder.advancement().parent(findFortress).display((ItemLike)Items.BLAZE_ROD, Component.translatable("advancements.nether.obtain_blaze_rod.title"), Component.translatable("advancements.nether.obtain_blaze_rod.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("blaze_rod", SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.LIVING_BLOCK).subPredicate(LivingBlockPredicate.ofItem(ItemPredicate.Builder.item().of(items, Items.BLAZE_ROD).build())))).save(output, "adventure/obtain_blaze_rod");
      AdvancementHolder followEnderEye = Advancement.Builder.advancement().parent(blazeRod).display((ItemLike)Items.ENDER_EYE, Component.translatable("advancements.story.follow_ender_eye.title"), Component.translatable("advancements.story.follow_ender_eye.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("in_stronghold", PlayerTrigger.TriggerInstance.located(LocationPredicate.Builder.inStructure(registries.lookupOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.STRONGHOLD)))).save(output, "adventure/follow_ender_eye");
      AdvancementHolder inputOutput = Advancement.Builder.advancement().parent(followEnderEye).display((ItemLike)Items.END_PORTAL_FRAME, Component.translatable("advancements.adventure.eye_frame_combination.title"), Component.translatable("advancements.adventure.eye_frame_combination.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("eye_frame_combination", PlayerTrigger.TriggerInstance.inputOutput()).save(output, "adventure/eye_frame_combination");
      AdvancementHolder endPortal = Advancement.Builder.advancement().parent(inputOutput).display((ItemLike)Items.ENDER_DRAGON_SPAWN_EGG, Component.translatable("advancements.adventure.end_portal.title"), Component.translatable("advancements.adventure.end_portal.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("craft_end_portal", PlayerTrigger.TriggerInstance.endPortalCrafted()).save(output, "adventure/craft_end_portal");
      AdvancementHolder enteredEnd = Advancement.Builder.advancement().parent(endPortal).display((ItemLike)Blocks.END_STONE, Component.translatable("advancements.story.enter_the_end.title"), Component.translatable("advancements.story.enter_the_end.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("entered_end", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(Level.END)).save(output, "adventure/enter_the_end");
      AdvancementHolder killDragon = Advancement.Builder.advancement().parent(enteredEnd).display((ItemLike)Blocks.DRAGON_HEAD, Component.translatable("advancements.end.kill_dragon.title"), Component.translatable("advancements.end.kill_dragon.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("killed_dragon", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityTypes, EntityType.ENDER_DRAGON))).save(output, "adventure/kill_dragon");
      AdvancementHolder barrelRoll = Advancement.Builder.advancement().parent(chest).display((ItemLike)Blocks.BARREL, Component.translatable("advancements.adventure.barrel_roll.title"), Component.translatable("advancements.adventure.barrel_roll.description"), (Identifier)null, AdvancementType.TASK, true, true, false).rewards(AdvancementRewards.Builder.experience(10)).addCriterion("barrel_roll", BarrelRollTrigger.TriggerInstance.barrelRolled()).save(output, "adventure/barrel_roll");
      AdvancementHolder netherBarrelRoll = Advancement.Builder.advancement().parent(barrelRoll).display((ItemLike)Blocks.BARREL, Component.translatable("advancements.nether.barrel_roll.title"), Component.translatable("advancements.nether.barrel_roll.description"), (Identifier)null, AdvancementType.TASK, true, true, false).addCriterion("barrel_roll", BarrelRollTrigger.TriggerInstance.barrelRolledInNether()).rewards(AdvancementRewards.Builder.experience(100)).save(output, "adventure/nether_barrel_roll");
      Advancement.Builder.advancement().parent(netherBarrelRoll).display((ItemLike)Blocks.BARREL, Component.translatable("advancements.end.barrel_roll.title"), Component.translatable("advancements.end.barrel_roll.description"), (Identifier)null, AdvancementType.TASK, true, true, false).rewards(AdvancementRewards.Builder.experience(1000)).addCriterion("barrel_roll", BarrelRollTrigger.TriggerInstance.barrelRolledInEnd()).save(output, "adventure/end_barrel_roll");
   }

   public static AdvancementHolder createMonsterHunterAdvancement(final AdvancementHolder parent, final Consumer<AdvancementHolder> output, final HolderGetter<EntityType<?>> entityTypes, final List<EntityType<?>> mobsToKill) {
      AdvancementHolder killAMob = addMobsToKill(Advancement.Builder.advancement(), entityTypes, mobsToKill).parent(parent).display((ItemLike)Items.IRON_SWORD, Component.translatable("advancements.adventure.kill_a_mob.title"), Component.translatable("advancements.adventure.kill_a_mob.description"), (Identifier)null, AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).save(output, "adventure/kill_a_mob");
      addMobsToKill(Advancement.Builder.advancement(), entityTypes, mobsToKill).parent(killAMob).display((ItemLike)Items.DIAMOND_SWORD, Component.translatable("advancements.adventure.kill_all_mobs.title"), Component.translatable("advancements.adventure.kill_all_mobs.description"), (Identifier)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(output, "adventure/kill_all_mobs");
      return killAMob;
   }

   private static Advancement.Builder addMobsToKill(final Advancement.Builder advancement, final HolderGetter<EntityType<?>> entityTypes, final List<EntityType<?>> mobsToKill) {
      mobsToKill.forEach((mob) -> advancement.addCriterion(BuiltInRegistries.ENTITY_TYPE.getKey(mob).toString(), KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(entityTypes, mob))));
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
