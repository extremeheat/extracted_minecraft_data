package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public class SimpleCookingSerializer<T extends AbstractCookingRecipe> implements RecipeSerializer<T> {
   private final AbstractCookingRecipe.Factory<T> factory;
   private final Codec<T> codec;

   public SimpleCookingSerializer(AbstractCookingRecipe.Factory<T> var1, int var2) {
      super();
      this.factory = var1;
      this.codec = RecordCodecBuilder.create(
         var2x -> var2x.group(
                  ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(var0x -> var0x.group),
                  CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(var0x -> var0x.category),
                  Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(var0x -> var0x.ingredient),
                  BuiltInRegistries.ITEM.byNameCodec().xmap(ItemStack::new, ItemStack::getItem).fieldOf("result").forGetter(var0x -> var0x.result),
                  Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(var0x -> var0x.experience),
                  Codec.INT.fieldOf("cookingtime").orElse(var2).forGetter(var0x -> var0x.cookingTime)
               )
               .apply(var2x, var1::create)
      );
   }

   @Override
   public Codec<T> codec() {
      return this.codec;
   }

   public T fromNetwork(FriendlyByteBuf var1) {
      String var2 = var1.readUtf();
      CookingBookCategory var3 = var1.readEnum(CookingBookCategory.class);
      Ingredient var4 = Ingredient.fromNetwork(var1);
      ItemStack var5 = var1.readItem();
      float var6 = var1.readFloat();
      int var7 = var1.readVarInt();
      return this.factory.create(var2, var3, var4, var5, var6, var7);
   }

   public void toNetwork(FriendlyByteBuf var1, T var2) {
      var1.writeUtf(var2.group);
      var1.writeEnum(var2.category());
      var2.ingredient.toNetwork(var1);
      var1.writeItem(var2.result);
      var1.writeFloat(var2.experience);
      var1.writeVarInt(var2.cookingTime);
   }

   public AbstractCookingRecipe create(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      return this.factory.create(var1, var2, var3, var4, var5, var6);
   }
}
