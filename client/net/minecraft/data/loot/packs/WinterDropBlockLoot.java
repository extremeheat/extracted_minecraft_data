package net.minecraft.data.loot.packs;

import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Blocks;

public class WinterDropBlockLoot extends BlockLootSubProvider {
   public WinterDropBlockLoot(HolderLookup.Provider var1) {
      super(Set.of(), FeatureFlagSet.of(FeatureFlags.WINTER_DROP), var1);
   }

   protected void generate() {
      this.dropSelf(Blocks.PALE_OAK_PLANKS);
      this.dropSelf(Blocks.PALE_OAK_SAPLING);
      this.dropSelf(Blocks.PALE_OAK_LOG);
      this.dropSelf(Blocks.STRIPPED_PALE_OAK_LOG);
      this.dropSelf(Blocks.PALE_OAK_WOOD);
      this.dropSelf(Blocks.STRIPPED_PALE_OAK_WOOD);
      this.dropSelf(Blocks.PALE_OAK_SIGN);
      this.dropSelf(Blocks.PALE_OAK_HANGING_SIGN);
      this.dropSelf(Blocks.PALE_OAK_PRESSURE_PLATE);
      this.dropSelf(Blocks.PALE_OAK_TRAPDOOR);
      this.dropSelf(Blocks.PALE_OAK_BUTTON);
      this.dropSelf(Blocks.PALE_OAK_STAIRS);
      this.dropSelf(Blocks.PALE_OAK_FENCE_GATE);
      this.dropSelf(Blocks.PALE_OAK_FENCE);
      this.add(Blocks.PALE_MOSS_CARPET, (var1) -> {
         return this.createMossyCarpetBlockDrops(var1);
      });
      this.add(Blocks.PALE_HANGING_MOSS, (var1) -> {
         return this.createShearsOrSilkTouchOnlyDrop(var1);
      });
      this.dropSelf(Blocks.PALE_MOSS_BLOCK);
      this.dropPottedContents(Blocks.POTTED_PALE_OAK_SAPLING);
      this.add(Blocks.PALE_OAK_SLAB, (var1) -> {
         return this.createSlabItemTable(var1);
      });
      this.add(Blocks.PALE_OAK_DOOR, (var1) -> {
         return this.createDoorTable(var1);
      });
      this.add(Blocks.PALE_OAK_LEAVES, (var1) -> {
         return this.createLeavesDrops(var1, Blocks.PALE_OAK_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.dropWhenSilkTouch(Blocks.CREAKING_HEART);
   }
}
