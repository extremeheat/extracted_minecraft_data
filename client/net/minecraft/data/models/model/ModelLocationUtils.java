package net.minecraft.data.models.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelLocationUtils {
   public ModelLocationUtils() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public static ResourceLocation decorateBlockModelLocation(String var0) {
      return ResourceLocation.withDefaultNamespace("block/" + var0);
   }

   public static ResourceLocation decorateItemModelLocation(String var0) {
      return ResourceLocation.withDefaultNamespace("item/" + var0);
   }

   public static ResourceLocation getModelLocation(Block var0, String var1) {
      ResourceLocation var2 = BuiltInRegistries.BLOCK.getKey(var0);
      return var2.withPath((var1x) -> {
         return "block/" + var1x + var1;
      });
   }

   public static ResourceLocation getModelLocation(Block var0) {
      ResourceLocation var1 = BuiltInRegistries.BLOCK.getKey(var0);
      return var1.withPrefix("block/");
   }

   public static ResourceLocation getModelLocation(Item var0) {
      ResourceLocation var1 = BuiltInRegistries.ITEM.getKey(var0);
      return var1.withPrefix("item/");
   }

   public static ResourceLocation getModelLocation(Item var0, String var1) {
      ResourceLocation var2 = BuiltInRegistries.ITEM.getKey(var0);
      return var2.withPath((var1x) -> {
         return "item/" + var1x + var1;
      });
   }
}
