package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record Tool(List<Rule> rules, float defaultMiningSpeed, int damagePerBlock, boolean canDestroyBlocksInCreative) {
   public static final Codec<Tool> CODEC = RecordCodecBuilder.create((i) -> i.group(Tool.Rule.CODEC.listOf().fieldOf("rules").forGetter(Tool::rules), Codec.FLOAT.optionalFieldOf("default_mining_speed", 1.0F).forGetter(Tool::defaultMiningSpeed), ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("damage_per_block", 1).forGetter(Tool::damagePerBlock), Codec.BOOL.optionalFieldOf("can_destroy_blocks_in_creative", true).forGetter(Tool::canDestroyBlocksInCreative)).apply(i, Tool::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, Tool> STREAM_CODEC;

   public Tool {
      super();
   }

   public float getMiningSpeed(final BlockState state) {
      for(Rule rule : this.rules) {
         if (rule.speed.isPresent() && state.is(rule.blocks)) {
            return (Float)rule.speed.get();
         }
      }

      return this.defaultMiningSpeed;
   }

   public boolean isCorrectForDrops(final BlockState state) {
      for(Rule rule : this.rules) {
         if (rule.correctForDrops.isPresent() && state.is(rule.blocks)) {
            return (Boolean)rule.correctForDrops.get();
         }
      }

      return false;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(Tool.Rule.STREAM_CODEC.apply(ByteBufCodecs.list()), Tool::rules, ByteBufCodecs.FLOAT, Tool::defaultMiningSpeed, ByteBufCodecs.VAR_INT, Tool::damagePerBlock, ByteBufCodecs.BOOL, Tool::canDestroyBlocksInCreative, Tool::new);
   }

   public static record Rule(HolderSet<Block> blocks, Optional<Float> speed, Optional<Boolean> correctForDrops) {
      public static final Codec<Rule> CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(Registries.BLOCK).fieldOf("blocks").forGetter(Rule::blocks), ExtraCodecs.POSITIVE_FLOAT.optionalFieldOf("speed").forGetter(Rule::speed), Codec.BOOL.optionalFieldOf("correct_for_drops").forGetter(Rule::correctForDrops)).apply(i, Rule::new));
      public static final StreamCodec<RegistryFriendlyByteBuf, Rule> STREAM_CODEC;

      public Rule {
         super();
      }

      public static Rule minesAndDrops(final HolderSet<Block> blocks, final float speed) {
         return new Rule(blocks, Optional.of(speed), Optional.of(true));
      }

      public static Rule deniesDrops(final HolderSet<Block> blocks) {
         return new Rule(blocks, Optional.empty(), Optional.of(false));
      }

      public static Rule overrideSpeed(final HolderSet<Block> blocks, final float speed) {
         return new Rule(blocks, Optional.of(speed), Optional.empty());
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderSet(Registries.BLOCK), Rule::blocks, ByteBufCodecs.FLOAT.apply(ByteBufCodecs::optional), Rule::speed, ByteBufCodecs.BOOL.apply(ByteBufCodecs::optional), Rule::correctForDrops, Rule::new);
      }
   }
}
