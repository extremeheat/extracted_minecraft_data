package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@Immutable
public class LockCode {
   public static final LockCode NO_LOCK = new LockCode("");
   public static final String TAG_LOCK = "Lock";
   private final String key;

   public LockCode(String var1) {
      super();
      this.key = var1;
   }

   public boolean unlocksWith(ItemStack var1) {
      return this.key.isEmpty() || !var1.isEmpty() && var1.hasCustomHoverName() && this.key.equals(var1.getHoverName().getString());
   }

   public void addToTag(CompoundTag var1) {
      if (!this.key.isEmpty()) {
         var1.putString("Lock", this.key);
      }

   }

   public static LockCode fromTag(CompoundTag var0) {
      return var0.contains("Lock", 8) ? new LockCode(var0.getString("Lock")) : NO_LOCK;
   }
}
