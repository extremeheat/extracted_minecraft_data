package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class ClientboundExplodePacket implements Packet<ClientGamePacketListener> {
   private final double x;
   private final double y;
   private final double z;
   private final float power;
   private final List<BlockPos> toBlow;
   private final float knockbackX;
   private final float knockbackY;
   private final float knockbackZ;
   private final ParticleOptions smallExplosionParticles;
   private final ParticleOptions largeExplosionParticles;
   private final Explosion.BlockInteraction blockInteraction;
   private final SoundEvent explosionSound;

   public ClientboundExplodePacket(
      double var1,
      double var3,
      double var5,
      float var7,
      List<BlockPos> var8,
      @Nullable Vec3 var9,
      Explosion.BlockInteraction var10,
      ParticleOptions var11,
      ParticleOptions var12,
      SoundEvent var13
   ) {
      super();
      this.x = var1;
      this.y = var3;
      this.z = var5;
      this.power = var7;
      this.toBlow = Lists.newArrayList(var8);
      this.explosionSound = var13;
      if (var9 != null) {
         this.knockbackX = (float)var9.x;
         this.knockbackY = (float)var9.y;
         this.knockbackZ = (float)var9.z;
      } else {
         this.knockbackX = 0.0F;
         this.knockbackY = 0.0F;
         this.knockbackZ = 0.0F;
      }

      this.blockInteraction = var10;
      this.smallExplosionParticles = var11;
      this.largeExplosionParticles = var12;
   }

   public ClientboundExplodePacket(FriendlyByteBuf var1) {
      super();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.power = var1.readFloat();
      int var2 = Mth.floor(this.x);
      int var3 = Mth.floor(this.y);
      int var4 = Mth.floor(this.z);
      this.toBlow = var1.readList(var3x -> {
         int var4xx = var3x.readByte() + var2;
         int var5 = var3x.readByte() + var3;
         int var6 = var3x.readByte() + var4;
         return new BlockPos(var4xx, var5, var6);
      });
      this.knockbackX = var1.readFloat();
      this.knockbackY = var1.readFloat();
      this.knockbackZ = var1.readFloat();
      this.blockInteraction = var1.readEnum(Explosion.BlockInteraction.class);
      this.smallExplosionParticles = this.readParticle(var1, var1.readById(BuiltInRegistries.PARTICLE_TYPE));
      this.largeExplosionParticles = this.readParticle(var1, var1.readById(BuiltInRegistries.PARTICLE_TYPE));
      this.explosionSound = SoundEvent.readFromNetwork(var1);
   }

   public void writeParticle(FriendlyByteBuf var1, ParticleOptions var2) {
      var1.writeId(BuiltInRegistries.PARTICLE_TYPE, var2.getType());
      var2.writeToNetwork(var1);
   }

   private <T extends ParticleOptions> T readParticle(FriendlyByteBuf var1, ParticleType<T> var2) {
      return (T)var2.getDeserializer().fromNetwork(var2, var1);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeFloat(this.power);
      int var2 = Mth.floor(this.x);
      int var3 = Mth.floor(this.y);
      int var4 = Mth.floor(this.z);
      var1.writeCollection(this.toBlow, (var3x, var4x) -> {
         int var5 = var4x.getX() - var2;
         int var6 = var4x.getY() - var3;
         int var7 = var4x.getZ() - var4;
         var3x.writeByte(var5);
         var3x.writeByte(var6);
         var3x.writeByte(var7);
      });
      var1.writeFloat(this.knockbackX);
      var1.writeFloat(this.knockbackY);
      var1.writeFloat(this.knockbackZ);
      var1.writeEnum(this.blockInteraction);
      this.writeParticle(var1, this.smallExplosionParticles);
      this.writeParticle(var1, this.largeExplosionParticles);
      this.explosionSound.writeToNetwork(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleExplosion(this);
   }

   public float getKnockbackX() {
      return this.knockbackX;
   }

   public float getKnockbackY() {
      return this.knockbackY;
   }

   public float getKnockbackZ() {
      return this.knockbackZ;
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

   public float getPower() {
      return this.power;
   }

   public List<BlockPos> getToBlow() {
      return this.toBlow;
   }

   public Explosion.BlockInteraction getBlockInteraction() {
      return this.blockInteraction;
   }

   public ParticleOptions getSmallExplosionParticles() {
      return this.smallExplosionParticles;
   }

   public ParticleOptions getLargeExplosionParticles() {
      return this.largeExplosionParticles;
   }

   public SoundEvent getExplosionSound() {
      return this.explosionSound;
   }
}
