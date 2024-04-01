package net.minecraft.world.level.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public class LubricationComponent implements TooltipProvider {
   public static final Codec<LubricationComponent> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.INT.fieldOf("level").forGetter(var0x -> var0x.level)).apply(var0, LubricationComponent::new)
   );
   public static final StreamCodec<ByteBuf, LubricationComponent> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
   private final int level;
   private final float lubricationFactor;

   public LubricationComponent(int var1) {
      super();
      this.level = var1;
      this.lubricationFactor = calculateLubricationFactor(var1);
   }

   public boolean isLubricated() {
      return this.level >= 1;
   }

   public int getLevel() {
      return this.level;
   }

   @Override
   public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
      if (this.isLubricated()) {
         MutableComponent var3 = this.level == 1
            ? Component.translatable("lubrication.tooltip.lubricated")
            : Component.translatable("lubrication.tooltip.lubricated_times", this.level);
         var1.accept(var3.withStyle(ChatFormatting.GOLD));
      }

      if (var2.isAdvanced()) {
         var1.accept(Component.literal("lubricationFactor: " + this.lubricationFactor).withStyle(ChatFormatting.GRAY));
      }
   }

   private static float calculateLubricationFactor(int var0) {
      return var0 <= 0 ? 0.0F : 1.0F - (float)Math.pow(0.75, (double)var0 + 6.228262518959627);
   }

   public float applyToFriction(float var1) {
      return this.isLubricated() ? 1.0F - (1.0F - var1) * (1.0F - this.lubricationFactor) : var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         LubricationComponent var2 = (LubricationComponent)var1;
         return this.level == var2.level;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.level);
   }

   public static void lubricate(ItemStack var0) {
      LubricationComponent var1 = var0.get(DataComponents.LUBRICATION);
      if (var1 != null) {
         var0.set(DataComponents.LUBRICATION, new LubricationComponent(var1.level + 1));
      } else {
         var0.set(DataComponents.LUBRICATION, new LubricationComponent(1));
      }
   }
}
