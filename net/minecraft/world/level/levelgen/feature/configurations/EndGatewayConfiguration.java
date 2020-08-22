package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import net.minecraft.core.BlockPos;

public class EndGatewayConfiguration implements FeatureConfiguration {
   private final Optional exit;
   private final boolean exact;

   private EndGatewayConfiguration(Optional var1, boolean var2) {
      this.exit = var1;
      this.exact = var2;
   }

   public static EndGatewayConfiguration knownExit(BlockPos var0, boolean var1) {
      return new EndGatewayConfiguration(Optional.of(var0), var1);
   }

   public static EndGatewayConfiguration delayedExitSearch() {
      return new EndGatewayConfiguration(Optional.empty(), false);
   }

   public Optional getExit() {
      return this.exit;
   }

   public boolean isExitExact() {
      return this.exact;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, this.exit.map((var2) -> {
         return var1.createMap(ImmutableMap.of(var1.createString("exit_x"), var1.createInt(var2.getX()), var1.createString("exit_y"), var1.createInt(var2.getY()), var1.createString("exit_z"), var1.createInt(var2.getZ()), var1.createString("exact"), var1.createBoolean(this.exact)));
      }).orElse(var1.emptyMap()));
   }

   public static EndGatewayConfiguration deserialize(Dynamic var0) {
      Optional var1 = var0.get("exit_x").asNumber().flatMap((var1x) -> {
         return var0.get("exit_y").asNumber().flatMap((var2) -> {
            return var0.get("exit_z").asNumber().map((var2x) -> {
               return new BlockPos(var1x.intValue(), var2.intValue(), var2x.intValue());
            });
         });
      });
      boolean var2 = var0.get("exact").asBoolean(false);
      return new EndGatewayConfiguration(var1, var2);
   }
}
