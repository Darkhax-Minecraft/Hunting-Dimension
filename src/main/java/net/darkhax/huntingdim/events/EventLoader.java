package net.darkhax.huntingdim.events;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import net.darkhax.huntingdim.HuntingDimension;
import net.darkhax.huntingdim.handler.SeasonalEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;

public class EventLoader {

    public static EventData currentEvent;
    public static int month;
    public static int day;
    public static int year;
    
    public static void loadCurrentEvents () {

        final Calendar cal = Calendar.getInstance();
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        year = cal.get(Calendar.YEAR);
            
        loadEvent("halloween", 10, 20, 10, 31);
        
        if (currentEvent != null) {
            
            HuntingDimension.LOG.info("Current event is {}.", currentEvent.getName());
            MinecraftForge.EVENT_BUS.register(new SeasonalEvents());
        }
    }
    
    private static void loadEvent (String name, int startMonth, int startDay, int endMonth, int endDay) {
        
        if (!isCurrent(startMonth, startDay, endMonth, endDay)) {
            
            return;
        }
        
        try (InputStream stream = HuntingDimension.class.getResourceAsStream("/assets/huntingdim/events/" + name + ".dat"); DataInputStream data = new DataInputStream(stream);) {

            final NBTTagCompound tag = CompressedStreamTools.read(data);
            currentEvent = new EventData(tag);
        }

        catch (final Exception e) {

            HuntingDimension.LOG.error("Unabled to read event data for {}.", name);
            HuntingDimension.LOG.catching(e);
        }
    }
    
    private static boolean isCurrent(int startMonth, int startDay, int endMonth, int endDay) {
                
        return startMonth <= month && endMonth >= month && startDay <= day && endDay >= day;
    }
}
