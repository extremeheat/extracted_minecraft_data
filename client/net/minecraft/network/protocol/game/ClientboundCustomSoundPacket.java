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
   // $FF: renamed from: x int
   private final int field_398;
   // $FF: renamed from: y int
   private final int field_399;
   // $FF: renamed from: z int
   private final int field_400;
   private final float volume;
   private final float pitch;

   public ClientboundCustomSoundPacket(ResourceLocation var1, SoundSource var2, Vec3 var3, float var4, float var5) {
      super();
      this.name = var1;
      this.source = var2;
      this.field_398 = (int)(var3.field_414 * 8.0D);
      this.field_399 = (int)(var3.field_415 * 8.0D);
      this.field_400 = (int)(var3.field_416 * 8.0D);
      this.volume = var4;
      this.pitch = var5;
   }

   public ClientboundCustomSoundPacket(FriendlyByteBuf var1) {
      super();
      this.name = var1.readResourceLocation();
      this.source = (SoundSource)var1.readEnum(SoundSource.class);
      this.field_398 = var1.readInt();
      this.field_399 = var1.readInt();
      this.field_400 = var1.readInt();
      this.volume = var1.readFloat();
      this.pitch = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.name);
      var1.writeEnum(this.source);
      var1.writeInt(this.field_398);
      var1.writeInt(this.field_399);
      var1.writeInt(this.field_400);
      var1.writeFloat(this.volume);
      var1.writeFloat(this.pitch);
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public SoundSource getSource() {
      return this.source;
   }

   public double getX() {
      return (double)((float)this.field_398 / 8.0F);
   }

   public double getY() {
      return (double)((float)this.field_399 / 8.0F);
   }

   public double getZ() {
      return (double)((float)this.field_400 / 8.0F);
   }

   public float getVolume() {
      return this.volume;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleCustomSoundEvent(this);
   }
}
