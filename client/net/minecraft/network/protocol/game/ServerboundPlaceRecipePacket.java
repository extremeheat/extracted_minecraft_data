package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ServerboundPlaceRecipePacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundPlaceRecipePacket> STREAM_CODEC = Packet.codec(
      ServerboundPlaceRecipePacket::write, ServerboundPlaceRecipePacket::new
   );
   private final int containerId;
   private final ResourceLocation recipe;
   private final boolean useMaxItems;

   public ServerboundPlaceRecipePacket(int var1, RecipeHolder<?> var2, boolean var3) {
      super();
      this.containerId = var1;
      this.recipe = var2.id();
      this.useMaxItems = var3;
   }

   private ServerboundPlaceRecipePacket(FriendlyByteBuf var1) {
      super();
      this.containerId = var1.readContainerId();
      this.recipe = var1.readResourceLocation();
      this.useMaxItems = var1.readBoolean();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeContainerId(this.containerId);
      var1.writeResourceLocation(this.recipe);
      var1.writeBoolean(this.useMaxItems);
   }

   @Override
   public PacketType<ServerboundPlaceRecipePacket> type() {
      return GamePacketTypes.SERVERBOUND_PLACE_RECIPE;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePlaceRecipe(this);
   }

   public int getContainerId() {
      return this.containerId;
   }

   public ResourceLocation getRecipe() {
      return this.recipe;
   }

   public boolean isUseMaxItems() {
      return this.useMaxItems;
   }
}
