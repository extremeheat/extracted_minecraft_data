package net.minecraft.client.resources.metadata.animation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ExtraCodecs;

public record AnimationMetadataSection(Optional<List<AnimationFrame>> frames, Optional<Integer> frameWidth, Optional<Integer> frameHeight, int defaultFrameTime, boolean interpolatedFrames) {
   public static final Codec<AnimationMetadataSection> CODEC = RecordCodecBuilder.create((var0) -> var0.group(AnimationFrame.CODEC.listOf().optionalFieldOf("frames").forGetter(AnimationMetadataSection::frames), ExtraCodecs.POSITIVE_INT.optionalFieldOf("width").forGetter(AnimationMetadataSection::frameWidth), ExtraCodecs.POSITIVE_INT.optionalFieldOf("height").forGetter(AnimationMetadataSection::frameHeight), ExtraCodecs.POSITIVE_INT.optionalFieldOf("frametime", 1).forGetter(AnimationMetadataSection::defaultFrameTime), Codec.BOOL.optionalFieldOf("interpolate", false).forGetter(AnimationMetadataSection::interpolatedFrames)).apply(var0, AnimationMetadataSection::new));
   public static final MetadataSectionType<AnimationMetadataSection> TYPE;

   public AnimationMetadataSection(Optional<List<AnimationFrame>> var1, Optional<Integer> var2, Optional<Integer> var3, int var4, boolean var5) {
      super();
      this.frames = var1;
      this.frameWidth = var2;
      this.frameHeight = var3;
      this.defaultFrameTime = var4;
      this.interpolatedFrames = var5;
   }

   public FrameSize calculateFrameSize(int var1, int var2) {
      if (this.frameWidth.isPresent()) {
         return this.frameHeight.isPresent() ? new FrameSize((Integer)this.frameWidth.get(), (Integer)this.frameHeight.get()) : new FrameSize((Integer)this.frameWidth.get(), var2);
      } else if (this.frameHeight.isPresent()) {
         return new FrameSize(var1, (Integer)this.frameHeight.get());
      } else {
         int var3 = Math.min(var1, var2);
         return new FrameSize(var3, var3);
      }
   }

   static {
      TYPE = new MetadataSectionType<AnimationMetadataSection>("animation", CODEC);
   }
}
