package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.Validate;

public class ClientboundSoundEntityPacket implements Packet {
   private SoundEvent sound;
   private SoundSource source;
   private int id;
   private float volume;
   private float pitch;

   public ClientboundSoundEntityPacket() {
   }

   public ClientboundSoundEntityPacket(SoundEvent var1, SoundSource var2, Entity var3, float var4, float var5) {
      Validate.notNull(var1, "sound", new Object[0]);
      this.sound = var1;
      this.source = var2;
      this.id = var3.getId();
      this.volume = var4;
      this.pitch = var5;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.sound = (SoundEvent)Registry.SOUND_EVENT.byId(var1.readVarInt());
      this.source = (SoundSource)var1.readEnum(SoundSource.class);
      this.id = var1.readVarInt();
      this.volume = var1.readFloat();
      this.pitch = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(Registry.SOUND_EVENT.getId(this.sound));
      var1.writeEnum(this.source);
      var1.writeVarInt(this.id);
      var1.writeFloat(this.volume);
      var1.writeFloat(this.pitch);
   }

   public SoundEvent getSound() {
      return this.sound;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public int getId() {
      return this.id;
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleSoundEntityEvent(this);
   }
}
