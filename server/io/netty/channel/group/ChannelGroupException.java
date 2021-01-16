package io.netty.channel.group;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;

public class ChannelGroupException extends ChannelException implements Iterable<Entry<Channel, Throwable>> {
   private static final long serialVersionUID = -4093064295562629453L;
   private final Collection<Entry<Channel, Throwable>> failed;

   public ChannelGroupException(Collection<Entry<Channel, Throwable>> var1) {
      super();
      if (var1 == null) {
         throw new NullPointerException("causes");
      } else if (var1.isEmpty()) {
         throw new IllegalArgumentException("causes must be non empty");
      } else {
         this.failed = Collections.unmodifiableCollection(var1);
      }
   }

   public Iterator<Entry<Channel, Throwable>> iterator() {
      return this.failed.iterator();
   }
}
