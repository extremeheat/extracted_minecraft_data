package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.sounds.BlockSoundSet;

public interface BlockSoundSetTags {
   TagKey<BlockSoundSet> SOUNDS_WOODEN = create("sounds_wooden");

   private static TagKey<BlockSoundSet> create(final String name) {
      return TagKey.<BlockSoundSet>create(Registries.BLOCK_SOUND_SET, Identifier.withDefaultNamespace(name));
   }
}
