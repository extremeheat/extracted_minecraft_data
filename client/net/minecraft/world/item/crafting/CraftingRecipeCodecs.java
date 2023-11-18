package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CraftingRecipeCodecs {
   private static final Codec<Item> ITEM_NONAIR_CODEC = ExtraCodecs.validate(
      BuiltInRegistries.ITEM.byNameCodec(),
      var0 -> var0 == Items.AIR ? DataResult.error(() -> "Crafting result must not be minecraft:air") : DataResult.success(var0)
   );
   public static final Codec<ItemStack> ITEMSTACK_OBJECT_CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ITEM_NONAIR_CODEC.fieldOf("item").forGetter(ItemStack::getItem),
               ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_INT, "count", 1).forGetter(ItemStack::getCount)
            )
            .apply(var0, ItemStack::new)
   );
   static final Codec<ItemStack> ITEMSTACK_NONAIR_CODEC = ExtraCodecs.validate(
         BuiltInRegistries.ITEM.byNameCodec(),
         var0 -> var0 == Items.AIR ? DataResult.error(() -> "Empty ingredient not allowed here") : DataResult.success(var0)
      )
      .xmap(ItemStack::new, ItemStack::getItem);

   public CraftingRecipeCodecs() {
      super();
   }
}
