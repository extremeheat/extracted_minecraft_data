package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;

public record ShapedCraftingRecipeDisplay(int width, int height, List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation)
   implements RecipeDisplay {
   public static final MapCodec<ShapedCraftingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               Codec.INT.fieldOf("width").forGetter(ShapedCraftingRecipeDisplay::width),
               Codec.INT.fieldOf("height").forGetter(ShapedCraftingRecipeDisplay::height),
               SlotDisplay.CODEC.listOf().fieldOf("ingredients").forGetter(ShapedCraftingRecipeDisplay::ingredients),
               SlotDisplay.CODEC.fieldOf("result").forGetter(ShapedCraftingRecipeDisplay::result),
               SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(ShapedCraftingRecipeDisplay::craftingStation)
            )
            .apply(var0, ShapedCraftingRecipeDisplay::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ShapedCraftingRecipeDisplay> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      ShapedCraftingRecipeDisplay::width,
      ByteBufCodecs.VAR_INT,
      ShapedCraftingRecipeDisplay::height,
      SlotDisplay.STREAM_CODEC.apply(ByteBufCodecs.list()),
      ShapedCraftingRecipeDisplay::ingredients,
      SlotDisplay.STREAM_CODEC,
      ShapedCraftingRecipeDisplay::result,
      SlotDisplay.STREAM_CODEC,
      ShapedCraftingRecipeDisplay::craftingStation,
      ShapedCraftingRecipeDisplay::new
   );
   public static final RecipeDisplay.Type<ShapedCraftingRecipeDisplay> TYPE = new RecipeDisplay.Type<>(MAP_CODEC, STREAM_CODEC);

   public ShapedCraftingRecipeDisplay(int width, int height, List<SlotDisplay> ingredients, SlotDisplay result, SlotDisplay craftingStation) {
      super();
      if (ingredients.size() != width * height) {
         throw new IllegalArgumentException("Invalid shaped recipe display contents");
      } else {
         this.width = width;
         this.height = height;
         this.ingredients = ingredients;
         this.result = result;
         this.craftingStation = craftingStation;
      }
   }

   @Override
   public RecipeDisplay.Type<ShapedCraftingRecipeDisplay> type() {
      return TYPE;
   }

   @Override
   public boolean isEnabled(FeatureFlagSet var1) {
      return this.ingredients.stream().allMatch(var1x -> var1x.isEnabled(var1)) && RecipeDisplay.super.isEnabled(var1);
   }
}
