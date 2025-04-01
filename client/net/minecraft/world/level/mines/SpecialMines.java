package net.minecraft.world.level.mines;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.UnlockCondition;
import net.minecraft.world.level.block.Blocks;

public interface SpecialMines {
   List<SpecialMine> DEFAULT_UNLOCKED = new ArrayList();
   SpecialMine ENDERMAN_BOSS = SpecialMine.builder("enderman_boss").withRequiredEffects(WorldEffects.ETERNAL_NIGHT, WorldEffects.SURFACE_WORLD, WorldEffects.DESERT, WorldEffects.THE_ENDERMAN_BOSS_FIGHT, WorldEffects.EVENT_EXIT).register();
   SpecialMine KUIPER_BELT = SpecialMine.builder("kuiper_belt").withRequiredEffects(WorldEffects.KUIPER_WORLD).register();
   SpecialMine SMALL_BUT_DEADLY_BOSS = SpecialMine.builder("small_but_deadly_boss").withRequiredEffects(WorldEffects.SMALL_BUT_DEADLY, WorldEffects.EVENT_EXIT).withOneOf(WorldEffects.BIOMES).register();
   SpecialMine SPOOKY_SCARY_SKELETONS_BOSS = SpecialMine.builder("spooky_scary_skeletons_boss").withRequiredEffects(WorldEffects.CAVE_WORLD, WorldEffects.DESERT, WorldEffects.SPOOKY_SCARY_SKELETONS, WorldEffects.EVENT_EXIT).addUnlockedBy(UnlockCondition.playerKilledEntity(EntityType.SKELETON)).register();
   SpecialMine ANGRY_GHAST_BOSS = SpecialMine.builder("angry_ghast_boss").withRequiredEffects(WorldEffects.FLOATING_ISLANDS_WORLD, WorldEffects.ANGRY_GHAST_BOSS_FIGHT, WorldEffects.EVENT_EXIT).withOneOf(WorldEffects.BIOMES).addUnlockedBy(UnlockCondition.playerAdvancement((var0, var1) -> var1.id().equals(ResourceLocation.withDefaultNamespace("feats/return_to_sender")))).register();
   SpecialMine RAID = SpecialMine.builder("raid").withRequiredEffects(WorldEffects.SURFACE_WORLD, WorldEffects.RAID, WorldEffects.PLAINS, WorldEffects.EVENT_EXIT).addUnlockedBy(UnlockCondition.playerKilledEntity(EntityType.PILLAGER)).register();
   SpecialMine WITHER_BOSS = SpecialMine.builder("wither_boss").withRequiredEffects(WorldEffects.CAVE_WORLD, WorldEffects.WITHER_BOSS_FIGHT, WorldEffects.EVENT_EXIT).withOneOf(WorldEffects.BIOMES).addUnlockedBy(UnlockCondition.obtainedItem((var0, var1, var2) -> var2.is(Items.WITHER_SKELETON_SKULL))).register();
   SpecialMine ENDER_DRAGON_BOSS = SpecialMine.builder("ender_dragon_boss").withRequiredEffects(WorldEffects.FLOATING_ISLANDS_WORLD, WorldEffects.ENDER_DRAGON_BOSS_FIGHT).withOneOf(WorldEffects.BIOMES).addUnlockedBy(UnlockCondition.mineCompletedWith(true, WorldEffects.END)).register();
   SpecialMine WARDEN_BOSS = SpecialMine.builder("warden_boss").withRequiredEffects(WorldEffects.WARDEN_BOSS_FIGHT, WorldEffects.EVENT_EXIT).withOneOf(WorldEffects.BIOMES).addUnlockedBy(UnlockCondition.blockBreak((BiFunction)((var0, var1) -> var1.is(Blocks.AMETHYST_BLOCK) || var1.is(Blocks.AMETHYST_CLUSTER) || var1.is(Blocks.BUDDING_AMETHYST) || var1.is(Blocks.SMALL_AMETHYST_BUD) || var1.is(Blocks.MEDIUM_AMETHYST_BUD) || var1.is(Blocks.LARGE_AMETHYST_BUD)))).register();

   static SpecialMine bootstrap(Registry<SpecialMine> var0) {
      return WITHER_BOSS;
   }
}
