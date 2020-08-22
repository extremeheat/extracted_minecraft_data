package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.Collection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ClientboundMapItemDataPacket implements Packet {
   private int mapId;
   private byte scale;
   private boolean trackingPosition;
   private boolean locked;
   private MapDecoration[] decorations;
   private int startX;
   private int startY;
   private int width;
   private int height;
   private byte[] mapColors;

   public ClientboundMapItemDataPacket() {
   }

   public ClientboundMapItemDataPacket(int var1, byte var2, boolean var3, boolean var4, Collection var5, byte[] var6, int var7, int var8, int var9, int var10) {
      this.mapId = var1;
      this.scale = var2;
      this.trackingPosition = var3;
      this.locked = var4;
      this.decorations = (MapDecoration[])var5.toArray(new MapDecoration[var5.size()]);
      this.startX = var7;
      this.startY = var8;
      this.width = var9;
      this.height = var10;
      this.mapColors = new byte[var9 * var10];

      for(int var11 = 0; var11 < var9; ++var11) {
         for(int var12 = 0; var12 < var10; ++var12) {
            this.mapColors[var11 + var12 * var9] = var6[var7 + var11 + (var8 + var12) * 128];
         }
      }

   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.mapId = var1.readVarInt();
      this.scale = var1.readByte();
      this.trackingPosition = var1.readBoolean();
      this.locked = var1.readBoolean();
      this.decorations = new MapDecoration[var1.readVarInt()];

      for(int var2 = 0; var2 < this.decorations.length; ++var2) {
         MapDecoration.Type var3 = (MapDecoration.Type)var1.readEnum(MapDecoration.Type.class);
         this.decorations[var2] = new MapDecoration(var3, var1.readByte(), var1.readByte(), (byte)(var1.readByte() & 15), var1.readBoolean() ? var1.readComponent() : null);
      }

      this.width = var1.readUnsignedByte();
      if (this.width > 0) {
         this.height = var1.readUnsignedByte();
         this.startX = var1.readUnsignedByte();
         this.startY = var1.readUnsignedByte();
         this.mapColors = var1.readByteArray();
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.mapId);
      var1.writeByte(this.scale);
      var1.writeBoolean(this.trackingPosition);
      var1.writeBoolean(this.locked);
      var1.writeVarInt(this.decorations.length);
      MapDecoration[] var2 = this.decorations;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MapDecoration var5 = var2[var4];
         var1.writeEnum(var5.getType());
         var1.writeByte(var5.getX());
         var1.writeByte(var5.getY());
         var1.writeByte(var5.getRot() & 15);
         if (var5.getName() != null) {
            var1.writeBoolean(true);
            var1.writeComponent(var5.getName());
         } else {
            var1.writeBoolean(false);
         }
      }

      var1.writeByte(this.width);
      if (this.width > 0) {
         var1.writeByte(this.height);
         var1.writeByte(this.startX);
         var1.writeByte(this.startY);
         var1.writeByteArray(this.mapColors);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMapItemData(this);
   }

   public int getMapId() {
      return this.mapId;
   }

   public void applyToMap(MapItemSavedData var1) {
      var1.scale = this.scale;
      var1.trackingPosition = this.trackingPosition;
      var1.locked = this.locked;
      var1.decorations.clear();

      int var2;
      for(var2 = 0; var2 < this.decorations.length; ++var2) {
         MapDecoration var3 = this.decorations[var2];
         var1.decorations.put("icon-" + var2, var3);
      }

      for(var2 = 0; var2 < this.width; ++var2) {
         for(int var4 = 0; var4 < this.height; ++var4) {
            var1.colors[this.startX + var2 + (this.startY + var4) * 128] = this.mapColors[var2 + var4 * this.width];
         }
      }

   }
}
