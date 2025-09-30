package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ContextNbtProvider implements NbtProvider {
   private static final ExtraCodecs.LateBoundIdMapper<String, Source<?>> SOURCES = new ExtraCodecs.LateBoundIdMapper<String, Source<?>>();
   private static final Codec<Source<?>> GETTER_CODEC;
   public static final MapCodec<ContextNbtProvider> MAP_CODEC;
   public static final Codec<ContextNbtProvider> INLINE_CODEC;
   private final Source<?> source;

   private ContextNbtProvider(Source<?> var1) {
      super();
      this.source = var1;
   }

   public LootNbtProviderType getType() {
      return NbtProviders.CONTEXT;
   }

   @Nullable
   public Tag get(LootContext var1) {
      return this.source.get(var1);
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(this.source.contextParam());
   }

   public static NbtProvider forContextEntity(LootContext.EntityTarget var0) {
      return new ContextNbtProvider(new EntitySource(var0.getParam()));
   }

   static {
      for(LootContext.EntityTarget var3 : LootContext.EntityTarget.values()) {
         SOURCES.put(var3.getSerializedName(), new EntitySource(var3.getParam()));
      }

      for(LootContext.BlockEntityTarget var7 : LootContext.BlockEntityTarget.values()) {
         SOURCES.put(var7.getSerializedName(), new BlockEntitySource(var7.getParam()));
      }

      GETTER_CODEC = SOURCES.codec(Codec.STRING);
      MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(GETTER_CODEC.fieldOf("target").forGetter((var0x) -> var0x.source)).apply(var0, ContextNbtProvider::new));
      INLINE_CODEC = GETTER_CODEC.xmap(ContextNbtProvider::new, (var0) -> var0.source);
   }

   interface Source<T> {
      ContextKey<? extends T> contextParam();

      @Nullable
      Tag get(T var1);

      @Nullable
      default Tag get(LootContext var1) {
         Object var2 = var1.getOptionalParameter(this.contextParam());
         return var2 != null ? this.get(var2) : null;
      }
   }

   static record BlockEntitySource(ContextKey<? extends BlockEntity> contextParam) implements Source<BlockEntity> {
      BlockEntitySource(ContextKey<? extends BlockEntity> var1) {
         super();
         this.contextParam = var1;
      }

      public Tag get(BlockEntity var1) {
         return var1.saveWithFullMetadata((HolderLookup.Provider)var1.getLevel().registryAccess());
      }
   }

   static record EntitySource(ContextKey<? extends Entity> contextParam) implements Source<Entity> {
      EntitySource(ContextKey<? extends Entity> var1) {
         super();
         this.contextParam = var1;
      }

      public Tag get(Entity var1) {
         return NbtPredicate.getEntityTagToCompare(var1);
      }
   }
}
