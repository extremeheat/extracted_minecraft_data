package net.minecraft.data.advancements;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.ChanneledLightningTrigger;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.KilledByCrossbowTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.ShotCrossbowTrigger;
import net.minecraft.advancements.critereon.SlideDownBlockTrigger;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.TargetBlockTrigger;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;

public class AdventureAdvancements implements Consumer<Consumer<Advancement>> {
   private static final List<ResourceKey<Biome>> EXPLORABLE_BIOMES;
   private static final EntityType<?>[] MOBS_TO_KILL;

   public AdventureAdvancements() {
      super();
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.advancement().display((ItemLike)Items.MAP, new TranslatableComponent("advancements.adventure.root.title"), new TranslatableComponent("advancements.adventure.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false).requirements(RequirementsStrategy.OR).addCriterion("killed_something", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity()).addCriterion("killed_by_something", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.entityKilledPlayer()).save(var1, "adventure/root");
      Advancement var3 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Blocks.RED_BED, new TranslatableComponent("advancements.adventure.sleep_in_bed.title"), new TranslatableComponent("advancements.adventure.sleep_in_bed.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("slept_in_bed", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.sleptInBed()).save(var1, "adventure/sleep_in_bed");
      addBiomes(Advancement.Builder.advancement(), EXPLORABLE_BIOMES).parent(var3).display((ItemLike)Items.DIAMOND_BOOTS, new TranslatableComponent("advancements.adventure.adventuring_time.title"), new TranslatableComponent("advancements.adventure.adventuring_time.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(500)).save(var1, "adventure/adventuring_time");
      Advancement var4 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.EMERALD, new TranslatableComponent("advancements.adventure.trade.title"), new TranslatableComponent("advancements.adventure.trade.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("traded", (CriterionTriggerInstance)TradeTrigger.TriggerInstance.tradedWithVillager()).save(var1, "adventure/trade");
      Advancement var5 = this.addMobsToKill(Advancement.Builder.advancement()).parent(var2).display((ItemLike)Items.IRON_SWORD, new TranslatableComponent("advancements.adventure.kill_a_mob.title"), new TranslatableComponent("advancements.adventure.kill_a_mob.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.OR).save(var1, "adventure/kill_a_mob");
      this.addMobsToKill(Advancement.Builder.advancement()).parent(var5).display((ItemLike)Items.DIAMOND_SWORD, new TranslatableComponent("advancements.adventure.kill_all_mobs.title"), new TranslatableComponent("advancements.adventure.kill_all_mobs.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(var1, "adventure/kill_all_mobs");
      Advancement var6 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.BOW, new TranslatableComponent("advancements.adventure.shoot_arrow.title"), new TranslatableComponent("advancements.adventure.shoot_arrow.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("shot_arrow", (CriterionTriggerInstance)PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.entity().of((Tag)EntityTypeTags.ARROWS))))).save(var1, "adventure/shoot_arrow");
      Advancement var7 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.TRIDENT, new TranslatableComponent("advancements.adventure.throw_trident.title"), new TranslatableComponent("advancements.adventure.throw_trident.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("shot_trident", (CriterionTriggerInstance)PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.entity().of(EntityType.TRIDENT))))).save(var1, "adventure/throw_trident");
      Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.TRIDENT, new TranslatableComponent("advancements.adventure.very_very_frightening.title"), new TranslatableComponent("advancements.adventure.very_very_frightening.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("struck_villager", (CriterionTriggerInstance)ChanneledLightningTrigger.TriggerInstance.channeledLightning(EntityPredicate.Builder.entity().of(EntityType.VILLAGER).build())).save(var1, "adventure/very_very_frightening");
      Advancement.Builder.advancement().parent(var4).display((ItemLike)Blocks.CARVED_PUMPKIN, new TranslatableComponent("advancements.adventure.summon_iron_golem.title"), new TranslatableComponent("advancements.adventure.summon_iron_golem.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("summoned_golem", (CriterionTriggerInstance)SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().of(EntityType.IRON_GOLEM))).save(var1, "adventure/summon_iron_golem");
      Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.ARROW, new TranslatableComponent("advancements.adventure.sniper_duel.title"), new TranslatableComponent("advancements.adventure.sniper_duel.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("killed_skeleton", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityType.SKELETON).distance(DistancePredicate.horizontal(MinMaxBounds.Floats.atLeast(50.0F))), DamageSourcePredicate.Builder.damageType().isProjectile(true))).save(var1, "adventure/sniper_duel");
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.TOTEM_OF_UNDYING, new TranslatableComponent("advancements.adventure.totem_of_undying.title"), new TranslatableComponent("advancements.adventure.totem_of_undying.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("used_totem", (CriterionTriggerInstance)UsedTotemTrigger.TriggerInstance.usedTotem(Items.TOTEM_OF_UNDYING)).save(var1, "adventure/totem_of_undying");
      Advancement var8 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.CROSSBOW, new TranslatableComponent("advancements.adventure.ol_betsy.title"), new TranslatableComponent("advancements.adventure.ol_betsy.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("shot_crossbow", (CriterionTriggerInstance)ShotCrossbowTrigger.TriggerInstance.shotCrossbow(Items.CROSSBOW)).save(var1, "adventure/ol_betsy");
      Advancement.Builder.advancement().parent(var8).display((ItemLike)Items.CROSSBOW, new TranslatableComponent("advancements.adventure.whos_the_pillager_now.title"), new TranslatableComponent("advancements.adventure.whos_the_pillager_now.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("kill_pillager", (CriterionTriggerInstance)KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(EntityPredicate.Builder.entity().of(EntityType.PILLAGER))).save(var1, "adventure/whos_the_pillager_now");
      Advancement.Builder.advancement().parent(var8).display((ItemLike)Items.CROSSBOW, new TranslatableComponent("advancements.adventure.two_birds_one_arrow.title"), new TranslatableComponent("advancements.adventure.two_birds_one_arrow.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(65)).addCriterion("two_birds", (CriterionTriggerInstance)KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(EntityPredicate.Builder.entity().of(EntityType.PHANTOM), EntityPredicate.Builder.entity().of(EntityType.PHANTOM))).save(var1, "adventure/two_birds_one_arrow");
      Advancement.Builder.advancement().parent(var8).display((ItemLike)Items.CROSSBOW, new TranslatableComponent("advancements.adventure.arbalistic.title"), new TranslatableComponent("advancements.adventure.arbalistic.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(85)).addCriterion("arbalistic", (CriterionTriggerInstance)KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(MinMaxBounds.Ints.exactly(5))).save(var1, "adventure/arbalistic");
      Advancement var9 = Advancement.Builder.advancement().parent(var2).display((ItemStack)Raid.getLeaderBannerInstance(), new TranslatableComponent("advancements.adventure.voluntary_exile.title"), new TranslatableComponent("advancements.adventure.voluntary_exile.description"), (ResourceLocation)null, FrameType.TASK, true, true, true).addCriterion("voluntary_exile", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of((Tag)EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.CAPTAIN))).save(var1, "adventure/voluntary_exile");
      Advancement.Builder.advancement().parent(var9).display((ItemStack)Raid.getLeaderBannerInstance(), new TranslatableComponent("advancements.adventure.hero_of_the_village.title"), new TranslatableComponent("advancements.adventure.hero_of_the_village.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("hero_of_the_village", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.raidWon()).save(var1, "adventure/hero_of_the_village");
      Advancement.Builder.advancement().parent(var2).display((ItemLike)Blocks.HONEY_BLOCK.asItem(), new TranslatableComponent("advancements.adventure.honey_block_slide.title"), new TranslatableComponent("advancements.adventure.honey_block_slide.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("honey_block_slide", (CriterionTriggerInstance)SlideDownBlockTrigger.TriggerInstance.slidesDownBlock(Blocks.HONEY_BLOCK)).save(var1, "adventure/honey_block_slide");
      Advancement.Builder.advancement().parent(var6).display((ItemLike)Blocks.TARGET.asItem(), new TranslatableComponent("advancements.adventure.bullseye.title"), new TranslatableComponent("advancements.adventure.bullseye.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("bullseye", (CriterionTriggerInstance)TargetBlockTrigger.TriggerInstance.targetHit(MinMaxBounds.Ints.exactly(15), EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().distance(DistancePredicate.horizontal(MinMaxBounds.Floats.atLeast(30.0F))).build()))).save(var1, "adventure/bullseye");
   }

   private Advancement.Builder addMobsToKill(Advancement.Builder var1) {
      EntityType[] var2 = MOBS_TO_KILL;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EntityType var5 = var2[var4];
         var1.addCriterion(Registry.ENTITY_TYPE.getKey(var5).toString(), (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(var5)));
      }

      return var1;
   }

   protected static Advancement.Builder addBiomes(Advancement.Builder var0, List<ResourceKey<Biome>> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ResourceKey var3 = (ResourceKey)var2.next();
         var0.addCriterion(var3.location().toString(), (CriterionTriggerInstance)LocationTrigger.TriggerInstance.located(LocationPredicate.inBiome(var3)));
      }

      return var0;
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((Consumer)var1);
   }

   static {
      EXPLORABLE_BIOMES = ImmutableList.of(Biomes.BIRCH_FOREST_HILLS, Biomes.RIVER, Biomes.SWAMP, Biomes.DESERT, Biomes.WOODED_HILLS, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.SNOWY_TAIGA, Biomes.BADLANDS, Biomes.FOREST, Biomes.STONE_SHORE, Biomes.SNOWY_TUNDRA, Biomes.TAIGA_HILLS, new ResourceKey[]{Biomes.SNOWY_MOUNTAINS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.SAVANNA, Biomes.PLAINS, Biomes.FROZEN_RIVER, Biomes.GIANT_TREE_TAIGA, Biomes.SNOWY_BEACH, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MUSHROOM_FIELD_SHORE, Biomes.MOUNTAINS, Biomes.DESERT_HILLS, Biomes.JUNGLE, Biomes.BEACH, Biomes.SAVANNA_PLATEAU, Biomes.SNOWY_TAIGA_HILLS, Biomes.BADLANDS_PLATEAU, Biomes.DARK_FOREST, Biomes.TAIGA, Biomes.BIRCH_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.WOODED_MOUNTAINS, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS});
      MOBS_TO_KILL = new EntityType[]{EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HOGLIN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.PHANTOM, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.STRAY, EntityType.VEX, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.ZOGLIN, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE, EntityType.ZOMBIFIED_PIGLIN};
   }
}
