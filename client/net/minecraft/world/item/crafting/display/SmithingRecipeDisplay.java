package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record SmithingRecipeDisplay(SlotDisplay template, SlotDisplay base, SlotDisplay addition, SlotDisplay result, SlotDisplay craftingStation) implements RecipeDisplay {
   public static final MapCodec<SmithingRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(SlotDisplay.CODEC.fieldOf("template").forGetter(SmithingRecipeDisplay::template), SlotDisplay.CODEC.fieldOf("base").forGetter(SmithingRecipeDisplay::base), SlotDisplay.CODEC.fieldOf("addition").forGetter(SmithingRecipeDisplay::addition), SlotDisplay.CODEC.fieldOf("result").forGetter(SmithingRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(SmithingRecipeDisplay::craftingStation)).apply(var0, SmithingRecipeDisplay::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, SmithingRecipeDisplay> STREAM_CODEC;
   public static final RecipeDisplay.Type<SmithingRecipeDisplay> TYPE;

   public SmithingRecipeDisplay(SlotDisplay var1, SlotDisplay var2, SlotDisplay var3, SlotDisplay var4, SlotDisplay var5) {
      super();
      this.template = var1;
      this.base = var2;
      this.addition = var3;
      this.result = var4;
      this.craftingStation = var5;
   }

   public RecipeDisplay.Type<SmithingRecipeDisplay> type() {
      return TYPE;
   }

   public SlotDisplay template() {
      return this.template;
   }

   public SlotDisplay base() {
      return this.base;
   }

   public SlotDisplay addition() {
      return this.addition;
   }

   public SlotDisplay result() {
      return this.result;
   }

   public SlotDisplay craftingStation() {
      return this.craftingStation;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, SmithingRecipeDisplay::template, SlotDisplay.STREAM_CODEC, SmithingRecipeDisplay::base, SlotDisplay.STREAM_CODEC, SmithingRecipeDisplay::addition, SlotDisplay.STREAM_CODEC, SmithingRecipeDisplay::result, SlotDisplay.STREAM_CODEC, SmithingRecipeDisplay::craftingStation, SmithingRecipeDisplay::new);
      TYPE = new RecipeDisplay.Type(MAP_CODEC, STREAM_CODEC);
   }
}
