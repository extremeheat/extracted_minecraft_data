package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public record BrainDebugPayload(BrainDebugPayload.BrainDump c) implements CustomPacketPayload {
   private final BrainDebugPayload.BrainDump brainDump;
   public static final StreamCodec<FriendlyByteBuf, BrainDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
      BrainDebugPayload::write, BrainDebugPayload::new
   );
   public static final CustomPacketPayload.Type<BrainDebugPayload> TYPE = CustomPacketPayload.createType("debug/brain");

   private BrainDebugPayload(FriendlyByteBuf var1) {
      this(new BrainDebugPayload.BrainDump(var1));
   }

   public BrainDebugPayload(BrainDebugPayload.BrainDump var1) {
      super();
      this.brainDump = var1;
   }

   private void write(FriendlyByteBuf var1) {
      this.brainDump.write(var1);
   }

   @Override
   public CustomPacketPayload.Type<BrainDebugPayload> type() {
      return TYPE;
   }

   public static record BrainDump(
      UUID a,
      int b,
      String c,
      String d,
      int e,
      float f,
      float g,
      Vec3 h,
      String i,
      @Nullable Path j,
      boolean k,
      int l,
      List<String> m,
      List<String> n,
      List<String> o,
      List<String> p,
      Set<BlockPos> q,
      Set<BlockPos> r
   ) {
      private final UUID uuid;
      private final int id;
      private final String name;
      private final String profession;
      private final int xp;
      private final float health;
      private final float maxHealth;
      private final Vec3 pos;
      private final String inventory;
      @Nullable
      private final Path path;
      private final boolean wantsGolem;
      private final int angerLevel;
      private final List<String> activities;
      private final List<String> behaviors;
      private final List<String> memories;
      private final List<String> gossips;
      private final Set<BlockPos> pois;
      private final Set<BlockPos> potentialPois;

      public BrainDump(FriendlyByteBuf var1) {
         this(
            var1.readUUID(),
            var1.readInt(),
            var1.readUtf(),
            var1.readUtf(),
            var1.readInt(),
            var1.readFloat(),
            var1.readFloat(),
            var1.readVec3(),
            var1.readUtf(),
            var1.readNullable(Path::createFromStream),
            var1.readBoolean(),
            var1.readInt(),
            var1.readList(FriendlyByteBuf::readUtf),
            var1.readList(FriendlyByteBuf::readUtf),
            var1.readList(FriendlyByteBuf::readUtf),
            var1.readList(FriendlyByteBuf::readUtf),
            var1.readCollection(HashSet::new, BlockPos.STREAM_CODEC),
            var1.readCollection(HashSet::new, BlockPos.STREAM_CODEC)
         );
      }

      public BrainDump(
         UUID var1,
         int var2,
         String var3,
         String var4,
         int var5,
         float var6,
         float var7,
         Vec3 var8,
         String var9,
         @Nullable Path var10,
         boolean var11,
         int var12,
         List<String> var13,
         List<String> var14,
         List<String> var15,
         List<String> var16,
         Set<BlockPos> var17,
         Set<BlockPos> var18
      ) {
         super();
         this.uuid = var1;
         this.id = var2;
         this.name = var3;
         this.profession = var4;
         this.xp = var5;
         this.health = var6;
         this.maxHealth = var7;
         this.pos = var8;
         this.inventory = var9;
         this.path = var10;
         this.wantsGolem = var11;
         this.angerLevel = var12;
         this.activities = var13;
         this.behaviors = var14;
         this.memories = var15;
         this.gossips = var16;
         this.pois = var17;
         this.potentialPois = var18;
      }

      public void write(FriendlyByteBuf var1) {
         var1.writeUUID(this.uuid);
         var1.writeInt(this.id);
         var1.writeUtf(this.name);
         var1.writeUtf(this.profession);
         var1.writeInt(this.xp);
         var1.writeFloat(this.health);
         var1.writeFloat(this.maxHealth);
         var1.writeVec3(this.pos);
         var1.writeUtf(this.inventory);
         var1.writeNullable(this.path, (var0, var1x) -> var1x.writeToStream(var0));
         var1.writeBoolean(this.wantsGolem);
         var1.writeInt(this.angerLevel);
         var1.writeCollection(this.activities, FriendlyByteBuf::writeUtf);
         var1.writeCollection(this.behaviors, FriendlyByteBuf::writeUtf);
         var1.writeCollection(this.memories, FriendlyByteBuf::writeUtf);
         var1.writeCollection(this.gossips, FriendlyByteBuf::writeUtf);
         var1.writeCollection(this.pois, BlockPos.STREAM_CODEC);
         var1.writeCollection(this.potentialPois, BlockPos.STREAM_CODEC);
      }

      public boolean hasPoi(BlockPos var1) {
         return this.pois.contains(var1);
      }

      public boolean hasPotentialPoi(BlockPos var1) {
         return this.potentialPois.contains(var1);
      }
   }
}
