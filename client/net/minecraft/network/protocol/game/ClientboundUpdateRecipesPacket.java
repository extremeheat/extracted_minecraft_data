package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

public class ClientboundUpdateRecipesPacket implements Packet<ClientGamePacketListener> {
   private final List<Recipe<?>> recipes;

   public ClientboundUpdateRecipesPacket(Collection<Recipe<?>> var1) {
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

   public List<Recipe<?>> getRecipes() {
      return this.recipes;
   }

   public static Recipe<?> fromNetwork(FriendlyByteBuf var0) {
      ResourceLocation var1 = var0.readResourceLocation();
      ResourceLocation var2 = var0.readResourceLocation();
      return Registry.RECIPE_SERIALIZER
         .getOptional(var1)
         .orElseThrow(() -> new IllegalArgumentException("Unknown recipe serializer " + var1))
         .fromNetwork(var2, var0);
   }

   public static <T extends Recipe<?>> void toNetwork(FriendlyByteBuf var0, T var1) {
      var0.writeResourceLocation(Registry.RECIPE_SERIALIZER.getKey(var1.getSerializer()));
      var0.writeResourceLocation(var1.getId());
      var1.getSerializer().toNetwork(var0, (T)var1);
   }
}
