package net.darkhax.huntingdim.dimension.events;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.darkhax.bookshelf.lib.Constants;
import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.bookshelf.util.WorldUtils;
import net.darkhax.huntingdim.HuntingDimension;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventXmas {

    private final NonNullList<ItemStack> held;
    private final NonNullList<ItemStack> worn;

    public EventXmas () {

        this.held = this.getSkulls("held");
        this.worn = this.getSkulls("worn");
        MinecraftForge.EVENT_BUS.register(this);
        HuntingDimension.LOG.info("Happy Holidays!");
    }

    @SubscribeEvent
    public void onMobSpawn (EntityJoinWorldEvent event) {

        if (WorldUtils.isDimension(event.getWorld(), HuntingDimension.dimensionType) && !event.getWorld().isRemote && (event.getEntity() instanceof EntityZombie || event.getEntity() instanceof EntitySkeleton)) {

            final EntityLiving living = (EntityLiving) event.getEntity();

            final EntityEquipmentSlot slot = MathsUtils.tryPercentage(0.5f) ? EntityEquipmentSlot.HEAD : EntityEquipmentSlot.MAINHAND;
            final NonNullList<ItemStack> pool = slot == EntityEquipmentSlot.HEAD ? this.worn : this.held;

            if (living.getItemStackFromSlot(slot).isEmpty()) {

                living.setItemStackToSlot(slot, pool.get(Constants.RANDOM.nextInt(pool.size())).copy());
                living.setDropChance(slot, 0.25f);
            }
        }
    }

    public NonNullList<ItemStack> getSkulls (String type) {

        final NonNullList<ItemStack> list = NonNullList.create();

        try {

            final File file = new File(HuntingDimension.class.getResource("/assets/huntingdim/skulls/" + type).toURI().getPath());

            for (final File nbtFile : file.listFiles()) {

                final NBTTagCompound tag = CompressedStreamTools.read(nbtFile);
                final ItemStack stack = new ItemStack(Items.SKULL, 1, 3);
                stack.setTagCompound(tag);
                list.add(stack);

                System.out.println(stack.toString());
            }
        }

        catch (URISyntaxException | IOException e) {

            HuntingDimension.LOG.catching(e);
        }

        return list;
    }
}
