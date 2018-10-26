package net.darkhax.huntingdim.events;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Calendar;

import net.darkhax.huntingdim.HuntingDimension;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class EventLoader {

    public static EventData currentHeadEvent;
    public static EventData currentHandEvent;
    public static int month;
    public static int day;
    
    public static void loadCurrentEvents () {

        final Calendar cal = Calendar.getInstance();
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        EventLoader.loadEvent("22af39170e3a13897893a6711cec7236", false);
        EventLoader.loadEvent("62842cecd0f3cee8f0aed17b75bbf0d9", true);
        
        HuntingDimension.LOG.info("Current head event is {}.", currentHeadEvent != null ? currentHeadEvent.getName() : "none");
        HuntingDimension.LOG.info("Current hand event is {}.", currentHandEvent != null ? currentHandEvent.getName() : "bone");
    }
    
    private static void loadEvent (String name, boolean head) {

        if ((head && currentHeadEvent != null) || (!head && currentHandEvent != null)) {
            
            return;
        }
        
        try (InputStream stream = HuntingDimension.class.getResourceAsStream("/assets/huntingdim/events/" + name + ".dat"); DataInputStream data = new DataInputStream(stream);) {

            final NBTTagCompound tag = CompressedStreamTools.read(data);
            final EventData event = new EventData(tag);
            
            if (event.isCurrent(month, day)) {
                
                if (head) {
                    
                    currentHeadEvent = event;
                }
                
                else {
                    
                    currentHandEvent = event;
                }
            }
        }

        catch (final Exception e) {

            HuntingDimension.LOG.error("Unabled to read event data for {}.", name);
            HuntingDimension.LOG.catching(e);
        }
    }
}
