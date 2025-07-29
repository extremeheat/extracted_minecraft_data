package net.minecraft.client.gui.components.debug;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class DebugEntryLookingAtBlock implements DebugScreenEntry {
   private static final ResourceLocation GROUP = ResourceLocation.withDefaultNamespace("looking_at_block");

   public DebugEntryLookingAtBlock() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Entity var5 = Minecraft.getInstance().getCameraEntity();
      if (var5 != null && var2 != null) {
         HitResult var6 = var5.pick(20.0, 0.0F, false);
         ArrayList var7 = new ArrayList();
         if (var6.getType() == HitResult.Type.BLOCK) {
            BlockPos var8 = ((BlockHitResult)var6).getBlockPos();
            BlockState var9 = var2.getBlockState(var8);
            String var10001 = String.valueOf(ChatFormatting.UNDERLINE);
            var7.add(var10001 + "Targeted Block: " + var8.getX() + ", " + var8.getY() + ", " + var8.getZ());
            var7.add(String.valueOf(BuiltInRegistries.BLOCK.getKey(var9.getBlock())));

            for(Map.Entry var11 : var9.getValues().entrySet()) {
               var7.add(this.getPropertyValueString(var11));
            }

            Stream var10000 = var9.getTags().map((var0) -> "#" + String.valueOf(var0.location()));
            Objects.requireNonNull(var7);
            var10000.forEach(var7::add);
         }

         var1.addToGroup(GROUP, var7);
      }
   }

   private String getPropertyValueString(Map.Entry<Property<?>, Comparable<?>> var1) {
      Property var2 = (Property)var1.getKey();
      Comparable var3 = (Comparable)var1.getValue();
      String var4 = Util.getPropertyName(var2, var3);
      if (Boolean.TRUE.equals(var3)) {
         String var10000 = String.valueOf(ChatFormatting.GREEN);
         var4 = var10000 + var4;
      } else if (Boolean.FALSE.equals(var3)) {
         String var5 = String.valueOf(ChatFormatting.RED);
         var4 = var5 + var4;
      }

      String var6 = var2.getName();
      return var6 + ": " + var4;
   }
}
