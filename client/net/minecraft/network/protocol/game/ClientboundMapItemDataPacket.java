package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class ClientboundMapItemDataPacket implements Packet<ClientGamePacketListener> {
   private final int mapId;
   private final byte scale;
   private final boolean locked;
   @Nullable
   private final List<MapDecoration> decorations;
   @Nullable
   private final MapItemSavedData.MapPatch colorPatch;

   public ClientboundMapItemDataPacket(int var1, byte var2, boolean var3, @Nullable Collection<MapDecoration> var4, @Nullable MapItemSavedData.MapPatch var5) {
      super();
      this.mapId = var1;
      this.scale = var2;
      this.locked = var3;
      this.decorations = var4 != null ? Lists.newArrayList(var4) : null;
      this.colorPatch = var5;
   }

   public ClientboundMapItemDataPacket(FriendlyByteBuf var1) {
      super();
      this.mapId = var1.readVarInt();
      this.scale = var1.readByte();
      this.locked = var1.readBoolean();
      if (var1.readBoolean()) {
         this.decorations = var1.readList((var0) -> {
            MapDecoration.Type var1 = (MapDecoration.Type)var0.readEnum(MapDecoration.Type.class);
            return new MapDecoration(var1, var0.readByte(), var0.readByte(), (byte)(var0.readByte() & 15), var0.readBoolean() ? var0.readComponent() : null);
         });
      } else {
         this.decorations = null;
      }

      short var2 = var1.readUnsignedByte();
      if (var2 > 0) {
         short var3 = var1.readUnsignedByte();
         short var4 = var1.readUnsignedByte();
         short var5 = var1.readUnsignedByte();
         byte[] var6 = var1.readByteArray();
         this.colorPatch = new MapItemSavedData.MapPatch(var4, var5, var2, var3, var6);
      } else {
         this.colorPatch = null;
      }

   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.mapId);
      var1.writeByte(this.scale);
      var1.writeBoolean(this.locked);
      if (this.decorations != null) {
         var1.writeBoolean(true);
         var1.writeCollection(this.decorations, (var0, var1x) -> {
            var0.writeEnum(var1x.getType());
            var0.writeByte(var1x.getX());
            var0.writeByte(var1x.getY());
            var0.writeByte(var1x.getRot() & 15);
            if (var1x.getName() != null) {
               var0.writeBoolean(true);
               var0.writeComponent(var1x.getName());
            } else {
               var0.writeBoolean(false);
            }

         });
      } else {
         var1.writeBoolean(false);
      }

      if (this.colorPatch != null) {
         var1.writeByte(this.colorPatch.width);
         var1.writeByte(this.colorPatch.height);
         var1.writeByte(this.colorPatch.startX);
         var1.writeByte(this.colorPatch.startY);
         var1.writeByteArray(this.colorPatch.mapColors);
      } else {
         var1.writeByte(0);
      }

   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMapItemData(this);
   }

   public int getMapId() {
      return this.mapId;
   }

   public void applyToMap(MapItemSavedData var1) {
      if (this.decorations != null) {
         var1.addClientSideDecorations(this.decorations);
      }

      if (this.colorPatch != null) {
         this.colorPatch.applyToMap(var1);
      }

   }

   public byte getScale() {
      return this.scale;
   }

   public boolean isLocked() {
      return this.locked;
   }
}
