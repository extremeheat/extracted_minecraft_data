package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ServerboundSeenAdvancementsPacket implements Packet {
   private ServerboundSeenAdvancementsPacket.Action action;
   private ResourceLocation tab;

   public ServerboundSeenAdvancementsPacket() {
   }

   public ServerboundSeenAdvancementsPacket(ServerboundSeenAdvancementsPacket.Action var1, @Nullable ResourceLocation var2) {
      this.action = var1;
      this.tab = var2;
   }

   public static ServerboundSeenAdvancementsPacket openedTab(Advancement var0) {
      return new ServerboundSeenAdvancementsPacket(ServerboundSeenAdvancementsPacket.Action.OPENED_TAB, var0.getId());
   }

   public static ServerboundSeenAdvancementsPacket closedScreen() {
      return new ServerboundSeenAdvancementsPacket(ServerboundSeenAdvancementsPacket.Action.CLOSED_SCREEN, (ResourceLocation)null);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.action = (ServerboundSeenAdvancementsPacket.Action)var1.readEnum(ServerboundSeenAdvancementsPacket.Action.class);
      if (this.action == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         this.tab = var1.readResourceLocation();
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeEnum(this.action);
      if (this.action == ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) {
         var1.writeResourceLocation(this.tab);
      }

   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSeenAdvancements(this);
   }

   public ServerboundSeenAdvancementsPacket.Action getAction() {
      return this.action;
   }

   public ResourceLocation getTab() {
      return this.tab;
   }

   public static enum Action {
      OPENED_TAB,
      CLOSED_SCREEN;
   }
}
