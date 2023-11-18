package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ClientboundUpdateRecipesPacket implements Packet<ClientGamePacketListener> {
   private final List<RecipeHolder<?>> recipes;

   public ClientboundUpdateRecipesPacket(Collection<RecipeHolder<?>> var1) {
      super();
      this.recipes = Lists.newArrayList(var1);
   }

   public ClientboundUpdateRecipesPacket(FriendlyByteBuf var1) {
      super();
      this.recipes = var1.readList(ClientboundUpdateRecipesPacket::fromNetwork);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.recipes, ClientboundUpdateRecipesPacket::toNetwork);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateRecipes(this);
   }

   public List<RecipeHolder<?>> getRecipes() {
      return this.recipes;
   }

   private static RecipeHolder<?> fromNetwork(FriendlyByteBuf var0) {
      ResourceLocation var1 = var0.readResourceLocation();
      ResourceLocation var2 = var0.readResourceLocation();
      Recipe var3 = BuiltInRegistries.RECIPE_SERIALIZER
         .getOptional(var1)
         .orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + var1))
         .fromNetwork(var0);
      return new RecipeHolder(var2, var3);
   }

   public static <T extends Recipe<?>> void toNetwork(FriendlyByteBuf var0, RecipeHolder<?> var1) {
      var0.writeResourceLocation(BuiltInRegistries.RECIPE_SERIALIZER.getKey(var1.value().getSerializer()));
      var0.writeResourceLocation(var1.id());
      var1.value().getSerializer().toNetwork(var0, (T)var1.value());
   }
}
