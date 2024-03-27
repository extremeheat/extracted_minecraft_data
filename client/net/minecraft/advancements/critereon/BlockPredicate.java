package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public record BlockPredicate(Optional<HolderSet<Block>> c, Optional<StatePropertiesPredicate> d, Optional<NbtPredicate> e) {
   private final Optional<HolderSet<Block>> blocks;
   private final Optional<StatePropertiesPredicate> properties;
   private final Optional<NbtPredicate> nbt;
   public static final Codec<BlockPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(BlockPredicate::blocks),
               StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(BlockPredicate::properties),
               NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(BlockPredicate::nbt)
            )
            .apply(var0, BlockPredicate::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, BlockPredicate> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.BLOCK)),
      BlockPredicate::blocks,
      ByteBufCodecs.optional(StatePropertiesPredicate.STREAM_CODEC),
      BlockPredicate::properties,
      ByteBufCodecs.optional(NbtPredicate.STREAM_CODEC),
      BlockPredicate::nbt,
      BlockPredicate::new
   );

   public BlockPredicate(Optional<HolderSet<Block>> var1, Optional<StatePropertiesPredicate> var2, Optional<NbtPredicate> var3) {
      super();
      this.blocks = var1;
      this.properties = var2;
      this.nbt = var3;
   }

   public boolean matches(ServerLevel var1, BlockPos var2) {
      if (!var1.isLoaded(var2)) {
         return false;
      } else if (!this.matchesState(var1.getBlockState(var2))) {
         return false;
      } else {
         return !this.nbt.isPresent() || matchesBlockEntity(var1, var1.getBlockEntity(var2), (NbtPredicate)this.nbt.get());
      }
   }

   public boolean matches(BlockInWorld var1) {
      if (!this.matchesState(var1.getState())) {
         return false;
      } else {
         return !this.nbt.isPresent() || matchesBlockEntity(var1.getLevel(), var1.getEntity(), (NbtPredicate)this.nbt.get());
      }
   }

   private boolean matchesState(BlockState var1) {
      if (this.blocks.isPresent() && !var1.is(this.blocks.get())) {
         return false;
      } else {
         return !this.properties.isPresent() || ((StatePropertiesPredicate)this.properties.get()).matches(var1);
      }
   }

   private static boolean matchesBlockEntity(LevelReader var0, @Nullable BlockEntity var1, NbtPredicate var2) {
      return var1 != null && var2.matches(var1.saveWithFullMetadata(var0.registryAccess()));
   }

   public boolean requiresNbt() {
      return this.nbt.isPresent();
   }

   public static class Builder {
      private Optional<HolderSet<Block>> blocks = Optional.empty();
      private Optional<StatePropertiesPredicate> properties = Optional.empty();
      private Optional<NbtPredicate> nbt = Optional.empty();

      private Builder() {
         super();
      }

      public static BlockPredicate.Builder block() {
         return new BlockPredicate.Builder();
      }

      public BlockPredicate.Builder of(Block... var1) {
         this.blocks = Optional.of(HolderSet.direct(Block::builtInRegistryHolder, var1));
         return this;
      }

      public BlockPredicate.Builder of(Collection<Block> var1) {
         this.blocks = Optional.of(HolderSet.direct(Block::builtInRegistryHolder, var1));
         return this;
      }

      public BlockPredicate.Builder of(TagKey<Block> var1) {
         this.blocks = Optional.of(BuiltInRegistries.BLOCK.getOrCreateTag(var1));
         return this;
      }

      public BlockPredicate.Builder hasNbt(CompoundTag var1) {
         this.nbt = Optional.of(new NbtPredicate(var1));
         return this;
      }

      public BlockPredicate.Builder setProperties(StatePropertiesPredicate.Builder var1) {
         this.properties = var1.build();
         return this;
      }

      public BlockPredicate build() {
         return new BlockPredicate(this.blocks, this.properties, this.nbt);
      }
   }
}
