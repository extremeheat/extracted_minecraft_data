package net.minecraft.world.entity.raid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Raids extends SavedData {
   private static final String RAID_FILE_ID = "raids";
   public static final Codec<Raids> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Raids.RaidWithId.CODEC.listOf().optionalFieldOf("raids", List.of()).forGetter((var0x) -> var0x.raidMap.int2ObjectEntrySet().stream().map(RaidWithId::from).toList()), Codec.INT.fieldOf("next_id").forGetter((var0x) -> var0x.nextId), Codec.INT.fieldOf("tick").forGetter((var0x) -> var0x.tick)).apply(var0, Raids::new));
   public static final SavedDataType<Raids> TYPE;
   public static final SavedDataType<Raids> TYPE_END;
   private final Int2ObjectMap<Raid> raidMap = new Int2ObjectOpenHashMap();
   private int nextId = 1;
   private int tick;

   public static SavedDataType<Raids> getType(Holder<DimensionType> var0) {
      return var0.is(BuiltinDimensionTypes.END) ? TYPE_END : TYPE;
   }

   public Raids() {
      super();
      this.setDirty();
   }

   private Raids(List<RaidWithId> var1, int var2, int var3) {
      super();

      for(RaidWithId var5 : var1) {
         this.raidMap.put(var5.id, var5.raid);
      }

      this.nextId = var2;
      this.tick = var3;
   }

   public @Nullable Raid get(int var1) {
      return (Raid)this.raidMap.get(var1);
   }

   public OptionalInt getId(Raid var1) {
      ObjectIterator var2 = this.raidMap.int2ObjectEntrySet().iterator();

      while(var2.hasNext()) {
         Int2ObjectMap.Entry var3 = (Int2ObjectMap.Entry)var2.next();
         if (var3.getValue() == var1) {
            return OptionalInt.of(var3.getIntKey());
         }
      }

      return OptionalInt.empty();
   }

   public void tick(ServerLevel var1) {
      ++this.tick;
      ObjectIterator var2 = this.raidMap.values().iterator();

      while(var2.hasNext()) {
         Raid var3 = (Raid)var2.next();
         if (!(Boolean)var1.getGameRules().get(GameRules.RAIDS)) {
            var3.stop();
         }

         if (var3.isStopped()) {
            var2.remove();
            this.setDirty();
         } else {
            var3.tick(var1);
         }
      }

      if (this.tick % 200 == 0) {
         this.setDirty();
      }

   }

   public static boolean canJoinRaid(Raider var0) {
      return var0.isAlive() && var0.canJoinRaid() && var0.getNoActionTime() <= 2400;
   }

   public @Nullable Raid createOrExtendRaid(ServerPlayer var1, BlockPos var2) {
      if (var1.isSpectator()) {
         return null;
      } else {
         ServerLevel var3 = var1.level();
         if (!(Boolean)var3.getGameRules().get(GameRules.RAIDS)) {
            return null;
         } else if (!(Boolean)var3.environmentAttributes().getValue(EnvironmentAttributes.CAN_START_RAID, var2)) {
            return null;
         } else {
            List var4 = var3.getPoiManager().getInRange((var0) -> var0.is(PoiTypeTags.VILLAGE), var2, 64, PoiManager.Occupancy.IS_OCCUPIED).toList();
            int var5 = 0;
            Vec3 var6 = Vec3.ZERO;

            for(PoiRecord var8 : var4) {
               BlockPos var9 = var8.getPos();
               var6 = var6.add((double)var9.getX(), (double)var9.getY(), (double)var9.getZ());
               ++var5;
            }

            BlockPos var11;
            if (var5 > 0) {
               var6 = var6.scale(1.0 / (double)var5);
               var11 = BlockPos.containing(var6);
            } else {
               var11 = var2;
            }

            Raid var12 = this.getOrCreateRaid(var3, var11);
            if (!var12.isStarted() && !this.raidMap.containsValue(var12)) {
               this.raidMap.put(this.getUniqueId(), var12);
            }

            if (!var12.isStarted() || var12.getRaidOmenLevel() < var12.getMaxRaidOmenLevel()) {
               var12.absorbRaidOmen(var1);
            }

            this.setDirty();
            return var12;
         }
      }
   }

   private Raid getOrCreateRaid(ServerLevel var1, BlockPos var2) {
      Raid var3 = var1.getRaidAt(var2);
      return var3 != null ? var3 : new Raid(var2, var1.getDifficulty());
   }

   public static Raids load(CompoundTag var0) {
      return (Raids)CODEC.parse(NbtOps.INSTANCE, var0).resultOrPartial().orElseGet(Raids::new);
   }

   private int getUniqueId() {
      return ++this.nextId;
   }

   public @Nullable Raid getNearbyRaid(BlockPos var1, int var2) {
      Raid var3 = null;
      double var4 = (double)var2;
      ObjectIterator var6 = this.raidMap.values().iterator();

      while(var6.hasNext()) {
         Raid var7 = (Raid)var6.next();
         double var8 = var7.getCenter().distSqr(var1);
         if (var7.isActive() && var8 < var4) {
            var3 = var7;
            var4 = var8;
         }
      }

      return var3;
   }

   @VisibleForDebug
   public List<BlockPos> getRaidCentersInChunk(ChunkPos var1) {
      Stream var10000 = this.raidMap.values().stream().map(Raid::getCenter);
      Objects.requireNonNull(var1);
      return var10000.filter(var1::contains).toList();
   }

   static {
      TYPE = new SavedDataType<Raids>("raids", Raids::new, CODEC, DataFixTypes.SAVED_DATA_RAIDS);
      TYPE_END = new SavedDataType<Raids>("raids_end", Raids::new, CODEC, DataFixTypes.SAVED_DATA_RAIDS);
   }

   static record RaidWithId(int id, Raid raid) {
      final int id;
      final Raid raid;
      public static final Codec<RaidWithId> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.INT.fieldOf("id").forGetter(RaidWithId::id), Raid.MAP_CODEC.forGetter(RaidWithId::raid)).apply(var0, RaidWithId::new));

      private RaidWithId(int var1, Raid var2) {
         super();
         this.id = var1;
         this.raid = var2;
      }

      public static RaidWithId from(Int2ObjectMap.Entry<Raid> var0) {
         return new RaidWithId(var0.getIntKey(), (Raid)var0.getValue());
      }
   }
}
