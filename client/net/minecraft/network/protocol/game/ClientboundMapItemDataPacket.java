package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
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
      this.decorations = (List)var1.readNullable((var0) -> {
         return var0.readList((var0x) -> {
            MapDecoration.Type var1 = (MapDecoration.Type)var0x.readEnum(MapDecoration.Type.class);
            byte var2 = var0x.readByte();
            byte var3 = var0x.readByte();
            byte var4 = (byte)(var0x.readByte() & 15);
            Component var5 = (Component)var0x.readNullable(FriendlyByteBuf::readComponent);
            return new MapDecoration(var1, var2, var3, var4, var5);
         });
      });
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
      var1.writeNullable(this.decorations, (var0, var1x) -> {
         var0.writeCollection(var1x, (var0x, var1) -> {
            var0x.writeEnum(var1.getType());
            var0x.writeByte(var1.getX());
            var0x.writeByte(var1.getY());
            var0x.writeByte(var1.getRot() & 15);
            var0x.writeNullable(var1.getName(), FriendlyByteBuf::writeComponent);
         });
      });
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
