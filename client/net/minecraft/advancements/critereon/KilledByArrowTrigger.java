package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class KilledByArrowTrigger extends SimpleCriterionTrigger<KilledByArrowTrigger.TriggerInstance> {
   public KilledByArrowTrigger() {
      super();
   }

   @Override
   public Codec<KilledByArrowTrigger.TriggerInstance> codec() {
      return KilledByArrowTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Collection<Entity> var2, @Nullable ItemStack var3) {
      ArrayList var4 = Lists.newArrayList();
      HashSet var5 = Sets.newHashSet();

      for (Entity var7 : var2) {
         var5.add(var7.getType());
         var4.add(EntityPredicate.createContext(var1, var7));
      }

      this.trigger(var1, var3x -> var3x.matches(var4, var5.size(), var3));
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
