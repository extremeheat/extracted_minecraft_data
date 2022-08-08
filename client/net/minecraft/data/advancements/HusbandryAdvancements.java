package net.minecraft.data.advancements;

import com.google.common.collect.BiMap;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.BeeNestDestroyedTrigger;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.BredAnimalsTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.EffectsChangedTrigger;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.FilledBucketTrigger;
import net.minecraft.advancements.critereon.FishingRodHookedTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemInteractWithBlockTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PickedUpItemTrigger;
import net.minecraft.advancements.critereon.PlacedBlockTrigger;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.StartRidingTrigger;
import net.minecraft.advancements.critereon.TameAnimalTrigger;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class HusbandryAdvancements implements Consumer<Consumer<Advancement>> {
   private static final EntityType<?>[] BREEDABLE_ANIMALS;
   private static final EntityType<?>[] INDIRECTLY_BREEDABLE_ANIMALS;
   private static final Item[] FISH;
   private static final Item[] FISH_BUCKETS;
   private static final Item[] EDIBLE_ITEMS;
   private static final Item[] WAX_SCRAPING_TOOLS;

   public HusbandryAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.advancement().display((ItemLike)Blocks.HAY_BLOCK, Component.translatable("advancements.husbandry.root.title"), Component.translatable("advancements.husbandry.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/husbandry.png"), FrameType.TASK, false, false, false).addCriterion("consumed_item", (CriterionTriggerInstance)ConsumeItemTrigger.TriggerInstance.usedItem()).save(var1, "husbandry/root");
      Advancement var3 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.WHEAT, Component.translatable("advancements.husbandry.plant_seed.title"), Component.translatable("advancements.husbandry.plant_seed.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("wheat", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.WHEAT)).addCriterion("pumpkin_stem", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.PUMPKIN_STEM)).addCriterion("melon_stem", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.MELON_STEM)).addCriterion("beetroots", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.BEETROOTS)).addCriterion("nether_wart", (CriterionTriggerInstance)PlacedBlockTrigger.TriggerInstance.placedBlock(Blocks.NETHER_WART)).save(var1, "husbandry/plant_seed");
      Advancement var4 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.WHEAT, Component.translatable("advancements.husbandry.breed_an_animal.title"), Component.translatable("advancements.husbandry.breed_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).addCriterion("bred", (CriterionTriggerInstance)BredAnimalsTrigger.TriggerInstance.bredAnimals()).save(var1, "husbandry/breed_an_animal");
      this.addFood(Advancement.Builder.advancement()).parent(var3).display((ItemLike)Items.APPLE, Component.translatable("advancements.husbandry.balanced_diet.title"), Component.translatable("advancements.husbandry.balanced_diet.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(var1, "husbandry/balanced_diet");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.NETHERITE_HOE, Component.translatable("advancements.husbandry.netherite_hoe.title"), Component.translatable("advancements.husbandry.netherite_hoe.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("netherite_hoe", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.NETHERITE_HOE)).save(var1, "husbandry/obtain_netherite_hoe");
      Advancement var5 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.LEAD, Component.translatable("advancements.husbandry.tame_an_animal.title"), Component.translatable("advancements.husbandry.tame_an_animal.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("tamed_animal", (CriterionTriggerInstance)TameAnimalTrigger.TriggerInstance.tamedAnimal()).save(var1, "husbandry/tame_an_animal");
      this.addBreedable(Advancement.Builder.advancement()).parent(var4).display((ItemLike)Items.GOLDEN_CARROT, Component.translatable("advancements.husbandry.breed_all_animals.title"), Component.translatable("advancements.husbandry.breed_all_animals.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(var1, "husbandry/bred_all_animals");
      Advancement var6 = this.addFish(Advancement.Builder.advancement()).parent(var2).requirements(RequirementsStrategy.OR).display((ItemLike)Items.FISHING_ROD, Component.translatable("advancements.husbandry.fishy_business.title"), Component.translatable("advancements.husbandry.fishy_business.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(var1, "husbandry/fishy_business");
      Advancement var7 = this.addFishBuckets(Advancement.Builder.advancement()).parent(var6).requirements(RequirementsStrategy.OR).display((ItemLike)Items.PUFFERFISH_BUCKET, Component.translatable("advancements.husbandry.tactical_fishing.title"), Component.translatable("advancements.husbandry.tactical_fishing.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(var1, "husbandry/tactical_fishing");
      Advancement var8 = Advancement.Builder.advancement().parent(var7).requirements(RequirementsStrategy.OR).addCriterion(Registry.ITEM.getKey(Items.AXOLOTL_BUCKET).getPath(), (CriterionTriggerInstance)FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(Items.AXOLOTL_BUCKET).build())).display((ItemLike)Items.AXOLOTL_BUCKET, Component.translatable("advancements.husbandry.axolotl_in_a_bucket.title"), Component.translatable("advancements.husbandry.axolotl_in_a_bucket.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(var1, "husbandry/axolotl_in_a_bucket");
      Advancement.Builder.advancement().parent(var8).addCriterion("kill_axolotl_target", (CriterionTriggerInstance)EffectsChangedTrigger.TriggerInstance.gotEffectsFrom(EntityPredicate.Builder.entity().of(EntityType.AXOLOTL).build())).display((ItemLike)Items.TROPICAL_FISH_BUCKET, Component.translatable("advancements.husbandry.kill_axolotl_target.title"), Component.translatable("advancements.husbandry.kill_axolotl_target.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(var1, "husbandry/kill_axolotl_target");
      this.addCatVariants(Advancement.Builder.advancement()).parent(var5).display((ItemLike)Items.COD, Component.translatable("advancements.husbandry.complete_catalogue.title"), Component.translatable("advancements.husbandry.complete_catalogue.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).save(var1, "husbandry/complete_catalogue");
      Advancement var9 = Advancement.Builder.advancement().parent(var2).addCriterion("safely_harvest_honey", (CriterionTriggerInstance)ItemInteractWithBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockTags.BEEHIVES).build()).setSmokey(true), ItemPredicate.Builder.item().of(Items.GLASS_BOTTLE))).display((ItemLike)Items.HONEY_BOTTLE, Component.translatable("advancements.husbandry.safely_harvest_honey.title"), Component.translatable("advancements.husbandry.safely_harvest_honey.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(var1, "husbandry/safely_harvest_honey");
      Advancement var10 = Advancement.Builder.advancement().parent(var9).display((ItemLike)Items.HONEYCOMB, Component.translatable("advancements.husbandry.wax_on.title"), Component.translatable("advancements.husbandry.wax_on.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("wax_on", (CriterionTriggerInstance)ItemInteractWithBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of((Iterable)((BiMap)HoneycombItem.WAXABLES.get()).keySet()).build()), ItemPredicate.Builder.item().of(Items.HONEYCOMB))).save(var1, "husbandry/wax_on");
      Advancement.Builder.advancement().parent(var10).display((ItemLike)Items.STONE_AXE, Component.translatable("advancements.husbandry.wax_off.title"), Component.translatable("advancements.husbandry.wax_off.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("wax_off", (CriterionTriggerInstance)ItemInteractWithBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of((Iterable)((BiMap)HoneycombItem.WAX_OFF_BY_BLOCK.get()).keySet()).build()), ItemPredicate.Builder.item().of((ItemLike[])WAX_SCRAPING_TOOLS))).save(var1, "husbandry/wax_off");
      Advancement var11 = Advancement.Builder.advancement().parent(var2).addCriterion(Registry.ITEM.getKey(Items.TADPOLE_BUCKET).getPath(), (CriterionTriggerInstance)FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(Items.TADPOLE_BUCKET).build())).display((ItemLike)Items.TADPOLE_BUCKET, Component.translatable("advancements.husbandry.tadpole_in_a_bucket.title"), Component.translatable("advancements.husbandry.tadpole_in_a_bucket.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(var1, "husbandry/tadpole_in_a_bucket");
      Advancement var12 = this.addLeashedFrogVariants(Advancement.Builder.advancement()).parent(var11).display((ItemLike)Items.LEAD, Component.translatable("advancements.husbandry.leash_all_frog_variants.title"), Component.translatable("advancements.husbandry.leash_all_frog_variants.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(var1, "husbandry/leash_all_frog_variants");
      Advancement.Builder.advancement().parent(var12).display((ItemLike)Items.VERDANT_FROGLIGHT, Component.translatable("advancements.husbandry.froglights.title"), Component.translatable("advancements.husbandry.froglights.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).addCriterion("froglights", (CriterionTriggerInstance)InventoryChangeTrigger.TriggerInstance.hasItems(Items.OCHRE_FROGLIGHT, Items.PEARLESCENT_FROGLIGHT, Items.VERDANT_FROGLIGHT)).save(var1, "husbandry/froglights");
      Advancement.Builder.advancement().parent(var2).addCriterion("silk_touch_nest", (CriterionTriggerInstance)BeeNestDestroyedTrigger.TriggerInstance.destroyedBeeNest(Blocks.BEE_NEST, ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))), MinMaxBounds.Ints.exactly(3))).display((ItemLike)Blocks.BEE_NEST, Component.translatable("advancements.husbandry.silk_touch_nest.title"), Component.translatable("advancements.husbandry.silk_touch_nest.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).save(var1, "husbandry/silk_touch_nest");
      Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.OAK_BOAT, Component.translatable("advancements.husbandry.ride_a_boat_with_a_goat.title"), Component.translatable("advancements.husbandry.ride_a_boat_with_a_goat.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("ride_a_boat_with_a_goat", (CriterionTriggerInstance)StartRidingTrigger.TriggerInstance.playerStartsRiding(EntityPredicate.Builder.entity().vehicle(EntityPredicate.Builder.entity().of(EntityType.BOAT).passenger(EntityPredicate.Builder.entity().of(EntityType.GOAT).build()).build()))).save(var1, "husbandry/ride_a_boat_with_a_goat");
      Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.GLOW_INK_SAC, Component.translatable("advancements.husbandry.make_a_sign_glow.title"), Component.translatable("advancements.husbandry.make_a_sign_glow.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("make_a_sign_glow", (CriterionTriggerInstance)ItemInteractWithBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(BlockTags.SIGNS).build()), ItemPredicate.Builder.item().of(Items.GLOW_INK_SAC))).save(var1, "husbandry/make_a_sign_glow");
      Advancement var13 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.COOKIE, Component.translatable("advancements.husbandry.allay_deliver_item_to_player.title"), Component.translatable("advancements.husbandry.allay_deliver_item_to_player.description"), (ResourceLocation)null, FrameType.TASK, true, true, true).addCriterion("allay_deliver_item_to_player", (CriterionTriggerInstance)PickedUpItemTrigger.TriggerInstance.thrownItemPickedUpByPlayer(EntityPredicate.Composite.ANY, ItemPredicate.ANY, EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().of(EntityType.ALLAY).build()))).save(var1, "husbandry/allay_deliver_item_to_player");
      Advancement.Builder.advancement().parent(var13).display((ItemLike)Items.NOTE_BLOCK, Component.translatable("advancements.husbandry.allay_deliver_cake_to_note_block.title"), Component.translatable("advancements.husbandry.allay_deliver_cake_to_note_block.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).addCriterion("allay_deliver_cake_to_note_block", (CriterionTriggerInstance)ItemInteractWithBlockTrigger.TriggerInstance.allayDropItemOnBlock(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(Blocks.NOTE_BLOCK).build()), ItemPredicate.Builder.item().of(Items.CAKE))).save(var1, "husbandry/allay_deliver_cake_to_note_block");
   }

   private Advancement.Builder addLeashedFrogVariants(Advancement.Builder var1) {
      Registry.FROG_VARIANT.holders().forEach((var1x) -> {
         var1.addCriterion(var1x.key().location().toString(), (CriterionTriggerInstance)PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(ItemPredicate.Builder.item().of(Items.LEAD), EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().of(EntityType.FROG).subPredicate(EntitySubPredicate.variant((FrogVariant)var1x.value())).build())));
      });
      return var1;
   }

   private Advancement.Builder addFood(Advancement.Builder var1) {
      Item[] var2 = EDIBLE_ITEMS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item var5 = var2[var4];
         var1.addCriterion(Registry.ITEM.getKey(var5).getPath(), (CriterionTriggerInstance)ConsumeItemTrigger.TriggerInstance.usedItem((ItemLike)var5));
      }

      return var1;
   }

   private Advancement.Builder addBreedable(Advancement.Builder var1) {
      EntityType[] var2 = BREEDABLE_ANIMALS;
      int var3 = var2.length;

      int var4;
      EntityType var5;
      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         var1.addCriterion(EntityType.getKey(var5).toString(), (CriterionTriggerInstance)BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(var5)));
      }

      var2 = INDIRECTLY_BREEDABLE_ANIMALS;
      var3 = var2.length;

      for(var4 = 0; var4 < var3; ++var4) {
         var5 = var2[var4];
         var1.addCriterion(EntityType.getKey(var5).toString(), (CriterionTriggerInstance)BredAnimalsTrigger.TriggerInstance.bredAnimals(EntityPredicate.Builder.entity().of(var5).build(), EntityPredicate.Builder.entity().of(var5).build(), EntityPredicate.ANY));
      }

      return var1;
   }

   private Advancement.Builder addFishBuckets(Advancement.Builder var1) {
      Item[] var2 = FISH_BUCKETS;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item var5 = var2[var4];
         var1.addCriterion(Registry.ITEM.getKey(var5).getPath(), (CriterionTriggerInstance)FilledBucketTrigger.TriggerInstance.filledBucket(ItemPredicate.Builder.item().of(var5).build()));
      }

      return var1;
   }

   private Advancement.Builder addFish(Advancement.Builder var1) {
      Item[] var2 = FISH;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Item var5 = var2[var4];
         var1.addCriterion(Registry.ITEM.getKey(var5).getPath(), (CriterionTriggerInstance)FishingRodHookedTrigger.TriggerInstance.fishedItem(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.item().of(var5).build()));
      }

      return var1;
   }

   private Advancement.Builder addCatVariants(Advancement.Builder var1) {
      Registry.CAT_VARIANT.entrySet().stream().sorted(Entry.comparingByKey(Comparator.comparing(ResourceKey::location))).forEach((var1x) -> {
         var1.addCriterion(((ResourceKey)var1x.getKey()).location().toString(), (CriterionTriggerInstance)TameAnimalTrigger.TriggerInstance.tamedAnimal(EntityPredicate.Builder.entity().subPredicate(EntitySubPredicate.variant((CatVariant)var1x.getValue())).build()));
      });
      return var1;
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }

   static {
      BREEDABLE_ANIMALS = new EntityType[]{EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.CAT, EntityType.PANDA, EntityType.FOX, EntityType.BEE, EntityType.HOGLIN, EntityType.STRIDER, EntityType.GOAT, EntityType.AXOLOTL};
      INDIRECTLY_BREEDABLE_ANIMALS = new EntityType[]{EntityType.TURTLE, EntityType.FROG};
      FISH = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
      FISH_BUCKETS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
      EDIBLE_ITEMS = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.HONEY_BOTTLE, Items.GLOW_BERRIES};
      WAX_SCRAPING_TOOLS = new Item[]{Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE};
   }
}
