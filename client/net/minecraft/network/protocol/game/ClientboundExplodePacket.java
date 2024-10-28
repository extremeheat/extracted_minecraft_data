package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class ClientboundExplodePacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundExplodePacket> STREAM_CODEC = Packet.codec(ClientboundExplodePacket::write, ClientboundExplodePacket::new);
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
   private final Holder<SoundEvent> explosionSound;

   public ClientboundExplodePacket(double var1, double var3, double var5, float var7, List<BlockPos> var8, @Nullable Vec3 var9, Explosion.BlockInteraction var10, ParticleOptions var11, ParticleOptions var12, Holder<SoundEvent> var13) {
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

   private ClientboundExplodePacket(RegistryFriendlyByteBuf var1) {
      super();
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.power = var1.readFloat();
      int var2 = Mth.floor(this.x);
      int var3 = Mth.floor(this.y);
      int var4 = Mth.floor(this.z);
      this.toBlow = var1.readList((var3x) -> {
         int var4x = var3x.readByte() + var2;
         int var5 = var3x.readByte() + var3;
         int var6 = var3x.readByte() + var4;
         return new BlockPos(var4x, var5, var6);
      });
      this.knockbackX = var1.readFloat();
      this.knockbackY = var1.readFloat();
      this.knockbackZ = var1.readFloat();
      this.blockInteraction = (Explosion.BlockInteraction)var1.readEnum(Explosion.BlockInteraction.class);
      this.smallExplosionParticles = (ParticleOptions)ParticleTypes.STREAM_CODEC.decode(var1);
      this.largeExplosionParticles = (ParticleOptions)ParticleTypes.STREAM_CODEC.decode(var1);
      this.explosionSound = (Holder)SoundEvent.STREAM_CODEC.decode(var1);
   }

   private void write(RegistryFriendlyByteBuf var1) {
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
      ParticleTypes.STREAM_CODEC.encode(var1, this.smallExplosionParticles);
      ParticleTypes.STREAM_CODEC.encode(var1, this.largeExplosionParticles);
      SoundEvent.STREAM_CODEC.encode(var1, this.explosionSound);
   }

   public PacketType<ClientboundExplodePacket> type() {
      return GamePacketTypes.CLIENTBOUND_EXPLODE;
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

   public Holder<SoundEvent> getExplosionSound() {
      return this.explosionSound;
   }
}
