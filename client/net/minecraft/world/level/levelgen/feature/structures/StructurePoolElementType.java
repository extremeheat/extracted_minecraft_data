package net.minecraft.world.level.levelgen.feature.structures;

import net.minecraft.core.Registry;
import net.minecraft.util.Deserializer;

public interface StructurePoolElementType extends Deserializer<StructurePoolElement> {
   StructurePoolElementType SINGLE = register("single_pool_element", SinglePoolElement::new);
   StructurePoolElementType LIST = register("list_pool_element", ListPoolElement::new);
   StructurePoolElementType FEATURE = register("feature_pool_element", FeaturePoolElement::new);
   StructurePoolElementType EMPTY = register("empty_pool_element", (var0) -> {
      return EmptyPoolElement.INSTANCE;
   });

   static StructurePoolElementType register(String var0, StructurePoolElementType var1) {
      return (StructurePoolElementType)Registry.register(Registry.STRUCTURE_POOL_ELEMENT, (String)var0, var1);
   }
}
