package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record StonecutterRecipeDisplay(SlotDisplay input, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
   public static final MapCodec<StonecutterRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(SlotDisplay.CODEC.fieldOf("input").forGetter(StonecutterRecipeDisplay::input), SlotDisplay.CODEC.fieldOf("result").forGetter(StonecutterRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(StonecutterRecipeDisplay::craftingStation)).apply(var0, StonecutterRecipeDisplay::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, StonecutterRecipeDisplay> STREAM_CODEC;
   public static final RecipeDisplay.Type<StonecutterRecipeDisplay> TYPE;

   public StonecutterRecipeDisplay(SlotDisplay var1, SlotDisplay var2, SlotDisplay var3) {
      super();
      this.input = var1;
      this.result = var2;
      this.craftingStation = var3;
   }

   public RecipeDisplay.Type<StonecutterRecipeDisplay> type() {
      return TYPE;
   }

   public SlotDisplay input() {
      return this.input;
   }

   public SlotDisplay result() {
      return this.result;
   }

   public SlotDisplay craftingStation() {
      return this.craftingStation;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, StonecutterRecipeDisplay::input, SlotDisplay.STREAM_CODEC, StonecutterRecipeDisplay::result, SlotDisplay.STREAM_CODEC, StonecutterRecipeDisplay::craftingStation, StonecutterRecipeDisplay::new);
      TYPE = new RecipeDisplay.Type(MAP_CODEC, STREAM_CODEC);
   }
}
