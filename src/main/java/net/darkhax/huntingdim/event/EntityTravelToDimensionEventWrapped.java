package net.darkhax.huntingdim.event;

import java.util.Arrays;

import net.darkhax.huntingdim.HuntingDim;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

/**
 * This class is a copy of EntityTravelToDimensionEvent with a minor change. It will print out
 * debug info when the event has been cancelled.
 */
@Cancelable
public class EntityTravelToDimensionEventWrapped extends EntityTravelToDimensionEvent {

    public EntityTravelToDimensionEventWrapped (Entity entity, int dimension) {

        super(entity, dimension);
    }

    @Override
    public void setCanceled (boolean cancel) {

        super.setCanceled(cancel);
        HuntingDim.LOG.noticableWarning(true, Arrays.asList("Teleportation was canceled!", "This is not necessarily an issue."));
    }

    public static boolean onTravelToDimension (Entity entity, int dimension) {

        final EntityTravelToDimensionEvent event = new EntityTravelToDimensionEventWrapped(entity, dimension);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {

            if (entity instanceof EntityMinecartContainer) {

                ((EntityMinecartContainer) entity).dropContentsWhenDead = true;
            }
        }

        return !event.isCanceled();
    }
}
