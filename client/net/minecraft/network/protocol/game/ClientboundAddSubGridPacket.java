package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.grid.GridCarrier;
import net.minecraft.world.grid.SubGridBlocks;
import net.minecraft.world.level.biome.Biome;

public record ClientboundAddSubGridPacket(int b, UUID c, double d, double e, double f, SubGridBlocks g, Holder<Biome> h)
   implements Packet<ClientGamePacketListener> {
   private final int id;
   private final UUID uuid;
   private final double x;
   private final double y;
   private final double z;
   private final SubGridBlocks blocks;
   private final Holder<Biome> biome;
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAddSubGridPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      ClientboundAddSubGridPacket::id,
      UUIDUtil.STREAM_CODEC,
      ClientboundAddSubGridPacket::uuid,
      ByteBufCodecs.DOUBLE,
      ClientboundAddSubGridPacket::x,
      ByteBufCodecs.DOUBLE,
      ClientboundAddSubGridPacket::y,
      ByteBufCodecs.DOUBLE,
      ClientboundAddSubGridPacket::z,
      SubGridBlocks.STREAM_CODEC,
      ClientboundAddSubGridPacket::blocks,
      ByteBufCodecs.holderRegistry(Registries.BIOME),
      ClientboundAddSubGridPacket::biome,
      ClientboundAddSubGridPacket::new
   );

   public ClientboundAddSubGridPacket(GridCarrier var1) {
      this(var1.getId(), var1.getUUID(), var1.getX(), var1.getY(), var1.getZ(), var1.grid().getBlocks().copy(), var1.grid().getBiome());
   }

   public ClientboundAddSubGridPacket(int var1, UUID var2, double var3, double var5, double var7, SubGridBlocks var9, Holder<Biome> var10) {
      super();
      this.id = var1;
      this.uuid = var2;
      this.x = var3;
      this.y = var5;
      this.z = var7;
      this.blocks = var9;
      this.biome = var10;
   }

   @Override
   public PacketType<ClientboundAddSubGridPacket> type() {
      return GamePacketTypes.CLIENTBOUND_ADD_SUB_GRID;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleAddSubGrid(this);
   }
}
