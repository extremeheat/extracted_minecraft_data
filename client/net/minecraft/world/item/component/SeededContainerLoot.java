package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record SeededContainerLoot(ResourceLocation b, long c) {
   private final ResourceLocation lootTable;
   private final long seed;
   public static final Codec<SeededContainerLoot> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ResourceLocation.CODEC.fieldOf("loot_table").forGetter(SeededContainerLoot::lootTable),
               ExtraCodecs.strictOptionalField(Codec.LONG, "seed", 0L).forGetter(SeededContainerLoot::seed)
            )
            .apply(var0, SeededContainerLoot::new)
   );

   public SeededContainerLoot(ResourceLocation var1, long var2) {
      super();
      this.lootTable = var1;
      this.seed = (long)var2;
   }
}
