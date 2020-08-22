package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.player.Abilities;

public class ClientboundPlayerAbilitiesPacket implements Packet {
   private boolean invulnerable;
   private boolean isFlying;
   private boolean canFly;
   private boolean instabuild;
   private float flyingSpeed;
   private float walkingSpeed;

   public ClientboundPlayerAbilitiesPacket() {
   }

   public ClientboundPlayerAbilitiesPacket(Abilities var1) {
      this.setInvulnerable(var1.invulnerable);
      this.setFlying(var1.flying);
      this.setCanFly(var1.mayfly);
      this.setInstabuild(var1.instabuild);
      this.setFlyingSpeed(var1.getFlyingSpeed());
      this.setWalkingSpeed(var1.getWalkingSpeed());
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      byte var2 = var1.readByte();
      this.setInvulnerable((var2 & 1) > 0);
      this.setFlying((var2 & 2) > 0);
      this.setCanFly((var2 & 4) > 0);
      this.setInstabuild((var2 & 8) > 0);
      this.setFlyingSpeed(var1.readFloat());
      this.setWalkingSpeed(var1.readFloat());
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      byte var2 = 0;
      if (this.isInvulnerable()) {
         var2 = (byte)(var2 | 1);
      }

      if (this.isFlying()) {
         var2 = (byte)(var2 | 2);
      }

      if (this.canFly()) {
         var2 = (byte)(var2 | 4);
      }

      if (this.canInstabuild()) {
         var2 = (byte)(var2 | 8);
      }

      var1.writeByte(var2);
      var1.writeFloat(this.flyingSpeed);
      var1.writeFloat(this.walkingSpeed);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerAbilities(this);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean var1) {
      this.invulnerable = var1;
   }

   public boolean isFlying() {
      return this.isFlying;
   }

   public void setFlying(boolean var1) {
      this.isFlying = var1;
   }

   public boolean canFly() {
      return this.canFly;
   }

   public void setCanFly(boolean var1) {
      this.canFly = var1;
   }

   public boolean canInstabuild() {
      return this.instabuild;
   }

   public void setInstabuild(boolean var1) {
      this.instabuild = var1;
   }

   public float getFlyingSpeed() {
      return this.flyingSpeed;
   }

   public void setFlyingSpeed(float var1) {
      this.flyingSpeed = var1;
   }

   public float getWalkingSpeed() {
      return this.walkingSpeed;
   }

   public void setWalkingSpeed(float var1) {
      this.walkingSpeed = var1;
   }
}
