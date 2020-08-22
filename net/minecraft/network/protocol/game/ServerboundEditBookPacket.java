package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ServerboundEditBookPacket implements Packet {
   private ItemStack book;
   private boolean signing;
   private InteractionHand hand;

   public ServerboundEditBookPacket() {
   }

   public ServerboundEditBookPacket(ItemStack var1, boolean var2, InteractionHand var3) {
      this.book = var1.copy();
      this.signing = var2;
      this.hand = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.book = var1.readItem();
      this.signing = var1.readBoolean();
      this.hand = (InteractionHand)var1.readEnum(InteractionHand.class);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeItem(this.book);
      var1.writeBoolean(this.signing);
      var1.writeEnum(this.hand);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleEditBook(this);
   }

   public ItemStack getBook() {
      return this.book;
   }

   public boolean isSigning() {
      return this.signing;
   }

   public InteractionHand getHand() {
      return this.hand;
   }
}
