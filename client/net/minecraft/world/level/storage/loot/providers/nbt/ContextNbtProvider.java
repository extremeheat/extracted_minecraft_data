package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.advancements.predicates.NbtPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextArg;
import org.jspecify.annotations.Nullable;

public class ContextNbtProvider implements NbtProvider {
   private static final Codec<LootContextArg<Tag>> GETTER_CODEC = LootContextArg.createArgCodec((builder) -> builder.anyBlockEntity(BlockEntitySource::new).anyEntity(EntitySource::new));
   public static final MapCodec<ContextNbtProvider> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(GETTER_CODEC.fieldOf("target").forGetter((p) -> p.source)).apply(i, ContextNbtProvider::new));
   public static final Codec<ContextNbtProvider> INLINE_CODEC;
   private final LootContextArg<Tag> source;

   private ContextNbtProvider(final LootContextArg<Tag> source) {
      super();
      this.source = source;
   }

   public MapCodec<ContextNbtProvider> codec() {
      return MAP_CODEC;
   }

   public @Nullable Tag get(final LootContext context) {
      return this.source.get(context);
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   public static NbtProvider forContextEntity(final LootContext.EntityTarget source) {
      return new ContextNbtProvider(new EntitySource(source.contextParam()));
   }

   static {
      INLINE_CODEC = GETTER_CODEC.xmap(ContextNbtProvider::new, (p) -> p.source);
   }

   private static record BlockEntitySource(ContextKey<? extends BlockEntity> contextParam) implements LootContextArg.Getter<BlockEntity, Tag> {
      private BlockEntitySource {
         super();
      }

      public Tag get(final BlockEntity blockEntity) {
         return blockEntity.saveWithFullMetadata((HolderLookup.Provider)blockEntity.getLevel().registryAccess());
      }
   }

   private static record EntitySource(ContextKey<? extends Entity> contextParam) implements LootContextArg.Getter<Entity, Tag> {
      private EntitySource {
         super();
      }

      public Tag get(final Entity entity) {
         return NbtPredicate.getEntityTagToCompare(entity);
      }
   }
}
