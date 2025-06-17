package net.minecraft.world;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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

   public void addToTag(ValueOutput var1) {
      if (this != NO_LOCK) {
         var1.store("lock", CODEC, this);
      }

   }

   public static LockCode fromTag(ValueInput var0) {
      return (LockCode)var0.read("lock", CODEC).orElse(NO_LOCK);
   }

   static {
      CODEC = ItemPredicate.CODEC.xmap(LockCode::new, LockCode::predicate);
   }
}
