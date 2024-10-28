package net.minecraft.world.item.crafting;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class SimpleCookingSerializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {
   private final AbstractCookingRecipe.Factory<T> factory;
   private final MapCodec<T> codec;
   private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

   public SimpleCookingSerializer(AbstractCookingRecipe.Factory<T> var1, int var2) {
      super();
      this.factory = var1;
      this.codec = RecordCodecBuilder.mapCodec((var2x) -> {
         Products.P6 var10000 = var2x.group(Codec.STRING.optionalFieldOf("group", "").forGetter((var0) -> {
            return var0.group;
         }), CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter((var0) -> {
            return var0.category;
         }), Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter((var0) -> {
            return var0.ingredient;
         }), ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter((var0) -> {
            return var0.result;
         }), Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter((var0) -> {
            return var0.experience;
         }), Codec.INT.fieldOf("cookingtime").orElse(var2).forGetter((var0) -> {
            return var0.cookingTime;
         }));
         Objects.requireNonNull(var1);
         return var10000.apply(var2x, var1::create);
      });
      this.streamCodec = StreamCodec.of(this::toNetwork, this::fromNetwork);
   }

   public MapCodec<T> codec() {
      return this.codec;
   }

   public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
      return this.streamCodec;
   }

   private T fromNetwork(RegistryFriendlyByteBuf var1) {
      String var2 = var1.readUtf();
      CookingBookCategory var3 = (CookingBookCategory)var1.readEnum(CookingBookCategory.class);
      Ingredient var4 = (Ingredient)Ingredient.CONTENTS_STREAM_CODEC.decode(var1);
      ItemStack var5 = (ItemStack)ItemStack.STREAM_CODEC.decode(var1);
      float var6 = var1.readFloat();
      int var7 = var1.readVarInt();
      return this.factory.create(var2, var3, var4, var5, var6, var7);
   }

   private void toNetwork(RegistryFriendlyByteBuf var1, T var2) {
      var1.writeUtf(var2.group);
      var1.writeEnum(var2.category());
      Ingredient.CONTENTS_STREAM_CODEC.encode(var1, var2.ingredient);
      ItemStack.STREAM_CODEC.encode(var1, var2.result);
      var1.writeFloat(var2.experience);
      var1.writeVarInt(var2.cookingTime);
   }

   public AbstractCookingRecipe create(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      return this.factory.create(var1, var2, var3, var4, var5, var6);
   }
}
