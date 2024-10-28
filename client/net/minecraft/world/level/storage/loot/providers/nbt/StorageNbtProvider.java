package net.minecraft.world.level.storage.loot.providers.nbt;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public record StorageNbtProvider(ResourceLocation id) implements NbtProvider {
   public static final MapCodec<StorageNbtProvider> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ResourceLocation.CODEC.fieldOf("source").forGetter(StorageNbtProvider::id)).apply(var0, StorageNbtProvider::new);
   });

   public StorageNbtProvider(ResourceLocation id) {
      super();
      this.id = id;
   }

   public LootNbtProviderType getType() {
      return NbtProviders.STORAGE;
   }

   @Nullable
   public Tag get(LootContext var1) {
      return var1.getLevel().getServer().getCommandStorage().get(this.id);
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of();
   }

   public ResourceLocation id() {
      return this.id;
   }
}
