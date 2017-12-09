package net.darkhax.huntingdim.handler;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import net.darkhax.huntingdim.HuntingDim;
import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {

    public static File directory = new File("config/huntingdim/");
    public static File configFile = new File(directory, "huntingdim.cfg");
    public static File worldPreset = new File(directory, "worldgen.json");

    public static Configuration config;

    public static String generatorPreset = "";

    public static int dimensionId = 28885;
    public static boolean quickPotionWearOff = true;
    public static double lootingChance = 0.25;
    public static double expChance = 0.2;
    public static double expMultiplier = 2.5f;

    public static int buffArmor = 0;
    public static float buffHealth = 0;
    public static float buffAttack = 0;

    public ConfigurationHandler () {

        if (!directory.exists()) {

            HuntingDim.LOG.info("Generating config folder");
            directory.mkdirs();
        }

        config = new Configuration(configFile);
        this.generatePresets();
        this.syncConfigData();
    }

    private void generatePresets () {

        final File preset = new File(directory, "world_generator_settings.json");

        if (!preset.exists()) {

            HuntingDim.LOG.info("World generator settings does not exist. Generating a new one.");

            try {

                FileUtils.copyURLToFile(HuntingDim.class.getResource("/assets/huntingdim/presets/hunting_dimension_generator_settings.json"), preset);
                HuntingDim.LOG.info("Finished generating world generator settings.");
            }

            catch (final IOException e) {

                HuntingDim.LOG.warn(e, "Could not generate world generator settings!");
            }
        }

        HuntingDim.LOG.info("Reading world generator settings.");

        try {

            generatorPreset = FileUtils.readFileToString(preset, StandardCharsets.UTF_8);
            HuntingDim.LOG.info("World settings loaded: " + generatorPreset.replaceAll("\\R", "").replaceAll("\\s", " "));
        }

        catch (final IOException e) {

            HuntingDim.LOG.warn(e, "Could not read world generator settings! Default will be used!");
        }
    }

    private void syncConfigData () {

        HuntingDim.LOG.info("Reading config file.");
        dimensionId = config.getInt("dimensionId", Configuration.CATEGORY_GENERAL, 28885, Integer.MIN_VALUE, Integer.MAX_VALUE, "The id for the hunting dimension.");
        quickPotionWearOff = config.getBoolean("quickPotionWearOff", Configuration.CATEGORY_GENERAL, true, "While true, beneficial potion effects on players will wear off twice as fast.");
        lootingChance = config.getFloat("lootingChance", Configuration.CATEGORY_GENERAL, 0.25f, 0f, 1f, "Whenever a mob dies in the hunting dimension, there is a chance that it will have +1 levels of looting applied to it's drop. Set to 0 to disable.");
        expChance = config.getFloat("expChance", Configuration.CATEGORY_GENERAL, 0.2f, 0f, 1f, "Whenever a mob dies in the hunting dimension, there is a chance that it will drop additional exp.");
        expMultiplier = config.getFloat("expModifier", Configuration.CATEGORY_GENERAL, 2.5f, 0f, 128f, "When a mob dies, it can drop additional exp. This changes how much more exp it will drop. Default is 2.5x.");

        buffArmor = config.getInt("buffArmor", Configuration.CATEGORY_GENERAL, 4, 0, 30, "The amount of additional armor to give mobs that spawn in the hunting dimension.");
        buffHealth = config.getFloat("buffHealth", Configuration.CATEGORY_GENERAL, 0.3f, 0, 1000f, "The percentage of additional health to give mobs that spawn in the hunting dimension.");
        buffAttack = config.getFloat("buffAttack", Configuration.CATEGORY_GENERAL, 0.3f, 0, 1000f, "The percentage of additional attack damage to give mobs that spawn in the hunting dimension.");

        if (config.hasChanged()) {

            HuntingDim.LOG.info("Saving config file.");
            config.save();
        }
    }
}