package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public record BeeDebugPayload(BeeInfo beeInfo) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, BeeDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(BeeDebugPayload::write, BeeDebugPayload::new);
   public static final CustomPacketPayload.Type<BeeDebugPayload> TYPE = CustomPacketPayload.createType("debug/bee");

   private BeeDebugPayload(FriendlyByteBuf var1) {
      this(new BeeInfo(var1));
   }

   public BeeDebugPayload(BeeInfo beeInfo) {
      super();
      this.beeInfo = beeInfo;
   }

   private void write(FriendlyByteBuf var1) {
      this.beeInfo.write(var1);
   }

   public CustomPacketPayload.Type<BeeDebugPayload> type() {
      return TYPE;
   }

   public BeeInfo beeInfo() {
      return this.beeInfo;
   }

   public static record BeeInfo(UUID uuid, int id, Vec3 pos, @Nullable Path path, @Nullable BlockPos hivePos, @Nullable BlockPos flowerPos, int travelTicks, Set<String> goals, List<BlockPos> blacklistedHives) {
      public BeeInfo(FriendlyByteBuf var1) {
         this(var1.readUUID(), var1.readInt(), var1.readVec3(), (Path)var1.readNullable(Path::createFromStream), (BlockPos)var1.readNullable(BlockPos.STREAM_CODEC), (BlockPos)var1.readNullable(BlockPos.STREAM_CODEC), var1.readInt(), (Set)var1.readCollection(HashSet::new, FriendlyByteBuf::readUtf), var1.readList(BlockPos.STREAM_CODEC));
      }

      public BeeInfo(UUID uuid, int id, Vec3 pos, @Nullable Path path, @Nullable BlockPos hivePos, @Nullable BlockPos flowerPos, int travelTicks, Set<String> goals, List<BlockPos> blacklistedHives) {
         super();
         this.uuid = uuid;
         this.id = id;
         this.pos = pos;
         this.path = path;
         this.hivePos = hivePos;
         this.flowerPos = flowerPos;
         this.travelTicks = travelTicks;
         this.goals = goals;
         this.blacklistedHives = blacklistedHives;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeUUID(this.uuid);
         var1.writeInt(this.id);
         var1.writeVec3(this.pos);
         var1.writeNullable(this.path, (var0, var1x) -> {
            var1x.writeToStream(var0);
         });
         var1.writeNullable(this.hivePos, BlockPos.STREAM_CODEC);
         var1.writeNullable(this.flowerPos, BlockPos.STREAM_CODEC);
         var1.writeInt(this.travelTicks);
         var1.writeCollection(this.goals, FriendlyByteBuf::writeUtf);
         var1.writeCollection(this.blacklistedHives, BlockPos.STREAM_CODEC);
      }

      public boolean hasHive(BlockPos var1) {
         return Objects.equals(var1, this.hivePos);
      }

      public String generateName() {
         return DebugEntityNameGenerator.getEntityName(this.uuid);
      }

      public String toString() {
         return this.generateName();
      }

      public UUID uuid() {
         return this.uuid;
      }

      public int id() {
         return this.id;
      }

      public Vec3 pos() {
         return this.pos;
      }

      @Nullable
      public Path path() {
         return this.path;
      }

      @Nullable
      public BlockPos hivePos() {
         return this.hivePos;
      }

      @Nullable
      public BlockPos flowerPos() {
         return this.flowerPos;
      }

      public int travelTicks() {
         return this.travelTicks;
      }

      public Set<String> goals() {
         return this.goals;
      }

      public List<BlockPos> blacklistedHives() {
         return this.blacklistedHives;
      }
   }
}
