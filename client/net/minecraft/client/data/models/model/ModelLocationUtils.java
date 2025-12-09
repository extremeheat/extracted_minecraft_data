package net.minecraft.client.data.models.model;

import java.util.function.UnaryOperator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModelLocationUtils {
   public ModelLocationUtils() {
      super();
   }

   /** @deprecated */
   @Deprecated
   public static Identifier decorateBlockModelLocation(String var0) {
      return Identifier.withDefaultNamespace("block/" + var0);
   }

   public static Identifier decorateItemModelLocation(String var0) {
      return Identifier.withDefaultNamespace("item/" + var0);
   }

   public static Identifier getModelLocation(Block var0, String var1) {
      Identifier var2 = BuiltInRegistries.BLOCK.getKey(var0);
      return var2.withPath((UnaryOperator)((var1x) -> "block/" + var1x + var1));
   }

   public static Identifier getModelLocation(Block var0) {
      Identifier var1 = BuiltInRegistries.BLOCK.getKey(var0);
      return var1.withPrefix("block/");
   }

   public static Identifier getModelLocation(Item var0) {
      Identifier var1 = BuiltInRegistries.ITEM.getKey(var0);
      return var1.withPrefix("item/");
   }

   public static Identifier getModelLocation(Item var0, String var1) {
      Identifier var2 = BuiltInRegistries.ITEM.getKey(var0);
      return var2.withPath((UnaryOperator)((var1x) -> "item/" + var1x + var1));
   }
}
