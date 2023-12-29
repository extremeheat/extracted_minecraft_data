package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public abstract class SingleItemRecipe implements Recipe<Container> {
   protected final Ingredient ingredient;
   protected final ItemStack result;
   private final RecipeType<?> type;
   private final RecipeSerializer<?> serializer;
   protected final String group;

   public SingleItemRecipe(RecipeType<?> var1, RecipeSerializer<?> var2, String var3, Ingredient var4, ItemStack var5) {
      super();
      this.type = var1;
      this.serializer = var2;
      this.group = var3;
      this.ingredient = var4;
      this.result = var5;
   }

   @Override
   public RecipeType<?> getType() {
      return this.type;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return this.serializer;
   }

   @Override
   public String getGroup() {
      return this.group;
   }

   @Override
   public ItemStack getResultItem(RegistryAccess var1) {
      return this.result;
   }

   @Override
   public NonNullList<Ingredient> getIngredients() {
      NonNullList var1 = NonNullList.create();
      var1.add(this.ingredient);
      return var1;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return true;
   }

   @Override
   public ItemStack assemble(Container var1, RegistryAccess var2) {
      return this.result.copy();
   }

   public interface Factory<T extends SingleItemRecipe> {
      T create(String var1, Ingredient var2, ItemStack var3);
   }

   public static class Serializer<T extends SingleItemRecipe> implements RecipeSerializer<T> {
      final SingleItemRecipe.Factory<T> factory;
      private final Codec<T> codec;

      protected Serializer(SingleItemRecipe.Factory<T> var1) {
         super();
         this.factory = var1;
         this.codec = RecordCodecBuilder.create(
            var1x -> var1x.group(
                     ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(var0x -> var0x.group),
                     Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(var0x -> var0x.ingredient),
                     ItemStack.RESULT_CODEC.forGetter(var0x -> var0x.result)
                  )
                  .apply(var1x, var1::create)
         );
      }

      @Override
      public Codec<T> codec() {
         return this.codec;
      }

      public T fromNetwork(FriendlyByteBuf var1) {
         String var2 = var1.readUtf();
         Ingredient var3 = Ingredient.fromNetwork(var1);
         ItemStack var4 = var1.readItem();
         return this.factory.create(var2, var3, var4);
      }

      public void toNetwork(FriendlyByteBuf var1, T var2) {
         var1.writeUtf(var2.group);
         var2.ingredient.toNetwork(var1);
         var1.writeItem(var2.result);
      }
   }
}
