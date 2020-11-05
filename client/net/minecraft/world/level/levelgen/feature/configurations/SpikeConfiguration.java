package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;

public class SpikeConfiguration implements FeatureConfiguration {
   public static final Codec<SpikeConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.BOOL.fieldOf("crystal_invulnerable").orElse(false).forGetter((var0x) -> {
         return var0x.crystalInvulnerable;
      }), SpikeFeature.EndSpike.CODEC.listOf().fieldOf("spikes").forGetter((var0x) -> {
         return var0x.spikes;
      }), BlockPos.CODEC.optionalFieldOf("crystal_beam_target").forGetter((var0x) -> {
         return Optional.ofNullable(var0x.crystalBeamTarget);
      })).apply(var0, SpikeConfiguration::new);
   });
   private final boolean crystalInvulnerable;
   private final List<SpikeFeature.EndSpike> spikes;
   @Nullable
   private final BlockPos crystalBeamTarget;

   public SpikeConfiguration(boolean var1, List<SpikeFeature.EndSpike> var2, @Nullable BlockPos var3) {
      this(var1, var2, Optional.ofNullable(var3));
   }

   private SpikeConfiguration(boolean var1, List<SpikeFeature.EndSpike> var2, Optional<BlockPos> var3) {
      super();
      this.crystalInvulnerable = var1;
      this.spikes = var2;
      this.crystalBeamTarget = (BlockPos)var3.orElse((Object)null);
   }

   public boolean isCrystalInvulnerable() {
      return this.crystalInvulnerable;
   }

   public List<SpikeFeature.EndSpike> getSpikes() {
      return this.spikes;
   }

   @Nullable
   public BlockPos getCrystalBeamTarget() {
      return this.crystalBeamTarget;
   }
}
