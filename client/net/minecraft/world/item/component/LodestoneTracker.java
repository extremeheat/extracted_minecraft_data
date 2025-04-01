package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public record LodestoneTracker(Optional<GlobalPos> target, boolean tracked, boolean exits) {
   public static final Codec<LodestoneTracker> CODEC = RecordCodecBuilder.create((var0) -> var0.group(GlobalPos.CODEC.optionalFieldOf("target").forGetter(LodestoneTracker::target), Codec.BOOL.optionalFieldOf("tracked", true).forGetter(LodestoneTracker::tracked), Codec.BOOL.optionalFieldOf("exits", true).forGetter(LodestoneTracker::exits)).apply(var0, LodestoneTracker::new));
   public static final StreamCodec<ByteBuf, LodestoneTracker> STREAM_CODEC;

   public LodestoneTracker(Optional<GlobalPos> var1, boolean var2, boolean var3) {
      super();
      this.target = var1;
      this.tracked = var2;
      this.exits = var3;
   }

   public LodestoneTracker tick(BlockPos var1, ServerLevel var2) {
      if (this.tracked && !this.target.isEmpty()) {
         if (((GlobalPos)this.target.get()).dimension() != var2.dimension()) {
            return this;
         } else {
            BlockPos var3 = ((GlobalPos)this.target.get()).pos();
            if (this.exits) {
               if (var2.getRandom().nextInt(10) != 0) {
                  return this;
               }

               LodestoneTracker var4 = getExitTracker(var1, var2);
               if (var4 != null) {
                  return var4;
               }
            } else if (!var2.isInWorldBounds(var3) || !var2.getPoiManager().existsAtPosition(PoiTypes.LODESTONE, var3)) {
               return new LodestoneTracker(Optional.empty(), true, this.exits);
            }

            return this;
         }
      } else {
         return this;
      }
   }

   @Nullable
   public static LodestoneTracker getExitTracker(BlockPos var0, ServerLevel var1) {
      BlockPos var2 = var1.findNearestMapStructure(StructureTags.EYE_OF_ENDER_LOCATED, var0, 100, false);
      return var2 != null ? new LodestoneTracker(Optional.of(GlobalPos.of(var1.dimension(), var2)), true, true) : null;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), LodestoneTracker::target, ByteBufCodecs.BOOL, LodestoneTracker::tracked, ByteBufCodecs.BOOL, LodestoneTracker::exits, LodestoneTracker::new);
   }
}
