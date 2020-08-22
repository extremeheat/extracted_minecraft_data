package net.minecraft.stats;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Stat extends ObjectiveCriteria {
   private final StatFormatter formatter;
   private final Object value;
   private final StatType type;

   protected Stat(StatType var1, Object var2, StatFormatter var3) {
      super(buildName(var1, var2));
      this.type = var1;
      this.formatter = var3;
      this.value = var2;
   }

   public static String buildName(StatType var0, Object var1) {
      return locationToKey(Registry.STAT_TYPE.getKey(var0)) + ":" + locationToKey(var0.getRegistry().getKey(var1));
   }

   private static String locationToKey(@Nullable ResourceLocation var0) {
      return var0.toString().replace(':', '.');
   }

   public StatType getType() {
      return this.type;
   }

   public Object getValue() {
      return this.value;
   }

   public String format(int var1) {
      return this.formatter.format(var1);
   }

   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof Stat && Objects.equals(this.getName(), ((Stat)var1).getName());
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public String toString() {
      return "Stat{name=" + this.getName() + ", formatter=" + this.formatter + '}';
   }
}
