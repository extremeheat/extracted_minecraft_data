package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.inventory.RecipeBookType;

public class ServerboundRecipeBookChangeSettingsPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundRecipeBookChangeSettingsPacket> STREAM_CODEC = Packet.codec(ServerboundRecipeBookChangeSettingsPacket::write, ServerboundRecipeBookChangeSettingsPacket::new);
   private final RecipeBookType bookType;
   private final boolean isOpen;
   private final boolean isFiltering;

   public ServerboundRecipeBookChangeSettingsPacket(RecipeBookType var1, boolean var2, boolean var3) {
      super();
      this.bookType = var1;
      this.isOpen = var2;
      this.isFiltering = var3;
   }

   private ServerboundRecipeBookChangeSettingsPacket(FriendlyByteBuf var1) {
      super();
      this.bookType = (RecipeBookType)var1.readEnum(RecipeBookType.class);
      this.isOpen = var1.readBoolean();
      this.isFiltering = var1.readBoolean();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.bookType);
      var1.writeBoolean(this.isOpen);
      var1.writeBoolean(this.isFiltering);
   }

   public PacketType<ServerboundRecipeBookChangeSettingsPacket> type() {
      return GamePacketTypes.SERVERBOUND_RECIPE_BOOK_CHANGE_SETTINGS;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleRecipeBookChangeSettingsPacket(this);
   }

   public RecipeBookType getBookType() {
      return this.bookType;
   }

   public boolean isOpen() {
      return this.isOpen;
   }

   public boolean isFiltering() {
      return this.isFiltering;
   }
}
