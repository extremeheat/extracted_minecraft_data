package net.minecraft.world.level.storage.loot.providers.nbt;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ContextNbtProvider implements NbtProvider {
   private static final String BLOCK_ENTITY_ID = "block_entity";
   private static final ContextNbtProvider.Getter BLOCK_ENTITY_PROVIDER = new ContextNbtProvider.Getter() {
      @Override
      public Tag get(LootContext var1) {
         BlockEntity var2 = var1.getParamOrNull(LootContextParams.BLOCK_ENTITY);
         return var2 != null ? var2.saveWithFullMetadata() : null;
      }

      @Override
      public String getId() {
         return "block_entity";
      }

      @Override
      public Set<LootContextParam<?>> getReferencedContextParams() {
         return ImmutableSet.of(LootContextParams.BLOCK_ENTITY);
      }
   };
   public static final ContextNbtProvider BLOCK_ENTITY = new ContextNbtProvider(BLOCK_ENTITY_PROVIDER);
   private static final Codec<ContextNbtProvider.Getter> GETTER_CODEC = Codec.STRING.xmap(var0 -> {
      if (var0.equals("block_entity")) {
         return BLOCK_ENTITY_PROVIDER;
      } else {
         LootContext.EntityTarget var1 = LootContext.EntityTarget.getByName(var0);
         return forEntity(var1);
      }
   }, ContextNbtProvider.Getter::getId);
   public static final Codec<ContextNbtProvider> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(GETTER_CODEC.fieldOf("target").forGetter(var0x -> var0x.getter)).apply(var0, ContextNbtProvider::new)
   );
   public static final Codec<ContextNbtProvider> INLINE_CODEC = GETTER_CODEC.xmap(ContextNbtProvider::new, var0 -> var0.getter);
   private final ContextNbtProvider.Getter getter;

   private static ContextNbtProvider.Getter forEntity(final LootContext.EntityTarget var0) {
      return new ContextNbtProvider.Getter() {
         @Nullable
         @Override
         public Tag get(LootContext var1) {
            Entity var2 = var1.getParamOrNull(var0.getParam());
            return var2 != null ? NbtPredicate.getEntityTagToCompare(var2) : null;
         }

         @Override
         public String getId() {
            return var0.name();
         }

         @Override
         public Set<LootContextParam<?>> getReferencedContextParams() {
            return ImmutableSet.of(var0.getParam());
         }
      };
   }

   private ContextNbtProvider(ContextNbtProvider.Getter var1) {
      super();
      this.getter = var1;
   }

   @Override
   public LootNbtProviderType getType() {
      return NbtProviders.CONTEXT;
   }

   @Nullable
   @Override
   public Tag get(LootContext var1) {
      return this.getter.get(var1);
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.getter.getReferencedContextParams();
   }

   public static NbtProvider forContextEntity(LootContext.EntityTarget var0) {
      return new ContextNbtProvider(forEntity(var0));
   }

   interface Getter {
      @Nullable
      Tag get(LootContext var1);

      String getId();

      Set<LootContextParam<?>> getReferencedContextParams();
   }
}
