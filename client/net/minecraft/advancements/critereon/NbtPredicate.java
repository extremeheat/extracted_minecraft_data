package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public record NbtPredicate(CompoundTag b) {
   private final CompoundTag tag;
   public static final Codec<NbtPredicate> CODEC = TagParser.AS_CODEC.xmap(NbtPredicate::new, NbtPredicate::tag);

   public NbtPredicate(CompoundTag var1) {
      super();
      this.tag = var1;
   }

   public boolean matches(ItemStack var1) {
      return this.matches(var1.getTag());
   }

   public boolean matches(Entity var1) {
      return this.matches(getEntityTagToCompare(var1));
   }

   public boolean matches(@Nullable Tag var1) {
      return var1 != null && NbtUtils.compareNbt(this.tag, var1, true);
   }

   public static CompoundTag getEntityTagToCompare(Entity var0) {
      CompoundTag var1 = var0.saveWithoutId(new CompoundTag());
      if (var0 instanceof Player) {
         ItemStack var2 = ((Player)var0).getInventory().getSelected();
         if (!var2.isEmpty()) {
            var1.put("SelectedItem", var2.save(new CompoundTag()));
         }
      }

      return var1;
   }
}
