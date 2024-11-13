package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.LootContext;

public record StorageNbtProvider(ResourceLocation id) implements NbtProvider {
   public static final MapCodec<StorageNbtProvider> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("source").forGetter(StorageNbtProvider::id)).apply(var0, StorageNbtProvider::new));

   public StorageNbtProvider(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public LootNbtProviderType getType() {
      return NbtProviders.STORAGE;
   }

   public Tag get(LootContext var1) {
      return var1.getLevel().getServer().getCommandStorage().get(this.id);
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of();
   }
}
