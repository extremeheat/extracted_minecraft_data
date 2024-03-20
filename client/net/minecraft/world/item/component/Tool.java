package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record Tool(List<Tool.Rule> c, float d, int e) {
   private final List<Tool.Rule> rules;
   private final float defaultMiningSpeed;
   private final int damagePerBlock;
   public static final Codec<Tool> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               Tool.Rule.CODEC.listOf().fieldOf("rules").forGetter(Tool::rules),
               ExtraCodecs.strictOptionalField(Codec.FLOAT, "default_mining_speed", 1.0F).forGetter(Tool::defaultMiningSpeed),
               ExtraCodecs.strictOptionalField(ExtraCodecs.NON_NEGATIVE_INT, "damage_per_block", 1).forGetter(Tool::damagePerBlock)
            )
            .apply(var0, Tool::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, Tool> STREAM_CODEC = StreamCodec.composite(
      Tool.Rule.STREAM_CODEC.apply(ByteBufCodecs.list()),
      Tool::rules,
      ByteBufCodecs.FLOAT,
      Tool::defaultMiningSpeed,
      ByteBufCodecs.VAR_INT,
      Tool::damagePerBlock,
      Tool::new
   );

   public Tool(List<Tool.Rule> var1, float var2, int var3) {
      super();
      this.rules = var1;
      this.defaultMiningSpeed = var2;
      this.damagePerBlock = var3;
   }

   public float getMiningSpeed(BlockState var1) {
      for(Tool.Rule var3 : this.rules) {
         if (var3.speed.isPresent() && var1.is(var3.blocks)) {
            return var3.speed.get();
         }
      }

      return this.defaultMiningSpeed;
   }

   public boolean isCorrectForDrops(BlockState var1) {
      for(Tool.Rule var3 : this.rules) {
         if (var3.correctForDrops.isPresent() && var1.is(var3.blocks)) {
            return var3.correctForDrops.get();
         }
      }

      return false;
   }

   public static record Rule(HolderSet<Block> c, Optional<Float> d, Optional<Boolean> e) {
      final HolderSet<Block> blocks;
      final Optional<Float> speed;
      final Optional<Boolean> correctForDrops;
      public static final Codec<Tool.Rule> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(Tool.Rule::blocks),
                  ExtraCodecs.strictOptionalField(ExtraCodecs.POSITIVE_FLOAT, "speed").forGetter(Tool.Rule::speed),
                  ExtraCodecs.strictOptionalField(Codec.BOOL, "correct_for_drops").forGetter(Tool.Rule::correctForDrops)
               )
               .apply(var0, Tool.Rule::new)
      );
      public static final StreamCodec<RegistryFriendlyByteBuf, Tool.Rule> STREAM_CODEC = StreamCodec.composite(
         ByteBufCodecs.holderSet(Registries.BLOCK),
         Tool.Rule::blocks,
         ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional),
         Tool.Rule::speed,
         ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional),
         Tool.Rule::correctForDrops,
         Tool.Rule::new
      );

      public Rule(HolderSet<Block> var1, Optional<Float> var2, Optional<Boolean> var3) {
         super();
         this.blocks = var1;
         this.speed = var2;
         this.correctForDrops = var3;
      }

      public static Tool.Rule minesAndDrops(List<Block> var0, float var1) {
         return forBlocks(var0, Optional.of(var1), Optional.of(true));
      }

      public static Tool.Rule minesAndDrops(TagKey<Block> var0, float var1) {
         return forTag(var0, Optional.of(var1), Optional.of(true));
      }

      public static Tool.Rule deniesDrops(TagKey<Block> var0) {
         return forTag(var0, Optional.empty(), Optional.of(false));
      }

      public static Tool.Rule overrideSpeed(TagKey<Block> var0, float var1) {
         return forTag(var0, Optional.of(var1), Optional.empty());
      }

      public static Tool.Rule overrideSpeed(List<Block> var0, float var1) {
         return forBlocks(var0, Optional.of(var1), Optional.empty());
      }

      private static Tool.Rule forTag(TagKey<Block> var0, Optional<Float> var1, Optional<Boolean> var2) {
         return new Tool.Rule(BuiltInRegistries.BLOCK.getOrCreateTag(var0), var1, var2);
      }

      private static Tool.Rule forBlocks(List<Block> var0, Optional<Float> var1, Optional<Boolean> var2) {
         return new Tool.Rule(HolderSet.direct(var0.stream().map(Block::builtInRegistryHolder).collect(Collectors.toList())), var1, var2);
      }
   }
}
