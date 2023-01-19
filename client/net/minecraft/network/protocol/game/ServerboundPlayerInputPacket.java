package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ServerboundPlayerInputPacket implements Packet<ServerGamePacketListener> {
   private static final int FLAG_JUMPING = 1;
   private static final int FLAG_SHIFT_KEY_DOWN = 2;
   private final float xxa;
   private final float zza;
   private final boolean isJumping;
   private final boolean isShiftKeyDown;

   public ServerboundPlayerInputPacket(float var1, float var2, boolean var3, boolean var4) {
      super();
      this.xxa = var1;
      this.zza = var2;
      this.isJumping = var3;
      this.isShiftKeyDown = var4;
   }

   public ServerboundPlayerInputPacket(FriendlyByteBuf var1) {
      super();
      this.xxa = var1.readFloat();
      this.zza = var1.readFloat();
      byte var2 = var1.readByte();
      this.isJumping = (var2 & 1) > 0;
      this.isShiftKeyDown = (var2 & 2) > 0;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
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
