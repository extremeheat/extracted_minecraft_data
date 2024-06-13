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
   public static final MapCodec<StorageValue> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               ResourceLocation.CODEC.fieldOf("storage").forGetter(StorageValue::storage),
               NbtPathArgument.NbtPath.CODEC.fieldOf("path").forGetter(StorageValue::path)
            )
            .apply(var0, StorageValue::new)
   );

   public StorageValue(ResourceLocation storage, NbtPathArgument.NbtPath path) {
      super();
      this.storage = storage;
      this.path = path;
   }

   @Override
   public LootNumberProviderType getType() {
      return NumberProviders.STORAGE;
   }

   private Optional<NumericTag> getNumericTag(LootContext var1) {
      CompoundTag var2 = var1.getLevel().getServer().getCommandStorage().get(this.storage);

      try {
         List var3 = this.path.get(var2);
         if (var3.size() == 1 && var3.get(0) instanceof NumericTag var4) {
            return Optional.of(var4);
         }
      } catch (CommandSyntaxException var6) {
      }

      return Optional.empty();
   }

   @Override
   public float getFloat(LootContext var1) {
      return this.getNumericTag(var1).map(NumericTag::getAsFloat).orElse(0.0F);
   }

   @Override
   public int getInt(LootContext var1) {
      return this.getNumericTag(var1).map(NumericTag::getAsInt).orElse(0);
   }
}
