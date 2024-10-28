package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record ClientboundDamageEventPacket(int entityId, Holder<DamageType> sourceType, int sourceCauseId, int sourceDirectId, Optional<Vec3> sourcePosition) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDamageEventPacket> STREAM_CODEC = Packet.codec(ClientboundDamageEventPacket::write, ClientboundDamageEventPacket::new);
   private static final StreamCodec<RegistryFriendlyByteBuf, Holder<DamageType>> DAMAGE_TYPE_ID_STREAM_CODEC;

   public ClientboundDamageEventPacket(Entity var1, DamageSource var2) {
      this(var1.getId(), var2.typeHolder(), var2.getEntity() != null ? var2.getEntity().getId() : -1, var2.getDirectEntity() != null ? var2.getDirectEntity().getId() : -1, Optional.ofNullable(var2.sourcePositionRaw()));
   }

   private ClientboundDamageEventPacket(RegistryFriendlyByteBuf var1) {
      this(var1.readVarInt(), (Holder)DAMAGE_TYPE_ID_STREAM_CODEC.decode(var1), readOptionalEntityId(var1), readOptionalEntityId(var1), var1.readOptional((var0) -> {
         return new Vec3(var0.readDouble(), var0.readDouble(), var0.readDouble());
      }));
   }

   public ClientboundDamageEventPacket(int var1, Holder<DamageType> var2, int var3, int var4, Optional<Vec3> var5) {
      super();
      this.entityId = var1;
      this.sourceType = var2;
      this.sourceCauseId = var3;
      this.sourceDirectId = var4;
      this.sourcePosition = var5;
   }

   private static void writeOptionalEntityId(FriendlyByteBuf var0, int var1) {
      var0.writeVarInt(var1 + 1);
   }

   private static int readOptionalEntityId(FriendlyByteBuf var0) {
      return var0.readVarInt() - 1;
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeVarInt(this.entityId);
      DAMAGE_TYPE_ID_STREAM_CODEC.encode(var1, this.sourceType);
      writeOptionalEntityId(var1, this.sourceCauseId);
      writeOptionalEntityId(var1, this.sourceDirectId);
      var1.writeOptional(this.sourcePosition, (var0, var1x) -> {
         var0.writeDouble(var1x.x());
         var0.writeDouble(var1x.y());
         var0.writeDouble(var1x.z());
      });
   }

   public PacketType<ClientboundDamageEventPacket> type() {
      return GamePacketTypes.CLIENTBOUND_DAMAGE_EVENT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleDamageEvent(this);
   }

   public DamageSource getSource(Level var1) {
      if (this.sourcePosition.isPresent()) {
         return new DamageSource(this.sourceType, (Vec3)this.sourcePosition.get());
      } else {
         Entity var2 = var1.getEntity(this.sourceCauseId);
         Entity var3 = var1.getEntity(this.sourceDirectId);
         return new DamageSource(this.sourceType, var3, var2);
      }
   }

   public int entityId() {
      return this.entityId;
   }

   public Holder<DamageType> sourceType() {
      return this.sourceType;
   }

   public int sourceCauseId() {
      return this.sourceCauseId;
   }

   public int sourceDirectId() {
      return this.sourceDirectId;
   }

   public Optional<Vec3> sourcePosition() {
      return this.sourcePosition;
   }

   static {
      DAMAGE_TYPE_ID_STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.DAMAGE_TYPE);
   }
}
