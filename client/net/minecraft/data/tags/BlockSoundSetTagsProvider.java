package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockSoundSetTags;
import net.minecraft.world.level.block.sounds.BlockSoundSet;
import net.minecraft.world.level.block.sounds.BlockSoundSets;

public class BlockSoundSetTagsProvider extends TagsProvider<BlockSoundSet> {
   public BlockSoundSetTagsProvider(final PackOutput output, final CompletableFuture<HolderLookup.Provider> lookupProvider) {
      super(output, Registries.BLOCK_SOUND_SET, lookupProvider);
   }

   protected void addTags(final HolderLookup.Provider registries) {
      this.tag(BlockSoundSetTags.SOUNDS_WOODEN).add(BlockSoundSets.WOOD, BlockSoundSets.NETHER_WOOD, BlockSoundSets.STEM, BlockSoundSets.CHERRY_WOOD, BlockSoundSets.BAMBOO_WOOD);
   }
}
