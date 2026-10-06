package net.minecraft.world.level.block.sounds;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import org.jspecify.annotations.Nullable;

public record BlockSoundSet(float volume, float pitch, Optional<Holder<SoundEvent>> breakSound, Optional<Holder<SoundEvent>> stepSound, Optional<Holder<SoundEvent>> placeSound, Optional<Holder<SoundEvent>> hitSound, Optional<Holder<SoundEvent>> fallSound) {
   public static final BlockSoundSet EMPTY = new BlockSoundSet(1.0F, 1.0F, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
   public static final Codec<BlockSoundSet> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.floatRange(1.0E-5F, 10.0F).optionalFieldOf("volume", 1.0F).forGetter(BlockSoundSet::volume), ExtraCodecs.floatRange(1.0E-5F, 2.0F).optionalFieldOf("pitch", 1.0F).forGetter(BlockSoundSet::pitch), SoundEvent.CODEC.optionalFieldOf("break_sound").forGetter(BlockSoundSet::breakSound), SoundEvent.CODEC.optionalFieldOf("step_sound").forGetter(BlockSoundSet::stepSound), SoundEvent.CODEC.optionalFieldOf("place_sound").forGetter(BlockSoundSet::placeSound), SoundEvent.CODEC.optionalFieldOf("hit_sound").forGetter(BlockSoundSet::hitSound), SoundEvent.CODEC.optionalFieldOf("fall_sound").forGetter(BlockSoundSet::fallSound)).apply(i, BlockSoundSet::new));

   public BlockSoundSet(final float volume, final float pitch, final @Nullable Holder<SoundEvent> breakSound, final @Nullable Holder<SoundEvent> stepSound, final @Nullable Holder<SoundEvent> placeSound, final @Nullable Holder<SoundEvent> hitSound, final @Nullable Holder<SoundEvent> fallSound) {
      this(volume, pitch, Optional.ofNullable(breakSound), Optional.ofNullable(stepSound), Optional.ofNullable(placeSound), Optional.ofNullable(hitSound), Optional.ofNullable(fallSound));
   }

   public BlockSoundSet(final @Nullable Holder<SoundEvent> breakSound, final @Nullable Holder<SoundEvent> stepSound, final @Nullable Holder<SoundEvent> placeSound, final @Nullable Holder<SoundEvent> hitSound, final @Nullable Holder<SoundEvent> fallSound) {
      this(1.0F, 1.0F, breakSound, stepSound, placeSound, hitSound, fallSound);
   }

   public BlockSoundSet {
      super();
   }
}
