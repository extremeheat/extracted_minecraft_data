package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SmithingRecipeDisplay(SlotDisplay template, SlotDisplay base, SlotDisplay addition, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
   public static final MapCodec<SmithingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(SlotDisplay.CODEC.fieldOf("template").forGetter(SmithingRecipeDisplay::template), SlotDisplay.CODEC.fieldOf("base").forGetter(SmithingRecipeDisplay::base), SlotDisplay.CODEC.fieldOf("addition").forGetter(SmithingRecipeDisplay::addition), SlotDisplay.CODEC.fieldOf("result").forGetter(SmithingRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SmithingRecipeDisplay::craftingStation)).apply(i, SmithingRecipeDisplay::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, SmithingRecipeDisplay> STREAM_CODEC;
   public static final RecipeDisplay.Type<SmithingRecipeDisplay> TYPE;

   public SmithingRecipeDisplay {
      super();
   }

   public RecipeDisplay.Type<SmithingRecipeDisplay> type() {
      return TYPE;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, SmithingRecipeDisplay::template, SlotDisplay.STREAM_CODEC, SmithingRecipeDisplay::base, SlotDisplay.STREAM_CODEC, SmithingRecipeDisplay::addition, SlotDisplay.STREAM_CODEC, SmithingRecipeDisplay::result, SlotDisplay.STREAM_CODEC, SmithingRecipeDisplay::craftingStation, SmithingRecipeDisplay::new);
      TYPE = new RecipeDisplay.Type<SmithingRecipeDisplay>(MAP_CODEC, STREAM_CODEC);
   }
}
