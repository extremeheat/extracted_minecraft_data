package net.minecraft.network.protocol.game;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundLevelParticlesPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelParticlesPacket> STREAM_CODEC = Packet.codec(ClientboundLevelParticlesPacket::write, ClientboundLevelParticlesPacket::new);
   private final double x;
   private final double y;
   private final double z;
   private final float xDist;
   private final float yDist;
   private final float zDist;
   private final float maxSpeed;
   private final int count;
   private final boolean overrideLimiter;
   private final boolean alwaysShow;
   private final ParticleOptions particle;

   public <T extends ParticleOptions> ClientboundLevelParticlesPacket(T var1, boolean var2, boolean var3, double var4, double var6, double var8, float var10, float var11, float var12, float var13, int var14) {
      super();
      this.particle = var1;
      this.overrideLimiter = var2;
      this.alwaysShow = var3;
      this.x = var4;
      this.y = var6;
      this.z = var8;
      this.xDist = var10;
      this.yDist = var11;
      this.zDist = var12;
      this.maxSpeed = var13;
      this.count = var14;
   }

   private ClientboundLevelParticlesPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.overrideLimiter = var1.readBoolean();
      this.alwaysShow = var1.readBoolean();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.xDist = var1.readFloat();
      this.yDist = var1.readFloat();
      this.zDist = var1.readFloat();
      this.maxSpeed = var1.readFloat();
      this.count = var1.readInt();
      this.particle = (ParticleOptions)ParticleTypes.STREAM_CODEC.decode(var1);
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeBoolean(this.overrideLimiter);
      var1.writeBoolean(this.alwaysShow);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeFloat(this.xDist);
      var1.writeFloat(this.yDist);
      var1.writeFloat(this.zDist);
      var1.writeFloat(this.maxSpeed);
      var1.writeInt(this.count);
      ParticleTypes.STREAM_CODEC.encode(var1, this.particle);
   }

   public PacketType<ClientboundLevelParticlesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_LEVEL_PARTICLES;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleParticleEvent(this);
   }

   public boolean isOverrideLimiter() {
      return this.overrideLimiter;
   }

   public boolean alwaysShow() {
      return this.alwaysShow;
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
}
