package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.TooltipFlag;

public record FireworkExplosion(FireworkExplosion.Shape e, IntList f, IntList g, boolean h, boolean i) implements TooltipProvider {
   private final FireworkExplosion.Shape shape;
   private final IntList colors;
   private final IntList fadeColors;
   private final boolean hasTrail;
   private final boolean hasTwinkle;
   public static final FireworkExplosion DEFAULT = new FireworkExplosion(FireworkExplosion.Shape.SMALL_BALL, IntList.of(), IntList.of(), false, false);
   public static final Codec<IntList> COLOR_LIST_CODEC = Codec.INT.listOf().xmap(IntArrayList::new, ArrayList::new);
   public static final Codec<FireworkExplosion> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               FireworkExplosion.Shape.CODEC.fieldOf("shape").forGetter(FireworkExplosion::shape),
               ExtraCodecs.strictOptionalField(COLOR_LIST_CODEC, "colors", IntList.of()).forGetter(FireworkExplosion::colors),
               ExtraCodecs.strictOptionalField(COLOR_LIST_CODEC, "fade_colors", IntList.of()).forGetter(FireworkExplosion::fadeColors),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "has_trail", false).forGetter(FireworkExplosion::hasTrail),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "has_twinkle", false).forGetter(FireworkExplosion::hasTwinkle)
            )
            .apply(var0, FireworkExplosion::new)
   );
   private static final StreamCodec<ByteBuf, IntList> COLOR_LIST_STREAM_CODEC = ByteBufCodecs.INT
      .<ArrayList<E>>apply(ByteBufCodecs.list())
      .map(IntArrayList::new, ArrayList::new);
   public static final StreamCodec<ByteBuf, FireworkExplosion> STREAM_CODEC = StreamCodec.composite(
      FireworkExplosion.Shape.STREAM_CODEC,
      FireworkExplosion::shape,
      COLOR_LIST_STREAM_CODEC,
      FireworkExplosion::colors,
      COLOR_LIST_STREAM_CODEC,
      FireworkExplosion::fadeColors,
      ByteBufCodecs.BOOL,
      FireworkExplosion::hasTrail,
      ByteBufCodecs.BOOL,
      FireworkExplosion::hasTwinkle,
      FireworkExplosion::new
   );
   private static final Component CUSTOM_COLOR_NAME = Component.translatable("item.minecraft.firework_star.custom_color");

   public FireworkExplosion(FireworkExplosion.Shape var1, IntList var2, IntList var3, boolean var4, boolean var5) {
      super();
      this.shape = var1;
      this.colors = var2;
      this.fadeColors = var3;
      this.hasTrail = var4;
      this.hasTwinkle = var5;
   }

   @Override
   public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
      this.addShapeNameTooltip(var1);
      this.addAdditionalTooltip(var1);
   }

   public void addShapeNameTooltip(Consumer<Component> var1) {
      var1.accept(this.shape.getName().withStyle(ChatFormatting.GRAY));
   }

   public void addAdditionalTooltip(Consumer<Component> var1) {
      if (!this.colors.isEmpty()) {
         var1.accept(appendColors(Component.empty().withStyle(ChatFormatting.GRAY), this.colors));
      }

      if (!this.fadeColors.isEmpty()) {
         var1.accept(
            appendColors(
               Component.translatable("item.minecraft.firework_star.fade_to").append(CommonComponents.SPACE).withStyle(ChatFormatting.GRAY), this.fadeColors
            )
         );
      }

      if (this.hasTrail) {
         var1.accept(Component.translatable("item.minecraft.firework_star.trail").withStyle(ChatFormatting.GRAY));
      }

      if (this.hasTwinkle) {
         var1.accept(Component.translatable("item.minecraft.firework_star.flicker").withStyle(ChatFormatting.GRAY));
      }
   }

   private static Component appendColors(MutableComponent var0, IntList var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         if (var2 > 0) {
            var0.append(", ");
         }

         var0.append(getColorName(var1.getInt(var2)));
      }

      return var0;
   }

   private static Component getColorName(int var0) {
      DyeColor var1 = DyeColor.byFireworkColor(var0);
      return (Component)(var1 == null ? CUSTOM_COLOR_NAME : Component.translatable("item.minecraft.firework_star." + var1.getName()));
   }

   public FireworkExplosion withFadeColors(IntList var1) {
      return new FireworkExplosion(this.shape, this.colors, new IntArrayList(var1), this.hasTrail, this.hasTwinkle);
   }

   public static enum Shape implements StringRepresentable {
      SMALL_BALL(0, "small_ball"),
      LARGE_BALL(1, "large_ball"),
      STAR(2, "star"),
      CREEPER(3, "creeper"),
      BURST(4, "burst");

      private static final IntFunction<FireworkExplosion.Shape> BY_ID = ByIdMap.continuous(
         FireworkExplosion.Shape::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO
      );
      public static final StreamCodec<ByteBuf, FireworkExplosion.Shape> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, FireworkExplosion.Shape::getId);
      public static final Codec<FireworkExplosion.Shape> CODEC = StringRepresentable.fromValues(FireworkExplosion.Shape::values);
      private final int id;
      private final String name;

      private Shape(int var3, String var4) {
         this.id = var3;
         this.name = var4;
      }

      public MutableComponent getName() {
         return Component.translatable("item.minecraft.firework_star.shape." + this.name);
      }

      public int getId() {
         return this.id;
      }

      public static FireworkExplosion.Shape byId(int var0) {
         return BY_ID.apply(var0);
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }
   }
}
