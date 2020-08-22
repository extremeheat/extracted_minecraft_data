package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.core.Registry;

public class FoliagePlacerType {
   public static final FoliagePlacerType BLOB_FOLIAGE_PLACER = register("blob_foliage_placer", BlobFoliagePlacer::new);
   public static final FoliagePlacerType SPRUCE_FOLIAGE_PLACER = register("spruce_foliage_placer", SpruceFoliagePlacer::new);
   public static final FoliagePlacerType PINE_FOLIAGE_PLACER = register("pine_foliage_placer", PineFoliagePlacer::new);
   public static final FoliagePlacerType ACACIA_FOLIAGE_PLACER = register("acacia_foliage_placer", AcaciaFoliagePlacer::new);
   private final Function deserializer;

   private static FoliagePlacerType register(String var0, Function var1) {
      return (FoliagePlacerType)Registry.register(Registry.FOLIAGE_PLACER_TYPES, (String)var0, new FoliagePlacerType(var1));
   }

   private FoliagePlacerType(Function var1) {
      this.deserializer = var1;
   }

   public FoliagePlacer deserialize(Dynamic var1) {
      return (FoliagePlacer)this.deserializer.apply(var1);
   }
}
