package net.minecraft.world.level.storage.loot.providers.nbt;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public class StorageNbtProvider implements NbtProvider {
   // $FF: renamed from: id net.minecraft.resources.ResourceLocation
   final ResourceLocation field_322;

   StorageNbtProvider(ResourceLocation var1) {
      super();
      this.field_322 = var1;
   }

   public LootNbtProviderType getType() {
      return NbtProviders.STORAGE;
   }

   @Nullable
   public Tag get(LootContext var1) {
      return var1.getLevel().getServer().getCommandStorage().get(this.field_322);
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of();
   }

   public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<StorageNbtProvider> {
      public Serializer() {
         super();
      }

      public void serialize(JsonObject var1, StorageNbtProvider var2, JsonSerializationContext var3) {
         var1.addProperty("source", var2.field_322.toString());
      }

      public StorageNbtProvider deserialize(JsonObject var1, JsonDeserializationContext var2) {
         String var3 = GsonHelper.getAsString(var1, "source");
         return new StorageNbtProvider(new ResourceLocation(var3));
      }

      // $FF: synthetic method
      public Object deserialize(JsonObject var1, JsonDeserializationContext var2) {
         return this.deserialize(var1, var2);
      }
   }
}
