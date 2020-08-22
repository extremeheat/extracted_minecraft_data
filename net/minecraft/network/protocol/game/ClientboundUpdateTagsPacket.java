package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.tags.TagManager;

public class ClientboundUpdateTagsPacket implements Packet {
   private TagManager tags;

   public ClientboundUpdateTagsPacket() {
   }

   public ClientboundUpdateTagsPacket(TagManager var1) {
      this.tags = var1;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.tags = TagManager.deserializeFromNetwork(var1);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      this.tags.serializeToNetwork(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateTags(this);
   }

   public TagManager getTags() {
      return this.tags;
   }
}
