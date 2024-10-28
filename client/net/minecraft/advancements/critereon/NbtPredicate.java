package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public record NbtPredicate(CompoundTag tag) {
   public static final Codec<NbtPredicate> STRING_CODEC;
   public static final Codec<NbtPredicate> COMPOUND_TAG_CODEC;
   public static final Codec<NbtPredicate> CODEC;
   public static final StreamCodec<ByteBuf, NbtPredicate> STREAM_CODEC;

   public NbtPredicate(CompoundTag var1) {
      super();
      this.tag = var1;
   }

   public boolean matches(ItemStack var1) {
      CustomData var2 = (CustomData)var1.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
      return var2.matchedBy(this.tag);
   }

   public boolean matches(Entity var1) {
      return this.matches((Tag)getEntityTagToCompare(var1));
   }

   public boolean matches(@Nullable Tag var1) {
      return var1 != null && NbtUtils.compareNbt(this.tag, var1, true);
   }

   public static CompoundTag getEntityTagToCompare(Entity var0) {
      CompoundTag var1 = var0.saveWithoutId(new CompoundTag());
      if (var0 instanceof Player) {
         ItemStack var2 = ((Player)var0).getInventory().getSelected();
         if (!var2.isEmpty()) {
            var1.put("SelectedItem", var2.save(var0.registryAccess()));
         }
      }

      return var1;
   }

   public CompoundTag tag() {
      return this.tag;
   }

   static {
      STRING_CODEC = TagParser.AS_CODEC.xmap(NbtPredicate::new, NbtPredicate::tag);
      COMPOUND_TAG_CODEC = CompoundTag.CODEC.xmap(NbtPredicate::new, NbtPredicate::tag);
      CODEC = Codec.withAlternative(STRING_CODEC, COMPOUND_TAG_CODEC);
      STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(NbtPredicate::new, NbtPredicate::tag);
   }
}
