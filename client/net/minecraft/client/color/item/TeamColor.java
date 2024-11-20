package net.minecraft.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public record TeamColor(int defaultColor) implements ItemTintSource {
   public static final MapCodec<TeamColor> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default").forGetter(TeamColor::defaultColor)).apply(var0, TeamColor::new));

   public TeamColor(int var1) {
      super();
      this.defaultColor = var1;
   }

   public int calculate(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3) {
      if (var3 != null) {
         PlayerTeam var4 = var3.getTeam();
         if (var4 != null) {
            ChatFormatting var5 = ((Team)var4).getColor();
            if (var5.getColor() != null) {
               return ARGB.opaque(var5.getColor());
            }
         }
      }

      return ARGB.opaque(this.defaultColor);
   }

   public MapCodec<TeamColor> type() {
      return MAP_CODEC;
   }
}
