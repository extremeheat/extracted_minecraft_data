package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootContext;

public record StorageValue(ResourceLocation storage, NbtPathArgument.NbtPath path) implements NumberProvider {
   public static final MapCodec<StorageValue> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("storage").forGetter(StorageValue::storage), NbtPathArgument.NbtPath.CODEC.fieldOf("path").forGetter(StorageValue::path)).apply(var0, StorageValue::new));

   public StorageValue(ResourceLocation var1, NbtPathArgument.NbtPath var2) {
      super();
      this.storage = var1;
      this.path = var2;
   }

   public LootNumberProviderType getType() {
      return NumberProviders.STORAGE;
   }

   private Number getNumericTag(LootContext var1, Number var2) {
      CompoundTag var3 = var1.getLevel().getServer().getCommandStorage().get(this.storage);

      try {
         List var4 = this.path.get(var3);
         if (var4.size() == 1) {
            Object var6 = var4.getFirst();
            if (var6 instanceof NumericTag) {
               NumericTag var5 = (NumericTag)var6;
               return var5.box();
            }
         }
      } catch (CommandSyntaxException var7) {
      }

      return var2;
   }

   public float getFloat(LootContext var1) {
      return this.getNumericTag(var1, 0.0F).floatValue();
   }

   public int getInt(LootContext var1) {
      return this.getNumericTag(var1, 0).intValue();
   }
}
