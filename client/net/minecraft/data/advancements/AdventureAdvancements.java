package net.minecraft.data.advancements;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ChanneledLightningTrigger;
import net.minecraft.advancements.critereon.DamagePredicate;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.DistanceTrigger;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemUsedOnBlockTrigger;
import net.minecraft.advancements.critereon.KilledByCrossbowTrigger;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.LighthingBoltPredicate;
import net.minecraft.advancements.critereon.LightningStrikeTrigger;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.LocationTrigger;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.PlayerHurtEntityTrigger;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.advancements.critereon.ShotCrossbowTrigger;
import net.minecraft.advancements.critereon.SlideDownBlockTrigger;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.advancements.critereon.TargetBlockTrigger;
import net.minecraft.advancements.critereon.TradeTrigger;
import net.minecraft.advancements.critereon.UsedTotemTrigger;
import net.minecraft.advancements.critereon.UsingItemTrigger;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.block.Blocks;

public class AdventureAdvancements implements Consumer<Consumer<Advancement>> {
   private static final int DISTANCE_FROM_BOTTOM_TO_TOP = 384;
   private static final int Y_COORDINATE_AT_TOP = 320;
   private static final int Y_COORDINATE_AT_BOTTOM = -64;
   private static final int BEDROCK_THICKNESS = 5;
   private static final EntityType<?>[] MOBS_TO_KILL;

   public AdventureAdvancements() {
      super();
   }

   private static LightningStrikeTrigger.TriggerInstance fireCountAndBystander(MinMaxBounds.Ints var0, EntityPredicate var1) {
      return LightningStrikeTrigger.TriggerInstance.lighthingStrike(EntityPredicate.Builder.entity().distance(DistancePredicate.absolute(MinMaxBounds.Doubles.atMost(30.0D))).lighthingBolt(LighthingBoltPredicate.blockSetOnFire(var0)).build(), var1);
   }

   private static UsingItemTrigger.TriggerInstance lookAtThroughItem(EntityType<?> var0, Item var1) {
      return UsingItemTrigger.TriggerInstance.lookingAt(EntityPredicate.Builder.entity().player(PlayerPredicate.Builder.player().setLookingAt(EntityPredicate.Builder.entity().method_61(var0).build()).build()), ItemPredicate.Builder.item().method_90(var1));
   }

