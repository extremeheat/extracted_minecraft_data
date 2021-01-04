package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundLevelParticlesPacket implements Packet<ClientGamePacketListener> {
   private float x;
   private float y;
   private float z;
   private float xDist;
   private float yDist;
   private float zDist;
   private float maxSpeed;
   private int count;
   private boolean overrideLimiter;
   private ParticleOptions particle;

   public ClientboundLevelParticlesPacket() {
      super();
   }

   public <T extends ParticleOptions> ClientboundLevelParticlesPacket(T var1, boolean var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10) {
      super();
      this.particle = var1;
      this.overrideLimiter = var2;
      this.x = var3;
      this.y = var4;
      this.z = var5;
      this.xDist = var6;
      this.yDist = var7;
      this.zDist = var8;
      this.maxSpeed = var9;
      this.count = var10;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      Object var2 = (ParticleType)Registry.PARTICLE_TYPE.byId(var1.readInt());
      if (var2 == null) {
         var2 = ParticleTypes.BARRIER;
      }

      this.overrideLimiter = var1.readBoolean();
      this.x = var1.readFloat();
      this.y = var1.readFloat();
      this.z = var1.readFloat();
      this.xDist = var1.readFloat();
      this.yDist = var1.readFloat();
      this.zDist = var1.readFloat();
      this.maxSpeed = var1.readFloat();
      this.count = var1.readInt();
      this.particle = this.readParticle(var1, (ParticleType)var2);
   }

   private <T extends ParticleOptions> T readParticle(FriendlyByteBuf var1, ParticleType<T> var2) {
      return var2.getDeserializer().fromNetwork(var2, var1);
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeInt(Registry.PARTICLE_TYPE.getId(this.particle.getType()));
      var1.writeBoolean(this.overrideLimiter);
      var1.writeFloat(this.x);
      var1.writeFloat(this.y);
      var1.writeFloat(this.z);
      var1.writeFloat(this.xDist);
      var1.writeFloat(this.yDist);
      var1.writeFloat(this.zDist);
      var1.writeFloat(this.maxSpeed);
      var1.writeInt(this.count);
      this.particle.writeToNetwork(var1);
   }

   public boolean isOverrideLimiter() {
      return this.overrideLimiter;
   }

   public double getX() {
      return (double)this.x;
   }

   public double getY() {
      return (double)this.y;
   }

   public double getZ() {
      return (double)this.z;
   }

   public float getXDist() {
      return this.xDist;
   }

   public float getYDist() {
      return this.yDist;
   }

   public float getZDist() {
      return this.zDist;
   }

   public float getMaxSpeed() {
      return this.maxSpeed;
   }

   public int getCount() {
      return this.count;
   }

   public ParticleOptions getParticle() {
      return this.particle;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleParticleEvent(this);
   }
}
