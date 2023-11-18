package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public record BlockPredicate(Optional<TagKey<Block>> b, Optional<HolderSet<Block>> c, Optional<StatePropertiesPredicate> d, Optional<NbtPredicate> e) {
   private final Optional<TagKey<Block>> tag;
   private final Optional<HolderSet<Block>> blocks;
   private final Optional<StatePropertiesPredicate> properties;
   private final Optional<NbtPredicate> nbt;
   private static final Codec<HolderSet<Block>> BLOCKS_CODEC = BuiltInRegistries.BLOCK
      .holderByNameCodec()
      .listOf()
      .xmap(HolderSet::direct, var0 -> var0.stream().toList());
   public static final Codec<BlockPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ExtraCodecs.strictOptionalField(TagKey.codec(Registries.BLOCK), "tag").forGetter(BlockPredicate::tag),
               ExtraCodecs.strictOptionalField(BLOCKS_CODEC, "blocks").forGetter(BlockPredicate::blocks),
               ExtraCodecs.strictOptionalField(StatePropertiesPredicate.CODEC, "state").forGetter(BlockPredicate::properties),
               ExtraCodecs.strictOptionalField(NbtPredicate.CODEC, "nbt").forGetter(BlockPredicate::nbt)
            )
            .apply(var0, BlockPredicate::new)
   );

   public BlockPredicate(Optional<TagKey<Block>> var1, Optional<HolderSet<Block>> var2, Optional<StatePropertiesPredicate> var3, Optional<NbtPredicate> var4) {
      super();
      this.tag = var1;
      this.blocks = var2;
      this.properties = var3;
      this.nbt = var4;
   }

   public boolean matches(ServerLevel var1, BlockPos var2) {
      if (!var1.isLoaded(var2)) {
         return false;
      } else {
         BlockState var3 = var1.getBlockState(var2);
         if (this.tag.isPresent() && !var3.is(this.tag.get())) {
            return false;
         } else if (this.blocks.isPresent() && !var3.is(this.blocks.get())) {
            return false;
         } else if (this.properties.isPresent() && !this.properties.get().matches(var3)) {
            return false;
         } else {
            if (this.nbt.isPresent()) {
               BlockEntity var4 = var1.getBlockEntity(var2);
               if (var4 == null || !this.nbt.get().matches(var4.saveWithFullMetadata())) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public static class Builder {
      private Optional<HolderSet<Block>> blocks = Optional.empty();
      private Optional<TagKey<Block>> tag = Optional.empty();
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
         this.tag = Optional.of(var1);
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
         return new BlockPredicate(this.tag, this.blocks, this.properties, this.nbt);
      }
   }
}
