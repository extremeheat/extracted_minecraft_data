package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.ChangeDimensionTrigger;
import net.minecraft.advancements.critereon.ConsumeItemTrigger;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.KilledTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.TagPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.SpecialMines;
import net.minecraft.world.level.mines.WorldEffects;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class VanillaFeatsAdvancements implements AdvancementSubProvider {
   public VanillaFeatsAdvancements() {
      super();
   }

   public void generate(HolderLookup.Provider var1, Consumer<AdvancementHolder> var2) {
      HolderLookup.RegistryLookup var3 = var1.lookupOrThrow(Registries.ENTITY_TYPE);
      HolderLookup.RegistryLookup var4 = var1.lookupOrThrow(Registries.ITEM);
      HolderLookup.RegistryLookup var5 = var1.lookupOrThrow(Registries.BLOCK);
      AdvancementHolder var6 = Advancement.Builder.advancement().display((ItemLike)Items.NETHERITE_SWORD, Component.translatable("advancements.feats.root.title"), Component.translatable("advancements.feats.root.description"), ResourceLocation.withDefaultNamespace("gui/advancements/backgrounds/feats"), AdvancementType.TASK, false, false, false).addCriterion("entered_mine", ChangeDimensionTrigger.TriggerInstance.changedDimension()).save(var2, "feats/root");
      AdvancementHolder var7 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.MINE, Component.translatable("advancements.adventure.special_mine_completed.title"), Component.translatable("advancements.adventure.special_mine_completed.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).rewards(AdvancementRewards.Builder.experience(10)).addCriterion("special_mine_completed", PlayerTrigger.TriggerInstance.specialMineCompleted()).save(var2, "mines/special_mine_completed");
      Advancement.Builder var8 = Advancement.Builder.advancement().parent(var7).display((ItemLike)Items.BEACON, Component.translatable("advancements.mines.all_special_mines_completed.title"), Component.translatable("advancements.mines.all_special_mines_completed.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100));
      BuiltInRegistries.SPECIAL_MINE.listElements().forEach((var1x) -> var8.addCriterion(var1x.getRegisteredName(), PlayerTrigger.TriggerInstance.specialMineCompleted((SpecialMine)var1x.value())));
      var8.save(var2, "mines/all_special_mines_completed");
      Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.ENDER_PEARL, Component.translatable("advancements.feats.enderman.title"), Component.translatable("advancements.feats.enderman.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("completed_event", PlayerTrigger.TriggerInstance.specialMineCompleted(SpecialMines.ENDERMAN_BOSS)).save(var2, "feats/enderman");
      ItemStack var9 = new ItemStack(Items.MINE);
      var9.set(DataComponents.ITEM_MODEL, WorldEffects.KUIPER_WORLD.itemModel());
      Advancement.Builder.advancement().parent(var6).display((ItemStack)var9, Component.translatable("advancements.feats.kuiper_world.title"), Component.translatable("advancements.feats.kuiper_world.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("completed_event", PlayerTrigger.TriggerInstance.specialMineCompleted(SpecialMines.KUIPER_BELT)).save(var2, "feats/kuiper_world");
      Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.SILVERFISH_SPAWN_EGG, Component.translatable("advancements.feats.small_but_deadly.title"), Component.translatable("advancements.feats.small_but_deadly.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("completed_event", PlayerTrigger.TriggerInstance.specialMineCompleted(SpecialMines.SMALL_BUT_DEADLY_BOSS)).save(var2, "feats/small_but_deadly");
      AdvancementHolder var10 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.BONE, Component.translatable("advancements.feats.kill_skeleton.title"), Component.translatable("advancements.feats.kill_skeleton.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("killed_skeleton", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(var3, EntityType.SKELETON))).save(var2, "feats/kill_skeleton");
      Advancement.Builder.advancement().parent(var10).display((ItemLike)Items.SKELETON_SKULL, Component.translatable("advancements.feats.spooky_scary_skeletons.title"), Component.translatable("advancements.feats.spooky_scary_skeletons.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("completed_event", PlayerTrigger.TriggerInstance.specialMineCompleted(SpecialMines.SPOOKY_SCARY_SKELETONS_BOSS)).save(var2, "feats/spooky_scary_skeletons");
      AdvancementHolder var11 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.FIRE_CHARGE, Component.translatable("advancements.nether.return_to_sender.title"), Component.translatable("advancements.nether.return_to_sender.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).addCriterion("killed_ghast", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(var3, EntityType.GHAST), DamageSourcePredicate.Builder.damageType().tag(TagPredicate.is(DamageTypeTags.IS_PROJECTILE)).direct(EntityPredicate.Builder.entity().of(var3, EntityType.FIREBALL)))).save(var2, "feats/return_to_sender");
      Advancement.Builder.advancement().parent(var11).display((ItemLike)Items.GHAST_TEAR, Component.translatable("advancements.feats.angry_ghast.title"), Component.translatable("advancements.feats.angry_ghast.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("completed_event", PlayerTrigger.TriggerInstance.specialMineCompleted(SpecialMines.ANGRY_GHAST_BOSS)).save(var2, "feats/angry_ghast");
      AdvancementHolder var12 = Advancement.Builder.advancement().parent(var6).display((ItemStack)Raid.getOminousBannerInstance(var1.lookupOrThrow(Registries.BANNER_PATTERN)), Component.translatable("advancements.feats.kill_pillager.title"), Component.translatable("advancements.feats.kill_pillager.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("killed_pillager", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(var3, EntityType.PILLAGER))).save(var2, "feats/kill_pillager");
      Advancement.Builder.advancement().parent(var12).display((ItemLike)Items.OMINOUS_BOTTLE, Component.translatable("advancements.feats.raid.title"), Component.translatable("advancements.feats.raid.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("completed_event", PlayerTrigger.TriggerInstance.specialMineCompleted(SpecialMines.RAID)).save(var2, "feats/raid");
      AdvancementHolder var13 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.WITHER_SKELETON_SKULL, Component.translatable("advancements.feats.wither_skeleton_skull.title"), Component.translatable("advancements.feats.wither_skeleton_skull.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("has_skull", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WITHER_SKELETON_SKULL)).save(var2, "feats/wither_skeleton_skull");
      Advancement.Builder.advancement().parent(var13).display((ItemLike)Items.NETHER_STAR, Component.translatable("advancements.feats.wither.title"), Component.translatable("advancements.feats.wither.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("completed_event", PlayerTrigger.TriggerInstance.specialMineCompleted(SpecialMines.WITHER_BOSS)).save(var2, "feats/wither");
      AdvancementHolder var14 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.END_CRYSTAL, Component.translatable("advancements.feats.end_world.title"), Component.translatable("advancements.feats.end_world.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("completed_mine", PlayerTrigger.TriggerInstance.levelCompletedWithEffects(WorldEffects.END)).save(var2, "feats/end_world");
      Advancement.Builder.advancement().parent(var14).display((ItemLike)Items.DRAGON_HEAD, Component.translatable("advancements.feats.ender_dragon.title"), Component.translatable("advancements.feats.ender_dragon.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("completed_event", PlayerTrigger.TriggerInstance.specialMineCompleted(SpecialMines.ENDER_DRAGON_BOSS)).save(var2, "feats/ender_dragon");
      Advancement.Builder.advancement().parent(var14).display((ItemLike)Items.DRAGON_BREATH, Component.translatable("advancements.end.dragon_breath.title"), Component.translatable("advancements.end.dragon_breath.description"), (ResourceLocation)null, AdvancementType.GOAL, true, true, false).addCriterion("dragon_breath", InventoryChangeTrigger.TriggerInstance.hasItems(Items.DRAGON_BREATH)).save(var2, "feats/dragon_breath");
      AdvancementHolder var15 = Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.LARGE_AMETHYST_BUD, Component.translatable("advancements.feats.crystal.title"), Component.translatable("advancements.feats.crystal.description"), (ResourceLocation)null, AdvancementType.TASK, true, true, false).addCriterion("has_crystal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(var4, (TagKey)ItemTags.AMETHYST_CRYSTALS))).save(var2, "feats/crystal");
      Advancement.Builder.advancement().parent(var15).display((ItemLike)Items.SCULK_CATALYST, Component.translatable("advancements.feats.warden.title"), Component.translatable("advancements.feats.warden.description"), (ResourceLocation)null, AdvancementType.CHALLENGE, true, true, false).addCriterion("completed_event", PlayerTrigger.TriggerInstance.specialMineCompleted(SpecialMines.WARDEN_BOSS)).save(var2, "feats/warden");
      Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.SHIMMERING_KEY, Component.translatable("advancements.feats.shimmering_key.title"), Component.translatable("advancements.feats.shimmering_key.description"), (ResourceLocation)null, AdvancementType.GOAL, true, true, false).addCriterion("shimmering_key", InventoryChangeTrigger.TriggerInstance.hasItems(Items.SHIMMERING_KEY)).save(var2, "mines/shimmering_key");
      Advancement.Builder.advancement().parent(var6).display((ItemLike)Items.POISONOUS_POTATO, Component.translatable("advancements.feats.eat_your_veggies.title"), Component.translatable("advancements.feats.eat_your_veggies.description"), (ResourceLocation)null, AdvancementType.GOAL, true, true, false).addCriterion("taters_eaten", ConsumeItemTrigger.TriggerInstance.hasEatenOneHundredPotatoes()).rewards((new AdvancementRewards.Builder()).addLootTable(BuiltInLootTables.EAT_YOUR_VEGGIES)).save(var2, "mines/eat_your_veggies");
   }
}
