package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class TagEntry extends LootPoolSingletonContainer {
   public static final Codec<TagEntry> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               TagKey.codec(Registries.ITEM).fieldOf("name").forGetter(var0x -> var0x.tag), Codec.BOOL.fieldOf("expand").forGetter(var0x -> var0x.expand)
            )
            .and(singletonFields(var0))
            .apply(var0, TagEntry::new)
   );
   private final TagKey<Item> tag;
   private final boolean expand;

   private TagEntry(TagKey<Item> var1, boolean var2, int var3, int var4, List<LootItemCondition> var5, List<LootItemFunction> var6) {
      super(var3, var4, var5, var6);
      this.tag = var1;
      this.expand = var2;
   }

   @Override
   public LootPoolEntryType getType() {
      return LootPoolEntries.TAG;
   }

   @Override
   public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
      BuiltInRegistries.ITEM.getTagOrEmpty(this.tag).forEach(var1x -> var1.accept(new ItemStack(var1x)));
   }

   private boolean expandTag(LootContext var1, Consumer<LootPoolEntry> var2) {
      if (!this.canRun(var1)) {
         return false;
      } else {
         for(final Holder var4 : BuiltInRegistries.ITEM.getTagOrEmpty(this.tag)) {
            var2.accept(new LootPoolSingletonContainer.EntryBase() {
               @Override
               public void createItemStack(Consumer<ItemStack> var1, LootContext var2) {
                  var1.accept(new ItemStack(var4));
               }
            });
         }

         return true;
      }
   }

   @Override
   public boolean expand(LootContext var1, Consumer<LootPoolEntry> var2) {
      return this.expand ? this.expandTag(var1, var2) : super.expand(var1, var2);
   }

   public static LootPoolSingletonContainer.Builder<?> tagContents(TagKey<Item> var0) {
      return simpleBuilder((var1, var2, var3, var4) -> new TagEntry(var0, false, var1, var2, var3, var4));
   }

   public static LootPoolSingletonContainer.Builder<?> expandTag(TagKey<Item> var0) {
      return simpleBuilder((var1, var2, var3, var4) -> new TagEntry(var0, true, var1, var2, var3, var4));
   }
}
