package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPlayerInputPacket implements Packet {
   private float xxa;
   private float zza;
   private boolean isJumping;
   private boolean isShiftKeyDown;

   public ServerboundPlayerInputPacket() {
   }

   public ServerboundPlayerInputPacket(float var1, float var2, boolean var3, boolean var4) {
      this.xxa = var1;
      this.zza = var2;
      this.isJumping = var3;
      this.isShiftKeyDown = var4;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.xxa = var1.readFloat();
      this.zza = var1.readFloat();
      byte var2 = var1.readByte();
      this.isJumping = (var2 & 1) > 0;
      this.isShiftKeyDown = (var2 & 2) > 0;
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeFloat(this.xxa);
      var1.writeFloat(this.zza);
      byte var2 = 0;
      if (this.isJumping) {
         var2 = (byte)(var2 | 1);
      }

      if (this.isShiftKeyDown) {
         var2 = (byte)(var2 | 2);
      }

      var1.writeByte(var2);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePlayerInput(this);
   }

   public float getXxa() {
      return this.xxa;
   }

   public float getZza() {
      return this.zza;
   }

   public boolean isJumping() {
      return this.isJumping;
   }

   public boolean isShiftKeyDown() {
      return this.isShiftKeyDown;
   }
}
