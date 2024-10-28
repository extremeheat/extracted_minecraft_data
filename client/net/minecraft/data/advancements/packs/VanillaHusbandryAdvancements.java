package net.minecraft.data.advancements.packs;

import com.google.common.collect.BiMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.BeeNestDestroyedTrigger;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PickedUpItemTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.StartRidingTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class VanillaHusbandryAdvancements implements AdvancementSubProvider {
   public static final List<EntityType<?>> BREEDABLE_ANIMALS;
   public static final List<EntityType<?>> INDIRECTLY_BREEDABLE_ANIMALS;
   private static final Item[] FISH;
   private static final Item[] FISH_BUCKETS;
   private static final Item[] EDIBLE_ITEMS;
   public static final Item[] WAX_SCRAPING_TOOLS;

   public VanillaHusbandryAdvancements() {
      super();
   }

   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      HolderLookup.RegistryLookup var3 = var1.lookupOrThrow(Registries.ENTITY_TYPE);
      HolderLookup.RegistryLookup var4 = var1.lookupOrThrow(Registries.ITEM);
      HolderLookup.RegistryLookup var5 = var1.lookupOrThrow(Registries.BLOCK);
      HolderLookup.RegistryLookup var6 = var1.lookupOrThrow(Registries.ENCHANTMENT);
      AdvancementHolder var7 = Advancement.Builder.advancement().display((ItemLike)Blocks.HAY_BLOCK, Component.translatable("advancements.husbandry.root.title"), Component.translatable("advancements.husbandry.root.description"), ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/husbandry.png"), AdvancementType.TASK, false, false, false).addCriterion("consumed_item", ConsumeItemTrigger.TriggerInstance.usedItem()).save(var2, "husbandry/root");
      AdvancementHolder var8 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.WHEAT, Component.translatable("advancements.husbandry.plant_seed.title"), Component.translatable("advancements.husbandry.plant_seed.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("wheat", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.WHEAT)).addCriterion("pumpkin_stem", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.PUMPKIN_STEM)).addCriterion("melon_stem", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.MELON_STEM)).addCriterion("beetroots", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.BEETROOTS)).addCriterion("nether_wart", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.NETHER_WART)).addCriterion("torchflower", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.TORCHFLOWER_CROP)).addCriterion("pitcher_pod", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.PITCHER_CROP)).save(var2, "husbandry/plant_seed");
      AdvancementHolder var9 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.WHEAT, Component.translatable("advancements.husbandry.breed_an_animal.title"), Component.translatable("advancements.husbandry.breed_an_animal.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).requirements(AdvancementRequirements.Strategy.OR).addCriterion("bred", BredAnimalsTrigger.TriggerInstance.bredAnimals()).save(var2, "husbandry/breed_an_animal");
      createBreedAllAnimalsAdvancement(var9, var2, var3, BREEDABLE_ANIMALS.stream(), INDIRECTLY_BREEDABLE_ANIMALS.stream());
      addFood(Advancement.Builder.advancement(), var4).parent(var8).display((ItemLike)Items.APPLE, Component.translatable("advancements.husbandry.balanced_diet.title"), Component.translatable("advancements.husbandry.balanced_diet.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(var2, "husbandry/balanced_diet");
      Advancement.Builder.advancement().parent(var8).display((ItemLike)Items.NETHERITE_HOE, Component.translatable("advancements.husbandry.netherite_hoe.title"), Component.translatable("advancements.husbandry.netherite_hoe.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("netherite_hoe", InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_HOE)).save(var2, "husbandry/obtain_netherite_hoe");
      AdvancementHolder var10 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.LEAD, Component.translatable("advancements.husbandry.tame_an_animal.title"), Component.translatable("advancements.husbandry.tame_an_animal.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("tamed_animal", TameAnimalTrigger.TriggerInstance.tamedAnimal()).save(var2, "husbandry/tame_an_animal");
      AdvancementHolder var11 = addFish(Advancement.Builder.advancement(), var4).parent(var7).requirements(AdvancementRequirements.Strategy.OR).display((ItemLike)Items.FISHING_ROD, Component.translatable("advancements.husbandry.fishy_business.title"), Component.translatable("advancements.husbandry.fishy_business.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).save(var2, "husbandry/fishy_business");
      AdvancementHolder var12 = addFishBuckets(Advancement.Builder.advancement(), var4).parent(var11).requirements(AdvancementRequirements.Strategy.OR).display((ItemLike)Items.PUFFERFISH_BUCKET, Component.translatable("advancements.husbandry.tactical_fishing.title"), Component.translatable("advancements.husbandry.tactical_fishing.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).save(var2, "husbandry/tactical_fishing");
      AdvancementHolder var13 = Advancement.Builder.advancement().parent(var12).requirements(AdvancementRequirements.Strategy.OR).addCriterion(BuiltInRegistries.ITEM.getKey(Items.AXOLOTL_BUCKET).getPath(), FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(var4, (ItemLike[])(Items.AXOLOTL_BUCKET)))).display((ItemLike)Items.AXOLOTL_BUCKET, Component.translatable("advancements.husbandry.axolotl_in_a_bucket.title"), Component.translatable("advancements.husbandry.axolotl_in_a_bucket.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).save(var2, "husbandry/axolotl_in_a_bucket");
      Advancement.Builder.advancement().parent(var13).addCriterion("kill_axolotl_target", EffectsChangedTrigger.TriggerInstance.gotEffectsFrom(EntityPredicate.Builder.entity().of(var3, (EntityType)EntityType.AXOLOTL))).display((ItemLike)Items.TROPICAL_FISH_BUCKET, Component.translatable("advancements.husbandry.kill_axolotl_target.title"), Component.translatable("advancements.husbandry.kill_axolotl_target.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).save(var2, "husbandry/kill_axolotl_target");
      addCatVariants(Advancement.Builder.advancement()).parent(var10).display((ItemLike)Items.COD, Component.translatable("advancements.husbandry.complete_catalogue.title"), Component.translatable("advancements.husbandry.complete_catalogue.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).save(var2, "husbandry/complete_catalogue");
      addTamedWolfVariants(Advancement.Builder.advancement(), var1).parent(var10).display((ItemLike)Items.BONE, Component.translatable("advancements.husbandry.whole_pack.title"), Component.translatable("advancements.husbandry.whole_pack.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).save(var2, "husbandry/whole_pack");
      AdvancementHolder var14 = Advancement.Builder.advancement().parent(var7).addCriterion("safely_harvest_honey", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var5, (TagKey)BlockTags.BEEHIVES)).setSmokey(true), ItemPredicate.Builder.item().of(var4, (ItemLike[])(Items.GLASS_BOTTLE)))).display((ItemLike)Items.HONEY_BOTTLE, Component.translatable("advancements.husbandry.safely_harvest_honey.title"), Component.translatable("advancements.husbandry.safely_harvest_honey.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).save(var2, "husbandry/safely_harvest_honey");
      AdvancementHolder var15 = Advancement.Builder.advancement().parent(var14).display((ItemLike)Items.HONEYCOMB, Component.translatable("advancements.husbandry.wax_on.title"), Component.translatable("advancements.husbandry.wax_on.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("wax_on", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var5, (Collection)((BiMap)HoneycombItem.WAXABLES.get()).keySet())), ItemPredicate.Builder.item().of(var4, (ItemLike[])(Items.HONEYCOMB)))).save(var2, "husbandry/wax_on");
      Advancement.Builder.advancement().parent(var15).display((ItemLike)Items.STONE_AXE, Component.translatable("advancements.husbandry.wax_off.title"), Component.translatable("advancements.husbandry.wax_off.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("wax_off", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var5, (Collection)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).keySet())), ItemPredicate.Builder.item().of(var4, (ItemLike[])WAX_SCRAPING_TOOLS))).save(var2, "husbandry/wax_off");
      AdvancementHolder var16 = Advancement.Builder.advancement().parent(var7).addCriterion(BuiltInRegistries.ITEM.getKey(Items.TADPOLE_BUCKET).getPath(), FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(var4, (ItemLike[])(Items.TADPOLE_BUCKET)))).display((ItemLike)Items.TADPOLE_BUCKET, Component.translatable("advancements.husbandry.tadpole_in_a_bucket.title"), Component.translatable("advancements.husbandry.tadpole_in_a_bucket.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).save(var2, "husbandry/tadpole_in_a_bucket");
      AdvancementHolder var17 = addLeashedFrogVariants(var3, var4, Advancement.Builder.advancement()).parent(var16).display((ItemLike)Items.LEAD, Component.translatable("advancements.husbandry.leash_all_frog_variants.title"), Component.translatable("advancements.husbandry.leash_all_frog_variants.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).save(var2, "husbandry/leash_all_frog_variants");
      Advancement.Builder.advancement().parent(var17).display((ItemLike)Items.VERDANT_FROGLIGHT, Component.translatable("advancements.husbandry.froglights.title"), Component.translatable("advancements.husbandry.froglights.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("froglights", InventoryChangeTrigger.TriggerInstance.hasItems(Items.OCHRE_FROGLIGHT, Items.PEARLESCENT_FROGLIGHT, Items.VERDANT_FROGLIGHT)).save(var2, "husbandry/froglights");
      Advancement.Builder.advancement().parent(var7).addCriterion("silk_touch_nest", BeeNestDestroyedTrigger.TriggerInstance.destroyedBeeNest(Blocks.BEE_NEST, ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(var6.getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1))))), MinMaxBounds.Ints.exactly(3))).display((ItemLike)Blocks.BEE_NEST, Component.translatable("advancements.husbandry.silk_touch_nest.title"), Component.translatable("advancements.husbandry.silk_touch_nest.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).save(var2, "husbandry/silk_touch_nest");
      Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.OAK_BOAT, Component.translatable("advancements.husbandry.ride_a_boat_with_a_goat.title"), Component.translatable("advancements.husbandry.ride_a_boat_with_a_goat.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("ride_a_boat_with_a_goat", StartRidingTrigger.TriggerInstance.playerStartsRiding(EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity().of(var3, (TagKey)EntityTypeTags.BOAT).passenger(EntityPredicate.Builder.entity().of(var3, (EntityType)EntityType.GOAT))))).save(var2, "husbandry/ride_a_boat_with_a_goat");
      Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.GLOW_INK_SAC, Component.translatable("advancements.husbandry.make_a_sign_glow.title"), Component.translatable("advancements.husbandry.make_a_sign_glow.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("make_a_sign_glow", ItemUsedOnLocationTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var5, (TagKey)BlockTags.ALL_SIGNS)), ItemPredicate.Builder.item().of(var4, (ItemLike[])(Items.GLOW_INK_SAC)))).save(var2, "husbandry/make_a_sign_glow");
      AdvancementHolder var18 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.COOKIE, Component.translatable("advancements.husbandry.allay_deliver_item_to_player.title"), Component.translatable("advancements.husbandry.allay_deliver_item_to_player.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, true).addCriterion("allay_deliver_item_to_player", PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(var3, (EntityType)EntityType.ALLAY))))).save(var2, "husbandry/allay_deliver_item_to_player");
      Advancement.Builder.advancement().parent(var18).display((ItemLike)Items.NOTE_BLOCK, Component.translatable("advancements.husbandry.allay_deliver_cake_to_note_block.title"), Component.translatable("advancements.husbandry.allay_deliver_cake_to_note_block.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, true).addCriterion("allay_deliver_cake_to_note_block", ItemUsedOnLocationTrigger.TriggerInstance.allayDropItemOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var5, (Block[])(Blocks.NOTE_BLOCK))), ItemPredicate.Builder.item().of(var4, (ItemLike[])(Items.CAKE)))).save(var2, "husbandry/allay_deliver_cake_to_note_block");
      AdvancementHolder var19 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.SNIFFER_EGG, Component.translatable("advancements.husbandry.obtain_sniffer_egg.title"), Component.translatable("advancements.husbandry.obtain_sniffer_egg.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, true).addCriterion("obtain_sniffer_egg", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SNIFFER_EGG)).save(var2, "husbandry/obtain_sniffer_egg");
      AdvancementHolder var20 = Advancement.Builder.advancement().parent(var19).display((ItemLike)Items.TORCHFLOWER_SEEDS, Component.translatable("advancements.husbandry.feed_snifflet.title"), Component.translatable("advancements.husbandry.feed_snifflet.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, true).addCriterion("feed_snifflet", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(var4, (TagKey)ItemTags.SNIFFER_FOOD), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(var3, (EntityType)EntityType.SNIFFER).flags(EntityFlagsPredicate.Builder.flags().setIsBaby(true)))))).save(var2, "husbandry/feed_snifflet");
      Advancement.Builder.advancement().parent(var20).display((ItemLike)Items.PITCHER_POD, Component.translatable("advancements.husbandry.plant_any_sniffer_seed.title"), Component.translatable("advancements.husbandry.plant_any_sniffer_seed.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, true).requirements(AdvancementRequirements.Strategy.OR).addCriterion("torchflower", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.TORCHFLOWER_CROP)).addCriterion("pitcher_pod", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(Blocks.PITCHER_CROP)).save(var2, "husbandry/plant_any_sniffer_seed");
      Advancement.Builder.advancement().parent(var10).display((ItemLike)Items.SHEARS, Component.translatable("advancements.husbandry.remove_wolf_armor.title"), Component.translatable("advancements.husbandry.remove_wolf_armor.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("remove_wolf_armor", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(var4, (ItemLike[])(Items.SHEARS)), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(var3, (EntityType)EntityType.WOLF))))).save(var2, "husbandry/remove_wolf_armor");
      Advancement.Builder.advancement().parent(var10).display((ItemLike)Items.WOLF_ARMOR, Component.translatable("advancements.husbandry.repair_wolf_armor.title"), Component.translatable("advancements.husbandry.repair_wolf_armor.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("repair_wolf_armor", PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(var4, (ItemLike[])(Items.ARMADILLO_SCUTE)), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(var3, (EntityType)EntityType.WOLF).equipment(EntityEquipmentPredicate.Builder.equipment().body(ItemPredicate.Builder.item().of(var4, (ItemLike[])(Items.WOLF_ARMOR)).hasComponents(DataComponentPredicate.builder().expect(DataComponents.DAMAGE, 0).build()))))))).save(var2, "husbandry/repair_wolf_armor");
   }

   public static AdvancementHolder createBreedAllAnimalsAdvancement(AdvancementHolder var0, Consumer<AdvancementHolder> var1, HolderGetter<EntityType<?>> var2, Stream<EntityType<?>> var3, Stream<EntityType<?>> var4) {
      return addBreedable(Advancement.Builder.advancement(), var3, var2, var4).parent(var0).display((ItemLike)Items.GOLDEN_CARROT, Component.translatable("advancements.husbandry.breed_all_animals.title"), Component.translatable("advancements.husbandry.breed_all_animals.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(var1, "husbandry/bred_all_animals");
   }

   private static Advancement.Builder addLeashedFrogVariants(HolderGetter<EntityType<?>> var0, HolderGetter<Item> var1, Advancement.Builder var2) {
      BuiltInRegistries.FROG_VARIANT.listElements().forEach((var3) -> {
         var2.addCriterion(var3.key().location().toString(), PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(var1, Items.LEAD), Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(var0, EntityType.FROG).subPredicate(EntitySubPredicates.frogVariant(var3))))));
      });
      return var2;
   }

   private static Advancement.Builder addFood(Advancement.Builder var0, HolderGetter<Item> var1) {
      Item[] var2 = EDIBLE_ITEMS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item var5 = var2[var4];
         var0.addCriterion(BuiltInRegistries.ITEM.getKey(var5).getPath(), ConsumeItemTrigger.TriggerInstance.usedItem(var1, var5));
      }

      return var0;
   }

   private static Advancement.Builder addBreedable(Advancement.Builder var0, Stream<EntityType<?>> var1, HolderGetter<EntityType<?>> var2, Stream<EntityType<?>> var3) {
      var1.forEach((var2x) -> {
         var0.addCriterion(EntityType.getKey(var2x).toString(), BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(var2, var2x)));
      });
      var3.forEach((var2x) -> {
         var0.addCriterion(EntityType.getKey(var2x).toString(), BredAnimalsTrigger.TriggerInstance.bredAnimals(Optional.of(EntityPredicate.Builder.entity().of(var2, var2x).build()), Optional.of(EntityPredicate.Builder.entity().of(var2, var2x).build()), Optional.empty()));
      });
      return var0;
   }

   private static Advancement.Builder addFishBuckets(Advancement.Builder var0, HolderGetter<Item> var1) {
      Item[] var2 = FISH_BUCKETS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item var5 = var2[var4];
         var0.addCriterion(BuiltInRegistries.ITEM.getKey(var5).getPath(), FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(var1, var5)));
      }

      return var0;
   }

   private static Advancement.Builder addFish(Advancement.Builder var0, HolderGetter<Item> var1) {
      Item[] var2 = FISH;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item var5 = var2[var4];
         var0.addCriterion(BuiltInRegistries.ITEM.getKey(var5).getPath(), FishingRodHookedTrigger.TriggerInstance.fishedItem(Optional.empty(), Optional.empty(), Optional.of(ItemPredicate.Builder.item().of(var1, var5).build())));
      }

      return var0;
   }

   private static Advancement.Builder addCatVariants(Advancement.Builder var0) {
      BuiltInRegistries.CAT_VARIANT.listElements().sorted(Comparator.comparing((var0x) -> {
         return var0x.key().location();
      })).forEach((var1) -> {
         var0.addCriterion(var1.key().location().toString(), TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().subPredicate(EntitySubPredicates.catVariant(var1))));
      });
      return var0;
   }

   private static Advancement.Builder addTamedWolfVariants(Advancement.Builder var0, HolderLookup.Provider var1) {
      HolderLookup.RegistryLookup var2 = var1.lookupOrThrow(Registries.WOLF_VARIANT);
      var2.listElementIds().sorted(Comparator.comparing(ResourceKey::location)).forEach((var2x) -> {
         Holder.Reference var3 = var2.getOrThrow(var2x);
         var0.addCriterion(var2x.location().toString(), TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().subPredicate(EntitySubPredicates.wolfVariant(HolderSet.direct(var3)))));
      });
      return var0;
   }

   static {
      BREEDABLE_ANIMALS = List.of(EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.CAT, EntityType.PANDA, EntityType.FOX, EntityType.BEE, EntityType.HOGLIN, EntityType.STRIDER, EntityType.GOAT, EntityType.AXOLOTL, EntityType.CAMEL, EntityType.ARMADILLO);
      INDIRECTLY_BREEDABLE_ANIMALS = List.of(EntityType.TURTLE, EntityType.FROG, EntityType.SNIFFER);
      FISH = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
      FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
      EDIBLE_ITEMS = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.HONEY_BOTTLE, Items.GLOW_BERRIES};
      WAX_SCRAPING_TOOLS = new Item[]{Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE};
   }
}
