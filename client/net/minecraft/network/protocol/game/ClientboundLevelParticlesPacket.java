package net.minecraft.network.protocol.game;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundLevelParticlesPacket implements Packet<ClientGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final float xDist;
   private final float yDist;
   private final float zDist;
   private final float maxSpeed;
   private final int count;
   private final boolean overrideLimiter;
   private final ParticleOptions particle;

   public <T extends ParticleOptions> ClientboundLevelParticlesPacket(
      T var1, boolean var2, double var3, double var5, double var7, float var9, float var10, float var11, float var12, int var13
   ) {
      super();
      this.particle = var1;
      this.overrideLimiter = var2;
      this.x = var3;
      this.y = var5;
      this.z = var7;
      this.xDist = var9;
      this.yDist = var10;
      this.zDist = var11;
      this.maxSpeed = var12;
      this.count = var13;
   }

   public ClientboundLevelParticlesPacket(FriendlyByteBuf var1) {
      super();
      ParticleType var2 = var1.readById(BuiltInRegistries.PARTICLE_TYPE);
      this.overrideLimiter = var1.readBoolean();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.xDist = var1.readFloat();
      this.yDist = var1.readFloat();
      this.zDist = var1.readFloat();
      this.maxSpeed = var1.readFloat();
      this.count = var1.readInt();
      this.particle = this.readParticle(var1, var2);
   }

   private <T extends ParticleOptions> T readParticle(FriendlyByteBuf var1, ParticleType<T> var2) {
      return (T)var2.getDeserializer().fromNetwork(var2, var1);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeId(BuiltInRegistries.PARTICLE_TYPE, this.particle.getType());
      var1.writeBoolean(this.overrideLimiter);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
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
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
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
