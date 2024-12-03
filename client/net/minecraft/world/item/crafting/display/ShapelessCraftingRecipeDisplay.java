package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;

public record ShapelessCraftingRecipeDisplay(List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
   public static final MapCodec<ShapelessCraftingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(ShapelessCraftingRecipeDisplay::ingredients), SlotDisplay.CODEC.fieldOf("result").forGetter(ShapelessCraftingRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(ShapelessCraftingRecipeDisplay::craftingStation)).apply(var0, ShapelessCraftingRecipeDisplay::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, ShapelessCraftingRecipeDisplay> STREAM_CODEC;
   public static final RecipeDisplay.Type<ShapelessCraftingRecipeDisplay> TYPE;

   public ShapelessCraftingRecipeDisplay(List<SlotDisplay> var1, SlotDisplay var2, SlotDisplay var3) {
      super();
      this.ingredients = var1;
      this.result = var2;
      this.craftingStation = var3;
   }

   public RecipeDisplay.Type<ShapelessCraftingRecipeDisplay> type() {
      return TYPE;
   }

   public boolean isEnabled(FeatureFlagSet var1) {
      return this.ingredients.stream().allMatch((var1x) -> var1x.isEnabled(var1)) && RecipeDisplay.super.isEnabled(var1);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()), ShapelessCraftingRecipeDisplay::ingredients, SlotDisplay.STREAM_CODEC, ShapelessCraftingRecipeDisplay::result, SlotDisplay.STREAM_CODEC, ShapelessCraftingRecipeDisplay::craftingStation, ShapelessCraftingRecipeDisplay::new);
      TYPE = new RecipeDisplay.Type<ShapelessCraftingRecipeDisplay>(MAP_CODEC, STREAM_CODEC);
   }
}
