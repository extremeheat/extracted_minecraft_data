package net.minecraft.network.protocol.game;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import org.apache.commons.lang3.Validate;

public class ClientboundSoundPacket implements Packet<ClientGamePacketListener> {
   public static final float LOCATION_ACCURACY = 8.0F;
   private final SoundEvent sound;
   private final SoundSource source;
   // $FF: renamed from: x int
   private final int field_487;
   // $FF: renamed from: y int
   private final int field_488;
   // $FF: renamed from: z int
   private final int field_489;
   private final float volume;
   private final float pitch;

   public ClientboundSoundPacket(SoundEvent var1, SoundSource var2, double var3, double var5, double var7, float var9, float var10) {
      super();
      Validate.notNull(var1, "sound", new Object[0]);
      this.sound = var1;
      this.source = var2;
      this.field_487 = (int)(var3 * 8.0D);
      this.field_488 = (int)(var5 * 8.0D);
      this.field_489 = (int)(var7 * 8.0D);
      this.volume = var9;
      this.pitch = var10;
   }

   public ClientboundSoundPacket(FriendlyByteBuf var1) {
      super();
      this.sound = (SoundEvent)Registry.SOUND_EVENT.byId(var1.readVarInt());
      this.source = (SoundSource)var1.readEnum(SoundSource.class);
      this.field_487 = var1.readInt();
      this.field_488 = var1.readInt();
      this.field_489 = var1.readInt();
      this.volume = var1.readFloat();
      this.pitch = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
      var1.writeEnum(this.source);
      var1.writeInt(this.field_487);
      var1.writeInt(this.field_488);
      var1.writeInt(this.field_489);
      var1.writeFloat(this.volume);
      var1.writeFloat(this.pitch);
   }

   public SoundEvent getSound() {
      return this.sound;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public double getX() {
      return (double)((float)this.field_487 / 8.0F);
   }

   public double getY() {
      return (double)((float)this.field_488 / 8.0F);
   }

   public double getZ() {
      return (double)((float)this.field_489 / 8.0F);
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSoundEvent(this);
   }
}
