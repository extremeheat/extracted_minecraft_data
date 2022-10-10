package net.minecraft.network.play.client;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;

public class CPacketSeenAdvancements implements Packet<INetHandlerPlayServer> {
   private CPacketSeenAdvancements.Action field_194166_a;
   private ResourceLocation field_194167_b;

   public CPacketSeenAdvancements() {
      super();
   }

   public CPacketSeenAdvancements(CPacketSeenAdvancements.Action var1, @Nullable ResourceLocation var2) {
      super();
      this.field_194166_a = var1;
      this.field_194167_b = var2;
   }

   public static CPacketSeenAdvancements func_194163_a(Advancement var0) {
      return new CPacketSeenAdvancements(CPacketSeenAdvancements.Action.OPENED_TAB, var0.func_192067_g());
   }

   public static CPacketSeenAdvancements func_194164_a() {
      return new CPacketSeenAdvancements(CPacketSeenAdvancements.Action.CLOSED_SCREEN, (ResourceLocation)null);
   }

   public void func_148837_a(PacketBuffer var1) throws IOException {
      this.field_194166_a = (CPacketSeenAdvancements.Action)var1.func_179257_a(CPacketSeenAdvancements.Action.class);
      if (this.field_194166_a == CPacketSeenAdvancements.Action.OPENED_TAB) {
         this.field_194167_b = var1.func_192575_l();
      }

   }

   public void func_148840_b(PacketBuffer var1) throws IOException {
      var1.func_179249_a(this.field_194166_a);
      if (this.field_194166_a == CPacketSeenAdvancements.Action.OPENED_TAB) {
         var1.func_192572_a(this.field_194167_b);
      }

   }

   public void func_148833_a(INetHandlerPlayServer var1) {
      var1.func_194027_a(this);
   }

   public CPacketSeenAdvancements.Action func_194162_b() {
      return this.field_194166_a;
   }

   public ResourceLocation func_194165_c() {
      return this.field_194167_b;
   }

   public static enum Action {
      OPENED_TAB,
      CLOSED_SCREEN;

      private Action() {
      }
   }
}
