package net.minecraft.network.protocol.game;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerPositionPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: x double
   private final double field_496;
   // $FF: renamed from: y double
   private final double field_497;
   // $FF: renamed from: z double
   private final double field_498;
   private final float yRot;
   private final float xRot;
   private final Set<ClientboundPlayerPositionPacket.RelativeArgument> relativeArguments;
   // $FF: renamed from: id int
   private final int field_499;
   private final boolean dismountVehicle;

   public ClientboundPlayerPositionPacket(double var1, double var3, double var5, float var7, float var8, Set<ClientboundPlayerPositionPacket.RelativeArgument> var9, int var10, boolean var11) {
      super();
      this.field_496 = var1;
      this.field_497 = var3;
      this.field_498 = var5;
      this.yRot = var7;
      this.xRot = var8;
      this.relativeArguments = var9;
      this.field_499 = var10;
      this.dismountVehicle = var11;
   }

   public ClientboundPlayerPositionPacket(FriendlyByteBuf var1) {
      super();
      this.field_496 = var1.readDouble();
      this.field_497 = var1.readDouble();
      this.field_498 = var1.readDouble();
      this.yRot = var1.readFloat();
      this.xRot = var1.readFloat();
      this.relativeArguments = ClientboundPlayerPositionPacket.RelativeArgument.unpack(var1.readUnsignedByte());
      this.field_499 = var1.readVarInt();
      this.dismountVehicle = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.field_496);
      var1.writeDouble(this.field_497);
      var1.writeDouble(this.field_498);
      var1.writeFloat(this.yRot);
      var1.writeFloat(this.xRot);
      var1.writeByte(ClientboundPlayerPositionPacket.RelativeArgument.pack(this.relativeArguments));
      var1.writeVarInt(this.field_499);
      var1.writeBoolean(this.dismountVehicle);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMovePlayer(this);
   }

   public double getX() {
      return this.field_496;
   }

   public double getY() {
      return this.field_497;
   }

   public double getZ() {
      return this.field_498;
   }

   public float getYRot() {
      return this.yRot;
   }

   public float getXRot() {
      return this.xRot;
   }

   public int getId() {
      return this.field_499;
   }

   public boolean requestDismountVehicle() {
      return this.dismountVehicle;
   }

   public Set<ClientboundPlayerPositionPacket.RelativeArgument> getRelativeArguments() {
      return this.relativeArguments;
   }

   public static enum RelativeArgument {
      // $FF: renamed from: X net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket$RelativeArgument
      field_228(0),
      // $FF: renamed from: Y net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket$RelativeArgument
      field_229(1),
      // $FF: renamed from: Z net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket$RelativeArgument
      field_230(2),
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
         ClientboundPlayerPositionPacket.RelativeArgument[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ClientboundPlayerPositionPacket.RelativeArgument var5 = var2[var4];
            if (var5.isSet(var0)) {
               var1.add(var5);
            }
         }

         return var1;
      }

      public static int pack(Set<ClientboundPlayerPositionPacket.RelativeArgument> var0) {
         int var1 = 0;

         ClientboundPlayerPositionPacket.RelativeArgument var3;
         for(Iterator var2 = var0.iterator(); var2.hasNext(); var1 |= var3.getMask()) {
            var3 = (ClientboundPlayerPositionPacket.RelativeArgument)var2.next();
         }

         return var1;
      }

      // $FF: synthetic method
      private static ClientboundPlayerPositionPacket.RelativeArgument[] $values() {
         return new ClientboundPlayerPositionPacket.RelativeArgument[]{field_228, field_229, field_230, Y_ROT, X_ROT};
      }
   }
}
