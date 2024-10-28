package net.minecraft.data.loot.packs;

import java.util.Set;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Blocks;

public class UpdateOneTwentyOneBlockLoot extends BlockLootSubProvider {
   protected UpdateOneTwentyOneBlockLoot() {
      super(Set.of(), FeatureFlagSet.of(FeatureFlags.UPDATE_1_21));
   }

   protected void generate() {
      this.dropSelf(Blocks.CRAFTER);
      this.dropSelf(Blocks.CHISELED_TUFF);
      this.dropSelf(Blocks.TUFF_STAIRS);
      this.dropSelf(Blocks.TUFF_WALL);
      this.dropSelf(Blocks.POLISHED_TUFF);
      this.dropSelf(Blocks.POLISHED_TUFF_STAIRS);
      this.dropSelf(Blocks.POLISHED_TUFF_WALL);
      this.dropSelf(Blocks.TUFF_BRICKS);
      this.dropSelf(Blocks.TUFF_BRICK_STAIRS);
      this.dropSelf(Blocks.TUFF_BRICK_WALL);
      this.dropSelf(Blocks.CHISELED_TUFF_BRICKS);
      this.add(Blocks.TUFF_SLAB, (var1) -> {
         return this.createSlabItemTable(var1);
      });
      this.add(Blocks.TUFF_BRICK_SLAB, (var1) -> {
         return this.createSlabItemTable(var1);
      });
      this.add(Blocks.POLISHED_TUFF_SLAB, (var1) -> {
         return this.createSlabItemTable(var1);
      });
      this.dropSelf(Blocks.CHISELED_COPPER);
      this.dropSelf(Blocks.EXPOSED_CHISELED_COPPER);
      this.dropSelf(Blocks.WEATHERED_CHISELED_COPPER);
      this.dropSelf(Blocks.OXIDIZED_CHISELED_COPPER);
      this.dropSelf(Blocks.WAXED_CHISELED_COPPER);
      this.dropSelf(Blocks.WAXED_EXPOSED_CHISELED_COPPER);
      this.dropSelf(Blocks.WAXED_WEATHERED_CHISELED_COPPER);
      this.dropSelf(Blocks.WAXED_OXIDIZED_CHISELED_COPPER);
      this.add(Blocks.COPPER_DOOR, (var1) -> {
         return this.createDoorTable(var1);
      });
      this.add(Blocks.EXPOSED_COPPER_DOOR, (var1) -> {
         return this.createDoorTable(var1);
      });
      this.add(Blocks.WEATHERED_COPPER_DOOR, (var1) -> {
         return this.createDoorTable(var1);
      });
      this.add(Blocks.OXIDIZED_COPPER_DOOR, (var1) -> {
         return this.createDoorTable(var1);
      });
      this.add(Blocks.WAXED_COPPER_DOOR, (var1) -> {
         return this.createDoorTable(var1);
      });
      this.add(Blocks.WAXED_EXPOSED_COPPER_DOOR, (var1) -> {
         return this.createDoorTable(var1);
      });
      this.add(Blocks.WAXED_WEATHERED_COPPER_DOOR, (var1) -> {
         return this.createDoorTable(var1);
      });
      this.add(Blocks.WAXED_OXIDIZED_COPPER_DOOR, (var1) -> {
         return this.createDoorTable(var1);
      });
      this.dropSelf(Blocks.COPPER_TRAPDOOR);
      this.dropSelf(Blocks.EXPOSED_COPPER_TRAPDOOR);
      this.dropSelf(Blocks.WEATHERED_COPPER_TRAPDOOR);
      this.dropSelf(Blocks.OXIDIZED_COPPER_TRAPDOOR);
      this.dropSelf(Blocks.WAXED_COPPER_TRAPDOOR);
      this.dropSelf(Blocks.WAXED_EXPOSED_COPPER_TRAPDOOR);
      this.dropSelf(Blocks.WAXED_WEATHERED_COPPER_TRAPDOOR);
      this.dropSelf(Blocks.WAXED_OXIDIZED_COPPER_TRAPDOOR);
      this.dropSelf(Blocks.COPPER_GRATE);
      this.dropSelf(Blocks.EXPOSED_COPPER_GRATE);
      this.dropSelf(Blocks.WEATHERED_COPPER_GRATE);
      this.dropSelf(Blocks.OXIDIZED_COPPER_GRATE);
      this.dropSelf(Blocks.WAXED_COPPER_GRATE);
      this.dropSelf(Blocks.WAXED_EXPOSED_COPPER_GRATE);
      this.dropSelf(Blocks.WAXED_WEATHERED_COPPER_GRATE);
      this.dropSelf(Blocks.WAXED_OXIDIZED_COPPER_GRATE);
      this.dropSelf(Blocks.COPPER_BULB);
      this.dropSelf(Blocks.EXPOSED_COPPER_BULB);
      this.dropSelf(Blocks.WEATHERED_COPPER_BULB);
      this.dropSelf(Blocks.OXIDIZED_COPPER_BULB);
      this.dropSelf(Blocks.WAXED_COPPER_BULB);
      this.dropSelf(Blocks.WAXED_EXPOSED_COPPER_BULB);
      this.dropSelf(Blocks.WAXED_WEATHERED_COPPER_BULB);
      this.dropSelf(Blocks.WAXED_OXIDIZED_COPPER_BULB);
      this.add(Blocks.TRIAL_SPAWNER, noDrop());
      this.add(Blocks.VAULT, noDrop());
      this.dropSelf(Blocks.HEAVY_CORE);
   }
}
