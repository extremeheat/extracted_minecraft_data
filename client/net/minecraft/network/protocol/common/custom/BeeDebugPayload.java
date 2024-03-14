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

public record BeeDebugPayload(BeeDebugPayload.BeeInfo c) implements CustomPacketPayload {
   private final BeeDebugPayload.BeeInfo beeInfo;
   public static final StreamCodec<FriendlyByteBuf, BeeDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(BeeDebugPayload::write, BeeDebugPayload::new);
   public static final CustomPacketPayload.Type<BeeDebugPayload> TYPE = CustomPacketPayload.createType("debug/bee");

   private BeeDebugPayload(FriendlyByteBuf var1) {
      this(new BeeDebugPayload.BeeInfo(var1));
   }

   public BeeDebugPayload(BeeDebugPayload.BeeInfo var1) {
      super();
      this.beeInfo = var1;
   }

   private void write(FriendlyByteBuf var1) {
      this.beeInfo.write(var1);
   }

   @Override
   public CustomPacketPayload.Type<BeeDebugPayload> type() {
      return TYPE;
   }

   public static record BeeInfo(UUID a, int b, Vec3 c, @Nullable Path d, @Nullable BlockPos e, @Nullable BlockPos f, int g, Set<String> h, List<BlockPos> i) {
      private final UUID uuid;
      private final int id;
      private final Vec3 pos;
      @Nullable
      private final Path path;
      @Nullable
      private final BlockPos hivePos;
      @Nullable
      private final BlockPos flowerPos;
      private final int travelTicks;
      private final Set<String> goals;
      private final List<BlockPos> blacklistedHives;

      public BeeInfo(FriendlyByteBuf var1) {
         this(
            var1.readUUID(),
            var1.readInt(),
            var1.readVec3(),
            var1.readNullable(Path::createFromStream),
            var1.readNullable(BlockPos.STREAM_CODEC),
            var1.readNullable(BlockPos.STREAM_CODEC),
            var1.readInt(),
            var1.readCollection(HashSet::new, FriendlyByteBuf::readUtf),
            var1.readList(BlockPos.STREAM_CODEC)
         );
      }

      public BeeInfo(
         UUID var1, int var2, Vec3 var3, @Nullable Path var4, @Nullable BlockPos var5, @Nullable BlockPos var6, int var7, Set<String> var8, List<BlockPos> var9
      ) {
         super();
         this.uuid = var1;
         this.id = var2;
         this.pos = var3;
         this.path = var4;
         this.hivePos = var5;
         this.flowerPos = var6;
         this.travelTicks = var7;
         this.goals = var8;
         this.blacklistedHives = var9;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeUUID(this.uuid);
         var1.writeInt(this.id);
         var1.writeVec3(this.pos);
         var1.writeNullable(this.path, (var0, var1x) -> var1x.writeToStream(var0));
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
   }
}
