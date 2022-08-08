package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public class ClientboundCustomSoundPacket implements Packet<ClientGamePacketListener> {
   public static final float LOCATION_ACCURACY = 8.0F;
   private final ResourceLocation name;
   private final SoundSource source;
   private final int x;
   private final int y;
   private final int z;
   private final float volume;
   private final float pitch;
   private final long seed;

   public ClientboundCustomSoundPacket(ResourceLocation var1, SoundSource var2, Vec3 var3, float var4, float var5, long var6) {
      super();
      this.name = var1;
      this.source = var2;
      this.x = (int)(var3.x * 8.0);
      this.y = (int)(var3.y * 8.0);
      this.z = (int)(var3.z * 8.0);
      this.volume = var4;
      this.pitch = var5;
      this.seed = var6;
   }

   public ClientboundCustomSoundPacket(FriendlyByteBuf var1) {
      super();
      this.name = var1.readResourceLocation();
      this.source = (SoundSource)var1.readEnum(SoundSource.class);
      this.x = var1.readInt();
      this.y = var1.readInt();
      this.z = var1.readInt();
      this.volume = var1.readFloat();
      this.pitch = var1.readFloat();
      this.seed = var1.readLong();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.name);
      var1.writeEnum(this.source);
      var1.writeInt(this.x);
      var1.writeInt(this.y);
      var1.writeInt(this.z);
      var1.writeFloat(this.volume);
      var1.writeFloat(this.pitch);
      var1.writeLong(this.seed);
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public double getX() {
      return (double)((float)this.x / 8.0F);
   }

   public double getY() {
      return (double)((float)this.y / 8.0F);
   }

   public double getZ() {
      return (double)((float)this.z / 8.0F);
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public long getSeed() {
      return this.seed;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCustomSoundEvent(this);
   }
}
