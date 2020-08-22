package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class NbtPredicate {
   public static final NbtPredicate ANY = new NbtPredicate((CompoundTag)null);
   @Nullable
   private final CompoundTag tag;

   public NbtPredicate(@Nullable CompoundTag var1) {
      this.tag = var1;
   }

   public boolean matches(ItemStack var1) {
      return this == ANY ? true : this.matches((Tag)var1.getTag());
   }

   public boolean matches(Entity var1) {
      return this == ANY ? true : this.matches((Tag)getEntityTagToCompare(var1));
   }

   public boolean matches(@Nullable Tag var1) {
      if (var1 == null) {
         return this == ANY;
      } else {
         return this.tag == null || NbtUtils.compareNbt(this.tag, var1, true);
      }
   }

   public JsonElement serializeToJson() {
      return (JsonElement)(this != ANY && this.tag != null ? new JsonPrimitive(this.tag.toString()) : JsonNull.INSTANCE);
   }

   public static NbtPredicate fromJson(@Nullable JsonElement var0) {
      if (var0 != null && !var0.isJsonNull()) {
         CompoundTag var1;
         try {
            var1 = TagParser.parseTag(GsonHelper.convertToString(var0, "nbt"));
         } catch (CommandSyntaxException var3) {
            throw new JsonSyntaxException("Invalid nbt tag: " + var3.getMessage());
         }

         return new NbtPredicate(var1);
      } else {
         return ANY;
      }
   }

   public static CompoundTag getEntityTagToCompare(Entity var0) {
      CompoundTag var1 = var0.saveWithoutId(new CompoundTag());
      if (var0 instanceof Player) {
         ItemStack var2 = ((Player)var0).inventory.getSelected();
         if (!var2.isEmpty()) {
            var1.put("SelectedItem", var2.save(new CompoundTag()));
         }
      }

      return var1;
   }
}
