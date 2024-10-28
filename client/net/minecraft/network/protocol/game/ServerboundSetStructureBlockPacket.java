package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;

public class ServerboundSetStructureBlockPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundSetStructureBlockPacket> STREAM_CODEC = Packet.codec(ServerboundSetStructureBlockPacket::write, ServerboundSetStructureBlockPacket::new);
   private static final int FLAG_IGNORE_ENTITIES = 1;
   private static final int FLAG_SHOW_AIR = 2;
   private static final int FLAG_SHOW_BOUNDING_BOX = 4;
   private final BlockPos pos;
   private final StructureBlockEntity.UpdateType updateType;
   private final StructureMode mode;
   private final String name;
   private final BlockPos offset;
   private final Vec3i size;
   private final Mirror mirror;
   private final Rotation rotation;
   private final String data;
   private final boolean ignoreEntities;
   private final boolean showAir;
   private final boolean showBoundingBox;
   private final float integrity;
   private final long seed;

   public ServerboundSetStructureBlockPacket(BlockPos var1, StructureBlockEntity.UpdateType var2, StructureMode var3, String var4, BlockPos var5, Vec3i var6, Mirror var7, Rotation var8, String var9, boolean var10, boolean var11, boolean var12, float var13, long var14) {
      super();
      this.pos = var1;
      this.updateType = var2;
      this.mode = var3;
      this.name = var4;
      this.offset = var5;
      this.size = var6;
      this.mirror = var7;
      this.rotation = var8;
      this.data = var9;
      this.ignoreEntities = var10;
      this.showAir = var11;
      this.showBoundingBox = var12;
      this.integrity = var13;
      this.seed = var14;
   }

   private ServerboundSetStructureBlockPacket(FriendlyByteBuf var1) {
      super();
      this.pos = var1.readBlockPos();
      this.updateType = (StructureBlockEntity.UpdateType)var1.readEnum(StructureBlockEntity.UpdateType.class);
      this.mode = (StructureMode)var1.readEnum(StructureMode.class);
      this.name = var1.readUtf();
      boolean var2 = true;
      this.offset = new BlockPos(Mth.clamp(var1.readByte(), -48, 48), Mth.clamp(var1.readByte(), -48, 48), Mth.clamp(var1.readByte(), -48, 48));
      boolean var3 = true;
      this.size = new Vec3i(Mth.clamp(var1.readByte(), 0, 48), Mth.clamp(var1.readByte(), 0, 48), Mth.clamp(var1.readByte(), 0, 48));
      this.mirror = (Mirror)var1.readEnum(Mirror.class);
      this.rotation = (Rotation)var1.readEnum(Rotation.class);
      this.data = var1.readUtf(128);
      this.integrity = Mth.clamp(var1.readFloat(), 0.0F, 1.0F);
      this.seed = var1.readVarLong();
      byte var4 = var1.readByte();
      this.ignoreEntities = (var4 & 1) != 0;
      this.showAir = (var4 & 2) != 0;
      this.showBoundingBox = (var4 & 4) != 0;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeBlockPos(this.pos);
      var1.writeEnum(this.updateType);
      var1.writeEnum(this.mode);
      var1.writeUtf(this.name);
      var1.writeByte(this.offset.getX());
      var1.writeByte(this.offset.getY());
      var1.writeByte(this.offset.getZ());
      var1.writeByte(this.size.getX());
      var1.writeByte(this.size.getY());
      var1.writeByte(this.size.getZ());
      var1.writeEnum(this.mirror);
      var1.writeEnum(this.rotation);
      var1.writeUtf(this.data);
      var1.writeFloat(this.integrity);
      var1.writeVarLong(this.seed);
      int var2 = 0;
      if (this.ignoreEntities) {
         var2 |= 1;
      }

      if (this.showAir) {
         var2 |= 2;
      }

      if (this.showBoundingBox) {
         var2 |= 4;
      }

      var1.writeByte(var2);
   }

   public PacketType<ServerboundSetStructureBlockPacket> type() {
      return GamePacketTypes.SERVERBOUND_SET_STRUCTURE_BLOCK;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetStructureBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public StructureBlockEntity.UpdateType getUpdateType() {
      return this.updateType;
   }

   public StructureMode getMode() {
      return this.mode;
   }

   public String getName() {
      return this.name;
   }

   public BlockPos getOffset() {
      return this.offset;
   }

   public Vec3i getSize() {
      return this.size;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public String getData() {
      return this.data;
   }

   public boolean isIgnoreEntities() {
      return this.ignoreEntities;
   }

   public boolean isShowAir() {
      return this.showAir;
   }

   public boolean isShowBoundingBox() {
      return this.showBoundingBox;
   }

   public float getIntegrity() {
      return this.integrity;
   }

   public long getSeed() {
      return this.seed;
   }
}
