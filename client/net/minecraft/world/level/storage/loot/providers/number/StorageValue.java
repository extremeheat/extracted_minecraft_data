package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;

public record StorageValue(ResourceLocation storage, NbtPathArgument.NbtPath path) implements NumberProvider {
   public static final MapCodec<StorageValue> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ResourceLocation.CODEC.fieldOf("storage").forGetter(StorageValue::storage), NbtPathArgument.NbtPath.CODEC.fieldOf("path").forGetter(StorageValue::path)).apply(var0, StorageValue::new);
   });

   public StorageValue(ResourceLocation var1, NbtPathArgument.NbtPath var2) {
      super();
      this.storage = var1;
      this.path = var2;
   }

   public LootNumberProviderType getType() {
      return NumberProviders.STORAGE;
   }

   private Optional<NumericTag> getNumericTag(LootContext var1) {
      CompoundTag var2 = var1.getLevel().getServer().getCommandStorage().get(this.storage);

      try {
         List var3 = this.path.get(var2);
         if (var3.size() == 1) {
            Object var5 = var3.get(0);
            if (var5 instanceof NumericTag) {
               NumericTag var4 = (NumericTag)var5;
               return Optional.of(var4);
            }
         }
      } catch (CommandSyntaxException var6) {
      }

      return Optional.empty();
   }

   public float getFloat(LootContext var1) {
      return (Float)this.getNumericTag(var1).map(NumericTag::getAsFloat).orElse(0.0F);
   }

   public int getInt(LootContext var1) {
      return (Integer)this.getNumericTag(var1).map(NumericTag::getAsInt).orElse(0);
   }

   public ResourceLocation storage() {
      return this.storage;
   }

   public NbtPathArgument.NbtPath path() {
      return this.path;
   }
}
