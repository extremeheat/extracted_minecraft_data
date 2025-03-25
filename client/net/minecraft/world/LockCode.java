package net.minecraft.world;

import com.mojang.serialization.Codec;
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
         var1.store("lock", CODEC, var2.createSerializationContext(NbtOps.INSTANCE), this);
      }

   }

   public static LockCode fromTag(CompoundTag var0, HolderLookup.Provider var1) {
      return (LockCode)var0.read("lock", CODEC, var1.createSerializationContext(NbtOps.INSTANCE)).orElse(NO_LOCK);
   }

   static {
      CODEC = ItemPredicate.CODEC.xmap(LockCode::new, LockCode::predicate);
   }
}
