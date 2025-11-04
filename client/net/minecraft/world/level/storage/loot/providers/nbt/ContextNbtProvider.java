package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.advancements.criterion.NbtPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextArg;
import org.jspecify.annotations.Nullable;

public class ContextNbtProvider implements NbtProvider {
   private static final Codec<LootContextArg<Tag>> GETTER_CODEC = LootContextArg.createArgCodec((var0) -> var0.anyBlockEntity(BlockEntitySource::new).anyEntity(EntitySource::new));
   public static final MapCodec<ContextNbtProvider> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(GETTER_CODEC.fieldOf("target").forGetter((var0x) -> var0x.source)).apply(var0, ContextNbtProvider::new));
   public static final Codec<ContextNbtProvider> INLINE_CODEC;
   private final LootContextArg<Tag> source;

   private ContextNbtProvider(LootContextArg<Tag> var1) {
      super();
      this.source = var1;
   }

   public LootNbtProviderType getType() {
      return NbtProviders.CONTEXT;
   }

   public @Nullable Tag get(LootContext var1) {
      return this.source.get(var1);
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   public static NbtProvider forContextEntity(LootContext.EntityTarget var0) {
      return new ContextNbtProvider(new EntitySource(var0.contextParam()));
   }

   static {
      INLINE_CODEC = GETTER_CODEC.xmap(ContextNbtProvider::new, (var0) -> var0.source);
   }

   static record BlockEntitySource(ContextKey<? extends BlockEntity> contextParam) implements LootContextArg.Getter<BlockEntity, Tag> {
      private BlockEntitySource(ContextKey<? extends BlockEntity> var1) {
         super();
         this.contextParam = var1;
      }

      public Tag get(BlockEntity var1) {
         return var1.saveWithFullMetadata((HolderLookup.Provider)var1.getLevel().registryAccess());
      }
   }

   static record EntitySource(ContextKey<? extends Entity> contextParam) implements LootContextArg.Getter<Entity, Tag> {
      EntitySource(ContextKey<? extends Entity> var1) {
         super();
         this.contextParam = var1;
      }

      public Tag get(Entity var1) {
         return NbtPredicate.getEntityTagToCompare(var1);
      }
   }
}
