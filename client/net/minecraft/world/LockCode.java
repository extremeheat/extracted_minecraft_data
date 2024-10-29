package net.minecraft.world;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;

public record LockCode(ItemPredicate predicate) {
   public static final LockCode NO_LOCK = new LockCode(ItemPredicate.Builder.item().build());
   public static final Codec<LockCode> CODEC;
   public static final String TAG_LOCK = "lock";

   public LockCode(ItemPredicate var1) {
      super();
      this.predicate = var1;
   }

   public boolean unlocksWith(ItemStack var1) {
      return this.predicate.test(var1);
   }

   public void addToTag(CompoundTag var1, HolderLookup.Provider var2) {
      if (this != NO_LOCK) {
         DataResult var3 = CODEC.encode(this, var2.createSerializationContext(NbtOps.INSTANCE), new CompoundTag());
         var3.result().ifPresent((var1x) -> {
            var1.put("lock", var1x);
         });
      }

   }

   public static LockCode fromTag(CompoundTag var0, HolderLookup.Provider var1) {
      if (var0.contains("lock", 10)) {
         DataResult var2 = CODEC.decode(var1.createSerializationContext(NbtOps.INSTANCE), var0.get("lock"));
         if (var2.isSuccess()) {
            return (LockCode)((Pair)var2.getOrThrow()).getFirst();
         }
      }

      return NO_LOCK;
   }

   public ItemPredicate predicate() {
      return this.predicate;
   }

   static {
      CODEC = ItemPredicate.CODEC.xmap(LockCode::new, LockCode::predicate);
   }
}
