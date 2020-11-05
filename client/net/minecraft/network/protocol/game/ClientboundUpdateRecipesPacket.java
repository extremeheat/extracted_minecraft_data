package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ClientboundUpdateRecipesPacket implements Packet<ClientGamePacketListener> {
   private List<Recipe<?>> recipes;

   public ClientboundUpdateRecipesPacket() {
      super();
   }

   public ClientboundUpdateRecipesPacket(Collection<Recipe<?>> var1) {
      super();
      this.recipes = Lists.newArrayList(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateRecipes(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.recipes = Lists.newArrayList();
      int var2 = var1.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.recipes.add(fromNetwork(var1));
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.recipes.size());
      Iterator var2 = this.recipes.iterator();

      while(var2.hasNext()) {
         Recipe var3 = (Recipe)var2.next();
         toNetwork(var3, var1);
      }

   }

   public List<Recipe<?>> getRecipes() {
      return this.recipes;
   }

   public static Recipe<?> fromNetwork(FriendlyByteBuf var0) {
      ResourceLocation var1 = var0.readResourceLocation();
      ResourceLocation var2 = var0.readResourceLocation();
      return ((RecipeSerializer)Registry.RECIPE_SERIALIZER.getOptional(var1).orElseThrow(() -> {
         return new IllegalArgumentException("Unknown recipe serializer " + var1);
      })).fromNetwork(var2, var0);
   }

   public static <T extends Recipe<?>> void toNetwork(T var0, FriendlyByteBuf var1) {
      var1.writeResourceLocation(Registry.RECIPE_SERIALIZER.getKey(var0.getSerializer()));
      var1.writeResourceLocation(var0.getId());
      var0.getSerializer().toNetwork(var1, var0);
   }
}
