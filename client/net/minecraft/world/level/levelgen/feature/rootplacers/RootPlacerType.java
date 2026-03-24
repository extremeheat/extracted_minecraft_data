package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class RootPlacerType<P extends RootPlacer> {
   public static final RootPlacerType<MangroveRootPlacer> MANGROVE_ROOT_PLACER;
   private final MapCodec<P> codec;

   private static <P extends RootPlacer> RootPlacerType<P> register(final String name, final MapCodec<P> codec) {
      return (RootPlacerType)Registry.register(BuiltInRegistries.ROOT_PLACER_TYPE, (String)name, new RootPlacerType(codec));
   }

   private RootPlacerType(final MapCodec<P> codec) {
      super();
      this.codec = codec;
   }

   public MapCodec<P> codec() {
      return this.codec;
   }

   static {
      MANGROVE_ROOT_PLACER = register("mangrove_root_placer", MangroveRootPlacer.CODEC);
   }
}
