package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;

public record LodestoneTracker(Optional<GlobalPos> c, boolean d) {
   private final Optional<GlobalPos> target;
   private final boolean tracked;
   public static final Codec<LodestoneTracker> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(GlobalPos.CODEC, "target").forGetter(LodestoneTracker::target),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "tracked", true).forGetter(LodestoneTracker::tracked)
            )
            .apply(var0, LodestoneTracker::new)
   );
   public static final StreamCodec<ByteBuf, LodestoneTracker> STREAM_CODEC = StreamCodec.composite(
      GlobalPos.STREAM_CODEC.apply(ByteBufCodecs::optional), LodestoneTracker::target, ByteBufCodecs.BOOL, LodestoneTracker::tracked, LodestoneTracker::new
   );

   public LodestoneTracker(Optional<GlobalPos> var1, boolean var2) {
      super();
      this.target = var1;
      this.tracked = var2;
   }

   public LodestoneTracker tick(ServerLevel var1) {
      if (this.tracked && !this.target.isEmpty()) {
         if (((GlobalPos)this.target.get()).dimension() != var1.dimension()) {
            return this;
         } else {
            BlockPos var2 = ((GlobalPos)this.target.get()).pos();
            return var1.isInWorldBounds(var2) && var1.getPoiManager().existsAtPosition(PoiTypes.LODESTONE, var2)
               ? this
               : new LodestoneTracker(Optional.empty(), true);
         }
      } else {
         return this;
      }
   }
}
