package net.minecraft.world.level.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;

public interface LevelData {
   RespawnData getRespawnData();

   long getGameTime();

   boolean isThundering();

   boolean isRaining();

   void setRaining(boolean raining);

   boolean isHardcore();

   Difficulty getDifficulty();

   boolean isDifficultyLocked();

   default void fillCrashReportCategory(final CrashReportCategory category, final LevelHeightAccessor levelHeightAccessor) {
      category.setDetail("Level spawn location", (CrashReportDetail)(() -> CrashReportCategory.formatLocation(levelHeightAccessor, this.getRespawnData().pos())));
   }

   public static record RespawnData(GlobalPos globalPos, float yaw, float pitch) {
      public static final RespawnData DEFAULT;
      public static final MapCodec<RespawnData> MAP_CODEC;
      public static final Codec<RespawnData> CODEC;
      public static final StreamCodec<ByteBuf, RespawnData> STREAM_CODEC;

      public RespawnData {
         super();
      }

      public static RespawnData of(final ResourceKey<Level> dimension, final BlockPos pos, final float yaw, final float pitch) {
         return new RespawnData(GlobalPos.of(dimension, pos.immutable()), Mth.wrapDegrees(yaw), Mth.clamp(pitch, -90.0F, 90.0F));
      }

      public ResourceKey<Level> dimension() {
         return this.globalPos.dimension();
      }

      public BlockPos pos() {
         return this.globalPos.pos();
      }

      static {
         DEFAULT = new RespawnData(GlobalPos.of(Level.OVERWORLD, BlockPos.ZERO), 0.0F, 0.0F);
         MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(GlobalPos.MAP_CODEC.forGetter(RespawnData::globalPos), Codec.floatRange(-180.0F, 180.0F).fieldOf("yaw").forGetter(RespawnData::yaw), Codec.floatRange(-90.0F, 90.0F).fieldOf("pitch").forGetter(RespawnData::pitch)).apply(i, RespawnData::new));
         CODEC = MAP_CODEC.codec();
         STREAM_CODEC = StreamCodec.composite(GlobalPos.STREAM_CODEC, RespawnData::globalPos, ByteBufCodecs.FLOAT, RespawnData::yaw, ByteBufCodecs.FLOAT, RespawnData::pitch, RespawnData::new);
      }
   }
}
