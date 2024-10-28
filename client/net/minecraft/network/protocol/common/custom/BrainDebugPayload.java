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

public record BrainDebugPayload(BrainDump brainDump) implements CustomPacketPayload {
   public static final StreamCodec<FriendlyByteBuf, BrainDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(BrainDebugPayload::write, BrainDebugPayload::new);
   public static final CustomPacketPayload.Type<BrainDebugPayload> TYPE = CustomPacketPayload.createType("debug/brain");

   private BrainDebugPayload(FriendlyByteBuf var1) {
      this(new BrainDump(var1));
   }

   public BrainDebugPayload(BrainDump brainDump) {
      super();
      this.brainDump = brainDump;
   }

   private void write(FriendlyByteBuf var1) {
      this.brainDump.write(var1);
   }

   public CustomPacketPayload.Type<BrainDebugPayload> type() {
      return TYPE;
   }

   public BrainDump brainDump() {
      return this.brainDump;
   }

   public static record BrainDump(UUID uuid, int id, String name, String profession, int xp, float health, float maxHealth, Vec3 pos, String inventory, @Nullable Path path, boolean wantsGolem, int angerLevel, List<String> activities, List<String> behaviors, List<String> memories, List<String> gossips, Set<BlockPos> pois, Set<BlockPos> potentialPois) {
      public BrainDump(FriendlyByteBuf var1) {
         this(var1.readUUID(), var1.readInt(), var1.readUtf(), var1.readUtf(), var1.readInt(), var1.readFloat(), var1.readFloat(), var1.readVec3(), var1.readUtf(), (Path)var1.readNullable(Path::createFromStream), var1.readBoolean(), var1.readInt(), var1.readList(FriendlyByteBuf::readUtf), var1.readList(FriendlyByteBuf::readUtf), var1.readList(FriendlyByteBuf::readUtf), var1.readList(FriendlyByteBuf::readUtf), (Set)var1.readCollection(HashSet::new, BlockPos.STREAM_CODEC), (Set)var1.readCollection(HashSet::new, BlockPos.STREAM_CODEC));
      }

      public BrainDump(UUID uuid, int id, String name, String profession, int xp, float health, float maxHealth, Vec3 pos, String inventory, @Nullable Path path, boolean wantsGolem, int angerLevel, List<String> activities, List<String> behaviors, List<String> memories, List<String> gossips, Set<BlockPos> pois, Set<BlockPos> potentialPois) {
         super();
         this.uuid = uuid;
         this.id = id;
         this.name = name;
         this.profession = profession;
         this.xp = xp;
         this.health = health;
         this.maxHealth = maxHealth;
         this.pos = pos;
         this.inventory = inventory;
         this.path = path;
         this.wantsGolem = wantsGolem;
         this.angerLevel = angerLevel;
         this.activities = activities;
         this.behaviors = behaviors;
         this.memories = memories;
         this.gossips = gossips;
         this.pois = pois;
         this.potentialPois = potentialPois;
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
         var1.writeNullable(this.path, (var0, var1x) -> {
            var1x.writeToStream(var0);
         });
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

      public UUID uuid() {
         return this.uuid;
      }

      public int id() {
         return this.id;
      }

      public String name() {
         return this.name;
      }

      public String profession() {
         return this.profession;
      }

      public int xp() {
         return this.xp;
      }

      public float health() {
         return this.health;
      }

      public float maxHealth() {
         return this.maxHealth;
      }

      public Vec3 pos() {
         return this.pos;
      }

      public String inventory() {
         return this.inventory;
      }

      @Nullable
      public Path path() {
         return this.path;
      }

      public boolean wantsGolem() {
         return this.wantsGolem;
      }

      public int angerLevel() {
         return this.angerLevel;
      }

      public List<String> activities() {
         return this.activities;
      }

      public List<String> behaviors() {
         return this.behaviors;
      }

      public List<String> memories() {
         return this.memories;
      }

      public List<String> gossips() {
         return this.gossips;
      }

      public Set<BlockPos> pois() {
         return this.pois;
      }

      public Set<BlockPos> potentialPois() {
         return this.potentialPois;
      }
   }
}
