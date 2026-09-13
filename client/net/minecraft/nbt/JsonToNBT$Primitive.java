package net.minecraft.nbt;

class JsonToNBT$Primitive extends JsonToNBT$Any {
   protected String field_150493_b;

   public JsonToNBT$Primitive(String var1, String var2) {
      super();
      this.field_150490_a = var1;
      this.field_150493_b = var2;
   }

   @Override
   public NBTBase func_150489_a() {
      try {
         if (this.field_150493_b.matches("[-+]?[0-9]*\\.?[0-9]+[d|D]")) {
            return new NBTTagDouble(Double.parseDouble(this.field_150493_b.substring(0, this.field_150493_b.length() - 1)));
         } else if (this.field_150493_b.matches("[-+]?[0-9]*\\.?[0-9]+[f|F]")) {
            return new NBTTagFloat(Float.parseFloat(this.field_150493_b.substring(0, this.field_150493_b.length() - 1)));
         } else if (this.field_150493_b.matches("[-+]?[0-9]+[b|B]")) {
            return new NBTTagByte(Byte.parseByte(this.field_150493_b.substring(0, this.field_150493_b.length() - 1)));
         } else if (this.field_150493_b.matches("[-+]?[0-9]+[l|L]")) {
            return new NBTTagLong(Long.parseLong(this.field_150493_b.substring(0, this.field_150493_b.length() - 1)));
         } else if (this.field_150493_b.matches("[-+]?[0-9]+[s|S]")) {
            return new NBTTagShort(Short.parseShort(this.field_150493_b.substring(0, this.field_150493_b.length() - 1)));
         } else if (this.field_150493_b.matches("[-+]?[0-9]+")) {
            return new NBTTagInt(Integer.parseInt(this.field_150493_b.substring(0, this.field_150493_b.length())));
         } else if (this.field_150493_b.matches("[-+]?[0-9]*\\.?[0-9]+")) {
            return new NBTTagDouble(Double.parseDouble(this.field_150493_b.substring(0, this.field_150493_b.length())));
         } else if (this.field_150493_b.equalsIgnoreCase("true") || this.field_150493_b.equalsIgnoreCase("false")) {
            return new NBTTagByte((byte)(Boolean.parseBoolean(this.field_150493_b) ? 1 : 0));
         } else if (this.field_150493_b.startsWith("[") && this.field_150493_b.endsWith("]")) {
            if (this.field_150493_b.length() > 2) {
               String var1 = this.field_150493_b.substring(1, this.field_150493_b.length() - 1);
               String[] var2 = var1.split(",");

               try {
                  if (var2.length <= 1) {
                     return new NBTTagIntArray(new int[]{Integer.parseInt(var1.trim())});
                  } else {
                     int[] var3 = new int[var2.length];

                     for(int var4 = 0; var4 < var2.length; ++var4) {
                        var3[var4] = Integer.parseInt(var2[var4].trim());
                     }

                     return new NBTTagIntArray(var3);
                  }
               } catch (NumberFormatException var5) {
                  return new NBTTagString(this.field_150493_b);
               }
            } else {
               return new NBTTagIntArray();
            }
         } else {
            if (this.field_150493_b.startsWith("\"") && this.field_150493_b.endsWith("\"") && this.field_150493_b.length() > 2) {
               this.field_150493_b = this.field_150493_b.substring(1, this.field_150493_b.length() - 1);
            }

            this.field_150493_b = this.field_150493_b.replaceAll("\\\\\"", "\"");
            return new NBTTagString(this.field_150493_b);
         }
      } catch (NumberFormatException var6) {
         this.field_150493_b = this.field_150493_b.replaceAll("\\\\\"", "\"");
         return new NBTTagString(this.field_150493_b);
      }
   }
}
