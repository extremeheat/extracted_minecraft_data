package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
import org.jspecify.annotations.Nullable;

public record BlockPredicate(Optional<HolderSet<Block>> blocks, Optional<StatePropertiesPredicate> properties, Optional<NbtPredicate> nbt, DataComponentMatchers components) {
   public static final Codec<BlockPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("blocks").forGetter(BlockPredicate::blocks), StatePropertiesPredicate.CODEC.optionalFieldOf("state").forGetter(BlockPredicate::properties), NbtPredicate.CODEC.optionalFieldOf("nbt").forGetter(BlockPredicate::nbt), DataComponentMatchers.CODEC.forGetter(BlockPredicate::components)).apply(i, BlockPredicate::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, BlockPredicate> STREAM_CODEC;

   public BlockPredicate {
      super();
   }

   public boolean matches(final ServerLevel level, final BlockPos pos) {
      if (!level.isLoaded(pos)) {
         return false;
      } else if (!this.matchesState(level.getBlockState(pos))) {
         return false;
      } else {
         if (this.nbt.isPresent() || !this.components.isEmpty()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (this.nbt.isPresent() && !matchesBlockEntity(level, blockEntity, (NbtPredicate)this.nbt.get())) {
               return false;
            }

            if (!this.components.isEmpty() && !matchesComponents(blockEntity, this.components)) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean matches(final BlockInWorld blockInWorld) {
      if (!this.matchesState(blockInWorld.getState())) {
         return false;
      } else {
         return !this.nbt.isPresent() || matchesBlockEntity(blockInWorld.getLevel(), blockInWorld.getEntity(), (NbtPredicate)this.nbt.get());
      }
   }

   private boolean matchesState(final BlockState state) {
      if (this.blocks.isPresent() && !state.is((HolderSet)this.blocks.get())) {
         return false;
      } else {
         return !this.properties.isPresent() || ((StatePropertiesPredicate)this.properties.get()).matches(state);
      }
   }

   private static boolean matchesBlockEntity(final LevelReader level, final @Nullable BlockEntity entity, final NbtPredicate nbt) {
      return entity != null && nbt.matches((Tag)entity.saveWithFullMetadata((HolderLookup.Provider)level.registryAccess()));
   }

   private static boolean matchesComponents(final @Nullable BlockEntity entity, final DataComponentMatchers components) {
      return entity != null && components.test((DataComponentGetter)entity.collectComponents());
   }

   public boolean requiresNbt() {
      return this.nbt.isPresent();
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.optional(ByteBufCodecs.holderSet(Registries.BLOCK)), BlockPredicate::blocks, ByteBufCodecs.optional(StatePropertiesPredicate.STREAM_CODEC), BlockPredicate::properties, ByteBufCodecs.optional(NbtPredicate.STREAM_CODEC), BlockPredicate::nbt, DataComponentMatchers.STREAM_CODEC, BlockPredicate::components, BlockPredicate::new);
   }

   public static class Builder {
      private Optional<HolderSet<Block>> blocks = Optional.empty();
      private Optional<StatePropertiesPredicate> properties = Optional.empty();
      private Optional<NbtPredicate> nbt = Optional.empty();
      private DataComponentMatchers components;

      private Builder() {
         super();
         this.components = DataComponentMatchers.ANY;
      }

      public static Builder block() {
         return new Builder();
      }

      public Builder of(final HolderGetter<Block> lookup, final Block... blocks) {
         return this.of(lookup, (Collection)Arrays.asList(blocks));
      }

      public Builder of(final HolderGetter<Block> lookup, final Collection<Block> blocks) {
         this.blocks = Optional.of(HolderSet.direct(Block::builtInRegistryHolder, blocks));
         return this;
      }

      public Builder of(final HolderGetter<Block> lookup, final TagKey<Block> tag) {
         this.blocks = Optional.of(lookup.getOrThrow(tag));
         return this;
      }

      public Builder hasNbt(final CompoundTag nbt) {
         this.nbt = Optional.of(new NbtPredicate(nbt));
         return this;
      }

      public Builder setProperties(final StatePropertiesPredicate.Builder properties) {
         this.properties = properties.build();
         return this;
      }

      public Builder components(final DataComponentMatchers components) {
         this.components = components;
         return this;
      }

      public BlockPredicate build() {
         return new BlockPredicate(this.blocks, this.properties, this.nbt, this.components);
      }
   }
}
