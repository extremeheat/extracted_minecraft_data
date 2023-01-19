package net.minecraft.network.protocol.game;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class ClientboundSoundPacket implements Packet<ClientGamePacketListener> {
   public static final float LOCATION_ACCURACY = 8.0F;
   private final Holder<SoundEvent> sound;
   private final SoundSource source;
   private final int x;
   private final int y;
   private final int z;
   private final float volume;
   private final float pitch;
   private final long seed;

   public ClientboundSoundPacket(Holder<SoundEvent> var1, SoundSource var2, double var3, double var5, double var7, float var9, float var10, long var11) {
      super();
      this.sound = var1;
      this.source = var2;
      this.x = (int)(var3 * 8.0);
      this.y = (int)(var5 * 8.0);
      this.z = (int)(var7 * 8.0);
      this.volume = var9;
      this.pitch = var10;
      this.seed = var11;
   }

   public ClientboundSoundPacket(FriendlyByteBuf var1) {
      super();
      this.sound = var1.readById(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), SoundEvent::readFromNetwork);
      this.source = var1.readEnum(SoundSource.class);
      this.x = var1.readInt();
      this.y = var1.readInt();
      this.z = var1.readInt();
      this.volume = var1.readFloat();
      this.pitch = var1.readFloat();
      this.seed = var1.readLong();
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeId(BuiltInRegistries.SOUND_EVENT.asHolderIdMap(), this.sound, (var0, var1x) -> var1x.writeToNetwork(var0));
      var1.writeEnum(this.source);
      var1.writeInt(this.x);
      var1.writeInt(this.y);
      var1.writeInt(this.z);
      var1.writeFloat(this.volume);
      var1.writeFloat(this.pitch);
      var1.writeLong(this.seed);
   }

   public Holder<SoundEvent> getSound() {
      return this.sound;
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
      var1.handleSoundEvent(this);
   }
}
