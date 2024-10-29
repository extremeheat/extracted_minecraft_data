package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;

public record ClientboundRecipeBookRemovePacket(List<RecipeDisplayId> recipes) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<ByteBuf, ClientboundRecipeBookRemovePacket> STREAM_CODEC;

   public ClientboundRecipeBookRemovePacket(List<RecipeDisplayId> var1) {
      super();
      this.recipes = var1;
   }

   public PacketType<ClientboundRecipeBookRemovePacket> type() {
      return GamePacketTypes.CLIENTBOUND_RECIPE_BOOK_REMOVE;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRecipeBookRemove(this);
   }

   public List<RecipeDisplayId> recipes() {
      return this.recipes;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(RecipeDisplayId.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundRecipeBookRemovePacket::recipes, ClientboundRecipeBookRemovePacket::new);
   }
}
