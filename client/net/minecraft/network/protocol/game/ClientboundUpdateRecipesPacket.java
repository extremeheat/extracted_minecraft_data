package net.minecraft.network.protocol.game;

import java.util.Collection;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ClientboundUpdateRecipesPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateRecipesPacket> STREAM_CODEC;
   private final List<RecipeHolder<?>> recipes;

   public ClientboundUpdateRecipesPacket(Collection<RecipeHolder<?>> var1) {
      super();
      this.recipes = List.copyOf(var1);
   }

   public PacketType<ClientboundUpdateRecipesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_RECIPES;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateRecipes(this);
   }

   public List<RecipeHolder<?>> getRecipes() {
      return this.recipes;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(RecipeHolder.STREAM_CODEC.apply(ByteBufCodecs.list()), (var0) -> {
         return var0.recipes;
      }, ClientboundUpdateRecipesPacket::new);
   }
}
