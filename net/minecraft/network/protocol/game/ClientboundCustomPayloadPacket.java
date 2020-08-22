package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;

public class ClientboundCustomPayloadPacket implements Packet {
   public static final ResourceLocation BRAND = new ResourceLocation("brand");
   public static final ResourceLocation DEBUG_PATHFINDING_PACKET = new ResourceLocation("debug/path");
   public static final ResourceLocation DEBUG_NEIGHBORSUPDATE_PACKET = new ResourceLocation("debug/neighbors_update");
   public static final ResourceLocation DEBUG_CAVES_PACKET = new ResourceLocation("debug/caves");
   public static final ResourceLocation DEBUG_STRUCTURES_PACKET = new ResourceLocation("debug/structures");
   public static final ResourceLocation DEBUG_WORLDGENATTEMPT_PACKET = new ResourceLocation("debug/worldgen_attempt");
   public static final ResourceLocation DEBUG_POI_TICKET_COUNT_PACKET = new ResourceLocation("debug/poi_ticket_count");
   public static final ResourceLocation DEBUG_POI_ADDED_PACKET = new ResourceLocation("debug/poi_added");
   public static final ResourceLocation DEBUG_POI_REMOVED_PACKET = new ResourceLocation("debug/poi_removed");
   public static final ResourceLocation DEBUG_VILLAGE_SECTIONS = new ResourceLocation("debug/village_sections");
   public static final ResourceLocation DEBUG_GOAL_SELECTOR = new ResourceLocation("debug/goal_selector");
   public static final ResourceLocation DEBUG_BRAIN = new ResourceLocation("debug/brain");
   public static final ResourceLocation DEBUG_BEE = new ResourceLocation("debug/bee");
   public static final ResourceLocation DEBUG_HIVE = new ResourceLocation("debug/hive");
   public static final ResourceLocation DEBUG_GAME_TEST_ADD_MARKER = new ResourceLocation("debug/game_test_add_marker");
   public static final ResourceLocation DEBUG_GAME_TEST_CLEAR = new ResourceLocation("debug/game_test_clear");
   public static final ResourceLocation DEBUG_RAIDS = new ResourceLocation("debug/raids");
   private ResourceLocation identifier;
   private FriendlyByteBuf data;

   public ClientboundCustomPayloadPacket() {
   }

   public ClientboundCustomPayloadPacket(ResourceLocation var1, FriendlyByteBuf var2) {
      this.identifier = var1;
      this.data = var2;
      if (var2.writerIndex() > 1048576) {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.identifier = var1.readResourceLocation();
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 1048576) {
         this.data = new FriendlyByteBuf(var1.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeResourceLocation(this.identifier);
      var1.writeBytes(this.data.copy());
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCustomPayload(this);
   }

   public ResourceLocation getIdentifier() {
      return this.identifier;
   }

   public FriendlyByteBuf getData() {
      return new FriendlyByteBuf(this.data.copy());
   }
}
