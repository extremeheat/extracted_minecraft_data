package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;

public record DiskConfiguration(BlockState b, IntProvider c, int d, List<BlockState> e) implements FeatureConfiguration {
   private final BlockState state;
   private final IntProvider radius;
   private final int halfHeight;
   private final List<BlockState> targets;
   public static final Codec<DiskConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockState.CODEC.fieldOf("state").forGetter(DiskConfiguration::state), IntProvider.codec(0, 8).fieldOf("radius").forGetter(DiskConfiguration::radius), Codec.intRange(0, 4).fieldOf("half_height").forGetter(DiskConfiguration::halfHeight), BlockState.CODEC.listOf().fieldOf("targets").forGetter(DiskConfiguration::targets)).apply(var0, DiskConfiguration::new);
   });

   public DiskConfiguration(BlockState var1, IntProvider var2, int var3, List<BlockState> var4) {
      super();
      this.state = var1;
      this.radius = var2;
      this.halfHeight = var3;
      this.targets = var4;
   }

   public BlockState state() {
      return this.state;
   }

   public IntProvider radius() {
      return this.radius;
   }

   public int halfHeight() {
      return this.halfHeight;
   }

   public List<BlockState> targets() {
      return this.targets;
   }
}
