package net.minecraft.world.item.crafting.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.flag.FeatureFlagSet;

public record FurnaceRecipeDisplay(SlotDisplay ingredient, SlotDisplay fuel, SlotDisplay result, SlotDisplay craftingStation, int duration, float experience) implements RecipeDisplay {
   public static final MapCodec<FurnaceRecipeDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(SlotDisplay.CODEC.fieldOf("ingredient").forGetter(FurnaceRecipeDisplay::ingredient), SlotDisplay.CODEC.fieldOf("fuel").forGetter(FurnaceRecipeDisplay::fuel), SlotDisplay.CODEC.fieldOf("result").forGetter(FurnaceRecipeDisplay::result), SlotDisplay.CODEC.fieldOf("crafting_station").forGetter(FurnaceRecipeDisplay::craftingStation), Codec.INT.fieldOf("duration").forGetter(FurnaceRecipeDisplay::duration), Codec.FLOAT.fieldOf("experience").forGetter(FurnaceRecipeDisplay::experience)).apply(var0, FurnaceRecipeDisplay::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, FurnaceRecipeDisplay> STREAM_CODEC;
   public static final RecipeDisplay.Type<FurnaceRecipeDisplay> TYPE;

   public FurnaceRecipeDisplay(SlotDisplay var1, SlotDisplay var2, SlotDisplay var3, SlotDisplay var4, int var5, float var6) {
      super();
      this.ingredient = var1;
      this.fuel = var2;
      this.result = var3;
      this.craftingStation = var4;
      this.duration = var5;
      this.experience = var6;
   }

   public RecipeDisplay.Type<FurnaceRecipeDisplay> type() {
      return TYPE;
   }

   public boolean isEnabled(FeatureFlagSet var1) {
      return this.ingredient.isEnabled(var1) && this.fuel().isEnabled(var1) && RecipeDisplay.super.isEnabled(var1);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(SlotDisplay.STREAM_CODEC, FurnaceRecipeDisplay::ingredient, SlotDisplay.STREAM_CODEC, FurnaceRecipeDisplay::fuel, SlotDisplay.STREAM_CODEC, FurnaceRecipeDisplay::result, SlotDisplay.STREAM_CODEC, FurnaceRecipeDisplay::craftingStation, ByteBufCodecs.VAR_INT, FurnaceRecipeDisplay::duration, ByteBufCodecs.FLOAT, FurnaceRecipeDisplay::experience, FurnaceRecipeDisplay::new);
      TYPE = new RecipeDisplay.Type<FurnaceRecipeDisplay>(MAP_CODEC, STREAM_CODEC);
   }
}
