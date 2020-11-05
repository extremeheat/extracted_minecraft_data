package net.minecraft.data.models.model;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelLocationUtils {
   @Deprecated
   public static ResourceLocation decorateBlockModelLocation(String var0) {
      return new ResourceLocation("minecraft", "block/" + var0);
   }

   public static ResourceLocation decorateItemModelLocation(String var0) {
      return new ResourceLocation("minecraft", "item/" + var0);
   }

   public static ResourceLocation getModelLocation(Block var0, String var1) {
      ResourceLocation var2 = Registry.BLOCK.getKey(var0);
      return new ResourceLocation(var2.getNamespace(), "block/" + var2.getPath() + var1);
   }

   public static ResourceLocation getModelLocation(Block var0) {
      ResourceLocation var1 = Registry.BLOCK.getKey(var0);
      return new ResourceLocation(var1.getNamespace(), "block/" + var1.getPath());
   }

   public static ResourceLocation getModelLocation(Item var0) {
      ResourceLocation var1 = Registry.ITEM.getKey(var0);
      return new ResourceLocation(var1.getNamespace(), "item/" + var1.getPath());
   }

   public static ResourceLocation getModelLocation(Item var0, String var1) {
      ResourceLocation var2 = Registry.ITEM.getKey(var0);
      return new ResourceLocation(var2.getNamespace(), "item/" + var2.getPath() + var1);
   }
}
