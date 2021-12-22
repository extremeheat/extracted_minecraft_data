package net.minecraft.world.level.storage.loot.providers.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.nbt.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.GsonAdapterFactory;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class ContextNbtProvider implements NbtProvider {
   private static final String BLOCK_ENTITY_ID = "block_entity";
   private static final ContextNbtProvider.Getter BLOCK_ENTITY_PROVIDER = new ContextNbtProvider.Getter() {
      public Tag get(LootContext var1) {
         BlockEntity var2 = (BlockEntity)var1.getParamOrNull(LootContextParams.BLOCK_ENTITY);
         return var2 != null ? var2.saveWithFullMetadata() : null;
      }

      public String getId() {
         return "block_entity";
      }

      public Set<LootContextParam<?>> getReferencedContextParams() {
         return ImmutableSet.of(LootContextParams.BLOCK_ENTITY);
      }
   };
   public static final ContextNbtProvider BLOCK_ENTITY;
   final ContextNbtProvider.Getter getter;

   private static ContextNbtProvider.Getter forEntity(final LootContext.EntityTarget var0) {
      return new ContextNbtProvider.Getter() {
         @Nullable
         public Tag get(LootContext var1) {
            Entity var2 = (Entity)var1.getParamOrNull(var0.getParam());
            return var2 != null ? NbtPredicate.getEntityTagToCompare(var2) : null;
         }

         public String getId() {
            return var0.name();
         }

         public Set<LootContextParam<?>> getReferencedContextParams() {
            return ImmutableSet.of(var0.getParam());
         }
      };
   }

   private ContextNbtProvider(ContextNbtProvider.Getter var1) {
      super();
      this.getter = var1;
   }

   public LootNbtProviderType getType() {
      return NbtProviders.CONTEXT;
   }

   @Nullable
   public Tag get(LootContext var1) {
      return this.getter.get(var1);
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.getter.getReferencedContextParams();
   }

   public static NbtProvider forContextEntity(LootContext.EntityTarget var0) {
      return new ContextNbtProvider(forEntity(var0));
   }

   static ContextNbtProvider createFromContext(String var0) {
      if (var0.equals("block_entity")) {
         return new ContextNbtProvider(BLOCK_ENTITY_PROVIDER);
      } else {
         LootContext.EntityTarget var1 = LootContext.EntityTarget.getByName(var0);
         return new ContextNbtProvider(forEntity(var1));
      }
   }

   static {
      BLOCK_ENTITY = new ContextNbtProvider(BLOCK_ENTITY_PROVIDER);
   }

   private interface Getter {
      @Nullable
      Tag get(LootContext var1);

      String getId();

      Set<LootContextParam<?>> getReferencedContextParams();
   }

   public static class InlineSerializer implements GsonAdapterFactory.InlineSerializer<ContextNbtProvider> {
      public InlineSerializer() {
         super();
      }

      public JsonElement serialize(ContextNbtProvider var1, JsonSerializationContext var2) {
         return new JsonPrimitive(var1.getter.getId());
      }

      public ContextNbtProvider deserialize(JsonElement var1, JsonDeserializationContext var2) {
         String var3 = var1.getAsString();
         return ContextNbtProvider.createFromContext(var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ContextNbtProvider> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, ContextNbtProvider var2, JsonSerializationContext var3) {
         var1.addProperty("target", var2.getter.getId());
      }

      public ContextNbtProvider deserialize(JsonObject var1, JsonDeserializationContext var2) {
         String var3 = GsonHelper.getAsString(var1, "target");
         return ContextNbtProvider.createFromContext(var3);
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
