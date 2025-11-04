package net.minecraft.client.gui.components.debug;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;

public class DebugEntryLookingAtBlock implements DebugScreenEntry {
   private static final Identifier GROUP = Identifier.withDefaultNamespace("looking_at_block");

   public DebugEntryLookingAtBlock() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Entity var5 = Minecraft.getInstance().getCameraEntity();
      Object var6 = SharedConstants.DEBUG_SHOW_SERVER_DEBUG_VALUES ? var2 : Minecraft.getInstance().level;
      if (var5 != null && var6 != null) {
         HitResult var7 = var5.pick(20.0, 0.0F, false);
         ArrayList var8 = new ArrayList();
         if (var7.getType() == HitResult.Type.BLOCK) {
            BlockPos var9 = ((BlockHitResult)var7).getBlockPos();
            BlockState var10 = ((Level)var6).getBlockState(var9);
            String var10001 = String.valueOf(ChatFormatting.UNDERLINE);
            var8.add(var10001 + "Targeted Block: " + var9.getX() + ", " + var9.getY() + ", " + var9.getZ());
            var8.add(String.valueOf(BuiltInRegistries.BLOCK.getKey(var10.getBlock())));

            for(Map.Entry var12 : var10.getValues().entrySet()) {
               var8.add(this.getPropertyValueString(var12));
            }

            Stream var10000 = var10.getTags().map((var0) -> "#" + String.valueOf(var0.location()));
            Objects.requireNonNull(var8);
            var10000.forEach(var8::add);
         }

         var1.addToGroup(GROUP, var8);
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
