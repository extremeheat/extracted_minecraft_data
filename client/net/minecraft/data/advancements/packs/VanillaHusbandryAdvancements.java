package net.minecraft.data.advancements.packs;

import com.google.common.collect.BiMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.BlockPredicate;
import net.minecraft.advancements.predicates.DataComponentMatchers;
import net.minecraft.advancements.predicates.EnchantmentPredicate;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.LocationPredicate;
import net.minecraft.advancements.predicates.MinMaxBounds;
import net.minecraft.advancements.predicates.entity.EntityEquipmentPredicate;
import net.minecraft.advancements.predicates.entity.EntityFlagsPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.BeeNestDestroyedTrigger;
import net.minecraft.advancements.triggers.BredAnimalsTrigger;
import net.minecraft.advancements.triggers.ConsumeItemTrigger;
import net.minecraft.advancements.triggers.EffectsChangedTrigger;
import net.minecraft.advancements.triggers.FilledBucketTrigger;
import net.minecraft.advancements.triggers.FishingRodHookedTrigger;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.triggers.PickedUpItemTrigger;
import net.minecraft.advancements.triggers.PlayerInteractTrigger;
import net.minecraft.advancements.triggers.StartRidingTrigger;
import net.minecraft.advancements.triggers.TameAnimalTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class VanillaHusbandryAdvancements extends AdvancementSubProvider {
   public static final List<EntityType<?>> BREEDABLE_ANIMALS;
   public static final List<EntityType<?>> INDIRECTLY_BREEDABLE_ANIMALS;
   private static final Item[] FISH;
   private static final Item[] FISH_BUCKETS;
   private static final Item[] EDIBLE_ITEMS;
   public static final Item[] WAX_SCRAPING_TOOLS;
   private static final Comparator<Holder.Reference<?>> HOLDER_KEY_COMPARATOR;
   private final HolderGetter<EntityType<?>> entityTypes;
   private final HolderGetter<Item> items;
   private final HolderGetter<Block> blocks;
   private final HolderGetter<Enchantment> enchantments;

   public VanillaHusbandryAdvancements(final BootstrapContext<Advancement> output) {
      super(output);
      this.entityTypes = output.lookup(Registries.ENTITY_TYPE);
      this.items = output.lookup(Registries.ITEM);
      this.blocks = output.lookup(Registries.BLOCK);
      this.enchantments = output.lookup(Registries.ENCHANTMENT);
   }

   public void generate() {
      AdvancementHolder root = Advancement.Builder.advancement().rootDisplay((Item)Items.HAY_BLOCK, Component.translatable("advancements.husbandry.root.title"), Component.translatable("advancements.husbandry.root.description"), Identifier.withDefaultNamespace("gui/advancements/backgrounds/husbandry"), AdvancementType.TASK, false, false, false).addCriterion("consumed_item", ConsumeItemTrigger.TriggerInstance.usedItem()).save(this.output, "husbandry/root");
      AdvancementHolder plantSeed = Advancement.Builder.advancement().parent(root).display((Item)Items.WHEAT, Component.translatable("advancements.husbandry.plant_seed.title"), Component.translatable("advancements.husbandry.plant_seed.description"), AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("wheat", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(this.blocks, Blocks.WHEAT)).addCriterion("pumpkin_stem", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(this.blocks, Blocks.PUMPKIN_STEM)).addCriterion("melon_stem", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(this.blocks, Blocks.MELON_STEM)).addCriterion("beetroots", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(this.blocks, Blocks.BEETROOTS)).addCriterion("nether_wart", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(this.blocks, Blocks.NETHER_WART)).addCriterion("torchflower", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(this.blocks, Blocks.TORCHFLOWER_CROP)).addCriterion("pitcher_pod", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(this.blocks, Blocks.PITCHER_CROP)).save(this.output, "husbandry/plant_seed");
      Advancement.Builder.advancement().parent(root).display((Item)Items.TNT, Component.translatable("advancements.husbandry.uh_oh.title"), Component.translatable("advancements.husbandry.uh_oh.description"), AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("pick_up_dropped_tnt", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByEntity(Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(this.items, ItemTags.SULFUR_CUBE_ARCHETYPE_EXPLOSIVE).build()), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.SULFUR_CUBE).flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false)))))).addCriterion("give_tnt_directly", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(Optional.empty(), ItemPredicate.Builder.item().of(this.items, ItemTags.SULFUR_CUBE_ARCHETYPE_EXPLOSIVE), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.SULFUR_CUBE).flags(EntityFlagsPredicate.Builder.flags().setIsBaby(false)))))).save(this.output, "husbandry/uh_oh");
      AdvancementHolder breedAnAnimalAdvancement = Advancement.Builder.advancement().parent(root).display((Item)Items.WHEAT, Component.translatable("advancements.husbandry.breed_an_animal.title"), Component.translatable("advancements.husbandry.breed_an_animal.description"), AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("bred", BredAnimalsTrigger.TriggerInstance.bredAnimals()).save(this.output, "husbandry/breed_an_animal");
      this.createBreedAllAnimalsAdvancement(breedAnAnimalAdvancement, BREEDABLE_ANIMALS.stream(), INDIRECTLY_BREEDABLE_ANIMALS.stream());
      this.addFood(Advancement.Builder.advancement()).parent(plantSeed).display((Item)Items.APPLE, Component.translatable("advancements.husbandry.balanced_diet.title"), Component.translatable("advancements.husbandry.balanced_diet.description"), AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(this.output, "husbandry/balanced_diet");
      Advancement.Builder.advancement().parent(plantSeed).display((Item)Items.NETHERITE_HOE, Component.translatable("advancements.husbandry.netherite_hoe.title"), Component.translatable("advancements.husbandry.netherite_hoe.description"), AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("netherite_hoe", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_HOE)).save(this.output, "husbandry/obtain_netherite_hoe");
      AdvancementHolder tameAnAnimal = Advancement.Builder.advancement().parent(root).display((Item)Items.LEAD, Component.translatable("advancements.husbandry.tame_an_animal.title"), Component.translatable("advancements.husbandry.tame_an_animal.description"), AdvancementType.TASK, true, true, false).addCriterion("tamed_animal", TameAnimalTrigger.TriggerInstance.tamedAnimal()).save(this.output, "husbandry/tame_an_animal");
      AdvancementHolder fishyBusiness = this.addFish(Advancement.Builder.advancement()).parent(root).requirements(AdvancementRequirements.Strategy.OR).display((Item)Items.FISHING_ROD, Component.translatable("advancements.husbandry.fishy_business.title"), Component.translatable("advancements.husbandry.fishy_business.description"), AdvancementType.TASK, true, true, false).save(this.output, "husbandry/fishy_business");
      AdvancementHolder tacticalFishing = this.addFishBuckets(Advancement.Builder.advancement()).parent(fishyBusiness).requirements(AdvancementRequirements.Strategy.OR).display((Item)Items.PUFFERFISH_BUCKET, Component.translatable("advancements.husbandry.tactical_fishing.title"), Component.translatable("advancements.husbandry.tactical_fishing.description"), AdvancementType.TASK, true, true, false).save(this.output, "husbandry/tactical_fishing");
      AdvancementHolder theCutestPredetor = Advancement.Builder.advancement().parent(tacticalFishing).requirements(AdvancementRequirements.Strategy.OR).addCriterion(BuiltInRegistries.ITEM.getKey(Items.AXOLOTL_BUCKET).getPath(), FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(this.items, Items.AXOLOTL_BUCKET))).display((Item)Items.AXOLOTL_BUCKET, Component.translatable("advancements.husbandry.axolotl_in_a_bucket.title"), Component.translatable("advancements.husbandry.axolotl_in_a_bucket.description"), AdvancementType.TASK, true, true, false).save(this.output, "husbandry/axolotl_in_a_bucket");
      Advancement.Builder.advancement().parent(theCutestPredetor).addCriterion("kill_axolotl_target", EffectsChangedTrigger.TriggerInstance.gotEffectsFrom(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.AXOLOTL))).display((Item)Items.TROPICAL_FISH_BUCKET, Component.translatable("advancements.husbandry.kill_axolotl_target.title"), Component.translatable("advancements.husbandry.kill_axolotl_target.description"), AdvancementType.TASK, true, true, false).save(this.output, "husbandry/kill_axolotl_target");
      this.addCatVariants(Advancement.Builder.advancement()).parent(tameAnAnimal).display((Item)Items.COD, Component.translatable("advancements.husbandry.complete_catalogue.title"), Component.translatable("advancements.husbandry.complete_catalogue.description"), AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).save(this.output, "husbandry/complete_catalogue");
      this.addTamedWolfVariants(Advancement.Builder.advancement()).parent(tameAnAnimal).display((Item)Items.BONE, Component.translatable("advancements.husbandry.whole_pack.title"), Component.translatable("advancements.husbandry.whole_pack.description"), AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).save(this.output, "husbandry/whole_pack");
      AdvancementHolder safelyHarvestHoney = Advancement.Builder.advancement().parent(root).addCriterion("safely_harvest_honey", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(this.blocks, BlockTags.BEEHIVES)).setSmokey(true), ItemPredicate.Builder.item().of(this.items, Items.GLASS_BOTTLE))).display((Item)Items.HONEY_BOTTLE, Component.translatable("advancements.husbandry.safely_harvest_honey.title"), Component.translatable("advancements.husbandry.safely_harvest_honey.description"), AdvancementType.TASK, true, true, false).save(this.output, "husbandry/safely_harvest_honey");
      AdvancementHolder waxOn = Advancement.Builder.advancement().parent(safelyHarvestHoney).display((Item)Items.HONEYCOMB, Component.translatable("advancements.husbandry.wax_on.title"), Component.translatable("advancements.husbandry.wax_on.description"), AdvancementType.TASK, true, true, false).addCriterion("wax_on", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(this.blocks, (Collection)((BiMap)HoneycombItem.WAXABLES.get()).keySet())), ItemPredicate.Builder.item().of(this.items, Items.HONEYCOMB))).save(this.output, "husbandry/wax_on");
      Advancement.Builder.advancement().parent(waxOn).display((Item)Items.STONE_AXE, Component.translatable("advancements.husbandry.wax_off.title"), Component.translatable("advancements.husbandry.wax_off.description"), AdvancementType.TASK, true, true, false).addCriterion("wax_off", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(this.blocks, (Collection)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).keySet())), ItemPredicate.Builder.item().of(this.items, WAX_SCRAPING_TOOLS))).save(this.output, "husbandry/wax_off");
      AdvancementHolder tadpoleInABucket = Advancement.Builder.advancement().parent(root).addCriterion(BuiltInRegistries.ITEM.getKey(Items.TADPOLE_BUCKET).getPath(), FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(this.items, Items.TADPOLE_BUCKET))).display((Item)Items.TADPOLE_BUCKET, Component.translatable("advancements.husbandry.tadpole_in_a_bucket.title"), Component.translatable("advancements.husbandry.tadpole_in_a_bucket.description"), AdvancementType.TASK, true, true, false).save(this.output, "husbandry/tadpole_in_a_bucket");
      AdvancementHolder allFrogsOnALeash = this.addLeashedFrogVariants(Advancement.Builder.advancement()).parent(tadpoleInABucket).display((Item)Items.LEAD, Component.translatable("advancements.husbandry.leash_all_frog_variants.title"), Component.translatable("advancements.husbandry.leash_all_frog_variants.description"), AdvancementType.TASK, true, true, false).save(this.output, "husbandry/leash_all_frog_variants");
      Advancement.Builder.advancement().parent(allFrogsOnALeash).display((Item)Items.VERDANT_FROGLIGHT, Component.translatable("advancements.husbandry.froglights.title"), Component.translatable("advancements.husbandry.froglights.description"), AdvancementType.CHALLENGE, true, true, false).addCriterion("froglights", InventoryChangeTrigger.TriggerInstance.hasItems(Items.OCHRE_FROGLIGHT, Items.PEARLESCENT_FROGLIGHT, Items.VERDANT_FROGLIGHT)).save(this.output, "husbandry/froglights");
      Advancement.Builder.advancement().parent(root).addCriterion("silk_touch_nest", BeeNestDestroyedTrigger.TriggerInstance.destroyedBeeNest(this.blocks, Blocks.BEE_NEST, ItemPredicate.Builder.item().withComponents(DataComponentMatchers.Builder.components().partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(this.enchantments.getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1))))).build()), MinMaxBounds.Ints.exactly(3))).display((Item)Items.BEE_NEST, Component.translatable("advancements.husbandry.silk_touch_nest.title"), Component.translatable("advancements.husbandry.silk_touch_nest.description"), AdvancementType.TASK, true, true, false).save(this.output, "husbandry/silk_touch_nest");
      Advancement.Builder.advancement().parent(root).display((Item)Items.OAK_BOAT, Component.translatable("advancements.husbandry.ride_a_boat_with_a_goat.title"), Component.translatable("advancements.husbandry.ride_a_boat_with_a_goat.description"), AdvancementType.TASK, true, true, false).addCriterion("ride_a_boat_with_a_goat", StartRidingTrigger.TriggerInstance.playerStartsRiding(EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypeTags.BOAT).passenger(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.GOAT))))).save(this.output, "husbandry/ride_a_boat_with_a_goat");
      Advancement.Builder.advancement().parent(root).display((Item)Items.GLOW_INK_SAC, Component.translatable("advancements.husbandry.make_a_sign_glow.title"), Component.translatable("advancements.husbandry.make_a_sign_glow.description"), AdvancementType.TASK, true, true, false).addCriterion("make_a_sign_glow", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(this.blocks, BlockTags.ALL_SIGNS)), ItemPredicate.Builder.item().of(this.items, Items.GLOW_INK_SAC))).save(this.output, "husbandry/make_a_sign_glow");
      AdvancementHolder itemDeliveredToPlayer = Advancement.Builder.advancement().parent(root).display((Item)Items.COOKIE, Component.translatable("advancements.husbandry.allay_deliver_item_to_player.title"), Component.translatable("advancements.husbandry.allay_deliver_item_to_player.description"), AdvancementType.TASK, true, true, true).addCriterion("allay_deliver_item_to_player", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.ALLAY))))).save(this.output, "husbandry/allay_deliver_item_to_player");
      Advancement.Builder.advancement().parent(itemDeliveredToPlayer).display((Item)Items.NOTE_BLOCK, Component.translatable("advancements.husbandry.allay_deliver_cake_to_note_block.title"), Component.translatable("advancements.husbandry.allay_deliver_cake_to_note_block.description"), AdvancementType.TASK, true, true, true).addCriterion("allay_deliver_cake_to_note_block", ItemUsedOnLocationTrigger.TriggerInstance.allayDropItemOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(this.blocks, Blocks.NOTE_BLOCK)), ItemPredicate.Builder.item().of(this.items, Items.CAKE))).save(this.output, "husbandry/allay_deliver_cake_to_note_block");
      AdvancementHolder obtainSnifferEgg = Advancement.Builder.advancement().parent(root).display((Item)Items.SNIFFER_EGG, Component.translatable("advancements.husbandry.obtain_sniffer_egg.title"), Component.translatable("advancements.husbandry.obtain_sniffer_egg.description"), AdvancementType.TASK, true, true, true).addCriterion("obtain_sniffer_egg", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SNIFFER_EGG)).save(this.output, "husbandry/obtain_sniffer_egg");
      AdvancementHolder feedSnifflet = Advancement.Builder.advancement().parent(obtainSnifferEgg).display((Item)Items.TORCHFLOWER_SEEDS, Component.translatable("advancements.husbandry.feed_snifflet.title"), Component.translatable("advancements.husbandry.feed_snifflet.description"), AdvancementType.TASK, true, true, true).addCriterion("feed_snifflet", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(this.items, ItemTags.SNIFFER_FOOD), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.SNIFFER).flags(EntityFlagsPredicate.Builder.flags().setIsBaby(true)))))).save(this.output, "husbandry/feed_snifflet");
      Advancement.Builder.advancement().parent(feedSnifflet).display((Item)Items.PITCHER_POD, Component.translatable("advancements.husbandry.plant_any_sniffer_seed.title"), Component.translatable("advancements.husbandry.plant_any_sniffer_seed.description"), AdvancementType.TASK, true, true, true).requirements(AdvancementRequirements.Strategy.OR).addCriterion("torchflower", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(this.blocks, Blocks.TORCHFLOWER_CROP)).addCriterion("pitcher_pod", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(this.blocks, Blocks.PITCHER_CROP)).save(this.output, "husbandry/plant_any_sniffer_seed");
      Advancement.Builder.advancement().parent(tameAnAnimal).display((Item)Items.SHEARS, Component.translatable("advancements.husbandry.remove_wolf_armor.title"), Component.translatable("advancements.husbandry.remove_wolf_armor.description"), AdvancementType.TASK, true, true, false).addCriterion("remove_wolf_armor", PlayerInteractTrigger.TriggerInstance.equipmentSheared(ItemPredicate.Builder.item().of(this.items, Items.WOLF_ARMOR), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.WOLF))))).save(this.output, "husbandry/remove_wolf_armor");
      Advancement.Builder.advancement().parent(tameAnAnimal).display((Item)Items.WOLF_ARMOR, Component.translatable("advancements.husbandry.repair_wolf_armor.title"), Component.translatable("advancements.husbandry.repair_wolf_armor.description"), AdvancementType.TASK, true, true, false).addCriterion("repair_wolf_armor", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(this.items, Items.ARMADILLO_SCUTE), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.WOLF).equipment(EntityEquipmentPredicate.Builder.equipment().body(ItemPredicate.Builder.item().of(this.items, Items.WOLF_ARMOR).withComponents(DataComponentMatchers.Builder.components().exact(DataComponentExactPredicate.expect(DataComponents.DAMAGE, 0)).build()))))))).save(this.output, "husbandry/repair_wolf_armor");
      Advancement.Builder.advancement().parent(root).display((Item)Items.DRIED_GHAST, Component.translatable("advancements.husbandry.place_dried_ghast_in_water.title"), Component.translatable("advancements.husbandry.place_dried_ghast_in_water.description"), AdvancementType.TASK, true, true, false).addCriterion("place_dried_ghast_in_water", ItemUsedOnLocationTrigger.TriggerInstance.placedBlockWithProperties(this.blocks, Blocks.DRIED_GHAST, BlockStateProperties.WATERLOGGED, true)).save(this.output, "husbandry/place_dried_ghast_in_water");
   }

   public AdvancementHolder createBreedAllAnimalsAdvancement(final AdvancementHolder parent, final Stream<EntityType<?>> breedable, final Stream<EntityType<?>> indirectlyBreedable) {
      return this.addBreedable(Advancement.Builder.advancement(), breedable, indirectlyBreedable).parent(parent).display((Item)Items.GOLDEN_CARROT, Component.translatable("advancements.husbandry.breed_all_animals.title"), Component.translatable("advancements.husbandry.breed_all_animals.description"), AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(this.output, "husbandry/bred_all_animals");
   }

   private Advancement.Builder addLeashedFrogVariants(final Advancement.Builder advancement) {
      this.sortedVariants(Registries.FROG_VARIANT).forEach((frogVariant) -> advancement.addCriterion(frogVariant.key().identifier().toString(), PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(this.items, Items.LEAD), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(this.entityTypes, EntityTypes.FROG).components(DataComponentMatchers.Builder.components().exact(DataComponentExactPredicate.expect(DataComponents.FROG_VARIANT, frogVariant)).build()))))));
      return advancement;
   }

   private <T> Stream<Holder.Reference<T>> sortedVariants(final ResourceKey<? extends Registry<T>> registryKey) {
      return this.output.listContextElements(registryKey).sorted(HOLDER_KEY_COMPARATOR);
   }

   private Advancement.Builder addFood(final Advancement.Builder advancement) {
      for(Item food : EDIBLE_ITEMS) {
         advancement.addCriterion(BuiltInRegistries.ITEM.getKey(food).getPath(), ConsumeItemTrigger.TriggerInstance.usedItem(this.items, food));
      }

      return advancement;
   }

   private Advancement.Builder addBreedable(final Advancement.Builder advancement, final Stream<EntityType<?>> breedable, final Stream<EntityType<?>> indirectlyBreedable) {
      breedable.forEach((animal) -> advancement.addCriterion(EntityType.getKey(animal).toString(), BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(this.entityTypes, animal))));
      indirectlyBreedable.forEach((animal) -> advancement.addCriterion(EntityType.getKey(animal).toString(), BredAnimalsTrigger.TriggerInstance.bredAnimals(Optional.of(EntityPredicate.Builder.entity().of(this.entityTypes, animal).build()), Optional.of(EntityPredicate.Builder.entity().of(this.entityTypes, animal).build()), Optional.empty())));
      return advancement;
   }

   private Advancement.Builder addFishBuckets(final Advancement.Builder advancement) {
      for(Item bucket : FISH_BUCKETS) {
         advancement.addCriterion(BuiltInRegistries.ITEM.getKey(bucket).getPath(), FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(this.items, bucket)));
      }

      return advancement;
   }

   private Advancement.Builder addFish(final Advancement.Builder advancement) {
      for(Item fish : FISH) {
         advancement.addCriterion(BuiltInRegistries.ITEM.getKey(fish).getPath(), FishingRodHookedTrigger.TriggerInstance.fishedItem(Optional.empty(), Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(this.items, fish).build())));
      }

      return advancement;
   }

   private Advancement.Builder addCatVariants(final Advancement.Builder advancement) {
      this.sortedVariants(Registries.CAT_VARIANT).forEach((v) -> advancement.addCriterion(v.key().identifier().toString(), TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().components(DataComponentExactPredicate.expect(DataComponents.CAT_VARIANT, v)))));
      return advancement;
   }

   private Advancement.Builder addTamedWolfVariants(final Advancement.Builder advancement) {
      this.sortedVariants(Registries.WOLF_VARIANT).forEach((v) -> advancement.addCriterion(v.key().identifier().toString(), TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().components(DataComponentExactPredicate.expect(DataComponents.WOLF_VARIANT, v)))));
      return advancement;
   }

   static {
      BREEDABLE_ANIMALS = List.of(EntityTypes.HORSE, EntityTypes.DONKEY, EntityTypes.MULE, EntityTypes.SHEEP, EntityTypes.COW, EntityTypes.MOOSHROOM, EntityTypes.PIG, EntityTypes.CHICKEN, EntityTypes.WOLF, EntityTypes.OCELOT, EntityTypes.RABBIT, EntityTypes.LLAMA, EntityTypes.CAT, EntityTypes.PANDA, EntityTypes.FOX, EntityTypes.BEE, EntityTypes.HOGLIN, EntityTypes.STRIDER, EntityTypes.GOAT, EntityTypes.AXOLOTL, EntityTypes.CAMEL, EntityTypes.ARMADILLO, EntityTypes.NAUTILUS);
      INDIRECTLY_BREEDABLE_ANIMALS = List.of(EntityTypes.TURTLE, EntityTypes.FROG, EntityTypes.SNIFFER);
      FISH = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
      FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
      EDIBLE_ITEMS = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.HONEY_BOTTLE, Items.GLOW_BERRIES};
      WAX_SCRAPING_TOOLS = new Item[]{Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.COPPER_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE};
      HOLDER_KEY_COMPARATOR = Comparator.comparing((e) -> e.key().identifier());
   }
}