   public void accept(Consumer<Advancement> var1) {
      Advancement var2 = Advancement.Builder.advancement().display((ItemLike)Items.MAP, new TranslatableComponent("advancements.adventure.root.title"), new TranslatableComponent("advancements.adventure.root.description"), new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png"), FrameType.TASK, false, false, false).requirements(RequirementsStrategy.field_0).addCriterion("killed_something", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity()).addCriterion("killed_by_something", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.entityKilledPlayer()).save(var1, "adventure/root");
      Advancement var3 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Blocks.RED_BED, new TranslatableComponent("advancements.adventure.sleep_in_bed.title"), new TranslatableComponent("advancements.adventure.sleep_in_bed.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("slept_in_bed", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.sleptInBed()).save(var1, "adventure/sleep_in_bed");
      addBiomes(Advancement.Builder.advancement(), this.getAllOverworldBiomes()).parent(var3).display((ItemLike)Items.DIAMOND_BOOTS, new TranslatableComponent("advancements.adventure.adventuring_time.title"), new TranslatableComponent("advancements.adventure.adventuring_time.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(500)).save(var1, "adventure/adventuring_time");
      Advancement var4 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.EMERALD, new TranslatableComponent("advancements.adventure.trade.title"), new TranslatableComponent("advancements.adventure.trade.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("traded", (CriterionTriggerInstance)TradeTrigger.TriggerInstance.tradedWithVillager()).save(var1, "adventure/trade");
      Advancement.Builder.advancement().parent(var4).display((ItemLike)Items.EMERALD, new TranslatableComponent("advancements.adventure.trade_at_world_height.title"), new TranslatableComponent("advancements.adventure.trade_at_world_height.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("trade_at_world_height", (CriterionTriggerInstance)TradeTrigger.TriggerInstance.tradedWithVillager(EntityPredicate.Builder.entity().located(LocationPredicate.atYLocation(MinMaxBounds.Doubles.atLeast(319.0D))))).save(var1, "adventure/trade_at_world_height");
      Advancement var5 = this.addMobsToKill(Advancement.Builder.advancement()).parent(var2).display((ItemLike)Items.IRON_SWORD, new TranslatableComponent("advancements.adventure.kill_a_mob.title"), new TranslatableComponent("advancements.adventure.kill_a_mob.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).requirements(RequirementsStrategy.field_0).save(var1, "adventure/kill_a_mob");
      this.addMobsToKill(Advancement.Builder.advancement()).parent(var5).display((ItemLike)Items.DIAMOND_SWORD, new TranslatableComponent("advancements.adventure.kill_all_mobs.title"), new TranslatableComponent("advancements.adventure.kill_all_mobs.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).save(var1, "adventure/kill_all_mobs");
      Advancement var6 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.BOW, new TranslatableComponent("advancements.adventure.shoot_arrow.title"), new TranslatableComponent("advancements.adventure.shoot_arrow.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("shot_arrow", (CriterionTriggerInstance)PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.entity().method_62(EntityTypeTags.ARROWS))))).save(var1, "adventure/shoot_arrow");
      Advancement var7 = Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.TRIDENT, new TranslatableComponent("advancements.adventure.throw_trident.title"), new TranslatableComponent("advancements.adventure.throw_trident.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("shot_trident", (CriterionTriggerInstance)PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntity(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().isProjectile(true).direct(EntityPredicate.Builder.entity().method_61(EntityType.TRIDENT))))).save(var1, "adventure/throw_trident");
      Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.TRIDENT, new TranslatableComponent("advancements.adventure.very_very_frightening.title"), new TranslatableComponent("advancements.adventure.very_very_frightening.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("struck_villager", (CriterionTriggerInstance)ChanneledLightningTrigger.TriggerInstance.channeledLightning(EntityPredicate.Builder.entity().method_61(EntityType.VILLAGER).build())).save(var1, "adventure/very_very_frightening");
      Advancement.Builder.advancement().parent(var4).display((ItemLike)Blocks.CARVED_PUMPKIN, new TranslatableComponent("advancements.adventure.summon_iron_golem.title"), new TranslatableComponent("advancements.adventure.summon_iron_golem.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("summoned_golem", (CriterionTriggerInstance)SummonedEntityTrigger.TriggerInstance.summonedEntity(EntityPredicate.Builder.entity().method_61(EntityType.IRON_GOLEM))).save(var1, "adventure/summon_iron_golem");
      Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.ARROW, new TranslatableComponent("advancements.adventure.sniper_duel.title"), new TranslatableComponent("advancements.adventure.sniper_duel.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("killed_skeleton", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().method_61(EntityType.SKELETON).distance(DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(50.0D))), DamageSourcePredicate.Builder.damageType().isProjectile(true))).save(var1, "adventure/sniper_duel");
      Advancement.Builder.advancement().parent(var5).display((ItemLike)Items.TOTEM_OF_UNDYING, new TranslatableComponent("advancements.adventure.totem_of_undying.title"), new TranslatableComponent("advancements.adventure.totem_of_undying.description"), (ResourceLocation)null, FrameType.GOAL, true, true, false).addCriterion("used_totem", (CriterionTriggerInstance)UsedTotemTrigger.TriggerInstance.usedTotem((ItemLike)Items.TOTEM_OF_UNDYING)).save(var1, "adventure/totem_of_undying");
      Advancement var8 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.CROSSBOW, new TranslatableComponent("advancements.adventure.ol_betsy.title"), new TranslatableComponent("advancements.adventure.ol_betsy.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("shot_crossbow", (CriterionTriggerInstance)ShotCrossbowTrigger.TriggerInstance.shotCrossbow((ItemLike)Items.CROSSBOW)).save(var1, "adventure/ol_betsy");
      Advancement.Builder.advancement().parent(var8).display((ItemLike)Items.CROSSBOW, new TranslatableComponent("advancements.adventure.whos_the_pillager_now.title"), new TranslatableComponent("advancements.adventure.whos_the_pillager_now.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("kill_pillager", (CriterionTriggerInstance)KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(EntityPredicate.Builder.entity().method_61(EntityType.PILLAGER))).save(var1, "adventure/whos_the_pillager_now");
      Advancement.Builder.advancement().parent(var8).display((ItemLike)Items.CROSSBOW, new TranslatableComponent("advancements.adventure.two_birds_one_arrow.title"), new TranslatableComponent("advancements.adventure.two_birds_one_arrow.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(65)).addCriterion("two_birds", (CriterionTriggerInstance)KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(EntityPredicate.Builder.entity().method_61(EntityType.PHANTOM), EntityPredicate.Builder.entity().method_61(EntityType.PHANTOM))).save(var1, "adventure/two_birds_one_arrow");
      Advancement.Builder.advancement().parent(var8).display((ItemLike)Items.CROSSBOW, new TranslatableComponent("advancements.adventure.arbalistic.title"), new TranslatableComponent("advancements.adventure.arbalistic.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(85)).addCriterion("arbalistic", (CriterionTriggerInstance)KilledByCrossbowTrigger.TriggerInstance.crossbowKilled(MinMaxBounds.Ints.exactly(5))).save(var1, "adventure/arbalistic");
      Advancement var9 = Advancement.Builder.advancement().parent(var2).display((ItemStack)Raid.getLeaderBannerInstance(), new TranslatableComponent("advancements.adventure.voluntary_exile.title"), new TranslatableComponent("advancements.adventure.voluntary_exile.description"), (ResourceLocation)null, FrameType.TASK, true, true, true).addCriterion("voluntary_exile", (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().method_62(EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.CAPTAIN))).save(var1, "adventure/voluntary_exile");
      Advancement.Builder.advancement().parent(var9).display((ItemStack)Raid.getLeaderBannerInstance(), new TranslatableComponent("advancements.adventure.hero_of_the_village.title"), new TranslatableComponent("advancements.adventure.hero_of_the_village.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(100)).addCriterion("hero_of_the_village", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.raidWon()).save(var1, "adventure/hero_of_the_village");
      Advancement.Builder.advancement().parent(var2).display((ItemLike)Blocks.HONEY_BLOCK.asItem(), new TranslatableComponent("advancements.adventure.honey_block_slide.title"), new TranslatableComponent("advancements.adventure.honey_block_slide.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("honey_block_slide", (CriterionTriggerInstance)SlideDownBlockTrigger.TriggerInstance.slidesDownBlock(Blocks.HONEY_BLOCK)).save(var1, "adventure/honey_block_slide");
      Advancement.Builder.advancement().parent(var6).display((ItemLike)Blocks.TARGET.asItem(), new TranslatableComponent("advancements.adventure.bullseye.title"), new TranslatableComponent("advancements.adventure.bullseye.description"), (ResourceLocation)null, FrameType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("bullseye", (CriterionTriggerInstance)TargetBlockTrigger.TriggerInstance.targetHit(MinMaxBounds.Ints.exactly(15), EntityPredicate.Composite.wrap(EntityPredicate.Builder.entity().distance(DistancePredicate.horizontal(MinMaxBounds.Doubles.atLeast(30.0D))).build()))).save(var1, "adventure/bullseye");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.LEATHER_BOOTS, new TranslatableComponent("advancements.adventure.walk_on_powder_snow_with_leather_boots.title"), new TranslatableComponent("advancements.adventure.walk_on_powder_snow_with_leather_boots.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("walk_on_powder_snow_with_leather_boots", (CriterionTriggerInstance)LocationTrigger.TriggerInstance.walkOnBlockWithEquipment(Blocks.POWDER_SNOW, Items.LEATHER_BOOTS)).save(var1, "adventure/walk_on_powder_snow_with_leather_boots");
      Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.LIGHTNING_ROD, new TranslatableComponent("advancements.adventure.lightning_rod_with_villager_no_fire.title"), new TranslatableComponent("advancements.adventure.lightning_rod_with_villager_no_fire.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("lightning_rod_with_villager_no_fire", (CriterionTriggerInstance)fireCountAndBystander(MinMaxBounds.Ints.exactly(0), EntityPredicate.Builder.entity().method_61(EntityType.VILLAGER).build())).save(var1, "adventure/lightning_rod_with_villager_no_fire");
      Advancement var10 = Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.SPYGLASS, new TranslatableComponent("advancements.adventure.spyglass_at_parrot.title"), new TranslatableComponent("advancements.adventure.spyglass_at_parrot.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("spyglass_at_parrot", (CriterionTriggerInstance)lookAtThroughItem(EntityType.PARROT, Items.SPYGLASS)).save(var1, "adventure/spyglass_at_parrot");
      Advancement var11 = Advancement.Builder.advancement().parent(var10).display((ItemLike)Items.SPYGLASS, new TranslatableComponent("advancements.adventure.spyglass_at_ghast.title"), new TranslatableComponent("advancements.adventure.spyglass_at_ghast.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("spyglass_at_ghast", (CriterionTriggerInstance)lookAtThroughItem(EntityType.GHAST, Items.SPYGLASS)).save(var1, "adventure/spyglass_at_ghast");
      Advancement.Builder.advancement().parent(var3).display((ItemLike)Items.JUKEBOX, new TranslatableComponent("advancements.adventure.play_jukebox_in_meadows.title"), new TranslatableComponent("advancements.adventure.play_jukebox_in_meadows.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("play_jukebox_in_meadows", (CriterionTriggerInstance)ItemUsedOnBlockTrigger.TriggerInstance.itemUsedOnBlock(LocationPredicate.Builder.location().setBiome(Biomes.MEADOW).setBlock(BlockPredicate.Builder.block().method_119(Blocks.JUKEBOX).build()), ItemPredicate.Builder.item().method_91(ItemTags.MUSIC_DISCS))).save(var1, "adventure/play_jukebox_in_meadows");
      Advancement.Builder.advancement().parent(var11).display((ItemLike)Items.SPYGLASS, new TranslatableComponent("advancements.adventure.spyglass_at_dragon.title"), new TranslatableComponent("advancements.adventure.spyglass_at_dragon.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("spyglass_at_dragon", (CriterionTriggerInstance)lookAtThroughItem(EntityType.ENDER_DRAGON, Items.SPYGLASS)).save(var1, "adventure/spyglass_at_dragon");
      Advancement.Builder.advancement().parent(var2).display((ItemLike)Items.WATER_BUCKET, new TranslatableComponent("advancements.adventure.fall_from_world_height.title"), new TranslatableComponent("advancements.adventure.fall_from_world_height.description"), (ResourceLocation)null, FrameType.TASK, true, true, false).addCriterion("fall_from_world_height", (CriterionTriggerInstance)DistanceTrigger.TriggerInstance.fallFromHeight(EntityPredicate.Builder.entity().located(LocationPredicate.atYLocation(MinMaxBounds.Doubles.atMost(-59.0D))), DistancePredicate.vertical(MinMaxBounds.Doubles.atLeast(379.0D)), LocationPredicate.atYLocation(MinMaxBounds.Doubles.atLeast(319.0D)))).save(var1, "adventure/fall_from_world_height");
   }

   private List<ResourceKey<Biome>> getAllOverworldBiomes() {
      Set var1 = MultiNoiseBiomeSource.Preset.OVERWORLD.biomeSource(BuiltinRegistries.BIOME).possibleBiomes();
      Stream var10000 = var1.stream();
      Registry var10001 = BuiltinRegistries.BIOME;
      Objects.requireNonNull(var10001);
      return (List)var10000.map(var10001::getResourceKey).flatMap(Optional::stream).collect(Collectors.toList());
   }

   private Advancement.Builder addMobsToKill(Advancement.Builder var1) {
      EntityType[] var2 = MOBS_TO_KILL;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EntityType var5 = var2[var4];
         var1.addCriterion(Registry.ENTITY_TYPE.getKey(var5).toString(), (CriterionTriggerInstance)KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().method_61(var5)));
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
      MOBS_TO_KILL = new EntityType[]{EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HOGLIN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.PHANTOM, EntityType.PIGLIN, EntityType.PIGLIN_BRUTE, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.STRAY, EntityType.VEX, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.ZOGLIN, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE, EntityType.ZOMBIFIED_PIGLIN};
   }
}
