package net.darkhax.huntingdim.events;

import java.io.DataInputStream;
import java.io.InputStream;

import net.darkhax.huntingdim.HuntingDimension;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class EventLoader {

    public static EventData loadEvent (String name) {

        try (InputStream stream = HuntingDimension.class.getResourceAsStream("/assets/huntingdim/events/" + name + ".dat"); DataInputStream data = new DataInputStream(stream);) {

            final NBTTagCompound tag = CompressedStreamTools.read(data);
            return new EventData(tag);
        }

        catch (final Exception e) {

            HuntingDimension.LOG.error("Unabled to read event data for {}.", name);
            HuntingDimension.LOG.catching(e);
        }

        return null;
    }
}
