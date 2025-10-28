package net.minecraft.client.gui.components.debug;

import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jspecify.annotations.Nullable;

public class DebugEntryLookingAtEntity implements DebugScreenEntry {
   private static final ResourceLocation GROUP = ResourceLocation.withDefaultNamespace("looking_at_entity");

   public DebugEntryLookingAtEntity() {
      super();
   }

   public void display(DebugScreenDisplayer var1, @Nullable Level var2, @Nullable LevelChunk var3, @Nullable LevelChunk var4) {
      Minecraft var5 = Minecraft.getInstance();
      Entity var6 = var5.crosshairPickEntity;
      ArrayList var7 = new ArrayList();
      if (var6 != null) {
         var7.add(String.valueOf(ChatFormatting.UNDERLINE) + "Targeted Entity");
         var7.add(String.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(var6.getType())));
      }

      var1.addToGroup(GROUP, var7);
   }
}
