package net.minecraft.world.level.storage.loot.providers.nbt;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

public record StorageNbtProvider(ResourceLocation b) implements NbtProvider {
   private final ResourceLocation id;
   public static final Codec<StorageNbtProvider> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(ResourceLocation.CODEC.fieldOf("source").forGetter(StorageNbtProvider::id)).apply(var0, StorageNbtProvider::new)
   );

   public StorageNbtProvider(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   @Override
   public LootNbtProviderType getType() {
      return NbtProviders.STORAGE;
   }

   @Nullable
   @Override
   public Tag get(LootContext var1) {
      return var1.getLevel().getServer().getCommandStorage().get(this.id);
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of();
   }
}
