package net.minecraft.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record LockCode(String key) {
   public static final LockCode NO_LOCK = new LockCode("");
   public static final Codec<LockCode> CODEC = Codec.STRING.xmap(LockCode::new, LockCode::key);
   public static final String TAG_LOCK = "Lock";

   public LockCode(String key) {
      super();
      this.key = key;
   }

   public boolean unlocksWith(ItemStack var1) {
      if (this.key.isEmpty()) {
         return true;
      } else {
         Component var2 = var1.get(DataComponents.CUSTOM_NAME);
         return var2 != null && this.key.equals(var2.getString());
      }
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
