package net.minecraft.advancements.critereon;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record NbtPredicate(CompoundTag tag) {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<NbtPredicate> CODEC;
   public static final StreamCodec<ByteBuf, NbtPredicate> STREAM_CODEC;
   public static final String SELECTED_ITEM_TAG = "SelectedItem";

   public NbtPredicate(CompoundTag var1) {
      super();
      this.tag = var1;
   }

   public boolean matches(DataComponentGetter var1) {
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
      try (ProblemReporter.ScopedCollector var1 = new ProblemReporter.ScopedCollector(var0.problemPath(), LOGGER)) {
         TagValueOutput var2 = TagValueOutput.createWithContext(var1, var0.registryAccess());
         var0.saveWithoutId(var2);
         if (var0 instanceof Player var3) {
            ItemStack var4 = var3.getInventory().getSelectedItem();
            if (!var4.isEmpty()) {
               var2.store("SelectedItem", ItemStack.CODEC, var4);
            }
         }

         return var2.buildResult();
      }
   }

   static {
      CODEC = TagParser.LENIENT_CODEC.xmap(NbtPredicate::new, NbtPredicate::tag);
      STREAM_CODEC = ByteBufCodecs.COMPOUND_TAG.map(NbtPredicate::new, NbtPredicate::tag);
   }
}
