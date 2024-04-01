package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record InventoryHeatComponent(@Nullable UUID c, int d, int e) {
   @Nullable
   private final UUID owner;
   private final int slot;
   private final int heat;
   public static final Codec<InventoryHeatComponent> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               UUIDUtil.CODEC.fieldOf("owner").forGetter(var0x -> var0x.owner),
               Codec.INT.fieldOf("slot").forGetter(var0x -> var0x.slot),
               Codec.INT.fieldOf("heat").forGetter(var0x -> var0x.heat)
            )
            .apply(var0, InventoryHeatComponent::new)
   );
   public static final StreamCodec<ByteBuf, InventoryHeatComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

   public InventoryHeatComponent(@Nullable UUID var1, int var2, int var3) {
      super();
      this.owner = var1;
      this.slot = var2;
      this.heat = var3;
   }
}
