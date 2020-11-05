package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;

public class EndGatewayConfiguration implements FeatureConfiguration {
   public static final Codec<EndGatewayConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockPos.CODEC.optionalFieldOf("exit").forGetter((var0x) -> {
         return var0x.exit;
      }), Codec.BOOL.fieldOf("exact").forGetter((var0x) -> {
         return var0x.exact;
      })).apply(var0, EndGatewayConfiguration::new);
   });
   private final Optional<BlockPos> exit;
   private final boolean exact;

   private EndGatewayConfiguration(Optional<BlockPos> var1, boolean var2) {
      super();
      this.exit = var1;
      this.exact = var2;
   }

   public static EndGatewayConfiguration knownExit(BlockPos var0, boolean var1) {
      return new EndGatewayConfiguration(Optional.of(var0), var1);
   }

   public static EndGatewayConfiguration delayedExitSearch() {
      return new EndGatewayConfiguration(Optional.empty(), false);
   }

   public Optional<BlockPos> getExit() {
      return this.exit;
   }

   public boolean isExitExact() {
      return this.exact;
   }
}
