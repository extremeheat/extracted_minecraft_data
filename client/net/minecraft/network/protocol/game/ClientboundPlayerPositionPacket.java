package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundPlayerPositionPacket implements Packet<ClientGamePacketListener> {
   private double x;
   private double y;
   private double z;
   private float yRot;
   private float xRot;
   private Set<ClientboundPlayerPositionPacket.RelativeArgument> relativeArguments;
   private int id;

   public ClientboundPlayerPositionPacket() {
      super();
   }

   public ClientboundPlayerPositionPacket(double var1, double var3, double var5, float var7, float var8, Set<ClientboundPlayerPositionPacket.RelativeArgument> var9, int var10) {
      super();
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.yRot = var7;
      this.xRot = var8;
      this.relativeArguments = var9;
      this.id = var10;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.yRot = var1.readFloat();
      this.xRot = var1.readFloat();
      this.relativeArguments = ClientboundPlayerPositionPacket.RelativeArgument.unpack(var1.readUnsignedByte());
      this.id = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeFloat(this.yRot);
      var1.writeFloat(this.xRot);
      var1.writeByte(ClientboundPlayerPositionPacket.RelativeArgument.pack(this.relativeArguments));
      var1.writeVarInt(this.id);
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
   }
}
