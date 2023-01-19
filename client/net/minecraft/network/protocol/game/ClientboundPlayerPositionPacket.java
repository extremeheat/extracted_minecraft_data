package net.minecraft.network.protocol.game;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerPositionPacket implements Packet<ClientGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final float yRot;
   private final float xRot;
   private final Set<ClientboundPlayerPositionPacket.RelativeArgument> relativeArguments;
   private final int id;
   private final boolean dismountVehicle;

   public ClientboundPlayerPositionPacket(
      double var1, double var3, double var5, float var7, float var8, Set<ClientboundPlayerPositionPacket.RelativeArgument> var9, int var10, boolean var11
   ) {
      super();
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.yRot = var7;
      this.xRot = var8;
      this.relativeArguments = var9;
      this.id = var10;
      this.dismountVehicle = var11;
   }

   public ClientboundPlayerPositionPacket(FriendlyByteBuf var1) {
      super();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.yRot = var1.readFloat();
      this.xRot = var1.readFloat();
      this.relativeArguments = ClientboundPlayerPositionPacket.RelativeArgument.unpack(var1.readUnsignedByte());
      this.id = var1.readVarInt();
      this.dismountVehicle = var1.readBoolean();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeFloat(this.yRot);
      var1.writeFloat(this.xRot);
      var1.writeByte(ClientboundPlayerPositionPacket.RelativeArgument.pack(this.relativeArguments));
      var1.writeVarInt(this.id);
      var1.writeBoolean(this.dismountVehicle);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMovePlayer(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }

   public int getId() {
      return this.id;
   }

   public boolean requestDismountVehicle() {
      return this.dismountVehicle;
   }

   public Set<ClientboundPlayerPositionPacket.RelativeArgument> getRelativeArguments() {
      return this.relativeArguments;
   }

   public static enum RelativeArgument {
      X(0),
      Y(1),
      Z(2),
      Y_ROT(3),
      X_ROT(4);

      private final int bit;

      private RelativeArgument(int var3) {
         this.bit = var3;
      }

      private int getMask() {
         return 1 << this.bit;
      }

      private boolean isSet(int var1) {
         return (var1 & this.getMask()) == this.getMask();
      }

      public static Set<ClientboundPlayerPositionPacket.RelativeArgument> unpack(int var0) {
         EnumSet var1 = EnumSet.noneOf(ClientboundPlayerPositionPacket.RelativeArgument.class);

         for(ClientboundPlayerPositionPacket.RelativeArgument var5 : values()) {
            if (var5.isSet(var0)) {
               var1.add(var5);
            }
         }

         return var1;
      }

      public static int pack(Set<ClientboundPlayerPositionPacket.RelativeArgument> var0) {
         int var1 = 0;

         for(ClientboundPlayerPositionPacket.RelativeArgument var3 : var0) {
            var1 |= var3.getMask();
         }

         return var1;
      }
   }
}
